#include "pillulebox.h"

#include <HTTPClient.h>
#include <ArduinoJson.h>
#include <EEPROM.h>

bool validateMacAddress() {

  String mac_address = WiFi.macAddress();

  HTTPClient http;
  String url = String(apiUrl) + "/validate_mac";

  StaticJsonDocument<200> doc;
  doc["mac_address"] = mac_address;

  String jsonString;
  serializeJson(doc, jsonString);

  http.begin(url);
  http.addHeader("Content-Type", "application/json");
  int httpResponseCode = http.POST(jsonString);

  if (httpResponseCode > 0) {
    String response = http.getString();
    StaticJsonDocument<300> responseDoc;
    DeserializationError error = deserializeJson(responseDoc, response);

    if (!error && responseDoc.containsKey("validated")) {
      bool isValidated = responseDoc["validated"].as<bool>();
      if (isValidated) {
        EEPROM.write(dirMacAuth, 1);
        EEPROM.commit();
      }
      return isValidated;
    }
  } else {
    setBackground(1);
    tft.setCursor(0, tft.height() / 2);
    tft.setTextSize(2);
    tft.setTextColor(TFT_YELLOW);
    tft.println("Error de conexion. Usando autorizacion previa.");
    delay(3000);
    return (EEPROM.read(dirMacAuth) == 1);
  }

  return false;
}

bool logIn(String username_email, String password) {
  HTTPClient http;

  String secretKey = generateSecretKey();
  String url = String(apiUrl) + "/login";

  StaticJsonDocument<200> doc;
  doc["username_email"] = username_email;
  doc["password"] = password;
  doc["secretKey"] = secretKey;
  doc["mac_address"] = WiFi.macAddress();

  String jsonString;
  serializeJson(doc, jsonString);

  http.begin(url);
  http.addHeader("Content-Type", "application/json");
  int httpResponseCode = http.POST(jsonString);

  if (httpResponseCode > 0) {
    String response = http.getString();

    // Parsea la respuesta JSON
    StaticJsonDocument<200> responseDoc;
    DeserializationError error = deserializeJson(responseDoc, response);

    if (!error) {
      if (responseDoc.containsKey("token")) {
        // Autenticación exitosa
        String token = responseDoc["token"].as<String>();
        writeStringInEEPROM(dirTOKEN, token.c_str(), tokenBufferSize);
        return true;
      }
    }
  }

  // Si llegamos aquí, hubo un error en la autenticación
  return false;
}

bool validateToken(const char* token) {
  HTTPClient http;
  String url = String(apiUrl) + "/validate_token";

  StaticJsonDocument<200> doc;
  doc["token"] = token;
  doc["mac_address"] = WiFi.macAddress();

  String jsonString;
  serializeJson(doc, jsonString);

  http.begin(url);
  http.addHeader("Content-Type", "application/json");
  int httpResponseCode = http.POST(jsonString);

  if (httpResponseCode > 0) {
    String response = http.getString();
    StaticJsonDocument<300> responseDoc;
    DeserializationError error = deserializeJson(responseDoc, response);

    if (error) {
      return false;
    }

    if (responseDoc.containsKey("validated")) {
      if (responseDoc["validated"].as<bool>()) {
        username = responseDoc["username"].as<String>();
        String email = responseDoc["email"].as<String>();
        return true;
      } else {
        String error = responseDoc["error"].as<String>();
        return false;
      }
    } else {
      return false;
    }
  } else {
    return false;
  }
}

bool updateCellsData(const char* token) {
    HTTPClient http;
    String url = String(apiUrl) + "/cells_with_modes/" + WiFi.macAddress();

    setBackground(2);
    tft.setCursor(0, 50);
    tft.setTextSize(2);
    tft.setTextColor(TFT_WHITE);
    tft.println("Actualizando datos\nde celdas...");

    Serial.println("\n--- Iniciando actualización de celdas ---");
    Serial.println("URL: " + url + "|");
    
    http.begin(url);
    http.addHeader("Authorization", token);
    
    int httpResponseCode = http.GET();
    Serial.printf("Código de respuesta HTTP: %d\n", httpResponseCode);
    
    if (httpResponseCode == 200) {
        String response = http.getString();
        
        DynamicJsonDocument doc(8192);
        DeserializationError error = deserializeJson(doc, response);
        
        if (!error) {
            JsonArray array = doc.as<JsonArray>();
            
            for (JsonVariant v : array) {
                int cellIndex = v["num_cell"].as<int>() - 1;
                
                if (cellIndex >= 0 && cellIndex < 14) {
                    Cell& cell = cells[cellIndex];
                    
                    try {
                        cell.setId(v["id"].as<int>());
                        cell.setNumCell(v["num_cell"].as<uint8_t>());
                        
                        // Usar la nueva función para parsear la fecha
                        if (!v["current_medicine_date"].isNull()) {
                            const char* dateStr = v["current_medicine_date"].as<const char*>();
                            time_t timestamp = parseDateTime(dateStr);
                            cell.setCurrentMedicineDate(timestamp);
                        }
                        else{
                          cell.setCurrentMedicineDate(-1);
                        }
                        
                        if (!v["single_id"].isNull()) {
                            SingleMode* sMode = new SingleMode(v["single_id"].as<int>());
                            sMode->setMedicineName(v["single_medicine"].as<const char*>());
                            
                            if (!v["dispensing_date"].isNull()) {
                                const char* dateStr = v["dispensing_date"].as<const char*>();
                                time_t timestamp = parseDateTime(dateStr);
                                sMode->setDispensingDate(timestamp);
                            }
                            
                            cell.setSingleMode(sMode);
                            setSingleModeAlarm(cell);
                        }

                        // SequentialMode
                        if (!v["seq_id"].isNull()) {
                            SequentialMode* sqMode = new SequentialMode(v["seq_id"].as<int>());
                            sqMode->setMedicineName(v["seq_medicine"].as<const char*>());
                            
                            if (!v["start_date"].isNull()) {
                                const char* dateStr = v["start_date"].as<const char*>();
                                time_t timestamp = parseDateTime(dateStr);
                                sqMode->setStartDate(timestamp);
                            }
                            
                            if (!v["end_date"].isNull()) {
                                const char* dateStr = v["end_date"].as<const char*>();
                                time_t timestamp = parseDateTime(dateStr);
                                sqMode->setEndDate(timestamp);
                            }
                            
                            tm period = {};
                            period.tm_hour = v["period_hour"].as<int>();
                            period.tm_min = v["period_min"].as<int>();
                            sqMode->setPeriod(period);
                            
                            sqMode->setLimitTimesConsumption(v["limit_times_consumption"].as<uint8_t>());
                            sqMode->setAffectedPeriods(v["affected_periods"].as<bool>());
                            sqMode->setCurrentTimesConsumption(v["current_times_consumption"].as<uint8_t>());
                            cell.setSequentialMode(sqMode);
                        }

                        // BasicMode (sin cambios ya que solo maneja horas)
                        if (!v["basic_id"].isNull()) {
                            BasicMode* bMode = new BasicMode(v["basic_id"].as<int>());
                            bMode->setMedicineName(v["basic_medicine"].as<const char*>());
                            
                            tm morningStart = {}, morningEnd = {};
                            morningStart.tm_hour = v["morning_start_hour"].as<int>();
                            morningStart.tm_min = v["morning_start_min"].as<int>();
                            morningEnd.tm_hour = v["morning_end_hour"].as<int>();
                            morningEnd.tm_min = v["morning_end_min"].as<int>();
                            bMode->setMorningStartTime(morningStart);
                            bMode->setMorningEndTime(morningEnd);
                            
                            tm afternoonStart = {}, afternoonEnd = {};
                            afternoonStart.tm_hour = v["afternoon_start_hour"].as<int>();
                            afternoonStart.tm_min = v["afternoon_start_min"].as<int>();
                            afternoonEnd.tm_hour = v["afternoon_end_hour"].as<int>();
                            afternoonEnd.tm_min = v["afternoon_end_min"].as<int>();
                            bMode->setAfternoonStartTime(afternoonStart);
                            bMode->setAfternoonEndTime(afternoonEnd);
                            
                            tm nightStart = {}, nightEnd = {};
                            nightStart.tm_hour = v["night_start_hour"].as<int>();
                            nightStart.tm_min = v["night_start_min"].as<int>();
                            nightEnd.tm_hour = v["night_end_hour"].as<int>();
                            nightEnd.tm_min = v["night_end_min"].as<int>();
                            bMode->setNightStartTime(nightStart);
                            bMode->setNightEndTime(nightEnd);
                            
                            cell.setBasicMode(bMode);
                        }
                        
                    } catch (const std::exception& e) {
                        Serial.printf("Error procesando celda: %s\n", e.what());
                        http.end();
                        return false;
                    }
                }
            }
            
            http.end();
            return true;
        }
    }
    
    http.end();
    return false;
}
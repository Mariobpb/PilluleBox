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
        username = "";
        return false;
      }
    } else {
      username = "";
      return false;
    }
  } else {
    username = "";
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

      // Primero, limpiar todas las celdas para garantizar que los modos eliminados se borren
      for (int i = 0; i < 14; i++) {
        cells[i].clearOtherModes();
      }

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
            } else {
              cell.setCurrentMedicineDate(-1);
            }

            // Verifica explícitamente cada modo y solo lo establece si no es nulo
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

            if (!v["basic_id"].isNull()) {
              BasicMode* bMode = new BasicMode(v["basic_id"].as<int>());
              bMode->setMedicineName(v["basic_medicine"].as<const char*>());

              // Parse time strings from database
              tm morningStart = {}, morningEnd = {};
              parseTimeString(v["morning_start_time"].as<const char*>(), &morningStart);
              parseTimeString(v["morning_end_time"].as<const char*>(), &morningEnd);
              bMode->setMorningStartTime(morningStart);
              bMode->setMorningEndTime(morningEnd);

              tm afternoonStart = {}, afternoonEnd = {};
              parseTimeString(v["afternoon_start_time"].as<const char*>(), &afternoonStart);
              parseTimeString(v["afternoon_end_time"].as<const char*>(), &afternoonEnd);
              bMode->setAfternoonStartTime(afternoonStart);
              bMode->setAfternoonEndTime(afternoonEnd);

              tm nightStart = {}, nightEnd = {};
              parseTimeString(v["night_start_time"].as<const char*>(), &nightStart);
              parseTimeString(v["night_end_time"].as<const char*>(), &nightEnd);
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

bool updateCurrentMedicineDate(int cellId, const char* token) {
  setBackground(2);
  tft.setCursor(0, 200);
  tft.setTextSize(3);
  tft.setTextColor(TFT_WHITE);
  tft.print("Actualizando\ndatos...");

  HTTPClient http;
  String url = String(apiUrl) + "/update_medicine_date";

  // Obtener timestamp Unix directamente
  time_t currentTime = rtc.now().unixtime();

  StaticJsonDocument<300> doc;
  doc["cell_id"] = cellId;
  doc["current_medicine_date"] = currentTime; // Enviar como timestamp Unix
  doc["mac_address"] = WiFi.macAddress();

  String jsonString;
  serializeJson(doc, jsonString);

  Serial.println("Actualizando fecha de medicina para celda ID: " + String(cellId));
  Serial.println("Timestamp Unix: " + String(currentTime));
  Serial.println("JSON: " + jsonString);

  http.begin(url);
  http.addHeader("Content-Type", "application/json");
  http.addHeader("Authorization", token);

  int httpResponseCode = http.POST(jsonString);

  if (httpResponseCode > 0) {
    String response = http.getString();
    Serial.printf("Código de respuesta HTTP: %d\n", httpResponseCode);
    Serial.println("Respuesta: " + response);

    if (httpResponseCode == 200) {
      StaticJsonDocument<200> responseDoc;
      DeserializationError error = deserializeJson(responseDoc, response);

      if (!error && responseDoc.containsKey("message")) {
        Serial.println("Fecha de medicina actualizada correctamente");
        http.end();
        return true;
      }
    }
  } else {
    Serial.printf("Error en la solicitud HTTP: %d\n", httpResponseCode);
  }

  http.end();
  return false;
}

bool registerHistory(const char* medicine_name, int consumption_status, const char* reason, int cell_id) {
  HTTPClient http;
  String url = String(apiUrl) + "/register_history/" + WiFi.macAddress();

  setBackground(2);
  tft.setCursor(0, 200);
  tft.setTextSize(3);
  tft.setTextColor(TFT_WHITE);
  tft.print("Actualizando\ndatos...");

  Serial.println("\n--- Registrando en historial ---");
  Serial.println("URL: " + url);
  Serial.println("Medicina: " + String(medicine_name));
  Serial.println("Estado: " + String(consumption_status));
  Serial.println("Razón: " + String(reason));
  Serial.println("Cell ID: " + String(cell_id));
  
  // Obtener timestamp Unix directamente
  time_t currentTime = rtc.now().unixtime();
  
  StaticJsonDocument<400> doc;
  doc["medicine_name"] = medicine_name;
  doc["consumption_status"] = consumption_status;
  doc["date_consumption"] = currentTime; // Enviar como timestamp Unix
  doc["reason"] = reason;
  doc["cell_id"] = cell_id;

  String jsonString;
  serializeJson(doc, jsonString);

  Serial.println("JSON enviado: " + jsonString);
  Serial.println("Timestamp Unix: " + String(currentTime));
  
  http.begin(url);
  http.addHeader("Content-Type", "application/json");
  
  int httpResponseCode = http.POST(jsonString);
  Serial.printf("Código de respuesta HTTP: %d\n", httpResponseCode);

  if (httpResponseCode > 0) {
    String response = http.getString();
    Serial.println("Respuesta del servidor: " + response);

    if (httpResponseCode == 200) {
      
      StaticJsonDocument<500> responseDoc;
      DeserializationError error = deserializeJson(responseDoc, response);

      if (!error && responseDoc.containsKey("message")) {
        Serial.println("Registro en historial exitoso");
        
        if (responseDoc.containsKey("mode_type")) {
          String modeType = responseDoc["mode_type"].as<String>();
          Serial.println("Tipo de modo: " + modeType);
        }
        
        if (responseDoc.containsKey("consumption_updated") && 
            responseDoc["consumption_updated"].as<bool>()) {
          Serial.println("Contador de consumos actualizado");
        }
        
        if (responseDoc.containsKey("cell_cleared") && 
            responseDoc["cell_cleared"].as<bool>()) {
          Serial.println("Celda limpiada correctamente");
        }

        delay(2000);
        http.end();
        return true;
      }
    } else {
      StaticJsonDocument<300> errorDoc;
      DeserializationError error = deserializeJson(errorDoc, response);
      
      String errorMessage = "Error desconocido";
      if (!error && errorDoc.containsKey("error")) {
        errorMessage = errorDoc["error"].as<String>();
      }
      
      Serial.println("Error del servidor: " + errorMessage);
      
      setBackground(2);
      tft.setCursor(0, 50);
      tft.setTextSize(2);
      tft.setTextColor(TFT_WHITE);
      tft.println("Error:");
      tft.println(errorMessage);
      delay(3000);
    }
  } else {
    Serial.printf("Error en la conexión HTTP: %d\n", httpResponseCode);
    
    setBackground(1);
    tft.setCursor(0, 50);
    tft.setTextSize(2);
    tft.setTextColor(TFT_YELLOW);
    tft.println("Error de\nconexion");
    delay(3000);
  }

  http.end();
  return false;
}
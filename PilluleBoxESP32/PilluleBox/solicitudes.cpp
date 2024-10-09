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

/*
void postReg(bool led, String fecha, String hora) {
  HTTPClient http;
  http.begin(apiUrl);
  StaticJsonDocument<200> doc;
  doc["led"] = led;
  doc["fecha"] = fecha;
  doc["hora"] = hora;

  String jsonData;
  serializeJson(doc, jsonData);
  Serial.println(jsonData);

  http.addHeader("Content-Type", "application/json");
  int httpResponseCode = http.POST(jsonData);

  if (httpResponseCode > 0) {
    String response = http.getString();
    Serial.println(response);
  } else {
    Serial.println("Error sending POST request");
  }

  http.end();
}
*/
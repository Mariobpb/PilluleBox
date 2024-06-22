#include "pillulebox.h"

#include <HTTPClient.h>
#include <ArduinoJson.h>

bool logIn(String username_email, String password) {
  HTTPClient http;
  
  String secretKey = generateSecretKey();
  String url = String(apiUrl) + "/login";
  
  StaticJsonDocument<200> doc;
  doc["username_email"] = username_email;
  doc["password"] = password;
  doc["secretKey"] = secretKey;
  
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
        return true;
      }
    }
  }
  
  // Si llegamos aquí, hubo un error en la autenticación
  return false;
}


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
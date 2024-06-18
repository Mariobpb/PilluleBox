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
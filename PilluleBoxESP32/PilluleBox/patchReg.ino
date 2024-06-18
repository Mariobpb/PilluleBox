/*
void patchReg(int id, bool led, String fecha, String hora) {
  HTTPClient http;
  String url = String(apiUrl) + "/" + String(id);
  http.begin(url);
  http.addHeader("Content-Type", "application/json");

  StaticJsonDocument<200> doc;
  doc["led"] = led;
  doc["fecha"] = fecha;
  doc["hora"] = hora;

  String jsonData;
  serializeJson(doc, jsonData);

  int httpResponseCode = http.sendRequest("PATCH", jsonData);

  if (httpResponseCode > 0) {
    String response = http.getString();
    Serial.println(response);
  } else {
    Serial.println("Error sending PATCH request");
  }

  http.end();
}
*/
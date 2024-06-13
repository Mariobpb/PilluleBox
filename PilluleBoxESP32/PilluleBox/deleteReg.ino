void deleteReg(int id) {
  HTTPClient http;
  String url = String(apiUrl)+"/"+String(id);
  http.begin(url);
  http.addHeader("Content-Type", "application/json");

  int httpResponseCode = http.sendRequest("DELETE");
  if (httpResponseCode > 0) {
    String response = http.getString();
    Serial.println(response);
  } else {
    Serial.println("Error sending DELETE request");
  }
  http.end();
}
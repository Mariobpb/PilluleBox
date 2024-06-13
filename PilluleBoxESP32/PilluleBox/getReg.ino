void getReg() {
  HTTPClient http;
  http.begin(apiUrl);
  int httpResponseCode = http.GET();
  if (httpResponseCode > 0) {
    String payload = http.getString();

    StaticJsonDocument<200> doc;  // Ajusta el tamaño según sea necesario
    DeserializationError error = deserializeJson(doc, payload);
    if (error) {
      Serial.print("deserializeJson() failed: ");
      Serial.println(error.c_str());
      return;
    }
    for (JsonObject obj : doc.as<JsonArray>()) {
      int id = obj["id"];
      Serial.print("ID: " + String(id));
      int led = obj["led"];
      Serial.print("    LED: " + String(led));
      String fecha = obj["fecha"];
      String formattedDate = fecha.substring(0, 10); // Toma solo los primeros 10 caracteres (YYYY-MM-DD)
      Serial.print("    Fecha: " + formattedDate);
      String hora = obj["hora"];
      Serial.println("    Hora: " + hora);
    }
  } else {
    Serial.println("Error sending GET request");
  }
  http.end();
}
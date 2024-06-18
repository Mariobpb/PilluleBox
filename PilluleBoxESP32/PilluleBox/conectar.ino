void conectar(const char* ssid, const char* password) {
  // Desconecta WiFi si ya está conectado
  WiFi.disconnect(true);

  // Espera un momento para que la desconexión tenga lugar
  delay(1000);

  WiFi.begin(ssid, password);

  tft.fillScreen(TFT_BLACK);
  tft.setCursor(0, 0);
  tft.print("Conectando a :");
  tft.setCursor(0, 30);
  tft.print(ssid);
  Serial.print("\n\nConectando a : |");
  Serial.print(ssid);
  Serial.print("|\nContraseña: |");
  Serial.print(password);
  Serial.println("|");

  int tiempo = 0;
  while (WiFi.status() != WL_CONNECTED && tiempo < 30) {
    delay(500);
    Serial.print(".");
    tiempo++;
  }
  if (tiempo == 30) {
    Serial.println("Conexión Fallida");
    return;
  }
  Serial.println("");
  Serial.print("Se ha conectado al wifi exitosamente\nIP: ");
  Serial.println(WiFi.localIP());
}
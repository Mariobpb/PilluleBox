void mostrarRedesDisponibles() {
  WiFi.scanDelete();  // Borrar la caché de escaneo
  delay(1000);
  int numRedes = WiFi.scanNetworks(); // Escanear redes WiFi
  if (numRedes == 0) {
    Serial.println("\nNo se encontraron redes disponibles.");
  } else {
    Serial.println("\nRedes disponibles:");
    
    for (int i = 0; i < numRedes; i++) {
      Serial.print(i + 1);
      Serial.print(": ");
      Serial.println(WiFi.SSID(i));
    }
  }
}
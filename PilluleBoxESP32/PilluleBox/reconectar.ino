void reconectar() {

  mostrarRedesDisponibles();
  reiniciarBuffer();

  Serial.write("\nSSID (Seleccione el número de la lista): ");
  while (!respuestaCompleta()) {
    // Puedes realizar otras tareas en el bucle principal mientras esperas la respuesta
  }
  Serial.write(buffer);

  int seleccion = atoi(buffer);
  WiFi.SSID(seleccion - 1).toCharArray(ssidBuffer, sizeof(ssidBuffer));
  reiniciarBuffer();

  Serial.write("\nPASSWORD: ");
  while (!respuestaCompleta()) {
    // Puedes realizar otras tareas en el bucle principal mientras esperas la respuesta
  }
  Serial.write(buffer);

  strncpy(passwordBuffer, buffer, bufferSize);
  passwordBuffer[strlen(passwordBuffer) - 1] = '\0';  // Remueve el último carácter

  reiniciarBuffer();
  escribirCadenaEnEEPROM(dirSSID, ssidBuffer, bufferSize);
  escribirCadenaEnEEPROM(dirPASSWORD, passwordBuffer, bufferSize);
  conectar(ssidBuffer, passwordBuffer);
}
void leerCadenaDesdeEEPROM(int direccion, char* cadena, int longitud) {
  for (int i = 0; i < longitud; i++) {
    cadena[i] = EEPROM.read(direccion + i);
  }
  cadena[longitud] = '\0';  // Agrega el carácter nulo al final de la cadena
}
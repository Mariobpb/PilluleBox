bool respuestaCompleta() {
  while (Serial.available() > 0) {
    char receivedChar = Serial.read();  // Lee el caracter recibido

    if (receivedChar == '\n') {  // Si se recibe un salto de línea, la respuesta está completa
      buffer[Index] = '\0';      // Agrega el carácter nulo al final del búfer para formar una cadena
      return true;
    } else {
      // Almacena el carácter en el búfer si no es un salto de línea
      buffer[Index] = receivedChar;
      Index = (Index + 1) % bufferSize;  // Evita desbordamientos del búfer
    }
  }

  return false;  // La respuesta no está completa todavía
}
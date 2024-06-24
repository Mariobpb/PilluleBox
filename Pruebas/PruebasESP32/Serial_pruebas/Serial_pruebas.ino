String inputString = "";
boolean stringComplete = false;

void setup() {
  Serial.begin(115200);
  inputString.reserve(200);  // Reserva espacio para la cadena
}

void loop() {
  Serial.println("Cadena recibida: " + esperarStringSerial());
  inputString = "";
  stringComplete = false;
}

void serialEvent() {
  while (Serial.available()) {
    char inChar = (char)Serial.read();
    inputString += inChar;
    if (inChar == '\n') {
      stringComplete = true;
    }
  }
}

String esperarStringSerial() {
  String res;
  while (!stringComplete) {
    serialEvent();
    Serial.print(".");
    delay(100);
  }
  Serial.println("Cadena recibida: " + inputString);
  res = inputString;
  inputString = "";
  stringComplete = false;
  return res;
}
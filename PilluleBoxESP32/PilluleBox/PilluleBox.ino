#include "pillulebox.h"

#include <EEPROM.h>
//#include <HTTPClient.h>
#include <WiFi.h>

char ssidBuffer[bufferSize];
char passwordBuffer[bufferSize];

String l[] = { "Wi-Fi", "Iniciar Sesion", "Visualizar contenido", "MAC Address" };
Lista listaOpciones(l, sizeof(l) / sizeof(l[0]));


void setup() {
  initPins();
  Serial.begin(115200);
  EEPROM.begin(512);

  Serial.println("Bienvenido :)");

  tft.init();
  tft.setRotation(0);
  tft.fillScreen(TFT_BLUE);

  char ssidEEPROM[bufferSize + 1];
  char passwordEEPROM[bufferSize + 1];

  leerCadenaDesdeEEPROM(dirSSID, ssidEEPROM, bufferSize);
  leerCadenaDesdeEEPROM(dirPASSWORD, passwordEEPROM, bufferSize);
  Serial.println("Dato leído desde la memoria flash:\nSSID: " + String(ssidEEPROM) + "\nPASSWORD: " + String(passwordEEPROM));
  if (!memoriaVacia(dirSSID, bufferSize)) {
    //conectar(ssidEEPROM, passwordEEPROM);
  }
}

void loop() {
  setBackground(1);
  listaOpciones.setTextSize(3);
  listaOpciones.setPositionY(30);
  listaOpciones.setHeight(170);
  int seleccion = listaOpciones.seleccionarLista();
  switch (seleccion) {
    case -1:
      break;
    case 1:
      reconectar();
      break;
    case 2:
      logInUI();
      break;
    case 3:
      dispenserUI();
      break;
    case 4:
      setBackground(1);
      tft.println("Direccion MAC: " + WiFi.macAddress());
      delay(3000);
      break;
  }
}
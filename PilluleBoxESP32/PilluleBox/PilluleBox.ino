#include "pillulebox.h"

#include <EEPROM.h>
//#include <HTTPClient.h>
#include <WiFi.h>

char ssidBuffer[bufferSize];
char passwordBuffer[bufferSize];


void setup() {
  initPins();
  Serial.begin(115200);
  EEPROM.begin(512);

  Serial.println("Bienvenido :)");

  tft.init();
  tft.setRotation(0);
  tft.fillScreen(TFT_BLACK);

  char ssidEEPROM[bufferSize + 1];
  char passwordEEPROM[bufferSize + 1];

  leerCadenaDesdeEEPROM(dirSSID, ssidEEPROM, bufferSize);
  leerCadenaDesdeEEPROM(dirPASSWORD, passwordEEPROM, bufferSize);
  Serial.println("Dato leído desde la memoria flash:\nSSID: " + String(ssidEEPROM) + "\nPASSWORD: " + String(passwordEEPROM));
  if (!memoriaVacia(dirSSID, bufferSize)) {
    conectar(ssidEEPROM, passwordEEPROM);
  }
}

void loop() {
  String l[] = { "Wi-Fi", "Iniciar Sesion", "Visualizar contenido", "MAC Address" };
  Lista listaOpciones(l, sizeof(l) / sizeof(l[0]));
  listaOpciones.setTextSize(4);
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
      tft.setCursor(0, 50);
      tft.println("Dirección MAC: " + WiFi.macAddress());
      delay(3000);
      break;
  }
}
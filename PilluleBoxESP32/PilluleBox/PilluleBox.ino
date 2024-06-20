#include "pillulebox.h"

#include <EEPROM.h>
//#include <HTTPClient.h>
#include <WiFi.h>

char buffer[bufferSize];
char ssidBuffer[bufferSize];
char passwordBuffer[bufferSize];


void setup() {
  Serial.begin(115200);
  EEPROM.begin(512);



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
  if ((WiFi.status() == WL_CONNECTED)) {  //Check the current connection status
    tft.setCursor(0, 20);
    tft.fillScreen(TFT_BLACK);
    tft.println("Conectado :)");
    String l[] = { "Reconectar", "MAC Address" };
    Lista listaOpciones(l, sizeof(l) / sizeof(l[0]));
    int seleccion = listaOpciones.SeleccionarLista();
    tft.setCursor(0, 20);
    tft.setTextSize(3);
    tft.fillScreen(TFT_BLACK);
    switch (seleccion) {
      case 1:
        reconectar();
        break;
      case 2:
        tft.println("Dirección MAC: " + WiFi.macAddress());
        break;
      case 3:
        tft.println("Dirección MAC: " + WiFi.macAddress());
        break;
    }
  } else {
    tft.fillScreen(TFT_BLACK);
    tft.println("WiFi Desconectado :)");
    Serial.println("WiFi Desconectado");
    reconectar();
  }
  delay(1000);
}
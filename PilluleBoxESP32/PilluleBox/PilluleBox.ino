#include "pillulebox.h"

#include <EEPROM.h>
//#include <HTTPClient.h>
#include <WiFi.h>

char ssidBuffer[bufferSize];
char passwordBuffer[bufferSize];

String WiFiConnectedList[] = { "Conectarse a\notra red", "Iniciar Sesion", "Visualizar contenido", "MAC Address" };
Lista OptionsWiFiConnected(WiFiConnectedList, sizeof(WiFiConnectedList) / sizeof(WiFiConnectedList[0]));

String WiFiDisconnectedList[] = { "Conectarse a\nred", "Visualizar contenido", "MAC Address" };
Lista OptionsWiFiDisconnected(WiFiDisconnectedList, sizeof(WiFiDisconnectedList) / sizeof(WiFiDisconnectedList[0]));


void setup() {
  initPins();
  Serial.begin(115200);
  EEPROM.begin(512);

  Serial.println("Bienvenido :)");

  tft.init();
  tft.setRotation(0);
  tft.fillScreen(TFT_BLACK);

  sprite.setColorDepth(8);

  char ssidEEPROM[bufferSize + 1];
  char passwordEEPROM[bufferSize + 1];

  leerCadenaDesdeEEPROM(dirSSID, ssidEEPROM, bufferSize);
  leerCadenaDesdeEEPROM(dirPASSWORD, passwordEEPROM, bufferSize);
  Serial.println("Dato leído desde la memoria flash:\nSSID: " + String(ssidEEPROM) + "\nPASSWORD: " + String(passwordEEPROM));
  if (!memoriaVacia(dirSSID, bufferSize)) {
    conectar(ssidEEPROM, passwordEEPROM);
    if(logIn("Mariobpb", "Z9aGo7irv4s8ZyNhWSsDFQ==")){
      tft.fillScreen(TFT_GREEN);
      delay(3000);
    } else {
      tft.fillScreen(TFT_RED);
      delay(3000);
    }
  }
}

void loop() {
  setBackground(1);
  if (WiFi.status() == WL_CONNECTED) {
    tft.setCursor(0, 20);
    tft.setTextSize(2);
    tft.setTextColor(TFT_WHITE);
    tft.println("Conectado a:\n" + WiFi.SSID() + "\n");
    OptionsWiFiConnected.setTextSize(3);
    OptionsWiFiConnected.setPositionY(tft.getCursorY());
    OptionsWiFiConnected.setHeight(180);
    int seleccion = OptionsWiFiConnected.seleccionarLista();
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
  else {
    OptionsWiFiDisconnected.setTextSize(3);
    OptionsWiFiDisconnected.setPositionY(30);
    OptionsWiFiDisconnected.setHeight(170);
    int seleccion = OptionsWiFiDisconnected.seleccionarLista();
    switch (seleccion) {
      case -1:
        break;
      case 1:
        reconectar();
        break;
      case 2:
        dispenserUI();
        break;
      case 3:
        setBackground(1);
        tft.println("Direccion MAC: " + WiFi.macAddress());
        delay(3000);
        break;
    }
  }
}
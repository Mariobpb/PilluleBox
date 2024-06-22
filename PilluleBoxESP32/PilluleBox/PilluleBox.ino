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
  menuUI();
  if ((WiFi.status() == WL_CONNECTED)) {  //Check the current connection status
    
  } else {
    
  }
  delay(1000);
}
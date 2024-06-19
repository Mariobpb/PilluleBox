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
    tft.fillScreen(TFT_BLACK);
    tft.println("Conectado :)");
    Serial.write("\n0: Reconectar\n1: GET\n2: POST\n3: DELETE\n4: PATCH\n5: MAC Address\n");
    int seleccion = esperarBuffer().toInt();
    int ID;
    bool LED;
    String Fecha, Hora;
    switch (seleccion) {
      case 0:
        reconectar();
        break;
      case 1:
        //getReg();
        break;
      case 2:
        Serial.write("LED:\n");
        LED = stringToBool(esperarBuffer());
        Serial.write("Fecha:\n");
        Fecha = esperarBuffer();
        Serial.write("Hora:\n");
        Hora = esperarBuffer();
        //postReg(LED, Fecha, Hora);
        break;
      case 3:
        Serial.write("\nID:\n");
        ID = esperarBuffer().toInt();
        //deleteReg(ID);
        break;
      case 4:
        Serial.write("\nID:\n");
        ID = esperarBuffer().toInt();
        Serial.write("LED:\n");
        LED = stringToBool(esperarBuffer());
        Serial.write("Fecha:\n");
        Fecha = esperarBuffer();
        Serial.write("Hora:\n");
        Hora = esperarBuffer();
        //patchReg(ID, LED, Fecha, Hora);
        break;
      case 5:
        Serial.println("Dirección MAC: " + WiFi.macAddress());
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
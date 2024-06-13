#include <Arduino.h>
#include <AES.h>
#include <AESLib.h>

#include <ArduinoJson.h>
#include <EEPROM.h>
#include <HTTPClient.h>
#include <WiFi.h>

const int dirSSID = 0;
const int dirPASSWORD = 64;
const char* apiUrl = "https://pillulebox-q376oyvn2a-uc.a.run.app/registros";

const int bufferSize = 64;
char buffer[bufferSize];
int Index = 0;
char ssidBuffer[bufferSize];      // Asignar memoria para ssidBuffer
char passwordBuffer[bufferSize];  // Asignar memoria para passwordBuffer

void setup() {
  Serial.begin(115200);
  EEPROM.begin(512);

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
        getReg();
        break;
      case 2:
        Serial.write("LED:\n");
        LED = stringToBool(esperarBuffer());
        Serial.write("Fecha:\n");
        Fecha = esperarBuffer();
        Serial.write("Hora:\n");
        Hora = esperarBuffer();
        postReg(LED, Fecha, Hora);
        break;
      case 3:
        Serial.write("\nID:\n");
        ID = esperarBuffer().toInt();
        deleteReg(ID);
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
        patchReg(ID, LED, Fecha, Hora);
        break;
      case 5:
        Serial.println("Dirección MAC: " + WiFi.macAddress());
        break;
    }
  } else {
    Serial.println("WiFi Desconectado");
    reconectar();
  }
  delay(1000);
}
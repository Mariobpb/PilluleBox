#include "pillulebox.h"

#include <EEPROM.h>
//#include <HTTPClient.h>
#include <WiFi.h>


String WiFiDisconnectedList[] = { "Conectarse a\nred", "Visualizar\ncontenido", "MAC Address" };
Lista OptionsWiFiDisconnected(WiFiDisconnectedList, sizeof(WiFiDisconnectedList) / sizeof(WiFiDisconnectedList[0]));

String logedInList[] = { "Conectarse a\notra red", "Cerrar Sesion", "Visualizar\ncontenido", "MAC Address" };
Lista OptionsLogedIn(logedInList, sizeof(logedInList) / sizeof(logedInList[0]));

String logedOutList[] = { "Conectarse a\notra red", "Iniciar Sesion", "Visualizar\ncontenido", "MAC Address" };
Lista OptionsLogedOut(logedOutList, sizeof(logedOutList) / sizeof(logedOutList[0]));


void setup() {
  initPins();
  Serial.begin(115200);
  EEPROM.begin(512);

  Serial.println("Bienvenido :)");

  tft.init();
  tft.setRotation(0);
  tft.fillScreen(TFT_BLACK);

  sprite.setColorDepth(8);

  char ssidEEPROM[wifiBufferSize + 1];
  char passwordEEPROM[wifiBufferSize + 1];

  readStringfromEEPROM(dirTOKEN, tokenEEPROM, tokenBufferSize);
  readStringfromEEPROM(dirSSID, ssidEEPROM, wifiBufferSize);
  readStringfromEEPROM(dirPASSWORD, passwordEEPROM, wifiBufferSize);
  if (!emptyDirFlash(dirSSID, wifiBufferSize)) {
    connectWiFi(ssidEEPROM, passwordEEPROM);
  }
}

void loop() {
  tft.setTextSize(2);
  if (WiFi.status() == WL_CONNECTED) {
    if (!emptyDirFlash(dirTOKEN, tokenBufferSize)){
      readStringfromEEPROM(dirTOKEN, tokenEEPROM, tokenBufferSize);
    }
    if (validateToken(tokenEEPROM)) {
      setBackground(1);
      tft.setTextColor(TFT_WHITE);
      tft.setCursor(0, 20);
      tft.setTextColor(TFT_BLUE);
      tft.println("Bienvenido " + username + "\n");
      tft.setTextColor(TFT_WHITE);
      tft.println("WiFi:\n" + WiFi.SSID() + "\n");
      OptionsLogedIn.setTextSize(3);
      OptionsLogedIn.setPositionY(tft.getCursorY());
      OptionsLogedIn.setHeight(180);
      int seleccion = OptionsLogedIn.selectItemFromList();
      switch (seleccion) {
        case -1:
          break;
        case 1:
          reconnectWiFi();
          break;
        case 2:
          writeStringInEEPROM(dirTOKEN, "", tokenBufferSize);
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
    } else {
      setBackground(1);
      tft.setTextColor(TFT_WHITE);
      tft.setCursor(0, 20);
      tft.println("WiFi:\n" + WiFi.SSID() + "\n");
      OptionsLogedOut.setTextSize(3);
      OptionsLogedOut.setPositionY(tft.getCursorY());
      OptionsLogedOut.setHeight(180);
      int seleccion = OptionsLogedOut.selectItemFromList();
      switch (seleccion) {
        case -1:
          break;
        case 1:
          reconnectWiFi();
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
  } else {
    tft.setCursor(0, 20);
    OptionsWiFiDisconnected.setTextSize(3);
    OptionsWiFiDisconnected.setPositionY(30);
    OptionsWiFiDisconnected.setHeight(170);
    int seleccion = OptionsWiFiDisconnected.selectItemFromList();
    switch (seleccion) {
      case -1:
        break;
      case 1:
        reconnectWiFi();
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
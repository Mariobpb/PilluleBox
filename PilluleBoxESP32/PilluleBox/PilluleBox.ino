#include "pillulebox.h"

#include <Wire.h>
#include <EEPROM.h>
//#include <HTTPClient.h>
#include <WiFi.h>


String WiFiDisconnectedList[] = { "Conectarse a\nred", "Ingresar\nmedicamentos", "MAC Address" };
Lista OptionsWiFiDisconnected(WiFiDisconnectedList, sizeof(WiFiDisconnectedList) / sizeof(WiFiDisconnectedList[0]));

String logedInList[] = { "Conectarse a\notra red", "Cerrar Sesion", "Ingresar\nmedicamentos", "MAC Address" };
Lista OptionsLogedIn(logedInList, sizeof(logedInList) / sizeof(logedInList[0]));

String logedOutList[] = { "Conectarse a\notra red", "Iniciar Sesion", "Ingresar\nmedicamentos", "MAC Address" };
Lista OptionsLogedOut(logedOutList, sizeof(logedOutList) / sizeof(logedOutList[0]));


void setup() {
  EEPROM.write(dirMacAuth, 0);
  initPins();
  Serial.begin(9600);
  EEPROM.begin(512);
  setenv("TZ", "GMT-6", 1);
  tzset();
  Wire.begin(SDA_RTC_PIN, SCL_RTC_PIN);

  if (!rtc.begin()) {
    setBackground(2);
    tft.setTextColor(TFT_WHITE);
    tft.setTextSize(2);
    tft.setCursor(0, 20);
    tft.print("No se pudo encontrar el módulo RTC,\n favor de conectarlo y reinicie");
    Serial.println("No se pudo encontrar el módulo RTC, favor de conectarlo");
    while (true)
      ;
  }

  if (rtc.lostPower()) {
    Serial.println("RTC perdió energía, estableciendo hora!");
    rtc.adjust(DateTime(F(__DATE__), F(__TIME__)));
  }

  Serial.println("Bienvenido :)");

  tft.init();
  tft.setRotation(0);
  tft.fillScreen(TFT_BLACK);

  sprite.setColorDepth(8);

  char ssidEEPROM[wifiEEPROMSize + 1];
  char passwordEEPROM[wifiEEPROMSize + 1];

  readStringfromEEPROM(dirTOKEN, tokenEEPROM, tokenBufferSize);
  readStringfromEEPROM(dirSSID, ssidEEPROM, wifiEEPROMSize);
  readStringfromEEPROM(dirPASSWORD, passwordEEPROM, wifiEEPROMSize);
  if (!emptyDirFlash(dirSSID, wifiEEPROMSize)) {
    connectWiFi(ssidEEPROM, passwordEEPROM);
  }
}

void loop() {
  tft.setTextSize(2);
  if (WiFi.status() == WL_CONNECTED) {
    if (validateMacAddress()) {
      if (!emptyDirFlash(dirTOKEN, tokenBufferSize)) {
        readStringfromEEPROM(dirTOKEN, tokenEEPROM, tokenBufferSize);
      }
      if (validateToken(tokenEEPROM)) {
        if (updateCellsAgain()) {
          if (updateCellsData(tokenEEPROM)) {
            Serial.println("Actualización exitosa");
            for (int i = 0; i < 14; i++) {
              printCellData(cells[i]);
            }
          } else {
            Serial.println("Falló la actualización");
          }
        }
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
            enterMedicine();
            delay(3000);
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
      setBackground(2);
      tft.setCursor(0, tft.height() / 2);
      tft.setTextSize(3);
      tft.setTextColor(TFT_RED);
      tft.print("Dispositivo no\nautorizado");
      while (true)
        ;
    }
  } else {
    setBackground(1);
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
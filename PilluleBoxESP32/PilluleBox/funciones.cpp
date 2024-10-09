#include "pillulebox.h"

#include <Arduino.h>

#include <AESLib.h>
#include "Base64.h"

#include <EEPROM.h>
#include <esp_random.h>

void initPins() {
  for (int i = 0; i < 6; i++) {
    pinMode(btnPins[i], INPUT_PULLDOWN);
  }
}

void resetBtns() {
  for (int i = 0; i < 6; i++) {
    btnCurrentStatus[i] = false;
  }
}

void readBtns() {
  for (int i = 0; i < 6; i++) {
    bool currentStatus = digitalRead(btnPins[i]);
    if (currentStatus && !prevBtnStatus[i]) {
      btnCurrentStatus[i] = true;
    } else {
      btnCurrentStatus[i] = false;
    }
    prevBtnStatus[i] = currentStatus;  // Updates PreviousStatus
  }
}

KeyboardType identifyArray(char (*keys)[10]) {
  if (keys == basicKeys) {
    return BK;
  } else if (keys == capitalKeys) {
    return CK;
  } else if (keys == numberSymbolKeys) {
    return NSK;
  } else {
    return Unknown;
  }
}

void checkPositionsKeyboard(char Keys[][10], int* posX, int* posY) {
  if (*posY < 0) *posY = 3;
  if (*posY > 3) *posY = 0;
  if (*posX > (countkeysInRow(Keys, *posY) - 1)) *posX = 0;
  if (*posX < 0) *posX = (countkeysInRow(Keys, *posY) - 1);
}

int countKeysSpacesInRow(char Keys[][10], int row) {
  int keysInRow = 0;
  int additionalKeys = 0;
  while ((Keys[row][keysInRow] != '\t') && keysInRow < 10) {
    keysInRow++;
    if (Keys[row][keysInRow] == ' ') {
      additionalKeys = 4;
    }
  }
  return keysInRow + additionalKeys;
}

int countkeysInRow(char Keys[][10], int row) {
  int keysInRow = 0;
  while (Keys[row][keysInRow] != '\t' && keysInRow < 10) {
    keysInRow++;
  }
  return keysInRow;
}

void Lista::updateSelection(int direccion) {
  itemSelected += direccion;
  if (itemSelected < 1) itemSelected = length;
  else if (itemSelected > length) itemSelected = 1;

  if (itemSelected <= startIndex) startIndex = itemSelected - 1;
  if (itemSelected > startIndex + itemsToShow) startIndex = itemSelected - itemsToShow;

  startIndex = max(0, min(startIndex, length - itemsToShow));
}

int Lista::selectItemFromList() {
  do {
    drawList();

    resetBtns();
    while (!btnCurrentStatus[0] && !btnCurrentStatus[1] && !btnCurrentStatus[2] && !btnCurrentStatus[3] && !btnCurrentStatus[4] && !btnCurrentStatus[5]) {
      readBtns();
      delay(50);
    };

    if (btnCurrentStatus[0]) updateSelection(-1);
    if (btnCurrentStatus[1]) updateSelection(1);
    if (btnCurrentStatus[5]) return -1;  // Salir si se presiona el botón de retorno

  } while (!btnCurrentStatus[4]);

  return itemSelected;
}

void connectWiFi(String ssid, String password) {
  // Disconnects WiFi if it is already connected
  WiFi.disconnect(true);

  delay(1000);

  WiFi.begin(ssid.c_str(), password.c_str());

  setBackground(1);
  tft.setTextColor(TFT_WHITE);
  tft.setCursor(0, 20);
  tft.setTextSize(3);
  tft.print("Conectando a :\n");
  tft.print(ssid);

  int tiempo = 0;
  tft.setCursor(0, 250);
  while (WiFi.status() != WL_CONNECTED && tiempo < 30) {
    delay(500);
    tft.setTextSize(2);
    tft.print(".");
    tiempo++;
  }
  tft.setTextColor(TFT_WHITE);
  tft.setCursor(0, 300);
  tft.setTextSize(4);
  if (tiempo == 30) {
    tft.print("Conexión Fallida");
    return;
  }
  tft.print("Conectado\nexitosamente");
  writeStringInEEPROM(dirSSID, ssid.c_str(), wifiEEPROMSize);
  writeStringInEEPROM(dirPASSWORD, password.c_str(), wifiEEPROMSize);
  delay(1000);
}

void reconnectWiFi() {
  int NumRed = selectNetwork();
  if (NumRed > 0) {
    String ssid = WiFi.SSID(NumRed - 1);
    setBackground(1);
    tft.setCursor(0, 20);
    tft.setTextSize(2);
    tft.println("Ingrese la contrasena:");
    String password = waitEnterText(basicKeys, "", 0, 0, tft.getCursorY() + tft.fontHeight());
    if (password == "\0") return;
    connectWiFi(ssid, password);
  } else {
    return;
  }
}

void readStringfromEEPROM(int address, char* str, int length) {
  for (int i = 0; i < length; i++) {
    str[i] = EEPROM.read(address + i);
  }
  str[length] = '\0';  // Agrega el carácter nulo al final de la cadena
}

bool emptyDirFlash(int direccion, int longitud) {
  for (int i = 0; i < longitud; i++) {
    if (EEPROM.read(direccion + i) != 0xFF) {
      return false;  // No está vacía
    }
  }
  return true;  // Está vacía
}

void writeStringInEEPROM(int direccion, const char* cadena, int longitud) {
  for (int i = 0; i < longitud; i++) {
    EEPROM.write(direccion + i, cadena[i]);
  }
  EEPROM.commit();
}

bool stringToBool(String value) {
  int intValue = value.toInt();
  return intValue != 0;
}

int selectNetwork() {
  WiFi.disconnect(true);
  WiFi.scanDelete();  // Borrar la caché de escaneo

  tft.setTextColor(TFT_WHITE);
  tft.setTextSize(3);
  setBackground(1);
  tft.setCursor(0, 20);

  delay(1000);

  WiFi.scanNetworks(true);  // Escanear redes WiFi
  tft.print("Escaneando redes ");
  int i = 0;
  while (WiFi.scanComplete() <= 0 && i < 11) {
    delay(600);
    i++;
    tft.print(".");
  }
  int numRedes = WiFi.scanComplete();
  setBackground(1);
  if (numRedes < 1) {
    tft.println("Sin redes disponibles");
    return -1;
  } else {
    String l[numRedes];
    for (int i = 0; i < numRedes; i++) {
      l[i] = WiFi.SSID(i);
    }
    tft.setCursor(0, 20);
    tft.setTextColor(TFT_WHITE);
    tft.setTextSize(2);
    tft.println("Redes disponibles:");
    Lista lista(l, numRedes);
    int sprPosY = tft.getCursorY() + tft.fontHeight();
    lista.setPositionY(sprPosY);
    lista.setHeight(tft.height() - sprPosY);
    //lista.setHeight(200));
    return lista.selectItemFromList();
  }
}

void setBackground(int b) {
  switch (b) {
    case 1:
      tft.fillRectVGradient(0, 0, tft.width(), tft.height(), convertRGBtoRGB565(130, 0, 0), TFT_BLACK);
      break;
    case 2:
      tft.fillScreen(TFT_BLACK);
      break;
  }
}

uint16_t convertRGBtoRGB565(uint8_t r, uint8_t g, uint8_t b) {
  return ((r & 0xF8) << 8) | ((g & 0xFC) << 3) | (b >> 3);
}

String generateSecretKey() {
  const int keyLength = 16;
  char key[keyLength + 1];

  for (int i = 0; i < keyLength; i++) {
    key[i] = char(esp_random() % 26 + 'A');  // Genera letras mayúsculas aleatorias
  }
  key[keyLength] = '\0';  // Asegura que la cadena termine con nulo

  return String(key);
}
/*
void serialEvent() {
  while (Serial.available()) {
    char inChar = (char)Serial.read();
    if (inChar == '\n' || inChar == '\r') {
      stringComplete = true;
    } else {
      inputString += inChar;
    }
  }
}

String esperarStringSerial() {
  String res;
  while (!stringComplete) {
    serialEvent();
  }
  res = inputString;

  // Eliminar el carácter de nueva línea si está presente
  res.trim();

  Serial.println("Cadena recibida: " + res);
  inputString = "";
  stringComplete = false;
  return res;
}
*/

String encryptPassword(String password) {
  byte key[16];
  byte iv[16];
  memcpy(key, Secret_Key, 16);
  memcpy(iv, IV, 16);

  int blockSize = 16;  // Tamaño de bloque AES
  int paddedLength = ((password.length() + blockSize - 1) / blockSize) * blockSize;
  byte input[paddedLength];
  byte encrypted[paddedLength];

  // Aplicar PKCS7 padding
  int paddingValue = paddedLength - password.length();
  memset(input, paddingValue, paddedLength);
  memcpy(input, password.c_str(), password.length());

  aesLib.set_paddingmode((paddingMode)0);  // Sin padding adicional
  aesLib.encrypt(input, paddedLength, encrypted, key, 16, iv);

  String encoded = base64_encode(encrypted, paddedLength);

  return encoded;
}

String base64_encode(uint8_t* data, size_t length) {
  static const char* base64_chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
  String encoded;
  int i = 0;
  int j = 0;
  uint8_t char_array_3[3];
  uint8_t char_array_4[4];
  while (length--) {
    char_array_3[i++] = *(data++);
    if (i == 3) {
      char_array_4[0] = (char_array_3[0] & 0xfc) >> 2;
      char_array_4[1] = ((char_array_3[0] & 0x03) << 4) + ((char_array_3[1] & 0xf0) >> 4);
      char_array_4[2] = ((char_array_3[1] & 0x0f) << 2) + ((char_array_3[2] & 0xc0) >> 6);
      char_array_4[3] = char_array_3[2] & 0x3f;

      for (i = 0; i < 4; i++)
        encoded += base64_chars[char_array_4[i]];
      i = 0;
    }
  }

  if (i) {
    for (j = i; j < 3; j++)
      char_array_3[j] = '\0';
    char_array_4[0] = (char_array_3[0] & 0xfc) >> 2;
    char_array_4[1] = ((char_array_3[0] & 0x03) << 4) + ((char_array_3[1] & 0xf0) >> 4);
    char_array_4[2] = ((char_array_3[1] & 0x0f) << 2) + ((char_array_3[2] & 0xc0) >> 6);
    for (j = 0; j < i + 1; j++)
      encoded += base64_chars[char_array_4[j]];
    while (i++ < 3)
      encoded += '=';
  }
  return encoded;
}
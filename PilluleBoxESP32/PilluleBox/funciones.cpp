#include "pillulebox.h"

#include <Arduino.h>

#include <AESLib.h>
#include "Base64.h"

#include <EEPROM.h>
#include <esp_random.h>

#include <Adafruit_PWMServoDriver.h>

void initPins() {
  for (int i = 0; i < 6; i++) {
    pinMode(btnPins[i], INPUT_PULLDOWN);
  }
  pinMode(Buzzer_PIN, OUTPUT);
  digitalWrite(Buzzer_PIN, LOW);
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
    prevBtnStatus[i] = currentStatus;
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
      if (WiFi.status() == WL_CONNECTED) {
        if (checkedAlarms()) {
          showBackgroundInfo();
          drawList();
        }
        if (updateCellsAgain()) {
          if (updateCellsData(tokenEEPROM)) {
            Serial.println("Actualización de celdas exitosa");
            
          } else {
            Serial.println("Falló la actualización de celdas");
            delay(1000);
          }
          return -1;
        }
      }
      delay(50);
    };

    if (btnCurrentStatus[0]) updateSelection(-1);
    if (btnCurrentStatus[1]) updateSelection(1);
    if (btnCurrentStatus[5]) return -1;

  } while (!btnCurrentStatus[4]);

  return itemSelected;
}

void connectWiFi(String ssid, String password) {
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
    tft.print("\nConexion\nFallida");
    delay(2000);
    return;
  }
  tft.print("\nConectado\nexitosamente");
  writeStringInEEPROM(dirSSID, ssid.c_str(), wifiEEPROMSize);
  writeStringInEEPROM(dirPASSWORD, password.c_str(), wifiEEPROMSize);
  delay(2000);
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
  str[length] = '\0';
}

bool emptyDirFlash(int direccion, int longitud) {
  for (int i = 0; i < longitud; i++) {
    if (EEPROM.read(direccion + i) != 0xFF) {
      return false;
    }
  }
  return true;
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
  WiFi.scanDelete();

  tft.setTextColor(TFT_WHITE);
  tft.setTextSize(3);
  setBackground(1);
  tft.setCursor(0, 20);

  delay(1000);

  WiFi.scanNetworks(true);
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
    lista.setHeight(tft.height() - (sprPosY + (tft.fontHeight() * 2)));
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
    case 3:
      tft.fillScreen(convertRGBtoRGB565(162, 85, 245));
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
  key[keyLength] = '\0';

  return String(key);
}

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

time_t parseDateTime(const char* dateStr) {
  struct tm tm = {};
  int year, month, day, hour, min, sec;
  float msec;
  if (sscanf(dateStr, "%d-%d-%dT%d:%d:%d.%fZ",
             &year, &month, &day, &hour, &min, &sec, &msec)
      >= 6) {
    tm.tm_year = year - 1900;
    tm.tm_mon = month - 1;
    tm.tm_mday = day;
    tm.tm_hour = hour;
    tm.tm_min = min;
    tm.tm_sec = sec;

    Serial.printf("Fecha UTC recibida: %s\n", dateStr);
    Serial.printf("Valores parseados UTC: %04d-%02d-%02d %02d:%02d:%02d\n",
                  year, month, day, hour, min, sec);
    time_t timestamp = mktime(&tm);
    timestamp -= 6 * 3600;
    struct tm* check = localtime(&timestamp);
    Serial.printf("Fecha ajustada a GMT-6: %04d-%02d-%02d %02d:%02d:%02d\n",
                  check->tm_year + 1900, check->tm_mon + 1, check->tm_mday,
                  check->tm_hour, check->tm_min, check->tm_sec);

    return timestamp;
  }

  Serial.println("Error parseando fecha: " + String(dateStr));
  return 0;
}

void setSingleModeAlarm(const Cell& cell) {
  if (cell.getSingleMode() != nullptr) {
    int cellIndex = cell.getNumCell() - 1;
    time_t dispensingTime = cell.getSingleMode()->getDispensingDate();

    struct tm* timeinfo = localtime(&dispensingTime);
    DateTime alarmTime(
      timeinfo->tm_year + 1900,
      timeinfo->tm_mon + 1,
      timeinfo->tm_mday,
      timeinfo->tm_hour,
      timeinfo->tm_min,
      timeinfo->tm_sec);

    DateTime now = rtc.now();

    if (alarms[cellIndex].isActive) {

      if (alarms[cellIndex].alarmTime > alarmTime) {
        Serial.print("Preservando alarma aplazada para celda ");
        Serial.print(cell.getNumCell());
        Serial.print(" - Hora aplazada: ");
        Serial.print(alarms[cellIndex].alarmTime.hour());
        Serial.print(":");
        Serial.print(alarms[cellIndex].alarmTime.minute());
        Serial.print(":");
        Serial.println(alarms[cellIndex].alarmTime.second());
        return;
      }
    }

    if (alarmTime > now || cell.getCurrentMedicineDate() != -1) {
      alarms[cellIndex].isActive = true;
      alarms[cellIndex].alarmTime = alarmTime;
      alarms[cellIndex].cellNumber = cell.getNumCell();

      if (cellIndex == 0) {
        rtc.setAlarm1(alarmTime, DS3231_A1_Date);
      } else if (cellIndex == 1) {
        rtc.setAlarm2(alarmTime, DS3231_A2_Date);
      }

      Serial.print("Alarma configurada para celda ");
      Serial.print(cell.getNumCell());
      Serial.print(" - Fecha: ");
      Serial.print(alarmTime.year());
      Serial.print("/");
      Serial.print(alarmTime.month());
      Serial.print("/");
      Serial.print(alarmTime.day());
      Serial.print(" ");
      Serial.print(alarmTime.hour());
      Serial.print(":");
      Serial.print(alarmTime.minute());
      Serial.print(":");
      Serial.println(alarmTime.second());
    } else {
      Serial.print("Ignorando alarma pasada para celda ");
      Serial.println(cell.getNumCell());
    }
  }
}


void snoozeAlarm(int alarmIndex) {
  int minutes = 5;
  if (alarmIndex >= 0 && alarmIndex < 14 && alarms[alarmIndex].isActive) {
    DateTime now = rtc.now();
    DateTime newAlarmTime = now + TimeSpan(0, 0, minutes, 0);
    alarms[alarmIndex].alarmTime = newAlarmTime;

    Serial.print("Alarma aplazada para celda ");
    Serial.print(alarms[alarmIndex].cellNumber);
    Serial.print(" por ");
    Serial.print(minutes);
    Serial.print(" minutos. Nueva hora: ");
    Serial.print(newAlarmTime.hour());
    Serial.print(":");
    Serial.print(newAlarmTime.minute());
    Serial.print(":");
    Serial.println(newAlarmTime.second());
    if (alarmIndex == 0) {
      rtc.setAlarm1(newAlarmTime, DS3231_A1_Date);
    } else if (alarmIndex == 1) {
      rtc.setAlarm2(newAlarmTime, DS3231_A2_Date);
    }
  }
}

bool checkedAlarms() {
  bool startedAlarm = false;
  DateTime now = rtc.now();

  for (int i = 0; i < 14; i++) {
    if (alarms[i].isActive) {
      int cellIndex = alarms[i].cellNumber - 1;
      if (now >= alarms[i].alarmTime && cells[cellIndex].getCurrentMedicineDate() != -1) {
        startedAlarm = true;

        String medicineName = "Med. Desconocido";

        if (cellIndex >= 0 && cellIndex < 14) {
          Cell& currentCell = cells[cellIndex];

          if (currentCell.getSingleMode() != nullptr) {
            medicineName = String(currentCell.getSingleMode()->getMedicineName());
          } else if (currentCell.getSequentialMode() != nullptr) {
            medicineName = String(currentCell.getSequentialMode()->getMedicineName());
          } else if (currentCell.getBasicMode() != nullptr) {
            medicineName = String(currentCell.getBasicMode()->getMedicineName());
          }
        }

        Serial.print("¡Alarma! Celda ");
        Serial.print(alarms[i].cellNumber);
        Serial.print(" - Medicamento: ");
        Serial.print(medicineName);
        Serial.println(" listo para dispensar");

        setBackground(3);
        tft.setCursor(10, 100);
        tft.setTextColor(TFT_WHITE);
        tft.setTextSize(4);
        tft.println(medicineName);
        tft.setTextSize(2);
        tft.setTextColor(TFT_WHITE);
        tft.print("\n  listo para dispensar");
        tft.fillRect(20, 360, 20, 20, TFT_GREEN);
        tft.setCursor(50, 360);
        tft.print("Dispensar");
        tft.fillRect(20, 420, 20, 20, TFT_RED);
        tft.setCursor(50, 420);
        tft.print("Posponer 5 min");

        digitalWrite(Buzzer_PIN, HIGH);
        readBtns();

        bool alarmHandled = false;
        do {
          resetBtns();
          while (!btnCurrentStatus[4] && !btnCurrentStatus[5]) {
            readBtns();
            delay(50);
          }

          if (btnCurrentStatus[5]) {
            digitalWrite(Buzzer_PIN, LOW);
            snoozeAlarm(i);

            setBackground(1);
            tft.setCursor(10, 200);
            tft.setTextColor(TFT_WHITE);
            tft.setTextSize(3);
            tft.println("Alarma aplazada");
            tft.setTextSize(2);
            tft.println("por 5 minutos");
            delay(2000);

            alarmHandled = true;
            break;
          }

          if (btnCurrentStatus[4]) {
            digitalWrite(Buzzer_PIN, LOW);
            int currentCell = alarms[i].cellNumber;
            posicionarCelda(currentCell);
            if (currentCell <= 7) {
              dispensarSeccion(SERVO180_1_CHANNEL);
            } else {
              dispensarSeccion(SERVO180_2_CHANNEL);
            }

            bool consumedMedicine = false;
            String reason = "";
            setBackground(1);
            tft.setCursor(0, 0);
            tft.setTextColor(TFT_WHITE);
            tft.setTextSize(3);
            tft.print("Favor de confirmar el\nconsumo del\nmedicamento");
            tft.fillRect(20, 200, 50, 50, TFT_GREEN);
            tft.setTextSize(2);
            tft.setCursor(80, 200);
            tft.print("Lo he consumido");
            tft.fillRect(20, 300, 50, 50, TFT_RED);
            tft.setCursor(80, 300);
            tft.print("No puedo consumirlo");
            resetBtns();
            while (!btnCurrentStatus[4] && !btnCurrentStatus[5]) {
              readBtns();
              delay(50);
            }
            if (btnCurrentStatus[4]) {
              consumedMedicine = true;
            } else if (btnCurrentStatus[5]) {
              setBackground(1);
              tft.setCursor(0, 20);
              tft.setTextColor(TFT_WHITE);
              tft.setTextSize(2);
              tft.print("Favor de definir la razon\npor su falta de consumo\n");
              reason = waitEnterText(basicKeys, "", 0, 0, tft.getCursorY() + tft.fontHeight());
            }
            registerHistory(medicineName.c_str(), consumedMedicine, reason.c_str(), cells[cellIndex].getId());
            if (updateCellsData(tokenEEPROM)) {
              Serial.println("Actualización de celdas exitosa");
            } else {
              Serial.println("Falló la actualización de celdas");
            }
            alarms[i].isActive = false;
            alarmHandled = true;
            break;
          }

        } while (!alarmHandled);

        delay(1000);
        readBtns();
        setBackground(1);
      }
    }
  }
  return startedAlarm;
}

void parseTimeString(const char* timeStr, tm* timeStruct) {
  if (timeStr && strlen(timeStr) >= 8) {
    int hour, min, sec;
    if (sscanf(timeStr, "%d:%d:%d", &hour, &min, &sec) == 3) {
      timeStruct->tm_hour = hour;
      timeStruct->tm_min = min;
      timeStruct->tm_sec = sec;
    }
  }
}


bool updateCellsAgain() {
  DateTime now = rtc.now();
  if (lastLocalUpdate.unixtime() == 0) {
    lastLocalUpdate = now;
    Serial.printf("Primera actualización a las %02d:%02d\n",
                  now.hour(),
                  now.minute());
    return true;
  }
  TimeSpan timePassed = now - lastLocalUpdate;
  long minutesPassed = timePassed.totalseconds() / 60;
  if (minutesPassed >= 1) {
    Serial.printf("Solicitando actualización a las %02d:%02d:%02d\n",
                  now.hour(),
                  now.minute(),
                  now.second());
    lastLocalUpdate = now;
    return true;
  }
  return false;
}

void enterMedicine() {
  setBackground(1);
  tft.setCursor(0, 0);
  tft.setTextColor(TFT_WHITE);
  tft.setTextSize(2);
  tft.print("Seleccione la celda\na ingresar el medicamento");
  tft.fillRect(20, 360, 20, 20, TFT_GREEN);
  tft.setCursor(50, 360);
  tft.print("Seleccionar");
  tft.fillRect(20, 420, 20, 20, TFT_RED);
  tft.setCursor(50, 420);
  tft.print("Cancelar");
  int column = 0;
  int row = 0;
  do {
    displayCellSelected(cells, column, row);
    resetBtns();
    while (!btnCurrentStatus[0] && !btnCurrentStatus[1] && !btnCurrentStatus[2] && !btnCurrentStatus[3] && !btnCurrentStatus[4] && !btnCurrentStatus[5]) {
      readBtns();
      delay(50);
    }
    if (btnCurrentStatus[5]) {
      return;
    }
    if (btnCurrentStatus[0]) {
      row--;
      if (row < 0) row = 6;
    }
    if (btnCurrentStatus[1]) {
      row++;
      if (row > 6) row = 0;
    }
    if (btnCurrentStatus[2]) {
      column--;
      if (column < 0) column = 1;
    }
    if (btnCurrentStatus[3]) {
      column++;
      if (column > 1) column = 0;
    }
  } while (!btnCurrentStatus[4]);
  int cellSelected = (row + 1) + (column * 7);
  Serial.println("Celda seleccionada: " + (String)cellSelected);

  posicionarCelda(cellSelected);
  setBackground(1);
  tft.setCursor(0, 0);
  tft.setTextColor(TFT_WHITE);
  tft.setTextSize(3);
  tft.print("Favor de\nconfirmar el\nIngreso del\nmedicamento");
  tft.fillRect(20, 200, 50, 50, TFT_GREEN);
  tft.setCursor(80, 200);
  tft.print("Confirmar");
  tft.fillRect(20, 300, 50, 50, TFT_RED);
  tft.setCursor(80, 300);
  tft.print("Cancelar");
  do {
    resetBtns();
    while (!btnCurrentStatus[4] && !btnCurrentStatus[5]) {
      readBtns();
      delay(50);
    }
    if (btnCurrentStatus[5]) {
      return;
    }
  } while (!btnCurrentStatus[4]);
  updateCurrentMedicineDate(cells[cellSelected - 1].getId(), tokenEEPROM);
  if (updateCellsData(tokenEEPROM)) {
    Serial.println("Actualización de celdas exitosa");
  } else {
    Serial.println("Falló la actualización de celdas");
  }
}

void buscarOffset(int servoChannel360, int pinOffset, int seccion) {
  encontradoOffset = false;
  pulsosOffset = 0;
  int currentState;

  Serial.print("Buscando offset en seccion ");
  Serial.println(seccion);

  pwm.setPWM(servoChannel360, 0, SERVO_SPIN);
  while (!encontradoOffset) {
    currentState = digitalRead(pinOffset);
    if (currentState == LOW && lastStateOffset == HIGH) {
      pulsosOffset++;
    }

    lastStateOffset = currentState;

    if (currentState == LOW) {
      encontradoOffset = true;
      pwm.setPWM(servoChannel360, 0, SERVO_ANTISPIN);
      delay(50);
      pwm.setPWM(servoChannel360, 0, SERVO_STOP);
      Serial.println("Offset encontrado!");
      delay(500);
    }
  }
}

void posicionarCelda(int numCell) {
  if (numCell >= 1 && numCell <= 14) {
    if (numCell <= 7) {
      Serial.println("\nProcesando Seccion 1");
      setBackground(2);
      procesarSeccion(1, numCell);
    } else {
      Serial.println("\nProcesando Seccion 2");
      procesarSeccion(2, numCell - 7);
    }
  }
}

void procesarSeccion(int seccion, int posicion) {
  setBackground(2);
  tft.setCursor(0, tft.height() / 2);
  tft.setTextSize(3);
  tft.setTextColor(TFT_WHITE);
  tft.print("Posicionando\ncelda...");

  int servoChannel360 = (seccion == 1) ? SERVO360_1_CHANNEL : SERVO360_2_CHANNEL;
  int servoChannel180 = (seccion == 1) ? SERVO180_1_CHANNEL : SERVO180_2_CHANNEL;
  int pinOffset = (seccion == 1) ? ENCODER1_OFFSET_INPUT : ENCODER2_OFFSET_INPUT;
  int pinPrincipal = (seccion == 1) ? ENCODER1_MAIN_INPUT : ENCODER2_MAIN_INPUT;

  pulsosPrincipales = 0;
  int currentState;

  buscarOffset(servoChannel360, pinOffset, seccion);

  if (posicion > 1) {
    int pulsosObjetivo = posicion - 1;
    Serial.print("Moviendo a posicion " + (String)posicion);

    pwm.setPWM(servoChannel360, 0, SERVO_SPIN);

    while (pulsosPrincipales < pulsosObjetivo) {
      currentState = digitalRead(pinPrincipal);

      if (currentState == LOW && lastStatePrincipal == HIGH) {
        pulsosPrincipales++;
      }

      lastStatePrincipal = currentState;
    }
    pwm.setPWM(servoChannel360, 0, SERVO_ANTISPIN);
    delay(50);
    pwm.setPWM(servoChannel360, 0, SERVO_STOP);
    Serial.println("Posicion alcanzada!");
  }
}

void dispensarSeccion(int servoChannel180) {
  setBackground(2);
  tft.setCursor(0, tft.height() / 2);
  tft.setTextSize(3);
  tft.setTextColor(TFT_WHITE);
  tft.print("Dispensando...");
  Serial.println("Activando servo de 180 grados");

  if (servoChannel180 == SERVO180_1_CHANNEL) {
    pwm.setPWM(servoChannel180, 0, SERVO_180);
    delay(2000);
    pwm.setPWM(servoChannel180, 0, SERVO_MIN);
    delay(2000);
  } else {
    pwm.setPWM(servoChannel180, 0, SERVO_MIN);
    delay(2000);
    pwm.setPWM(servoChannel180, 0, SERVO_180);
    delay(2000);
  }
  Serial.println("Servo de 180 grados completado");
}
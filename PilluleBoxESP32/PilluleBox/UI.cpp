#include "pillulebox.h"
#include <TFT_eSPI.h>

void menuUI() {
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
      tft.println("Dirección MAC: " + WiFi.macAddress());
      break;
  }
  delay(5000);
}

void logInUI() {

  setBackground(1);
  tft.setCursor(0, 20);
  tft.println("Username:");
  String username_email = esperarStringSerial();
  tft.setTextSize(3);
  tft.println(username_email);
  tft.setTextSize(4);
  tft.println("\nPassword:");
  String password = esperarStringSerial();
  tft.setTextSize(3);
  tft.println(password);
  String encryptedPassword = encryptPassword(password);
  tft.setTextSize(1);
  tft.println(encryptedPassword);
  tft.setTextSize(4);
  tft.setCursor(0, 300);
  tft.setTextColor(TFT_BLUE);
  if (logIn(username_email, encryptedPassword)) {
    tft.println("usuario autenticado :)");
  } else {
    tft.println("Error de autenticacion :(");
  }
  delay(20000);
}

void dispenserUI() {
}

int Lista::seleccionarLista() {
  int itemsToShow = 10;
  int startIndex = 0;
  tft.setTextSize(textSize);

  int itemSelected = 1;
  /*
  if (stringComplete) {
    Serial.println("Cadena recibida: " + inputString);
    inputString = "";
    stringComplete = false;
  }
  */
  do {
    tft.setCursor(0, tft.height() - 40);
    tft.setTextColor(TFT_RED, TFT_BLACK);
    tft.print(btnCurrentStatus[5]);
    if (btnCurrentStatus[5]) {
      return -1;
    }

    setBackground(1);
    tft.setCursor(0, 20);
    if (btnCurrentStatus[0]) itemSelected--;
    if (btnCurrentStatus[1]) itemSelected++;

    if (itemSelected < 1) itemSelected = length;
    else if (itemSelected > length) itemSelected = 1;

    if (itemSelected <= startIndex) startIndex = itemSelected - 1;
    if (itemSelected > startIndex + itemsToShow) startIndex = itemSelected - itemsToShow;

    startIndex = max(0, min(startIndex, length - itemsToShow));

    for (int i = 0; i < itemsToShow && (i + startIndex) < length; i++) {
      int currentIndex = i + startIndex;
      if ((currentIndex + 1) == itemSelected) {
        tft.setTextColor(textSelectedColor, textSelectedBackgroundColor);
      } else {
        tft.setTextColor(textColor);
      }

      tft.print(currentIndex + 1);
      Serial.print(currentIndex + 1);

      Serial.print(": ");
      tft.print(": ");

      Serial.println(list[currentIndex]);
      tft.println(list[currentIndex]);
    }

    if (startIndex > 0) {
      tft.setCursor(tft.width() - 20, 0);
      tft.print("^");
    }
    if (startIndex + itemsToShow < length) {
      tft.setCursor(tft.width() - 20, tft.height() - 20);
      tft.print("v");
    }
    resetBtns();
    while (!btnCurrentStatus[0] && !btnCurrentStatus[1] && !btnCurrentStatus[2] && !btnCurrentStatus[3] && !btnCurrentStatus[4] && !btnCurrentStatus[5]) {
      readBtns();
      delay(50);
    };
  } while (!btnCurrentStatus[4]);

  return itemSelected;
}

String waitEnterText(char Keys[][10], String str, int posX, int posY, int initialPosY) {
  char(*currentKeys)[10] = Keys;
  bool exitLoop = false;
  KeyboardType keyboardType = identifyArray(currentKeys);

  displayEditableText(str, initialPosY);

  while (!exitLoop && !textConfirmed) {
    displayCharSelectedKeyboard(currentKeys, posX, posY, initialPosY);
    delay(50);

    readBtns();
    if (btnCurrentStatus[0]) posY--;  // Up
    if (btnCurrentStatus[1]) posY++;  // Down
    if (btnCurrentStatus[2]) posX--;  // Left
    if (btnCurrentStatus[3]) posX++;  // Right
    if (btnCurrentStatus[5]) return "\0";

    checkPositionsKeyboard(currentKeys, &posX, &posY);

    if (btnCurrentStatus[4]) {  // Botón Backspace o selección
      switch (keyboardType) {
        case BK:  // Basic Keys
          if (posX == 0 && posY == 2) {
            posX = 0;
            posY = 0;
            currentKeys = capitalKeys;
            keyboardType = CK;
          } else if (posX == 0 && posY == 3) {
            posX = 0;
            posY = 0;
            currentKeys = numberSymbolKeys;
            keyboardType = NSK;
          } else if (posX == 8 && posY == 2) {
            str.remove(str.length() - 1);
            displayEditableText(str, initialPosY);
          } else if (posX == 3 && posY == 3) {
            textConfirmed = true;
            exitLoop = true;
          } else {
            str.concat(currentKeys[posY][posX]);
            displayEditableText(str, initialPosY);
          }
          break;

        case CK:  // Capital Keys
          if (posX == 0 && posY == 2) {
            posX = 0;
            posY = 0;
            currentKeys = basicKeys;
            keyboardType = BK;
          } else if (posX == 0 && posY == 3) {
            posX = 0;
            posY = 0;
            currentKeys = numberSymbolKeys;
            keyboardType = NSK;
          } else if (posX == 8 && posY == 2) {
            str.remove(str.length() - 1);
            displayEditableText(str, initialPosY);
          } else if (posX == 3 && posY == 3) {
            textConfirmed = true;
            exitLoop = true;
          } else {
            str.concat(currentKeys[posY][posX]);
            displayEditableText(str, initialPosY);
          }
          break;

        case NSK:  // Number/Symbol Keys
          if (posX == 0 && posY == 3) {
            posX = 0;
            posY = 0;
            currentKeys = basicKeys;
            keyboardType = BK;
          } else if (posX == 4 && posY == 2) {
            str.remove(str.length() - 1);
            displayEditableText(str, initialPosY);
          } else if (posX == 3 && posY == 3) {
            textConfirmed = true;
            exitLoop = true;
          } else {
            str.concat(currentKeys[posY][posX]);
            displayEditableText(str, initialPosY);
          }
          break;

        default:
          break;
      }
    }
  }
  textConfirmed = false;
  return str;
}

void displayEditableText(String str, int initialPosY) {
  tft.fillRect(4, 4 + initialPosY, tft.width() - 8, (34 * 4) + 2, TFT_DARKGREY);
  tft.fillRect(5, 5 + initialPosY, tft.width() - 10, 34 * 4, TFT_BLACK);
  sprite.setColorDepth(8);
  sprite.createSprite(tft.width() - 30, (34 * 4) - 20);
  sprite.fillSprite(TFT_BLACK);
  sprite.setCursor(0, 0);
  sprite.setTextColor(TFT_WHITE);
  sprite.setTextSize(2.5);
  sprite.print(str);
  sprite.fillRect(sprite.getCursorX(), sprite.getCursorY() - 4, 8, 20, TFT_WHITE);  //Select final position from text
  sprite.pushSprite(15, 15 + initialPosY);
  sprite.deleteSprite();
}

void displayCharSelectedKeyboard(char Keys[][10], int positionX, int positionY, int initialPosY) {
  int keyHeight = 42;
  sprite.setColorDepth(8);
  sprite.createSprite(tft.width(), (keyHeight * 4));
  sprite.fillSprite(TFT_WHITE);

  TFT_eSprite keySprite = TFT_eSprite(&tft);
  keySprite.setColorDepth(8);

  int currentPositionY = 0;
  for (int i = 0; i < 4; i++) {
    int currentPositionX = 0;
    int keysInRow = countKeysSpacesInRow(Keys, i);
    int keyWidth = sprite.width() / keysInRow;
    for (int j = 0; j < keysInRow; j++) {
      int currentKeyWidth = keyWidth;
      keySprite.createSprite(keyWidth, keyHeight);
      if (Keys[i][j] == ' ') {
        currentKeyWidth *= 5;
        keySprite.deleteSprite();
        keySprite.createSprite(currentKeyWidth, keyHeight);
      }
      keySprite.fillSprite(TFT_WHITE);
      if ((i == positionY) && (j == positionX)) {
        keySprite.setTextColor(TFT_BLACK);
      } else {
        keySprite.fillRect(1, 1, currentKeyWidth - 2, keyHeight - 2, TFT_BLACK);
        keySprite.setTextColor(TFT_WHITE);
      }
      keySprite.setTextSize(4);
      int textPositionWidth = (keySprite.width() / 2) - (keySprite.textWidth(String(Keys[i][j])) / 2);
      int textPositionHeight = (keySprite.height() / 2) - (keySprite.fontHeight() / 2);
      keySprite.setCursor(textPositionWidth, textPositionHeight);
      keySprite.print(Keys[i][j]);
      keySprite.pushToSprite(&sprite, currentPositionX, currentPositionY);
      keySprite.deleteSprite();
      currentPositionX += currentKeyWidth;
    }
    currentPositionY += keyHeight;
  }
  sprite.pushSprite(0, 200 + initialPosY);
  sprite.deleteSprite();
}
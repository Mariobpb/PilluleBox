#include "pillulebox.h"
#include <TFT_eSPI.h>

void menuUI() {
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

void Lista::dibujarLista() {
  sprite.createSprite(tft.width(), spriteHeight);
  sprite.fillSprite(TFT_BLACK);
  sprite.setCursor(0, 0);
  sprite.setTextSize(textSize);

  for (int i = 0; i < itemsToShow && (i + startIndex) < length; i++) {
    int currentIndex = i + startIndex;
    if ((currentIndex + 1) == itemSelected) {
      sprite.setTextColor(textSelectedColor, textSelectedBackgroundColor);
    } else {
      sprite.setTextColor(textColor);
    }

    sprite.print(currentIndex + 1);
    sprite.print(": ");
    sprite.println(list[currentIndex]);
  }

  if (startIndex > 0) {
    sprite.setCursor(sprite.width() - 20, 0);
    sprite.print("^");
  }
  if (startIndex + itemsToShow < length) {
    sprite.setCursor(sprite.width() - 20, sprite.height() - 20);
    sprite.print("v");
  }
  // Mostrar el sprite en la pantalla
  sprite.pushSprite(0, spritePosY);
  sprite.deleteSprite();
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
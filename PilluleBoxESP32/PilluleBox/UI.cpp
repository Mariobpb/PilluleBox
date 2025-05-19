#include "pillulebox.h"
#include <TFT_eSPI.h>

void menuUI() {
}

void logInUI() {
  setBackground(1);
  bool accountConfirmed = false;

  String username_email = "";
  String password = "";
  int selectedField = 0;  // 0: username, 1: password, 2: confirm button

  while (!accountConfirmed) {
    tft.setTextColor(TFT_WHITE);
    tft.setTextSize(2);
    tft.setCursor(10, 20);
    tft.println("Username:");
    tft.setCursor(10, 130);
    tft.println("Password:");

    sprite.createSprite(tft.width() - 20, 40);

    // Username
    sprite.fillSprite(selectedField == 0 ? TFT_BLUE : TFT_BLACK);
    sprite.setTextColor(TFT_WHITE);
    sprite.setTextSize(2);
    sprite.setCursor(5, 10);
    sprite.print(username_email);
    sprite.pushSprite(10, 45);

    // Password
    sprite.fillSprite(selectedField == 1 ? TFT_BLUE : TFT_BLACK);
    sprite.setTextColor(TFT_WHITE);
    sprite.setTextSize(2);
    sprite.setCursor(5, 10);
    String maskedPassword = "";
    for (int i = 0; i < password.length(); i++) {
      maskedPassword += "*";
    }
    sprite.print(maskedPassword);
    sprite.pushSprite(10, 155);

    // Confirmation
    sprite.fillSprite(selectedField == 2 ? TFT_BLUE : TFT_GREEN);
    sprite.setTextColor(selectedField == 2 ? TFT_WHITE : TFT_BLACK);
    sprite.setTextSize(2);
    sprite.setCursor(30, 12);
    sprite.print("Confirmar");
    sprite.pushSprite(10, 300);

    sprite.deleteSprite();

    readBtns();

    if (btnCurrentStatus[0]) {  // Up
      selectedField = (selectedField - 1 + 3) % 3;
    }
    if (btnCurrentStatus[1]) {  // Down
      selectedField = (selectedField + 1) % 3;
    }

    if (btnCurrentStatus[4]) {  // Enter

      switch (selectedField) {
        case 0:
          username_email = waitEnterText(basicKeys, username_email, 0, 0, 65);
          break;
        case 1:
          password = waitEnterText(basicKeys, password, 0, 0, 65);
          break;
        case 2:
          if (username_email.length() > 0 && password.length() > 0) {
            String encryptedPassword = encryptPassword(password);
            tft.setTextSize(3);
            tft.setTextColor(TFT_WHITE);
            tft.setCursor(10, tft.height() - (tft.fontHeight() * 2));
            tft.print("Autenticando...");
            if (logIn(username_email, encryptedPassword)) {
              accountConfirmed = true;
              setBackground(2);
              tft.setCursor(10, 120);
              tft.setTextColor(TFT_GREEN);
              tft.setTextSize(2);
              tft.println("Usuario\nautenticado :)");
            } else {
              setBackground(2);
              tft.setCursor(10, 120);
              tft.setTextColor(TFT_RED);
              tft.setTextSize(3);
              tft.println("Error de\nautenticacion :(");
              delay(2000);
              setBackground(1);
            }
          } else {
            tft.fillScreen(TFT_BLACK);
            tft.setCursor(10, 120);
            tft.setTextColor(TFT_YELLOW);
            tft.setTextSize(3);
            tft.println("Favor de completar\nambos campos");
            delay(2000);
            setBackground(1);
          }
          break;
      }
    }

    if (btnCurrentStatus[5]) {  // Back
      btnCurrentStatus[5] = !btnCurrentStatus[5];
      return;
    }

    delay(50);
  }

  delay(2000);
}

void dispenserUI() {
}

void Lista::drawList() {
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
    if (btnCurrentStatus[5]) {
      btnCurrentStatus[5] = !btnCurrentStatus[5];
      setBackground(1);
      return "\0";
    }

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
  setBackground(1);
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

void displayCellsList() {
  String cellsList[] = {
    "Celda 1",
    "Celda 2",
    "Celda 3",
    "Celda 4",
    "Celda 5",
    "Celda 6",
    "Celda 7",
    "Celda 8",
    "Celda 9",
    "Celda 10",
    "Celda 11",
    "Celda 12",
    "Celda 13",
    "Celda 14",
  };
  Lista OptionsCells(cellsList, sizeof(cellsList) / sizeof(cellsList[0]));

  setBackground(1);
  tft.setTextColor(TFT_WHITE);
  tft.setCursor(0, 20);
  tft.setTextColor(TFT_BLUE);
  OptionsCells.setTextSize(3);
  OptionsCells.setPositionY(tft.getCursorY());
  OptionsCells.setHeight(300);
  int seleccion = OptionsCells.selectItemFromList();
}

void displayCellSelected(Cell cells[], int columnSelected, int rowSelected) {
  int cellSizeX = 60;
  int cellSizeY = 35;

  sprite.createSprite(tft.width() - 40, (cellSizeY * 7) + 40);
  sprite.setColorDepth(8);
  sprite.fillSprite(TFT_BLACK);

  int PosX = (sprite.width() / 2) - (cellSizeX + 10);
  int PosY = 5;
  for (int column = 0; column <= 1; column++) {
    for (int row = 0; row <= 6; row++) {
      if (column == columnSelected && row == rowSelected) {
        sprite.fillRect(PosX, PosY, cellSizeX, cellSizeY, TFT_BLUE);
      } else {
        sprite.fillRect(PosX, PosY, cellSizeX, cellSizeY, TFT_WHITE);
      }
      PosY += cellSizeY + 5;
    }
    PosX += cellSizeX + 20;
    PosY = 5;
  }
  sprite.pushSprite(20, 40);
  sprite.deleteSprite();
}

void showBackgroundInfo() {
  DateTime now = rtc.now();
  char dateStr[20];
  sprintf(dateStr, "%02d/%02d/%04d", now.day(), now.month(), now.year());
  char timeStr[20];
  sprintf(timeStr, "%02d:%02d", now.hour(), now.minute());
  char dateTimeStr[50];
  snprintf(dateTimeStr, sizeof(dateTimeStr), "%s %s\n", dateStr, timeStr);
  
  setBackground(1);
  tft.setCursor(0, 20);
  tft.setTextColor(TFT_WHITE);
  tft.println(dateTimeStr);
  if (username != "") {
    tft.setTextColor(TFT_BLUE);
    tft.println("Bienvenido " + username + "\n");
  }
  if (WiFi.status() == WL_CONNECTED) {
    tft.setTextColor(TFT_WHITE);
    tft.println("WiFi:\n" + WiFi.SSID() + "\n");
  }
}
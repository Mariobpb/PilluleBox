#include <TFT_eSPI.h>

const int btnPins[6] = { 4, 5, 6, 7, 15, 16 };  //Up, Down, Left, Right, Enter, Back
bool btnStatus[6] = { false, false, false, false, false, false };


char basicKeys[4][10] = {
  { 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p' },
  { 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', '\t' },
  { '\0', 'z', 'x', 'c', 'v', 'b', 'n', 'm', '\0', '\t' },
  { '\0', ' ', '.', '\0', '\t', '\t', '\t', '\t', '\t', '\t' }
};

char capitalKeys[4][10] = {
  { 'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P' },
  { 'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', '\t' },
  { '\0', 'Z', 'X', 'C', 'V', 'B', 'N', 'M', '\0', '\t' },
  { '\0', ' ', '.', '\0', '\t', '\t', '\t', '\t', '\t', '\t' }
};

char numberSymbolKeys[4][10] = {
  { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' },
  { '@', '#', '$', '_', '&', '+', '*', '\t', '\t', '\t' },
  { '\0', '!', '=', ':', '\0', '\t', '\t', '\t', '\t', '\t' },
  { '\0', ' ', '.', '\0', '\t', '\t', '\t', '\t', '\t', '\t' }
};

enum KeyboardType { BK,
                    CK,
                    NSK,
                    Unknown };

TFT_eSPI tft = TFT_eSPI();
TFT_eSprite sprite = TFT_eSprite(&tft);

void setup() {
  Serial.begin(115200);
  initPins();
  tft.init();
  tft.setRotation(0);
  setBackground();
  displayEnterText(basicKeys, "& ! + : # * _ $ = @ . oiwhfuHLALFWCHUwloh123456789012345678901234567890123457890123456789012345678901234567890123456789009876543211234567890987654123456789012345678900000000000", 0, 0);
}

void loop() {
  setBackground();
  delay(1000);
}

void initPins() {
  for (int i = 0; i < 6; i++) {
    pinMode(btnPins[i], INPUT_PULLDOWN);
  }
}

uint16_t convertRGBtoRGB565(uint8_t r, uint8_t g, uint8_t b) {
  return ((r & 0xF8) << 8) | ((g & 0xFC) << 3) | (b >> 3);
}

void setBackground() {
  //tft.fillRectVGradient(0, 0, tft.width(), tft.height(), convertRGBtoRGB565(130, 0, 0), TFT_BLACK);
  tft.fillScreen(TFT_BLACK);
}

void resetBtns() {
  for (int i = 0; i < 6; i++) {
    btnStatus[i] = false;
  }
}

void readBtns() {
  for (int i = 0; i < 6; i++) {
    btnStatus[i] = digitalRead(btnPins[i]);
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

void displayEnterText(char Keys[][10], String str, int posX, int posY) {
  KeyboardType keyboardType = identifyArray(Keys);

  displayEditableText(str);
  displayCharSelectedKeyboard(Keys, posX, posY);
  bool textConfirmed = false;
  while (!btnStatus[4] && !textConfirmed) {
    readBtns();
    if (btnStatus[0]) posY--;
    if (btnStatus[1]) posY++;
    if (btnStatus[2]) posX--;
    if (btnStatus[3]) posX++;
    checkPositionsKeyboard(Keys, &posX, &posY);
    if (btnStatus[5]) {
      switch (keyboardType) {
        case BK:
          Serial.println("Using basicKeys");
          break;
        case CK:
          Serial.println("Using CapitalKeys");
          break;
        case NSK:
          Serial.println("Using NumberSymbolKeys");
          break;
        default:
          Serial.println("Unknown array");
          break;
      }
      textConfirmed = true;
    }
    displayCharSelectedKeyboard(Keys, posX, posY);
    delay(300);
  }
}

void displayEditableText(String str) {
  tft.fillRect(4, 4, tft.width() - 8, (34 * 4) + 2, TFT_DARKGREY);
  tft.fillRect(5, 5, tft.width() - 10, 34 * 4, TFT_BLACK);
  sprite.setColorDepth(8);
  sprite.createSprite(tft.width() - 30, (34 * 4) - 20);
  sprite.fillSprite(TFT_BLACK);
  sprite.setCursor(0, 0);
  sprite.setTextColor(TFT_WHITE);
  sprite.setTextSize(2.5);
  sprite.print(str);
  sprite.fillRect(sprite.getCursorX(), sprite.getCursorY() - 4, 8, 20, TFT_WHITE);  //Select final position from text
  sprite.pushSprite(15, 15);
  sprite.deleteSprite();
}

void checkPositionsKeyboard(char Keys[][10], int *posX, int *posY) {
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

void displayCharSelectedKeyboard(char Keys[][10], int positionX, int positionY) {
  int keyHeight = 42;
  sprite.setColorDepth(8);
  sprite.createSprite(tft.width(), (keyHeight * 4));
  sprite.fillSprite(TFT_WHITE);

  TFT_eSprite keySprite = TFT_eSprite(&tft);
  keySprite.setColorDepth(8);

  // Line 1
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
  sprite.pushSprite(0, 200);
  sprite.deleteSprite();
}
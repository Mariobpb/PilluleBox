#include <TFT_eSPI.h>

const int btnPins[6] = { 4, 5, 6, 7, 15, 16 };
bool btnStatus[6];
bool btnPrevStatus[6] = { false, false, false, false, false, false };


char BasicKeys[4][10] = {
  { 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p' },
  { 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', '\t' },
  { ' ', 'z', 'x', 'c', 'v', 'b', 'n', 'm', ' ', '\t' },
  { '\0', ' ', '.', '\0', '\t', '\t', '\t', '\t', '\t', '\t' }
};

char keys[] = {
  'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p',
  'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l',
  ' ', 'z', 'x', 'c', 'v', 'b', 'n', 'm', ' ',
  ' ', ' ', '.', ' '
};

TFT_eSPI tft = TFT_eSPI();
TFT_eSprite sprite = TFT_eSprite(&tft);

void setup() {
  initPins();
  tft.init();
  tft.setRotation(0);
  setBackground();
  displayEnterText("& ! + : # * _ $ = @ . oiwhfuHLALFWCHUwloh123456789012345678901234567890123457890123456789012345678901234567890123456789009876543211234567890987654123456789012345678900000000000");
}

void loop() {
  delay(1000);
}

void initPins() {
  for (int i = 0; i < 6; i++) {
    pinMode(btnPins[i], INPUT_PULLDOWN);
    attachInterrupt(digitalPinToInterrupt(btnPins[i]), readBtns, CHANGE);
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

bool checkBtnsStatus() {
  for (int i = 0; i < 6; i++) {
    if (btnStatus[i]) return true;
  }
  return false;
}

void displayEnterText(String str) {
  tft.fillRect(4, 4, tft.width() - 8, (34 * 4) + 2, TFT_DARKGREY);
  tft.fillRect(5, 5, tft.width() - 10, 34 * 4, TFT_BLACK);
  sprite.setColorDepth(1);
  sprite.createSprite(tft.width() - 30, (34 * 4) - 20);
  sprite.fillSprite(TFT_BLACK);
  sprite.setCursor(0, 0);
  sprite.setTextColor(TFT_WHITE);
  sprite.setTextSize(2.5);
  sprite.print(str);
  sprite.fillRect(sprite.getCursorX(), sprite.getCursorY() - 4, 8, 20, TFT_WHITE);  //Select final position from text
  sprite.pushSprite(15, 15);
  sprite.deleteSprite();

  keyboardCharSelected(BasicKeys, 0, 0);
}
char keyboardCharSelected(char Keys[][10], int positionX, int positionY) {
  int keyHeight = 42;
  sprite.setColorDepth(1);
  sprite.createSprite(tft.width(), (keyHeight * 4));
  sprite.fillSprite(TFT_WHITE);
  sprite.setTextColor(TFT_DARKGREY);
  sprite.setTextSize(5);

  TFT_eSprite keySprite = TFT_eSprite(&tft);
  keySprite.setColorDepth(1);

  // Line 1
  int currentPositionY = 0;
  for (int i = 0; i < 4; i++) {
    int columns = 0;
    while (Keys[i][columns] != '\t') { columns++ }
    int keyWidth = sprite.width() / columns;
    keySprite.createSprite(keyWidth, keyHeight);
    keySprite.fillSprite(TFT_WHITE);
    keySprite.setTextColor(TFT_DARKGREY);
    keySprite.setTextSize(4);
    keySprite.fillRect(1, 1, keyWidth-2, keyHeight-2, TFT_BLACK);
    spritePrincipal.drawSprite(spriteSecundario, 0, 0);
    
    sprite.setPivot(0, 0);
    keySprite.setPivot(0, 0);
  }
  for (int i = 0; i < 10; i++) {
    sprite.setCursor((keyWidth * i) + 1, currentPositionY + 1);
    sprite.fillRect(sprite.getCursorX(), sprite.getCursorY(), keyWidth - 2, keyHeight - 2, TFT_BLACK);  //Select final position from text
    sprite.print(String(keys[i]));
  }
  // Line 2 & 3
  currentPositionY += keyHeight;
  keyWidth = sprite.width() / 9;
  for (int i = 0; i < 9; i++) {
    sprite.setCursor((keyWidth * i) + 1, currentPositionY + 1);
    sprite.fillRect(sprite.getCursorX(), sprite.getCursorY(), keyWidth - 2, keyHeight - 2, TFT_BLACK);  //Select final position from text
    sprite.print(String(keys[i + 10]));
    sprite.setCursor((keyWidth * i) + 1, currentPositionY + keyHeight + 1);
    sprite.fillRect(sprite.getCursorX(), sprite.getCursorY(), keyWidth - 2, keyHeight - 2, TFT_BLACK);  //Select final position from text
    sprite.print(String(keys[i + 19]));
  }
  // Line 4
  currentPositionY += keyHeight * 2;
  int currentPositionX = 0;
  for (int i = 0; i < 4; i++) {
    sprite.setCursor((currentPositionX) + 1, currentPositionY + 1);
    if (i == 1) {
      int spaceKeyWidth = keyWidth * 6;
      sprite.fillRect(sprite.getCursorX(), sprite.getCursorY(), spaceKeyWidth - 2, keyHeight - 2, TFT_BLACK);  //Select final position from text
      sprite.print(String(keys[i + 28]));
      currentPositionX += spaceKeyWidth;
    } else {
      sprite.fillRect(sprite.getCursorX(), sprite.getCursorY(), keyWidth - 2, keyHeight - 2, TFT_BLACK);  //Select final position from text
      sprite.print(String(keys[i + 28]));
      currentPositionX += keyWidth;
    }
  }
  sprite.pushSprite(0, 200);
  sprite.deleteSprite();
}
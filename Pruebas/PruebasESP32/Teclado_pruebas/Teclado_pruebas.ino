#include <TFT_eSPI.h>

const int btnPins[6] = { 4, 5, 6, 7, 15, 16 };
bool btnStatus[6];
bool btnPrevStatus[6] = { false, false, false, false, false, false };

char teclasBasicas[] = {
  'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p',
  'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'ñ',
  'z', 'x', 'c', 'v', 'b', 'n', 'm'
};

TFT_eSPI tft = TFT_eSPI();

void setup() {
  initPins();
  tft.init();
  tft.setRotation(0);
  setBackground();
}

void loop() {
  displayKeyboard("Hola");
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
  tft.fillRectVGradient(0, 0, tft.width(), tft.height(), convertRGBtoRGB565(130, 0, 0), TFT_BLACK);
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

void displayKeyboard(String str) {
  tft.fillRect(5, 5, tft.width() - 10, 34, TFT_BLACK);
  tft.setCursor(15, 15);
  tft.setTextColor(TFT_WHITE);
  tft.setTextSize(2.5);
  tft.print(str);
  tft.fillRect(tft.getCursorX(), 9, 5, 26, TFT_WHITE);
  
}
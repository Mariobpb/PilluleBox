#include <TFT_eSPI.h>

TFT_eSPI tft = TFT_eSPI();
TFT_eSprite sprite = TFT_eSprite(&tft);

void setup() {
  tft.init();
  tft.setRotation(0);
}

void loop() {
  char c = 'w';
  tft.fillScreen(TFT_WHITE);
  sprite.createSprite(tft.width() / 1, tft.height() / 2);
  sprite.setCursor(0, 0);
  sprite.setTextColor(TFT_BLUE, TFT_RED);
  sprite.setTextSize(3);
  int CursorX = (sprite.width() / 2) - (sprite.textWidth(&c, 1) / 2);
  int CursorY = (sprite.height() / 2) - (sprite.fontHeight() / 2);
  sprite.setCursor(CursorX, CursorY);
  sprite.print((String)c);
  sprite.pushSprite(0, 0);
  sprite.deleteSprite();
  delay(1000);
}

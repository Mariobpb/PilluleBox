#include <TFT_eSPI.h>
#include <WiFi.h>

TFT_eSPI tft = TFT_eSPI();

void setup() {
  tft.init();
}

void loop() {
  tft.setRotation(0);  // Ajusta la rotación si es necesario (0-3)
  tft.fillScreen(TFT_RED);
  tft.setTextColor(TFT_WHITE, TFT_BLACK);
  tft.setTextSize(4);
  tft.setCursor(0, 0);
  tft.println("Networks found:");
  tft.println("Direccion MAC: " + WiFi.macAddress());

  delay(10000);

  tft.setRotation(0);  // Ajusta la rotación si es necesario (0-3)
  tft.fillScreen(TFT_RED);
  tft.setTextColor(TFT_WHITE, TFT_BLACK);
  tft.setTextSize(4);
  tft.drawString("Hola, Mundo!", 30, 0);

  delay(1000);

  tft.setRotation(0);  // Ajusta la rotación si es necesario (0-3)
  tft.fillScreen(TFT_RED);
  tft.setTextColor(TFT_WHITE, TFT_BLACK);
  tft.setTextSize(4);
  tft.drawString("Hola, Mundo!", 0, 30);

  delay(1000);

  tft.setRotation(0);  // Ajusta la rotación si es necesario (0-3)
  tft.fillScreen(TFT_RED);
  tft.setTextColor(TFT_WHITE, TFT_BLACK);
  tft.setTextSize(4);
  tft.drawString("Hola, Mundo!", 30, 30);

  delay(1000);
}
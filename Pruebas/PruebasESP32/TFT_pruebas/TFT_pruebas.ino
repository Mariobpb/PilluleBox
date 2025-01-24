#include <TFT_eSPI.h>
TFT_eSPI tft = TFT_eSPI();

void setup() {
  Serial.begin(115200);
  Serial.println("Iniciando prueba de display");
  
  tft.init();
  tft.setRotation(0);
  Serial.println("Pantalla inicializada");
  
  // Test de colores básicos
  tft.fillScreen(TFT_BLACK);
  delay(500);
  Serial.println("Negro OK");
  
  tft.fillScreen(TFT_RED);
  delay(500);
  Serial.println("Rojo OK");
  
  tft.fillScreen(TFT_GREEN);
  delay(500);
  Serial.println("Verde OK");
  
  tft.fillScreen(TFT_BLUE);
  delay(500);
  Serial.println("Azul OK");
}

void loop() {
  // Patrón de diagnóstico
  tft.fillScreen(TFT_BLACK);
  tft.drawRect(0, 0, tft.width()-1, tft.height()-1, TFT_WHITE);
  tft.drawLine(0, 0, tft.width()-1, tft.height()-1, TFT_RED);
  delay(2000);
}
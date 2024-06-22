#include <TFT_eSPI.h>

TFT_eSPI tft = TFT_eSPI();

void setup() {
  Serial.begin(115200);
  delay(1000); // Espera para que el monitor serial se inicialice
  Serial.println("Iniciando...");
  
  // Comenta o elimina todo el código relacionado con la pantalla TFT
  delay(1000);
  tft.init();
  /*
  tft.setRotation(0);
  tft.fillScreen(TFT_BLACK);
  tft.setCursor(0, 0);
  tft.setTextColor(TFT_WHITE);
  tft.setTextSize(2);
  tft.println("Hola Mundo!");
  */
  
  Serial.println("Setup completado");
}

void loop() {
  Serial.println("Loop ejecutándose");
  delay(1000);
}
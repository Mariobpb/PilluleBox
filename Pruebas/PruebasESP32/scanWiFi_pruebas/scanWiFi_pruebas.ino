#include <TFT_eSPI.h>
#include <WiFi.h>

TFT_eSPI tft = TFT_eSPI();

void setup() {
  Serial.begin(115200);
  tft.init();
  tft.setRotation(0);
  tft.fillScreen(TFT_BLACK);
  tft.setTextColor(TFT_WHITE, TFT_BLACK);
  tft.setTextSize(2);
}

void loop() {
  escanearRedesWiFi();
  delay(10000); // Espera 10 segundos antes de escanear nuevamente
}

void escanearRedesWiFi() {
  tft.fillScreen(TFT_BLACK);
  tft.setCursor(0, 0);
  tft.println("Escaneando redes WiFi...");

  WiFi.scanNetworks(true); // Iniciar el escaneo de redes WiFi

  int intentos = 0;
  while (WiFi.scanComplete() == -1) {
    delay(100);
    intentos++;
    if (intentos > 100) { // Tiempo máximo de espera: 10 segundos
      tft.println("Error al escanear redes");
      return;
    }
    tft.print(".");
  }

  int numRedes = WiFi.scanComplete();

  if (numRedes == -2) {
    tft.println("Error al escanear redes");
    return;
  }

  if (numRedes == 0) {
    tft.println("No se encontraron redes disponibles");
    return;
  }

  tft.println("Redes disponibles:");
  for (int i = 0; i < numRedes; i++) {
    tft.print(i + 1);
    tft.print(": ");
    tft.println(WiFi.SSID(i));
  }
}
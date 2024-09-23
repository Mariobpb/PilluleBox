#include <TFT_eSPI.h>

TFT_eSPI tft = TFT_eSPI();  // Inicializa la pantalla TFT
const int btnPins[6] = { 4, 5, 6, 7, 15, 16 };  // Pines de los botones
int btnState[6];  // Almacena el estado actual de los botones
int lastBtnState[6];  // Almacena el estado anterior de los botones

void setup() {
  tft.init();
  tft.setRotation(1);  // Ajusta la orientación de la pantalla si es necesario
  tft.fillScreen(TFT_BLACK);  // Limpia la pantalla
  tft.setTextColor(TFT_WHITE, TFT_BLACK);  // Color del texto
  
  // Configura los pines de los botones como entrada
  for (int i = 0; i < 6; i++) {
    pinMode(btnPins[i], INPUT);
    lastBtnState[i] = digitalRead(btnPins[i]);  // Inicializa el estado anterior de los botones
  }
  
  // Dibuja el estado inicial
  mostrarEstadoBotones();
}

void loop() {
  bool botonCambiado = false;
  
  // Lee el estado de los botones
  for (int i = 0; i < 6; i++) {
    btnState[i] = digitalRead(btnPins[i]);
    
    // Verifica si el estado del botón ha cambiado
    if (btnState[i] != lastBtnState[i]) {
      botonCambiado = true;  // Se detectó un cambio
      lastBtnState[i] = btnState[i];  // Actualiza el estado anterior
    }
  }

  // Si ha habido un cambio en algún botón, actualiza la pantalla
  if (botonCambiado) {
    mostrarEstadoBotones();
  }

  delay(100);  // Pequeña pausa para evitar lecturas rápidas
}

// Función para mostrar los estados de los botones en la pantalla
void mostrarEstadoBotones() {
  tft.fillScreen(TFT_BLACK);  // Limpia la pantalla
  
  for (int i = 0; i < 6; i++) {
    tft.setCursor(10, 30 + i * 20);  // Posiciona el texto en la pantalla
    tft.printf("Boton %d: %s", i + 1, btnState[i] == HIGH ? "PRESIONADO" : "LIBRE");
  }
}

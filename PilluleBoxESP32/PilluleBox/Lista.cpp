#include "pillulebox.h"

#include <TFT_eSPI.h>

Lista::Lista(String list[], int length) {
  this->length = length;
  this->list = new String[length];
  for (int i = 0; i < length; i++) {
    this->list[i] = list[i];
  }
}

Lista::~Lista() {
  delete[] list;
}

void Lista::setTextSize(int size){
  this->textSize = size;
}

int Lista::SeleccionarLista() {
  int itemsToShow = 10; // Número de elementos a mostrar en la pantalla a la vez
  int startIndex = 0; // Índice del primer elemento mostrado
  tft.setTextSize(textSize);

  int itemSelected = 1;
  int option = -1;
  reiniciarBuffer();

  while (option != 3) {
    tft.fillScreen(backgroundColor);
    tft.setCursor(0, 20);
    if (option == 1) itemSelected--;
    if (option == 2) itemSelected++;

    if (itemSelected < 1) itemSelected = length;
    else if (itemSelected > length) itemSelected = 1;

    // Ajustar startIndex si el elemento seleccionado está fuera de la vista
    if (itemSelected <= startIndex) startIndex = itemSelected - 1;
    if (itemSelected > startIndex + itemsToShow) startIndex = itemSelected - itemsToShow;

    // Asegurarse de que startIndex no sea negativo
    startIndex = max(0, min(startIndex, length - itemsToShow));

    for (int i = 0; i < itemsToShow && (i + startIndex) < length; i++) {
      int currentIndex = i + startIndex;
      if ((currentIndex + 1) == itemSelected) {
        tft.setTextColor(textSelectedColor, textSelectedBackgroundColor);
      } else {
        tft.setTextColor(textColor, textBackgroundColor);
      }

      tft.print(currentIndex + 1);
      Serial.print(currentIndex + 1);

      Serial.print(": ");
      tft.print(": ");

      Serial.println(list[currentIndex]);
      tft.println(list[currentIndex]);
    }

    // Indicadores de desplazamiento
    if (startIndex > 0) {
      tft.setCursor(tft.width() - 20, 0);
      tft.print("^");
    }
    if (startIndex + itemsToShow < length) {
      tft.setCursor(tft.width() - 20, tft.height() - 20);
      tft.print("v");
    }

    reiniciarBuffer();
    while (!respuestaCompleta()) {
    }
    option = atoi(buffer);
  }

  return itemSelected;
}
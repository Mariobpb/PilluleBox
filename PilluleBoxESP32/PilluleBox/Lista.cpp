#include "pillulebox.h"

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

void Lista::setTextSize(int size) {
  this->textSize = size;
}

void Lista::setHeight(int spriteHeight) {
  this->spriteHeight = spriteHeight;
}

void Lista::setPositionY(int spritePosY) {
  this->spritePosY = spritePosY;
}
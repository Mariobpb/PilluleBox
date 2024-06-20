// pillulebox.h
#ifndef pillulebox
#define pillulebox

#include <TFT_eSPI.h>

// Declaraciones de funciones
void conectar(const char* ssid, const char* password);
void reconectar();
String esperarBuffer();
void leerCadenaDesdeEEPROM(int direccion, char* cadena, int longitud);
bool memoriaVacia(int direccion, int longitud);
int SeleccionarRed();
void reiniciarBuffer();
bool respuestaCompleta();
bool stringToBool(String value);
void escribirCadenaEnEEPROM(int direccion, const char* cadena, int longitud);

// Estructura de datos
class Lista {
private:
  int textSize = 1;
  uint16_t backgroundColor = TFT_BLACK;
  uint16_t textColor = TFT_WHITE;
  uint16_t textSelectedColor = TFT_BLACK;
  uint16_t textBackgroundColor = TFT_BLACK;
  uint16_t textSelectedBackgroundColor = TFT_BLUE;

public:
  Lista(String list[], int length) {
    this->list = list;
    this->length = length;
  }

  int SeleccionarLista() {
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

      for (int i = 0; i < length; i++) {
        if ((i + 1) == itemSelected) {
          tft.setTextColor(textSelectedColor, textSelectedBackgroundColor);
        } else {
          tft.setTextColor(textColor, textBackgroundColor);
        }

        tft.print(i + 1);
        Serial.print(i + 1);

        Serial.print(": ");
        tft.print(": ");

        Serial.println(list[i]);
        tft.println(list[i]);
      }
      reiniciarBuffer();
      while (!respuestaCompleta()) {
      }
      option = atoi(buffer);
    }

    return itemSelected;
  }
}

// Variables globales
extern TFT_eSPI tft;
extern int Index;
extern const char* apiUrl;
extern const int dirPASSWORD;
extern const int dirSSID;
const int bufferSize = 64;

extern char buffer[bufferSize];
extern char ssidBuffer[bufferSize];      // Asignar memoria para ssidBuffer
extern char passwordBuffer[bufferSize];  // Asignar memoria para passwordBuffer


#endif
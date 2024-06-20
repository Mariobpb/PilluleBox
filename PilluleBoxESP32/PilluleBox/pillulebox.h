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

class Lista {
private:
  int textSize = 3;
  uint16_t backgroundColor = TFT_BLACK;
  uint16_t textColor = TFT_WHITE;
  uint16_t textSelectedColor = TFT_BLACK;
  uint16_t textBackgroundColor = TFT_BLACK;
  uint16_t textSelectedBackgroundColor = TFT_BLUE;
  String* list;
  int length;

public:
  Lista(String list[], int length);
  ~Lista();
  int SeleccionarLista();
  void setTextSize(int size);
};


#endif
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
bool RedesEscaneadas();
void reiniciarBuffer();
bool respuestaCompleta();
bool stringToBool(String value);
void escribirCadenaEnEEPROM(int direccion, const char* cadena, int longitud);

// Estructura de datos

// Variables globales
extern TFT_eSPI tft;
extern int Index;
extern const char* apiUrl;
extern const int dirPASSWORD;
extern const int dirSSID;
const int bufferSize = 16;

extern char buffer[bufferSize];
extern char ssidBuffer[bufferSize];      // Asignar memoria para ssidBuffer
extern char passwordBuffer[bufferSize];  // Asignar memoria para passwordBuffer


#endif
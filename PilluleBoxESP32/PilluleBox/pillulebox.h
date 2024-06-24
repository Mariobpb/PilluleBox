// pillulebox.h
#ifndef pillulebox
#define pillulebox

#include <TFT_eSPI.h>
#include <WiFi.h>
#include <AESLib.h>
#include <Arduino.h>
#include "Base64.h"


// Funciones
void conectar(String ssid, String password);
void reconectar();
String esperarBuffer();
void leerCadenaDesdeEEPROM(int direccion, char* cadena, int longitud);
bool memoriaVacia(int direccion, int longitud);
bool stringToBool(String value);
void escribirCadenaEnEEPROM(int direccion, const char* cadena, int longitud);
int seleccionarRed();
uint16_t convertRGBtoRGB565(uint8_t r, uint8_t g, uint8_t b);
String generateSecretKey();
void serialEvent();
String esperarStringSerial();
String encryptPassword(String password);
String base64_encode(uint8_t *data, size_t length);

// UI
void setBackground(int b);
void menuUI();
void logInUI();
void dispenserUI();

//Solicitudes
bool logIn(String username_email, String password);

// Variables globales
extern TFT_eSPI tft;
extern String inputString;
extern boolean stringComplete;
extern int Index;
extern const char* apiUrl;
extern const int dirPASSWORD;
extern const int dirSSID;
const int bufferSize = 64;
extern AESLib aesLib;
extern const char* Secret_Key;
extern const char* IV;

extern char ssidBuffer[bufferSize];      // Asignar memoria para ssidBuffer
extern char passwordBuffer[bufferSize];  // Asignar memoria para passwordBuffer

// Clases
class Lista {
private:
  int textSize = 3;
  uint16_t textColor = TFT_WHITE;
  uint16_t textSelectedColor = TFT_WHITE;
  uint16_t textSelectedBackgroundColor = convertRGBtoRGB565(0, 30, 150);
  String* list;
  int length;

public:
  Lista(String list[], int length);
  ~Lista();
  int seleccionarLista();
  void setTextSize(int size);
};


#endif
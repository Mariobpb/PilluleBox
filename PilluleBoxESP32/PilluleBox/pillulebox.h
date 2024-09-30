// pillulebox.h
#ifndef pillulebox
#define pillulebox

#include <TFT_eSPI.h>
#include <WiFi.h>
#include <AESLib.h>
#include <Arduino.h>
#include "Base64.h"


enum KeyboardType { BK,
                    CK,
                    NSK,
                    Unknown };

// Funciones
void initPins();
void resetBtns();
void readBtns();
KeyboardType identifyArray(char (*keys)[10]);
void checkPositionsKeyboard(char Keys[][10], int *posX, int *posY);
int countKeysSpacesInRow(char Keys[][10], int row);
int countkeysInRow(char Keys[][10], int row);
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
String waitEnterText(char Keys[][10], String str, int posX, int posY, int initialPosY);
void displayEditableText(String str, int initialPosY);
void displayCharSelectedKeyboard(char Keys[][10], int positionX, int positionY, int initialPosY);
void menuUI();
void logInUI();
void dispenserUI();

//Solicitudes
bool logIn(String username_email, String password);

// Variables globales
extern const int btnPins[6];
extern bool btnCurrentStatus[6];
extern bool prevBtnStatus[6];
extern bool textConfirmed;
extern char basicKeys[4][10];
extern char capitalKeys[4][10];
extern char numberSymbolKeys[4][10];
extern TFT_eSPI tft;
extern TFT_eSprite sprite;
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
    int spritePosY = 0;
    int spriteHeight = tft.height();
    int length;
    int itemSelected = 1;
    int startIndex = 0;
    int itemsToShow = 10;

public:
    Lista(String list[], int length);
    ~Lista();
    int seleccionarLista();
    void setTextSize(int size);
    void setHeight(int height);
    void setPositionY(int spritePosY);
    void dibujarLista();
    void actualizarSeleccion(int direccion);
};


#endif
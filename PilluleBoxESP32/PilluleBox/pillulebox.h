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
void checkPositionsKeyboard(char Keys[][10], int* posX, int* posY);
int countKeysSpacesInRow(char Keys[][10], int row);
int countkeysInRow(char Keys[][10], int row);
void connectWiFi(String ssid, String password);
void reconnectWiFi();
String esperarBuffer();
void readStringfromEEPROM(int direccion, char* cadena, int longitud);
bool emptyDirFlash(int direccion, int longitud);
bool stringToBool(String value);
void writeStringInEEPROM(int direccion, const char* cadena, int longitud);
int selectNetwork();
uint16_t convertRGBtoRGB565(uint8_t r, uint8_t g, uint8_t b);
String generateSecretKey();
//void serialEvent();
//String esperarStringSerial();
String encryptPassword(String password);
String base64_encode(uint8_t* data, size_t length);

// UI
void setBackground(int b);
String waitEnterText(char Keys[][10], String str, int posX, int posY, int initialPosY);
void displayEditableText(String str, int initialPosY);
void displayCharSelectedKeyboard(char Keys[][10], int positionX, int positionY, int initialPosY);
void menuUI();
void logInUI();
void dispenserUI();

//Solicitudes
bool validateMacAddress();
bool logIn(String username_email, String password);
bool validateToken(const char* token);

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
//extern String inputString;
//extern boolean stringComplete;
extern const char* apiUrl;
extern const int dirTOKEN;
extern const int dirPASSWORD;
extern const int dirSSID;
extern const int dirMacAuth;
extern const int wifiEEPROMSize;
extern const int tokenBufferSize;
extern const int macAuthEEPROMSize;
extern AESLib aesLib;
extern const char* Secret_Key;
extern const char* IV;
extern String username;
extern char tokenEEPROM[256 + 1];

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
  int selectItemFromList();
  void setTextSize(int size);
  void setHeight(int height);
  void setPositionY(int spritePosY);
  void drawList();
  void updateSelection(int direccion);
};

class Cell {
private:
    int id;
    uint8_t num_cell;
    time_t current_medicine_date;
    SingleMode* single_mode;
    SequentialMode* sequential_mode;
    BasicMode* basic_mode;
    
public:
    Cell();
    ~Cell();
    
    void setNumber(uint8_t number);
    void setSingleMode(SingleMode* mode);
    void setSequentialMode(SequentialMode* mode);
    void setBasicMode(BasicMode* mode);
    void clearModes();
    
    bool canDispense(time_t current_time);
    void updateDispenseTime(time_t new_time);
    const char* getCurrentMedicineName() const;
    uint8_t getNumber() const { return num_cell; }
    int getId() const { return id; }
};

class SingleMode {
private:
    int id;
    char medicine_name[31];
    time_t dispensing_date;

public:
    SingleMode();
    void setMedicineName(const char* name);
    void setDispensingDate(time_t date);
    bool canDispense(time_t current_time);
    const char* getMedicineName() const;
    int getId() const { return id; }
};

class SequentialMode {
private:
    int id;
    char medicine_name[31];
    time_t start_date;
    time_t end_date;
    tm period;
    uint8_t limit_times_consumption;
    bool affected_periods;
    uint8_t current_times_consumption;

public:
    SequentialMode();
    void setMedicineName(const char* name);
    void setDateRange(time_t start, time_t end);
    void setPeriod(tm period_time);
    void setConsumptionLimit(uint8_t limit);
    void setAffectedPeriods(bool affected);
    bool incrementConsumption();
    bool canDispense(time_t current_time);
    const char* getMedicineName() const;
    int getId() const { return id; }
};

class BasicMode {
private:
    int id;
    char medicine_name[31];
    tm morning_start_time;
    tm morning_end_time;
    tm afternoon_start_time;
    tm afternoon_end_time;
    tm night_start_time;
    tm night_end_time;

public:
    BasicMode();
    void setMedicineName(const char* name);
    void setMorningTime(tm start, tm end);
    void setAfternoonTime(tm start, tm end);
    void setNightTime(tm start, tm end);
    bool isInMorningPeriod(tm current_time);
    bool isInAfternoonPeriod(tm current_time);
    bool isInNightPeriod(tm current_time);
    const char* getMedicineName() const;
    int getId() const { return id; }
};

#endif
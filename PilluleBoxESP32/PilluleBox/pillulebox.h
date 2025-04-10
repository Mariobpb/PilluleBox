// pillulebox.h
#ifndef pillulebox
#define pillulebox

#include <TFT_eSPI.h>
#include <WiFi.h>
#include <AESLib.h>
#include <Arduino.h>
#include "Base64.h"
#include <RTClib.h>
#include <Adafruit_PWMServoDriver.h>


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
time_t parseDateTime(const char* dateStr);
bool checkedAlarms();
bool updateCellsAgain();
void positionCell(int cellNumber);
void dispenseMedicine();
void enterMedicine();
int getNumCell(int column, int row);
void mostrarPulsos(int seccion, bool esOffset);
void buscarOffset(int servoChannel360, int pinOffset, int seccion);
void procesarSeccion(int seccion, int posicion);
void activarServo180(int servoChannel180);

// UI
void setBackground(int b);
String waitEnterText(char Keys[][10], String str, int posX, int posY, int initialPosY);
void displayEditableText(String str, int initialPosY);
void displayCharSelectedKeyboard(char Keys[][10], int positionX, int positionY, int initialPosY);
void menuUI();
void logInUI();
void displayCellsList();
void dispenserUI();
void showBackgroundInfo();

//Solicitudes
bool validateMacAddress();
bool logIn(String username_email, String password);
bool validateToken(const char* token);
bool updateCellsData(const char* token);

// Variables globales
extern const int btnPins[6];
extern bool btnCurrentStatus[6];
extern bool prevBtnStatus[6];
extern bool textConfirmed;
extern RTC_DS3231 rtc;
extern const int SDA_RTC_PIN;
extern const int SCL_RTC_PIN;

extern const int DRIVER_SDA;
extern const int DRIVER_SCL;
extern const int ENCODER1_MAIN_INPUT;
extern const int ENCODER2_MAIN_INPUT;
extern const int ENCODER1_OFFSET_INPUT;
extern const int ENCODER2_OFFSET_INPUT;
extern const int SERVO360_1_CHANNEL;
extern const int SERVO180_1_CHANNEL;
extern const int SERVO360_2_CHANNEL;
extern const int SERVO180_2_CHANNEL;
extern Adafruit_PWMServoDriver pwm;
extern const int SERVO_MIN;
extern const int SERVO_MAX;
extern const int SERVO_STOP;
extern int SERVO_SPIN;
extern int SERVO_ANTISPIN;
extern const int SERVO_180;
extern volatile int pulsosPrincipales;
extern volatile int pulsosOffset;
extern bool encontradoOffset;
extern int lastStateOffset;
extern int lastStatePrincipal;

extern DateTime lastLocalUpdate;
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

class SingleMode {
private:
  int id;
  char medicine_name[31];
  time_t dispensing_date;

public:
  SingleMode(int id);
  ~SingleMode();
  
  int getId() const;
  const char* getMedicineName() const;
  time_t getDispensingDate() const;
  
  void setId(int id);
  void setMedicineName(const char* name);
  void setDispensingDate(time_t date);
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
  SequentialMode(int id);
  ~SequentialMode();
  
  int getId() const;
  const char* getMedicineName() const;
  time_t getStartDate() const;
  time_t getEndDate() const;
  tm getPeriod() const;
  uint8_t getLimitTimesConsumption() const;
  bool getAffectedPeriods() const;
  uint8_t getCurrentTimesConsumption() const;
  
  void setId(int id);
  void setMedicineName(const char* name);
  void setStartDate(time_t date);
  void setEndDate(time_t date);
  void setPeriod(const tm& period);
  void setLimitTimesConsumption(uint8_t limit);
  void setAffectedPeriods(bool affected);
  void setCurrentTimesConsumption(uint8_t times);
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
  BasicMode(int id);
  ~BasicMode();
  
  int getId() const;
  const char* getMedicineName() const;
  tm getMorningStartTime() const;
  tm getMorningEndTime() const;
  tm getAfternoonStartTime() const;
  tm getAfternoonEndTime() const;
  tm getNightStartTime() const;
  tm getNightEndTime() const;
  
  void setId(int id);
  void setMedicineName(const char* name);
  void setMorningStartTime(const tm& time);
  void setMorningEndTime(const tm& time);
  void setAfternoonStartTime(const tm& time);
  void setAfternoonEndTime(const tm& time);
  void setNightStartTime(const tm& time);
  void setNightEndTime(const tm& time);
};

class Cell {
private:
  int id;
  uint8_t num_cell;
  time_t current_medicine_date;
  SingleMode* single_mode;
  SequentialMode* sequential_mode;
  BasicMode* basic_mode;

  void clearOtherModes();

public:
  Cell();
  ~Cell();
  
  int getId() const;
  uint8_t getNumCell() const;
  time_t getCurrentMedicineDate() const;
  SingleMode* getSingleMode() const;
  SequentialMode* getSequentialMode() const;
  BasicMode* getBasicMode() const;
  
  void setId(int id);
  void setNumCell(uint8_t num_cell);
  void setCurrentMedicineDate(time_t date);
  void setSingleMode(SingleMode* mode);
  void setSequentialMode(SequentialMode* mode);
  void setBasicMode(BasicMode* mode);
};

extern Cell cells[14];

struct AlarmInfo {
    bool isActive;
    DateTime alarmTime;
    uint8_t cellNumber;
    
    AlarmInfo() : isActive(false), cellNumber(0) {}
};

void setSingleModeAlarm(const Cell& cell);
void printCellData(const Cell& cell);
void displayCellSelected(Cell cells[], int columnSelected, int rowSelected);

extern AlarmInfo alarms[14];

#endif
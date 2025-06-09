#include "pillulebox.h"

const int btnPins[6] = { 46, 11, 12, 10, 3, 9 };  //Up, Down, Left, Right, Enter, Back
bool btnCurrentStatus[6] = { false, false, false, false, false, false };
bool prevBtnStatus[6] = { false, false, false, false, false, false };
bool textConfirmed;

const int Buzzer_PIN = 4;

RTC_DS3231 rtc;
const int SDA_RTC_PIN = 7;
const int SCL_RTC_PIN = 6;




const int DRIVER_SDA = 15;
const int DRIVER_SCL = 16;

const int ENCODER1_MAIN_INPUT = 17;
const int ENCODER2_MAIN_INPUT = 36;
const int ENCODER1_OFFSET_INPUT = 18;
const int ENCODER2_OFFSET_INPUT = 35;

const int SERVO360_1_CHANNEL = 11;
const int SERVO180_1_CHANNEL = 9;
const int SERVO360_2_CHANNEL = 15;
const int SERVO180_2_CHANNEL = 13;

Adafruit_PWMServoDriver pwm = Adafruit_PWMServoDriver(0x40, Wire1);

// Valores de control para servos de 360 grados
const int SERVO_MIN = 75;
const int SERVO_MAX = 563;
const int SERVO_STOP = 305;                                            // Detener
int SERVO_SPIN = SERVO_STOP + 50;                                 // Girar
int SERVO_ANTISPIN = ((SERVO_STOP) - (SERVO_SPIN - SERVO_STOP));  // Girar
const int SERVO_180 = 480;                                             // 180 grados

// Variable global para el número a procesar (1-14)

// Variables para contar pulsos
volatile int pulsosPrincipales = 0;
volatile int pulsosOffset = 0;
bool encontradoOffset = false;

// Variables para el estado anterior de los encoders
int lastStateOffset = HIGH;
int lastStatePrincipal = HIGH;





DateTime lastLocalUpdate;

char basicKeys[4][10] = {
  { 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p' },
  { 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', '\t' },
  { '\0', 'z', 'x', 'c', 'v', 'b', 'n', 'm', '\0', '\t' },
  { '\0', ' ', '.', '\0', '\t', '\t', '\t', '\t', '\t', '\t' }
};

char capitalKeys[4][10] = {
  { 'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P' },
  { 'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', '\t' },
  { '\0', 'Z', 'X', 'C', 'V', 'B', 'N', 'M', '\0', '\t' },
  { '\0', ' ', '.', '\0', '\t', '\t', '\t', '\t', '\t', '\t' }
};

char numberSymbolKeys[4][10] = {
  { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' },
  { '@', '#', '$', '_', '&', '+', '\t', '\t', '\t', '\t' },
  { '*', '!', '=', ':', '\0', '\t', '\t', '\t', '\t', '\t' },
  { '\0', ' ', '.', '\0', '\t', '\t', '\t', '\t', '\t', '\t' }
};

TFT_eSPI tft = TFT_eSPI();
TFT_eSprite sprite = TFT_eSprite(&tft);
const char* apiUrl = "http://192.168.100.9:8080";
const int dirTOKEN = 128;
const int dirPASSWORD = 64;
const int dirSSID = 0;
const int dirMacAuth = 384;
const int wifiEEPROMSize = 64;
const int tokenBufferSize = 256;
const int macAuthEEPROMSize = 1;
AESLib aesLib;
const char* Secret_Key = "1234567890123456"; // 16 bytes
const char* IV = "iughvnbaklsvvkhj"; // 16 bytes
char tokenEEPROM[tokenBufferSize + 1];
String username = "";

Cell cells[14];
AlarmInfo alarms[14];
#include "pillulebox.h"

const int btnPins[6] = { 46, 11, 12, 10, 3, 9 };  //Up, Down, Left, Right, Enter, Back
bool btnCurrentStatus[6] = { false, false, false, false, false, false };
bool prevBtnStatus[6] = { false, false, false, false, false, false };
bool textConfirmed;

int Buzzer_PIN = 4;

RTC_DS3231 rtc;
int SDA_RTC_PIN = 7;
int SCL_RTC_PIN = 6;

int TX_PIN = 18;
int RX_PIN = 17;

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
//extern String inputString = "";
//extern boolean stringComplete = false;
const char* apiUrl = "http://192.168.137.217:8080";
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
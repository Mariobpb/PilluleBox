#include "pillulebox.h"

TFT_eSPI tft = TFT_eSPI();
extern String inputString = "";
extern boolean stringComplete = false;
int Index = 0;
const char* apiUrl = "http://192.168.100.14:8080";
const int dirPASSWORD = 64;
const int dirSSID = 0;
AESLib aesLib;
const char* Secret_Key = "1234567890123456"; // 16 bytes
const char* IV = "iughvnbaklsvvkhj"; // 16 bytes
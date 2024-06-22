#include "pillulebox.h"

TFT_eSPI tft = TFT_eSPI();
int Index = 0;
const char* apiUrl = "http://192.168.137.131:8080";
const int dirPASSWORD = 64;
const int dirSSID = 0;
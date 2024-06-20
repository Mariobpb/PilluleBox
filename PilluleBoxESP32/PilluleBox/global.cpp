#include "pillulebox.h"

TFT_eSPI tft = TFT_eSPI();
int Index = 0;
const char* apiUrl = "https://pillulebox-q376oyvn2a-uc.a.run.app/registros";
const int dirPASSWORD = 64;
const int dirSSID = 0;
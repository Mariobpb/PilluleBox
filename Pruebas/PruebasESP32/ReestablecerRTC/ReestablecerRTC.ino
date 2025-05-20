#include <Wire.h>
#include <RTClib.h>
#include <TFT_eSPI.h> // Incluye tu librería para la pantalla TFT

// Definir los pines SDA y SCL para el RTC
#define RTC_SDA_PIN 7
#define RTC_SCL_PIN 6

RTC_DS3231 rtc; // Asumiendo que usas un DS3231, ajusta según tu módulo RTC
TFT_eSPI tft = TFT_eSPI();

void setup() {
  Serial.begin(115200);
  
  // Inicializar I2C con los pines especificados
  Wire.begin(RTC_SDA_PIN, RTC_SCL_PIN);

  // Inicializar pantalla TFT
  tft.init();
  tft.setRotation(1);
  tft.fillScreen(TFT_BLACK);
  tft.setTextColor(TFT_WHITE, TFT_BLACK);
  tft.setTextSize(2);

  // Inicializar el RTC
  if (!rtc.begin()) {
    Serial.println("No se pudo encontrar el RTC");
    tft.setCursor(10, 10);
    tft.println("Error: RTC no encontrado!");
    while (1);
  }

  // Siempre ajustamos el RTC con la hora de compilación
  // Esta línea asigna la fecha y hora de tu PC al momento de compilar
  rtc.adjust(DateTime(F(__DATE__), F(__TIME__)));
  
  Serial.println("RTC ajustado con la hora de compilación");
  Serial.println("Fecha de compilación: " + String(F(__DATE__)));
  Serial.println("Hora de compilación: " + String(F(__TIME__)));
  
  // Esperar un momento para asegurar que el RTC se actualice
  delay(1000);
}

void loop() {
  // Obtener la hora actual del RTC
  DateTime now = rtc.now();
  
  // Mostrar fecha y hora en la pantalla TFT
  displayOnTFT(now);
  
  // Mostrar la misma fecha y hora en el Serial
  displayOnSerial(now);
  
  // Actualizar cada segundo
  delay(1000);
}

void displayOnTFT(DateTime time) {
  tft.fillScreen(TFT_BLACK);
  
  // Mostrar fecha
  tft.setCursor(10, 20);
  tft.print("Fecha: ");
  tft.print(time.year(), DEC);
  tft.print('/');
  printDigits(tft, time.month());
  tft.print('/');
  printDigits(tft, time.day());
  
  // Mostrar hora
  tft.setCursor(10, 60);
  tft.print("Hora: ");
  printDigits(tft, time.hour());
  tft.print(':');
  printDigits(tft, time.minute());
  tft.print(':');
  printDigits(tft, time.second());
  
  // Mostrar temperatura (si usas DS3231)
  tft.setCursor(10, 100);
  tft.print("Temp: ");
  tft.print(rtc.getTemperature());
  tft.print(" C");
}

void displayOnSerial(DateTime time) {
  Serial.print("Fecha y hora: ");
  Serial.print(time.year(), DEC);
  Serial.print('/');
  printDigits(Serial, time.month());
  Serial.print('/');
  printDigits(Serial, time.day());
  Serial.print(" ");
  printDigits(Serial, time.hour());
  Serial.print(':');
  printDigits(Serial, time.minute());
  Serial.print(':');
  printDigits(Serial, time.second());
  Serial.println();
}

void printDigits(Print &output, int digits) {
  if (digits < 10) {
    output.print('0');
  }
  output.print(digits);
}
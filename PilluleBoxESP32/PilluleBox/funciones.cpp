#include "pillulebox.h"

#include <Arduino.h>
#include <AES.h>
#include <AESLib.h>

#include <ArduinoJson.h>
#include <EEPROM.h>
//#include <HTTPClient.h>
#include <WiFi.h>

#include <TFT_eSPI.h>

TFT_eSPI tft = TFT_eSPI();
int Index = 0;
const char* apiUrl = "https://pillulebox-q376oyvn2a-uc.a.run.app/registros";
const int dirPASSWORD = 64;
const int dirSSID = 0;


void conectar(const char* ssid, const char* password) {
  // Desconecta WiFi si ya está conectado
  WiFi.disconnect(true);

  // Espera un momento para que la desconexión tenga lugar
  delay(1000);

  WiFi.begin(ssid, password);

  tft.setTextColor(TFT_WHITE, TFT_BLACK);
  tft.setCursor(0, 0);
  tft.setTextSize(2);
  tft.print("Conectando a :\n");
  tft.setTextSize(3);
  tft.print(ssid);
  Serial.print("\n\nConectando a : |");
  Serial.print(ssid);

  tft.setTextSize(2);
  tft.setCursor(0, 100);
  tft.print("Contrasena :\n");
  tft.setTextSize(3);
  tft.print(password);
  Serial.print("|\nContrasena: |");
  Serial.print(password);
  Serial.println("|");

  int tiempo = 0;
  tft.setCursor(0, 250);
  while (WiFi.status() != WL_CONNECTED && tiempo < 30) {
    delay(500);
    tft.setTextSize(2);
    tft.print(".");
    Serial.print(".");
    tiempo++;
  }
  tft.setTextColor(TFT_RED, TFT_BLACK);
  tft.setCursor(0, 300);
  if (tiempo == 30) {
    tft.print("Conexión Fallida");
    Serial.println("Conexion Fallida");
    delay(1000);
    return;
  }
  tft.print("Se ha conectado al wifi exitosamente\nIP: ");
  Serial.print("\nSe ha conectado al wifi exitosamente\nIP: ");
  Serial.println(WiFi.localIP());
}

void reconectar() {

  mostrarRedesDisponibles();
  reiniciarBuffer();

  Serial.write("\nSSID (Seleccione el número de la lista): ");
  while (!respuestaCompleta()) {
    // Puedes realizar otras tareas en el bucle principal mientras esperas la respuesta
  }
  Serial.write(buffer);

  int seleccion = atoi(buffer);
  WiFi.SSID(seleccion - 1).toCharArray(ssidBuffer, sizeof(ssidBuffer));
  reiniciarBuffer();

  Serial.write("\nPASSWORD: ");
  while (!respuestaCompleta()) {
    // Puedes realizar otras tareas en el bucle principal mientras esperas la respuesta
  }
  Serial.write(buffer);

  strncpy(passwordBuffer, buffer, bufferSize);
  passwordBuffer[strlen(passwordBuffer) - 1] = '\0';  // Remueve el último carácter

  reiniciarBuffer();
  escribirCadenaEnEEPROM(dirSSID, ssidBuffer, bufferSize);
  escribirCadenaEnEEPROM(dirPASSWORD, passwordBuffer, bufferSize);
  conectar(ssidBuffer, passwordBuffer);
}

String esperarBuffer() {
  reiniciarBuffer();
  String res;
  while (!respuestaCompleta()) {}
  res = String(buffer);
  reiniciarBuffer();
  return res;
}

void leerCadenaDesdeEEPROM(int direccion, char* cadena, int longitud) {
  for (int i = 0; i < longitud; i++) {
    cadena[i] = EEPROM.read(direccion + i);
  }
  cadena[longitud] = '\0';  // Agrega el carácter nulo al final de la cadena
}

bool memoriaVacia(int direccion, int longitud) {
  for (int i = 0; i < longitud; i++) {
    if (EEPROM.read(direccion + i) != 0xFF) {
      return false;  // No está vacía
    }
  }
  return true;  // Está vacía
}

void mostrarRedesDisponibles() {
  WiFi.scanDelete();  // Borrar la caché de escaneo

  tft.fillScreen(TFT_BLACK);
  tft.setTextColor(TFT_WHITE, TFT_BLACK);
  tft.setTextSize(2.5);
  tft.setCursor(0, 20);

  delay(1000);
  int numRedes = WiFi.scanNetworks();  // Escanear redes WiFi
  delay(1000);
  if (numRedes == 0) {
    tft.println("Sin redes disponibles");
    Serial.println("\nNo se encontraron redes disponibles.");
  } else {
    Serial.println("\nRedes disponibles:");

    tft.setTextSize(2);
    for (int i = 0; i < numRedes; i++) {
      tft.print(i + 1);
      Serial.print(i + 1);

      Serial.print(": ");
      tft.print(": ");

      Serial.println(WiFi.SSID(i));
      tft.println(WiFi.SSID(i));
    }
  }
}

void reiniciarBuffer() {
  Index = 0;
  memset(buffer, 0, bufferSize);
}

bool respuestaCompleta() {
  while (Serial.available() > 0) {
    char receivedChar = Serial.read();  // Lee el caracter recibido

    if (receivedChar == '\n') {  // Si se recibe un salto de línea, la respuesta está completa
      buffer[Index] = '\0';      // Agrega el carácter nulo al final del búfer para formar una cadena
      return true;
    } else {
      // Almacena el carácter en el búfer si no es un salto de línea
      buffer[Index] = receivedChar;
      Index = (Index + 1) % bufferSize;  // Evita desbordamientos del búfer
    }
  }

  return false;  // La respuesta no está completa todavía
}

bool stringToBool(String value) {
  int intValue = value.toInt();
  return intValue != 0;
}

void escribirCadenaEnEEPROM(int direccion, const char* cadena, int longitud) {
  for (int i = 0; i < longitud; i++) {
    EEPROM.write(direccion + i, cadena[i]);
  }
  EEPROM.commit();
}

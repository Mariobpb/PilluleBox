#include <Wire.h>
#include <RTClib.h>

// Crear objeto RTC
RTC_DS3231 rtc;

#define SDA_PIN 7  // Definir el pin SDA
#define SCL_PIN 6  // Definir el pin SCL

// Pin para LED indicador (puedes cambiarlo según tu configuración)
const int LED_PIN = 4;

// Variable para almacenar la hora de la alarma
DateTime alarmTime;

// Función que se ejecutará cuando se active la alarma
void IRAM_ATTR onAlarm() {
}

void setup() {
    Serial.begin(115200);
    pinMode(LED_PIN, OUTPUT);

    Wire.begin(SDA_PIN, SCL_PIN);

    // Inicializar RTC
    if (!rtc.begin()) {
        Serial.println("No se pudo encontrar el RTC");
        while (1);
    }

    // Si se ha perdido la energía, fijar la fecha y hora
    if (rtc.lostPower()) {
        Serial.println("RTC perdió energía, fijando fecha y hora!");
        // Fijar a la fecha y hora de compilación
        rtc.adjust(DateTime(F(__DATE__), F(__TIME__)));
    }

    // Configurar la hora de la alarma (ejemplo: 12:00:00)
    alarmTime = DateTime(2024, 11, 10, 21, 57, 0);
    
    // Habilitar la interrupción para la alarma 1
    rtc.clearAlarm(1);
    rtc.disableAlarm(1);
    rtc.setAlarm1(alarmTime, DS3231_A1_Hour); // Alarma cuando coincidan hora y minutos
    
    // Configurar pin de interrupción
    pinMode(15, INPUT_PULLUP); // Ajusta el pin según tu conexión
    attachInterrupt(digitalPinToInterrupt(15), onAlarm, FALLING);
}

void loop() {
    DateTime now = rtc.now();
    
    // Imprimir la hora actual cada segundo
    Serial.printf("Hora actual: %02d:%02d:%02d\n", 
        now.hour(), now.minute(), now.second());
    
    // Verificar si hay alguna alarma pendiente
    if (rtc.alarmFired(1)) {
        Serial.println("¡Alarma activada!");
        rtc.clearAlarm(1);
    }
    
    delay(1000);
}
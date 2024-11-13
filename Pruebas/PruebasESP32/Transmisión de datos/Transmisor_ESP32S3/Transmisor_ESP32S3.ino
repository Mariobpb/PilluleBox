#include <Adafruit_PWMServoDriver.h>

// ESP32-S3 (Transmisor)
void setup() {
  Serial.begin(9600);    // Monitor serial
  Serial2.begin(9600, SERIAL_8N1, 17, 18);   // RX2(17), TX2(18)
  delay(1000);
  Serial.println("ESP32-S3 iniciado");
}

void loop() {
  // Estructura de los datos a enviar:
  // velocidad (0-255)
  // dirección (0 para sentido horario, 1 para antihorario)
  uint8_t datos[2];
  
  // Ejemplo: velocidad 200, sentido horario
  datos[0] = 200;  // velocidad
  datos[1] = 0;    // dirección (0: horario)
  
  // Enviamos los datos
  Serial2.write(datos, 2);
  
  // Mostramos lo que enviamos
  Serial.print("Enviado - Velocidad: ");
  Serial.print(datos[0]);
  Serial.print(", Dirección: ");
  Serial.println(datos[1] == 0 ? "Horario" : "Antihorario");
  
  delay(2000);  // Esperamos 2 segundos
  
  // Cambiamos dirección
  datos[1] = 1;  // dirección (1: antihorario)
  
  // Enviamos nuevamente
  Serial2.write(datos, 2);
  
  Serial.print("Enviado - Velocidad: ");
  Serial.print(datos[0]);
  Serial.print(", Dirección: ");
  Serial.println(datos[1] == 0 ? "Horario" : "Antihorario");
  
  delay(2000);  // Esperamos 2 segundos
}
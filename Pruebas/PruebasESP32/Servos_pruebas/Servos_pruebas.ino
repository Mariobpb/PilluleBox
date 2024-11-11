#include <ESP32Servo.h>

// Crear objeto servo
Servo servo360;

// Pin donde está conectado el servo
const int servoPin = 48;

void setup() {
  // Iniciar comunicación serial para debug
  Serial.begin(115200);
  
  // Esperar a que el serial esté disponible (útil para debug)
  delay(1000);
  Serial.println("Iniciando programa de servo...");
  
  // Configuración específica para ESP32-S3
  servo360.attach(servoPin);  // Forma simplificada de attach para S3
  
  // Verificar si el servo se adjuntó correctamente
  if (servo360.attached()) {
    Serial.println("Servo adjuntado correctamente");
  } else {
    Serial.println("Error al adjuntar el servo");
  }
  
  // Asegurarse que el servo empiece detenido
  servo360.write(90);
  delay(1000); // Dar tiempo para que el servo se estabilice
}

void loop() {
  // Girar en sentido horario
  Serial.println("Girando en sentido horario");
  for(int pos = 90; pos <= 180; pos += 10) {
    servo360.write(pos);
    Serial.printf("Posición: %d\n", pos);
    delay(500);
  }
  
  // Detener
  Serial.println("Deteniendo");
  servo360.write(90);
  delay(2000);
  
  // Girar en sentido antihorario
  Serial.println("Girando en sentido antihorario");
  for(int pos = 90; pos >= 0; pos -= 10) {
    servo360.write(pos);
    Serial.printf("Posición: %d\n", pos);
    delay(500);
  }
  
  // Detener nuevamente
  Serial.println("Deteniendo");
  servo360.write(90);
  delay(2000);
}
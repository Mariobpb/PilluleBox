#include <ESP32Servo.h>

// Pin donde conectaremos el servo (puede ser cualquier pin GPIO)
#define SERVO_PIN 38  // Puedes cambiar este pin

Servo myservo;

void setup() {
  Serial.begin(115200);
  Serial.println("Iniciando prueba directa de servo...");
  
  // Permitir asignación de timer para el canal PWM
  ESP32PWM::allocateTimer(0);
  
  // Configurar el servo
  myservo.setPeriodHertz(50);  // PWM a 50Hz
  myservo.attach(SERVO_PIN);   // Adjuntar el servo al pin
  
  Serial.println("Servo inicializado");
}

void loop() {
  Serial.println("Punto medio - debería detenerse");
  myservo.write(90);  // Posición central
  delay(3000);
  
  Serial.println("Rotación completa en una dirección");
  myservo.write(180); // Máximo en una dirección
  delay(3000);
  
  Serial.println("Punto medio nuevamente");
  myservo.write(90);  // Regreso al centro
  delay(3000);
  
  Serial.println("Rotación completa en dirección opuesta");
  myservo.write(0);   // Máximo en la otra dirección
  delay(3000);
}
#include <Servo.h>

const int encoderPin = 2;   // Pin de entrada del encoder (debe ser un pin de interrupción en el Arduino UNO)
const int servoPin = 9;     // Pin del servo
const int targetPulses = 6; // Número de pulsos objetivo para detener el servo
volatile int pulseCount = 0; // Contador de pulsos (volátil para el uso en ISR)
Servo myServo;

// Interrupción para contar los pulsos
void countPulses() {
  pulseCount++;
  Serial.println("Contador: " + (String) pulseCount);
}

void setup() {
  Serial.begin(9600);
  
  // Configurar el pin del encoder y habilitar la interrupción
  pinMode(encoderPin, INPUT);
  attachInterrupt(digitalPinToInterrupt(encoderPin), countPulses, RISING);

  // Configurar el servo
  myServo.attach(servoPin);
  myServo.write(90);  // Posición inicial (detenida)

  delay(1000);        // Pausa para estabilización
}

void loop() {
  pulseCount = 0;         // Reinicia el contador de pulsos
  
  // Mueve el servo en una dirección
  myServo.writeMicroseconds(1600);    // Ajusta el valor para movimiento lento en sentido horario
  while (pulseCount < targetPulses) {
    // Espera a que el contador de pulsos llegue al objetivo
  }
  
  // Detiene el servo
  myServo.writeMicroseconds(1500);

  Serial.print("Pulsos alcanzados: ");
  Serial.println(pulseCount);

  delay(1000);            // Pausa antes de volver a intentar
}

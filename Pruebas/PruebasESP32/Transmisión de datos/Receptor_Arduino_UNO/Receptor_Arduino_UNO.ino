// Arduino UNO (Receptor con control de servo)
#include <Servo.h>

Servo servoMotor;
const int pinServo = 9;

void setup() {
  Serial.begin(9600);
  servoMotor.attach(pinServo);
  servoMotor.write(90);  // Posición inicial (detenido)
  Serial.println("Arduino iniciado - Servo conectado");
}

void loop() {
  if(Serial.available() >= 2) {  // Esperamos recibir 2 bytes
    uint8_t velocidad = Serial.read();
    uint8_t direccion = Serial.read();
    
    // Convertimos la velocidad (0-255) a un valor de servo (0-180)
    // Para un servo continuo:
    // - 90 es detenido
    // - 0 es máxima velocidad en un sentido
    // - 180 es máxima velocidad en sentido contrario
    int valorServo;
    
    if(direccion == 0) {  // Sentido horario
      // Mapeo de velocidad para sentido horario (90 a 0)
      valorServo = map(velocidad, 0, 255, 90, 0);
    } else {  // Sentido antihorario
      // Mapeo de velocidad para sentido antihorario (90 a 180)
      valorServo = map(velocidad, 0, 255, 90, 180);
    }
    
    // Movemos el servo
    servoMotor.write(valorServo);
    
    // Mostramos los datos recibidos
    Serial.print("Recibido - Velocidad: ");
    Serial.print(velocidad);
    Serial.print(", Dirección: ");
    Serial.print(direccion == 0 ? "Horario" : "Antihorario");
    Serial.print(", Valor Servo: ");
    Serial.println(valorServo);
  }
}
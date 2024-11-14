#include <Wire.h>
#include <Adafruit_PWMServoDriver.h>

Adafruit_PWMServoDriver pwm = Adafruit_PWMServoDriver(0x40);

const int servoPin = 0; // Canal del servomotor en el driver PCA9685
int servoPos = 0; // Posición actual del servomotor

void setup() {
  Serial.begin(115200);
  pwm.begin();
  pwm.setPWMFreq(50); // Frecuencia de 50Hz para servomotores
}

void loop() {
  // Mover el servomotor hacia la derecha
  for (servoPos = 0; servoPos < 4096; servoPos++) {
    pwm.setPWM(servoPin, 0, servoPos);
    delay(1); // Velocidad de movimiento
  }

  // Mantener la posición final durante 2 segundos
  delay(2000);
}
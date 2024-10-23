#include <Servo.h>

const int servoPin = 9;    // Cable de señal del servo conectado al pin 9

Servo myServo;

void setup() {
  myServo.attach(servoPin);
}

void loop() {
  myServo.write(180);
  delay(10000);
  myServo.write(0);
  delay(10000);
}
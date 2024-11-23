#include <Wire.h>
#include <Adafruit_PWMServoDriver.h>

Adafruit_PWMServoDriver servoDriver = Adafruit_PWMServoDriver();

#define SERVO_CHANNEL 0
#define SERVO_FREQ 50
#define SERVOMIN 150
#define SERVOMAX 600
#define CLOCKWISE 400
#define STOP 375

void setup() {
  Serial.begin(115200);
  
  Wire.begin(41, 40);  // SDA = GPIO41, SCL = GPIO40
  
  servoDriver.begin();
  servoDriver.setPWMFreq(SERVO_FREQ);
  
  delay(10);
}

void loop() {
  // Girar el servo hacia la derecha
  Serial.println("Girando hacia la derecha...");
  servoDriver.setPWM(SERVO_CHANNEL, 0, CLOCKWISE);
  
  delay(3000);  // Girar durante 3 segundos
  
  // Detener el servo
  Serial.println("Deteniendo...");
  servoDriver.setPWM(SERVO_CHANNEL, 0, STOP);
  
  delay(2000);  // Esperar 2 segundos antes de repetir
}
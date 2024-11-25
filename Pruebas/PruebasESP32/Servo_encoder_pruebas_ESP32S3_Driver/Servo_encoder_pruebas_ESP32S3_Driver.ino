#include <Wire.h>
#include <Adafruit_PWMServoDriver.h>

// Define los pines I2C que quieras usar
#define I2C_SDA 8  // GPIO8
#define I2C_SCL 9  // GPIO9

int SERVOMIN = 200;
int SERVOMAX = 430;
int SERVOSTOP = (((SERVOMAX - SERVOMIN)/2)+SERVOMIN);

Adafruit_PWMServoDriver pwm = Adafruit_PWMServoDriver();

void setup() {
  Serial.begin(9600);
  // Inicializa I2C con los pines específicos
  Wire.begin(I2C_SDA, I2C_SCL);

  pwm.begin();
  pwm.setPWMFreq(50);
}

void loop() {
  /*
  Serial.println("Valor mínimo:\n");
  for (int i = 80; i < 100; i++) {
    pwm.setPWM(0, 0, i);
    Serial.println((String)i);
    delay(2000);
  }

  pwm.setPWM(0, 0, 320);  // Aproximadamente 1700 microsegundos
  Serial.println("Parando...");
  delay(5000);

  Serial.println("Valor máximo:\n");
  for (int i = 500; i < 600; i++) {
    pwm.setPWM(0, 0, i);
    Serial.println((String)i);
    delay(2000);
  }
  // Parar (punto neutro, normalmente alrededor de 1500 microsegundos)
  */

  pwm.setPWM(0, 0, SERVOMIN);  // Canal 0 del driver
  Serial.println("Girando izquierda CH0...");

  delay(5000);

  // Girar en sentido horario (CW) - velocidad máxima
  pwm.setPWM(0, 0, 320);  // Aproximadamente 1700 microsegundos
  Serial.println("Parando CH0...");

  delay(5000);

  // Girar en sentido antihorario (CCW) - velocidad máxima
  pwm.setPWM(4, 0, SERVOMAX);  // Aproximadamente 1300 microsegundos
  Serial.println("180 CH4...");

  delay(5000);

  pwm.setPWM(4, 0, SERVOMIN);  // Aproximadamente 1700 microsegundos
  Serial.println("0 CH4...");

  delay(5000);

  pwm.setPWM(1, 0, SERVOMIN);  // Canal 0 del driver
  Serial.println("Girando izquierda CH1...");

  delay(5000);

  // Girar en sentido horario (CW) - velocidad máxima
  pwm.setPWM(1, 0, 320);  // Aproximadamente 1700 microsegundos
  Serial.println("Parando CH1...");

  delay(5000);
}
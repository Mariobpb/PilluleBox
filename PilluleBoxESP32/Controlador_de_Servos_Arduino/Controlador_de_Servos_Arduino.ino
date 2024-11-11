// Arduino UNO Code
#include <Arduino.h>
#include <Servo.h>

// Pin definitions
#define SERVO360_1_PIN 11
#define ENCODER_OFFSET_1_PIN 13
#define ENCODER_PRINCIPAL_1_PIN 12
#define SERVO180_1_PIN 10

#define SERVO360_2_PIN 6
#define ENCODER_OFFSET_2_PIN 8
#define ENCODER_PRINCIPAL_2_PIN 7
#define SERVO180_2_PIN 5

#define SERVO_STOP 1500
#define SERVO_CW 1600

int cellNumber = 0;
bool isDispensing = false;

Servo servo360_1;
Servo servo360_2;
Servo servo180_1;
Servo servo180_2;

volatile int pulsosPrincipales = 0;
volatile int pulsosOffset = 0;
bool encontradoOffset = false;

int lastStateOffset = HIGH;
int lastStatePrincipal = HIGH;

void setup() {
  // Attach servos to their respective pins
  servo360_1.attach(SERVO360_1_PIN);
  servo360_2.attach(SERVO360_2_PIN);
  servo180_1.attach(SERVO180_1_PIN);
  servo180_2.attach(SERVO180_2_PIN);

  // Set up encoder input pins
  pinMode(ENCODER_OFFSET_1_PIN, INPUT);
  pinMode(ENCODER_PRINCIPAL_1_PIN, INPUT);
  pinMode(ENCODER_OFFSET_2_PIN, INPUT);
  pinMode(ENCODER_PRINCIPAL_2_PIN, INPUT);

  // Set servos to stop position
  servo360_1.writeMicroseconds(SERVO_STOP);
  servo360_2.writeMicroseconds(SERVO_STOP);

  // Set 180-degree servos to 180 degrees
  servo180_1.write(180);
  servo180_2.write(180);

  Serial.begin(9600);
}

void loop() {
  if (Serial.available() > 0) {
    String input = Serial.readStringUntil(',');
    int cellNumber = input.toInt();

    int isDispensing = Serial.parseInt();  // Leer un entero

    if (cellNumber >= 1 && cellNumber <= 14) {
      if (cellNumber <= 7) {
        processSection(1, cellNumber);
      } else {
        processSection(2, cellNumber - 7);
      }
    }

    if (isDispensing == 1) {  // Si el entero es 1, significa que se debe dispensar
      Serial.println(1);      // Enviar 1 para indicar "true"
    } else {
      Serial.println(0);  // Enviar 0 para indicar "false"
    }
  }
}

void findOffset(Servo* servo360, int pinOffset, int section) {
  encontradoOffset = false;
  pulsosOffset = 0;
  int currentState;

  servo360->writeMicroseconds(SERVO_CW);

  while (!encontradoOffset) {
    currentState = digitalRead(pinOffset);

    // Detect change from HIGH to LOW (falling edge)
    if (currentState == LOW && lastStateOffset == HIGH) {
      pulsosOffset++;
    }

    lastStateOffset = currentState;

    if (currentState == LOW) {
      encontradoOffset = true;
      servo360->writeMicroseconds(SERVO_STOP);
      delay(500);
    }
  }
}

void processSection(int section, int position) {
  Servo* servo360 = (section == 1) ? &servo360_1 : &servo360_2;
  Servo* servo180 = (section == 1) ? &servo180_1 : &servo180_2;
  int pinOffset = (section == 1) ? ENCODER_OFFSET_1_PIN : ENCODER_OFFSET_2_PIN;
  int pinPrincipal = (section == 1) ? ENCODER_PRINCIPAL_1_PIN : ENCODER_PRINCIPAL_2_PIN;

  pulsosPrincipales = 0;
  int currentState;

  findOffset(servo360, pinOffset, section);

  if (position > 1) {
    int pulsosObjetivo = position - 1;

    servo360->writeMicroseconds(SERVO_CW);

    while (pulsosPrincipales < pulsosObjetivo) {
      currentState = digitalRead(pinPrincipal);

      if (currentState == LOW && lastStatePrincipal == HIGH) {
        pulsosPrincipales++;
      }

      lastStatePrincipal = currentState;
    }

    servo360->writeMicroseconds(SERVO_STOP);
  }

  if (isDispensing) {
    activate180DegreeServo(servo180);
  }
}

void activate180DegreeServo(Servo* servo) {
  servo->write(0);
  delay(3000);
  servo->write(180);
}
#include <Wire.h>
#include <Adafruit_PWMServoDriver.h>
#include <TFT_eSPI.h>

TFT_eSPI tft = TFT_eSPI();

// Define los pines I2C que quieras usar
#define I2C_SDA 15  // GPIO8
#define I2C_SCL 16  // GPIO9


#define ENCODER1_MAIN_INPUT 17
#define ENCODER2_MAIN_INPUT 36
#define ENCODER1_OFFSET_INPUT 18
#define ENCODER2_OFFSET_INPUT 35

#define SERVO360_1_CHANNEL 11
#define SERVO180_1_CHANNEL 9
#define SERVO360_2_CHANNEL 15
#define SERVO180_2_CHANNEL 13

Adafruit_PWMServoDriver pwm = Adafruit_PWMServoDriver();

// Valores de control para servos de 360 grados
#define SERVO_MIN 75
#define SERVO_MAX 563
#define SERVO_STOP 305                                            // Detener
int SERVO_SPIN = SERVO_STOP + 50;                                 // Girar
int SERVO_ANTISPIN = ((SERVO_STOP) - (SERVO_SPIN - SERVO_STOP));  // Girar
#define SERVO_180 480                                             // 180 grados

// Variable global para el número a procesar (1-14)

// Variables para contar pulsos
volatile int pulsosPrincipales = 0;
volatile int pulsosOffset = 0;
bool encontradoOffset = false;

// Variables para el estado anterior de los encoders
int lastStateOffset = HIGH;
int lastStatePrincipal = HIGH;

void setup() {
  Serial.begin(9600);

  tft.init();
  tft.setRotation(0);
  tft.setTextColor(TFT_WHITE);
  tft.fillScreen(TFT_BLACK);
  tft.setTextSize(4);
  tft.setCursor(0, 50);
  tft.println("Iniciando...");
  tft.setTextSize(2);
  Serial.println("Pantalla inicializada");

  Wire.begin(I2C_SDA, I2C_SCL);

  pwm.begin();
  pwm.setPWMFreq(50);

  // Configuración de pines de entrada
  pinMode(ENCODER1_OFFSET_INPUT, INPUT);
  pinMode(ENCODER1_MAIN_INPUT, INPUT);
  pinMode(ENCODER2_OFFSET_INPUT, INPUT);
  pinMode(ENCODER2_MAIN_INPUT, INPUT);

  // Inicializar servos de 180 grados a posición inicial
  pwm.setPWM(SERVO180_1_CHANNEL, 0, SERVO_MIN);
  pwm.setPWM(SERVO180_2_CHANNEL, 0, SERVO_180);


  Serial.println("Sistema iniciado");
  delay(1000);
}

void loop() {
  for (int numeroRecibido = 0; numeroRecibido <= 14; numeroRecibido++) {
    Serial.println("\n\nNúmero recibido: " + (String)numeroRecibido);
    tft.fillScreen(TFT_BLACK);
    tft.setCursor(0, 0);
    tft.println("Numero recibido: " + (String)numeroRecibido);

    if (numeroRecibido >= 1 && numeroRecibido <= 14) {
      if (numeroRecibido <= 7) {
        Serial.println("\nProcesando Seccion 1");
        procesarSeccion(1, numeroRecibido);
      } else {
        Serial.println("\nProcesando Seccion 2");
        procesarSeccion(2, numeroRecibido - 7);
      }

      // Esperar antes de procesar otro número
      delay(5000);
    }
  }
}

void mostrarPulsos(int seccion, bool esOffset) {
  if (esOffset) {
    Serial.print("Seccion ");
    Serial.print(seccion);
    Serial.print(" - Pulsos Offset: ");
    Serial.println(pulsosOffset);
  } else {
    Serial.print("Seccion ");
    Serial.print(seccion);
    Serial.print(" - Pulsos Principal: ");
    Serial.println(pulsosPrincipales);
  }
}

void buscarOffset(int servoChannel360, int pinOffset, int seccion) {
  encontradoOffset = false;
  pulsosOffset = 0;
  int currentState;

  Serial.print("Buscando offset en seccion ");
  tft.println("Buscando offset...");
  Serial.println(seccion);

  pwm.setPWM(servoChannel360, 0, SERVO_SPIN);  // Iniciar movimiento

  while (!encontradoOffset) {
    currentState = digitalRead(pinOffset);

    // Detectar cambio de HIGH a LOW (flanco descendente)
    if (currentState == LOW && lastStateOffset == HIGH) {
      pulsosOffset++;
      mostrarPulsos(seccion, true);
    }

    lastStateOffset = currentState;

    if (currentState == LOW) {
      encontradoOffset = true;
      pwm.setPWM(servoChannel360, 0, SERVO_ANTISPIN);
      delay(50);
      pwm.setPWM(servoChannel360, 0, SERVO_STOP);  // Detener servo
      Serial.println("Offset encontrado!");
      tft.println("Offset encontrado!");
      delay(2000);
    }
  }
}

void procesarSeccion(int seccion, int posicion) {
  int servoChannel360 = (seccion == 1) ? SERVO360_1_CHANNEL : SERVO360_2_CHANNEL;
  int servoChannel180 = (seccion == 1) ? SERVO180_1_CHANNEL : SERVO180_2_CHANNEL;
  int pinOffset = (seccion == 1) ? ENCODER1_OFFSET_INPUT : ENCODER2_OFFSET_INPUT;
  int pinPrincipal = (seccion == 1) ? ENCODER1_MAIN_INPUT : ENCODER2_MAIN_INPUT;

  pulsosPrincipales = 0;
  int currentState;

  // Siempre buscar el offset primero
  buscarOffset(servoChannel360, pinOffset, seccion);

  // Si necesitamos movernos a una posición específica (mayor a 1)
  if (posicion > 1) {
    int pulsosObjetivo = posicion - 1;
    Serial.print("Moviendo a posicion " + (String) posicion);
    tft.println("Moviendo a posicion " + (String) posicion);

    pwm.setPWM(servoChannel360, 0, SERVO_SPIN);  // Continuar movimiento

    while (pulsosPrincipales < pulsosObjetivo) {
      currentState = digitalRead(pinPrincipal);

      // Detectar cambio de HIGH a LOW (flanco descendente)
      if (currentState == LOW && lastStatePrincipal == HIGH) {
        pulsosPrincipales++;
        mostrarPulsos(seccion, false);
      }

      lastStatePrincipal = currentState;
    }
    pwm.setPWM(servoChannel360, 0, SERVO_ANTISPIN);
    delay(50);
    pwm.setPWM(servoChannel360, 0, SERVO_STOP);  // Detener servo
    Serial.println("Posicion alcanzada!");
    tft.println("Posicion alcanzada!");
  }

  // Activar servo de 180 grados
  activarServo180(servoChannel180);
}

void activarServo180(int servoChannel180) {
  Serial.println("Activando servo de 180 grados");
  tft.println("Activando servo de 180 grados");
  if (servoChannel180 == SERVO180_1_CHANNEL) {
    pwm.setPWM(servoChannel180, 0, SERVO_180);
    delay(2000);
    pwm.setPWM(servoChannel180, 0, SERVO_MIN);
    delay(2000);
  } else {
    pwm.setPWM(servoChannel180, 0, SERVO_MIN);  // Mover a 0 grados
    delay(2000);
    pwm.setPWM(servoChannel180, 0, SERVO_180);  // Regresar a 180 grados
    delay(2000);
  }
  Serial.println("Servo de 180 grados completado");
}
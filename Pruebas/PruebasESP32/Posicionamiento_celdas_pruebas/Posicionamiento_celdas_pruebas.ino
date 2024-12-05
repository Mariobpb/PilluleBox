#include <Wire.h>
#include <Adafruit_PWMServoDriver.h>

// Define los pines I2C que quieras usar
#define I2C_SDA 15  // GPIO8
#define I2C_SCL 16  // GPIO9


#define ENCODER1_MAIN_INPUT 36
#define ENCODER2_MAIN_INPUT 17
#define ENCODER1_OFFSET_INPUT 35
#define ENCODER2_OFFSET_INPUT 18

#define SERVO360_1_CHANNEL 0
#define SERVO180_1_CHANNEL 1
#define SERVO360_2_CHANNEL 4
#define SERVO180_2_CHANNEL 5

Adafruit_PWMServoDriver pwm = Adafruit_PWMServoDriver();

// Valores de control para servos de 360 grados
#define SERVO_MIN 75
#define SERVO_MAX 563
#define SERVO_STOP 320      // Detener
int SERVO_SPIN = SERVO_STOP + 30;      // Girar
int SERVO_ANTISPIN = ((SERVO_STOP)-(SERVO_SPIN - SERVO_STOP));  // Girar
#define SERVO_180 480       // 180 grados

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
  Wire.begin(I2C_SDA, I2C_SCL);

  pwm.begin();
  pwm.setPWMFreq(50);

  // Configuración de pines de entrada
  pinMode(ENCODER1_OFFSET_INPUT, INPUT);
  pinMode(ENCODER1_MAIN_INPUT, INPUT);
  pinMode(ENCODER2_OFFSET_INPUT, INPUT);
  pinMode(ENCODER2_MAIN_INPUT, INPUT);

  // Inicializar servos de 180 grados a posición inicial
  pwm.setPWM(SERVO180_1_CHANNEL, 0, SERVO_180);
  pwm.setPWM(SERVO180_2_CHANNEL, 0, SERVO_180);


  Serial.println("Sistema iniciado");
  delay(1000);
}

void loop() {
  for (int numeroRecibido = 1; numeroRecibido <= 7; numeroRecibido++) {
    Serial.println("\n\nNúmero recibido: " + (String) numeroRecibido);
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
      delay(500);
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
    Serial.print("Moviendo a posicion ");
    Serial.println(posicion);

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
  }

  // Activar servo de 180 grados
  activarServo180(servoChannel180);
}

void activarServo180(int servoChannel180) {
  Serial.println("Activando servo de 180 grados");
  pwm.setPWM(servoChannel180, 0, SERVO_MIN);  // Mover a 0 grados
  delay(3000);                                // Esperar 3 segundos
  pwm.setPWM(servoChannel180, 0, SERVO_180);  // Regresar a 180 grados
  Serial.println("Servo de 180 grados completado");
}
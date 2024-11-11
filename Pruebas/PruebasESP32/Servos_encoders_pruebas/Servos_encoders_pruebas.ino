#include <Servo.h>

// Definición de pines - Sección 1
#define SERVO360_1_PIN 11
#define ENCODER_OFFSET_1_PIN 13
#define ENCODER_PRINCIPAL_1_PIN 12
#define SERVO180_1_PIN 10

// Definición de pines - Sección 2
#define SERVO360_2_PIN 6
#define ENCODER_OFFSET_2_PIN 8
#define ENCODER_PRINCIPAL_2_PIN 7
#define SERVO180_2_PIN 5

// Valores de control para servos de 360 grados
#define SERVO_STOP 1500    // Detener
#define SERVO_CW 1600      // Girar en sentido horario

// Variable global para el número a procesar (1-14)
int numeroRecibido = 5; // Cambiar este valor para pruebas

// Creación de objetos Servo
Servo servo360_1;
Servo servo360_2;
Servo servo180_1;
Servo servo180_2;

// Variables para contar pulsos
volatile int pulsosPrincipales = 0;
volatile int pulsosOffset = 0;
bool encontradoOffset = false;

// Variables para el estado anterior de los encoders
int lastStateOffset = HIGH;
int lastStatePrincipal = HIGH;

void setup() {
  // Inicialización de servos
  servo360_1.attach(SERVO360_1_PIN);
  servo360_2.attach(SERVO360_2_PIN);
  servo180_1.attach(SERVO180_1_PIN);
  servo180_2.attach(SERVO180_2_PIN);
  
  // Configuración de pines de entrada
  pinMode(ENCODER_OFFSET_1_PIN, INPUT);
  pinMode(ENCODER_PRINCIPAL_1_PIN, INPUT);
  pinMode(ENCODER_OFFSET_2_PIN, INPUT);
  pinMode(ENCODER_PRINCIPAL_2_PIN, INPUT);
  
  // Detener servos de 360 grados inicialmente
  servo360_1.writeMicroseconds(SERVO_STOP);
  servo360_2.writeMicroseconds(SERVO_STOP);
  
  // Inicializar servos de 180 grados a posición inicial
  servo180_1.write(180);
  servo180_2.write(180);
  
  Serial.begin(9600);
  Serial.println("Sistema iniciado");
  delay(1000);
}

void loop() {
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

void buscarOffset(Servo* servo360, int pinOffset, int seccion) {
  encontradoOffset = false;
  pulsosOffset = 0;
  int currentState;
  
  Serial.print("Buscando offset en seccion ");
  Serial.println(seccion);
  
  servo360->writeMicroseconds(SERVO_CW); // Iniciar movimiento
  
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
      servo360->writeMicroseconds(SERVO_STOP); // Detener servo
      Serial.println("Offset encontrado!");
      delay(500);
    }
  }
}

void procesarSeccion(int seccion, int posicion) {
  Servo* servo360 = (seccion == 1) ? &servo360_1 : &servo360_2;
  Servo* servo180 = (seccion == 1) ? &servo180_1 : &servo180_2;
  int pinOffset = (seccion == 1) ? ENCODER_OFFSET_1_PIN : ENCODER_OFFSET_2_PIN;
  int pinPrincipal = (seccion == 1) ? ENCODER_PRINCIPAL_1_PIN : ENCODER_PRINCIPAL_2_PIN;
  
  pulsosPrincipales = 0;
  int currentState;
  
  // Siempre buscar el offset primero
  buscarOffset(servo360, pinOffset, seccion);
  
  // Si necesitamos movernos a una posición específica (mayor a 1)
  if (posicion > 1) {
    int pulsosObjetivo = posicion - 1;
    Serial.print("Moviendo a posicion ");
    Serial.println(posicion);
    
    servo360->writeMicroseconds(SERVO_CW); // Continuar movimiento
    
    while (pulsosPrincipales < pulsosObjetivo) {
      currentState = digitalRead(pinPrincipal);
      
      // Detectar cambio de HIGH a LOW (flanco descendente)
      if (currentState == LOW && lastStatePrincipal == HIGH) {
        pulsosPrincipales++;
        mostrarPulsos(seccion, false);
      }
      
      lastStatePrincipal = currentState;
    }
    
    servo360->writeMicroseconds(SERVO_STOP); // Detener servo
    Serial.println("Posicion alcanzada!");
  }
  
  // Activar servo de 180 grados
  activarServo180(servo180);
}

void activarServo180(Servo* servo) {
  Serial.println("Activando servo de 180 grados");
  servo->write(0);    // Mover a 0 grados
  delay(3000);        // Esperar 3 segundos
  servo->write(180);  // Regresar a 180 grados
  Serial.println("Servo de 180 grados completado");
}
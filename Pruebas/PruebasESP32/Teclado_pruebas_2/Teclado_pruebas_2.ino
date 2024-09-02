#include <TFT_eSPI.h>

TFT_eSPI tft = TFT_eSPI();  // Crear objeto TFT
TFT_eSprite spritePrincipal = TFT_eSprite(&tft);  // Crear sprite principal
TFT_eSprite spriteSecundario = TFT_eSprite(&tft);  // Crear sprite secundario

void setup() {
    tft.begin();
    tft.setRotation(1);

    // Crear el sprite principal con dimensiones (200x200 píxeles)
    spritePrincipal.createSprite(200, 200);
    spritePrincipal.fillSprite(TFT_WHITE);  // Llenar el fondo del sprite principal con blanco

    // Crear el sprite secundario con dimensiones (100x100 píxeles)
    spriteSecundario.createSprite(100, 100);
    spriteSecundario.fillSprite(TFT_RED);  // Llenar el sprite secundario con rojo

    // Dibujar el sprite secundario en el sprite principal en las coordenadas (0,0)
    spritePrincipal.setPivot(0, 0);  // Establecer el punto de origen del sprite principal
    spriteSecundario.setPivot(0, 0);  // Establecer el punto de origen del sprite secundario
    spritePrincipal.pushToSprite(spriteSecundario, 0, 0);

    // Mostrar el sprite principal en la pantalla en la posición (160, 120)
    spritePrincipal.pushSprite(spritePrincipal, 160 - (spritePrincipal.width() / 2), 120 - (spritePrincipal.height() / 2));
}

void loop() {
    // No se requiere código en loop para este ejemplo
}

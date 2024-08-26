#include "pillulebox.h"

void menuUI() {
  String l[] = { "Wi-Fi", "Iniciar Sesion", "Visualizar contenido", "MAC Address" };
  Lista listaOpciones(l, sizeof(l) / sizeof(l[0]));
  listaOpciones.setTextSize(4);
  int seleccion = listaOpciones.seleccionarLista();
  switch (seleccion) {
    case -1:
      menuUI();
      break;
    case 1:
      reconectar();
      break;
    case 2:
      logInUI();
      break;
    case 3:
      dispenserUI();
      break;
    case 4:
      setBackground(1);
      tft.println("Dirección MAC: " + WiFi.macAddress());
      break;
  }
  delay(5000);
}

void logInUI() {

  setBackground(1);
  tft.setCursor(0, 20);
  tft.println("Username:");
  String username_email = esperarStringSerial();
  tft.setTextSize(3);
  tft.println(username_email);
  tft.setTextSize(4);
  tft.println("\nPassword:");
  String password = esperarStringSerial();
  tft.setTextSize(3);
  tft.println(password);
  String encryptedPassword = encryptPassword(password);
  tft.setTextSize(1);
  tft.println(encryptedPassword);
  tft.setTextSize(4);
  tft.setCursor(0, 300);
  tft.setTextColor(TFT_BLUE);
  if (logIn(username_email, encryptedPassword)) {
    tft.println("usuario autenticado :)");
  } else {
    tft.println("Error de autenticacion :(");
  }
  delay(20000);
}

void dispenserUI() {
}

int Lista::seleccionarLista() {
  int itemsToShow = 10;
  int startIndex = 0;
  tft.setTextSize(textSize);

  int itemSelected = 1;
  /*
  if (stringComplete) {
    Serial.println("Cadena recibida: " + inputString);
    inputString = "";
    stringComplete = false;
  }
  */
 do
 {
  tft.setCursor(0, tft.height() - 40);
    tft.setTextColor(TFT_RED, TFT_BLACK);
    tft.print(BackStatus);
    if (BackStatus)
    {
      delay(3000);
      return -1;
    }
    
    setBackground(1);
    tft.setCursor(0, 20);
    if (UpStatus) itemSelected--;
    if (DownStatus) itemSelected++;

    if (itemSelected < 1) itemSelected = length;
    else if (itemSelected > length) itemSelected = 1;

    if (itemSelected <= startIndex) startIndex = itemSelected - 1;
    if (itemSelected > startIndex + itemsToShow) startIndex = itemSelected - itemsToShow;

    startIndex = max(0, min(startIndex, length - itemsToShow));

    for (int i = 0; i < itemsToShow && (i + startIndex) < length; i++) {
      int currentIndex = i + startIndex;
      if ((currentIndex + 1) == itemSelected) {
        tft.setTextColor(textSelectedColor, textSelectedBackgroundColor);
      } else {
        tft.setTextColor(textColor);
      }

      tft.print(currentIndex + 1);
      Serial.print(currentIndex + 1);

      Serial.print(": ");
      tft.print(": ");

      Serial.println(list[currentIndex]);
      tft.println(list[currentIndex]);
    }

    if (startIndex > 0) {
      tft.setCursor(tft.width() - 20, 0);
      tft.print("^");
    }
    if (startIndex + itemsToShow < length) {
      tft.setCursor(tft.width() - 20, tft.height() - 20);
      tft.print("v");
    }
  while(!UpStatus && !DownStatus && !LeftStatus && !RightStatus && !EnterStatus && !BackStatus){};
 } while (!EnterStatus);

  return itemSelected;
}
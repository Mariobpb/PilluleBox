String esperarBuffer(){
  reiniciarBuffer();
  String res;
  while (!respuestaCompleta()) {}
  res = String(buffer);
  reiniciarBuffer();
  return res;
}
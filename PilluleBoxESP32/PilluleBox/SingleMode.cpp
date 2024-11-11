#include "pillulebox.h"

SingleMode::SingleMode(int id) {
  this->id = id;
}

SingleMode::~SingleMode() {}

int SingleMode::getId() const {
  return id;
}

const char* SingleMode::getMedicineName() const {
  return medicine_name;
}

time_t SingleMode::getDispensingDate() const {
  return dispensing_date;
}

void SingleMode::setId(int id) {
  this->id = id;
}

void SingleMode::setMedicineName(const char* name) {
  strncpy(medicine_name, name, sizeof(medicine_name) - 1);
  medicine_name[sizeof(medicine_name) - 1] = '\0';
}

void SingleMode::setDispensingDate(time_t date) {
  dispensing_date = date;
}
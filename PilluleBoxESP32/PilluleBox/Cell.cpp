#include "pillulebox.h"

Cell::Cell()
  : current_medicine_date(-1), single_mode(nullptr), sequential_mode(nullptr), basic_mode(nullptr) {}

Cell::~Cell() {
  delete single_mode;
  delete sequential_mode;
  delete basic_mode;
}

void Cell::clearOtherModes() {
  delete single_mode;
  single_mode = nullptr;

  delete sequential_mode;
  sequential_mode = nullptr;

  delete basic_mode;
  basic_mode = nullptr;
}


int Cell::getId() const {
  return id;
}

uint8_t Cell::getNumCell() const {
  return num_cell;
}

time_t Cell::getCurrentMedicineDate() const {
  return current_medicine_date;
}

SingleMode* Cell::getSingleMode() const {
  return single_mode;
}

SequentialMode* Cell::getSequentialMode() const {
  return sequential_mode;
}

BasicMode* Cell::getBasicMode() const {
  return basic_mode;
}


void Cell::setId(int id) {
  this->id = id;
}

void Cell::setNumCell(uint8_t num_cell) {
  this->num_cell = num_cell;
}

void Cell::setCurrentMedicineDate(time_t date) {
  this->current_medicine_date = date;
}

void Cell::setSingleMode(SingleMode* mode) {
  clearOtherModes();
  single_mode = mode;
}

void Cell::setSequentialMode(SequentialMode* mode) {
  clearOtherModes();
  sequential_mode = mode;
}

void Cell::setBasicMode(BasicMode* mode) {
  clearOtherModes();
  basic_mode = mode;
}
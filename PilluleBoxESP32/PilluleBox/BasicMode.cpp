#include "pillulebox.h"

BasicMode::BasicMode(int id) {
  this->id = id;
}

BasicMode::~BasicMode() {}

int BasicMode::getId() const {
  return id;
}

const char* BasicMode::getMedicineName() const {
  return medicine_name;
}

tm BasicMode::getMorningStartTime() const {
  return morning_start_time;
}

tm BasicMode::getMorningEndTime() const {
  return morning_end_time;
}

tm BasicMode::getAfternoonStartTime() const {
  return afternoon_start_time;
}

tm BasicMode::getAfternoonEndTime() const {
  return afternoon_end_time;
}

tm BasicMode::getNightStartTime() const {
  return night_start_time;
}

tm BasicMode::getNightEndTime() const {
  return night_end_time;
}

void BasicMode::setId(int id) {
  this->id = id;
}

void BasicMode::setMedicineName(const char* name) {
  strncpy(medicine_name, name, sizeof(medicine_name) - 1);
  medicine_name[sizeof(medicine_name) - 1] = '\0';  // Asegura la terminación nula
}

void BasicMode::setMorningStartTime(const tm& time) {
  morning_start_time = time;
}

void BasicMode::setMorningEndTime(const tm& time) {
  morning_end_time = time;
}

void BasicMode::setAfternoonStartTime(const tm& time) {
  afternoon_start_time = time;
}

void BasicMode::setAfternoonEndTime(const tm& time) {
  afternoon_end_time = time;
}

void BasicMode::setNightStartTime(const tm& time) {
  night_start_time = time;
}

void BasicMode::setNightEndTime(const tm& time) {
  night_end_time = time;
}
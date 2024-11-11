#include "pillulebox.h"

SequentialMode::SequentialMode(int id) {
  this->id = id;
}

SequentialMode::~SequentialMode() {}

int SequentialMode::getId() const {
  return id;
}

const char* SequentialMode::getMedicineName() const {
  return medicine_name;
}

time_t SequentialMode::getStartDate() const {
  return start_date;
}

time_t SequentialMode::getEndDate() const {
  return end_date;
}

tm SequentialMode::getPeriod() const {
  return period;
}

uint8_t SequentialMode::getLimitTimesConsumption() const {
  return limit_times_consumption;
}

bool SequentialMode::getAffectedPeriods() const {
  return affected_periods;
}

uint8_t SequentialMode::getCurrentTimesConsumption() const {
  return current_times_consumption;
}

void SequentialMode::setId(int id) {
  this->id = id;
}

void SequentialMode::setMedicineName(const char* name) {
  strncpy(medicine_name, name, sizeof(medicine_name) - 1);
  medicine_name[sizeof(medicine_name) - 1] = '\0';  // Asegura la terminación nula
}

void SequentialMode::setStartDate(time_t date) {
  start_date = date;
}

void SequentialMode::setEndDate(time_t date) {
  end_date = date;
}

void SequentialMode::setPeriod(const tm& period) {
  this->period = period;
}

void SequentialMode::setLimitTimesConsumption(uint8_t limit) {
  limit_times_consumption = limit;
}

void SequentialMode::setAffectedPeriods(bool affected) {
  affected_periods = affected;
}

void SequentialMode::setCurrentTimesConsumption(uint8_t times) {
  current_times_consumption = times;
}
package Models.ScheduleModes;

import java.io.Serializable;
import java.util.Date;

public class SequentialMode implements Serializable {
    private int id;
    private String medicineName;
    private Date startDate;
    private Date endDate;
    private Date period;
    private int limitTimesConsumption;
    private boolean affectedPeriods;
    private int currentTimesConsumption;

    public SequentialMode(int id, String medicineName, Date startDate) {
        this.id = id;
        this.medicineName = medicineName;
        this.startDate = startDate;
    }

    public SequentialMode(int id, String medicineName, Date startDate,
                          Date endDate, Date period, int limitTimesConsumption,
                          boolean affectedPeriods, int currentTimesConsumption) {
        this.id = id;
        this.medicineName = medicineName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.period = period;
        this.limitTimesConsumption = limitTimesConsumption;
        this.affectedPeriods = affectedPeriods;
        this.currentTimesConsumption = currentTimesConsumption;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public Date getPeriod() { return period; }
    public void setPeriod(Date period) { this.period = period; }
    public int getLimitTimesConsumption() { return limitTimesConsumption; }
    public void setLimitTimesConsumption(int limitTimesConsumption) { this.limitTimesConsumption = limitTimesConsumption; }
    public boolean isAffectedPeriods() { return affectedPeriods; }
    public void setAffectedPeriods(boolean affectedPeriods) { this.affectedPeriods = affectedPeriods; }
    public int getCurrentTimesConsumption() { return currentTimesConsumption; }
    public void setCurrentTimesConsumption(int currentTimesConsumption) { this.currentTimesConsumption = currentTimesConsumption; }
}
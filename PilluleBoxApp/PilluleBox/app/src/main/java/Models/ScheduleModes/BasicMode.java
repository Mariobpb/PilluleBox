package Models.ScheduleModes;

import java.io.Serializable;
import java.util.Date;

public class BasicMode implements Serializable {
    private int id;
    private String mac;
    private String medicineName;
    private Date morningStartTime;
    private Date morningEndTime;
    private Date afternoonStartTime;
    private Date afternoonEndTime;
    private Date nightStartTime;
    private Date nightEndTime;

    public BasicMode(int id, String medicineName) {
        this.id = id;
        this.mac = mac;
        this.medicineName = medicineName;
    }

    public BasicMode(int id, String mac, String medicineName,
                     Date morningStartTime, Date morningEndTime,
                     Date afternoonStartTime, Date afternoonEndTime,
                     Date nightStartTime, Date nightEndTime) {
        this.id = id;
        this.mac = mac;
        this.medicineName = medicineName;
        this.morningStartTime = morningStartTime;
        this.morningEndTime = morningEndTime;
        this.afternoonStartTime = afternoonStartTime;
        this.afternoonEndTime = afternoonEndTime;
        this.nightStartTime = nightStartTime;
        this.nightEndTime = nightEndTime;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getMac() { return mac; }
    public void setMac(String mac) { this.mac = mac; }
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
    public Date getMorningStartTime() { return morningStartTime; }
    public void setMorningStartTime(Date morningStartTime) { this.morningStartTime = morningStartTime; }
    public Date getMorningEndTime() { return morningEndTime; }
    public void setMorningEndTime(Date morningEndTime) { this.morningEndTime = morningEndTime; }
    public Date getAfternoonStartTime() { return afternoonStartTime; }
    public void setAfternoonStartTime(Date afternoonStartTime) { this.afternoonStartTime = afternoonStartTime; }
    public Date getAfternoonEndTime() { return afternoonEndTime; }
    public void setAfternoonEndTime(Date afternoonEndTime) { this.afternoonEndTime = afternoonEndTime; }
    public Date getNightStartTime() { return nightStartTime; }
    public void setNightStartTime(Date nightStartTime) { this.nightStartTime = nightStartTime; }
    public Date getNightEndTime() { return nightEndTime; }
    public void setNightEndTime(Date nightEndTime) { this.nightEndTime = nightEndTime; }
}
package Models.ScheduleModes;

import java.io.Serializable;
import java.sql.Time;

public class BasicMode implements Serializable {
    private int id;
    private String medicineName;
    private Time morningStartTime;
    private Time morningEndTime;
    private Time afternoonStartTime;
    private Time afternoonEndTime;
    private Time nightStartTime;
    private Time nightEndTime;

    public BasicMode(String medicineName,
                     Time morningStartTime, Time morningEndTime,
                     Time afternoonStartTime, Time afternoonEndTime,
                     Time nightStartTime, Time nightEndTime) {
        this.medicineName = medicineName;
        this.morningStartTime = morningStartTime;
        this.morningEndTime = morningEndTime;
        this.afternoonStartTime = afternoonStartTime;
        this.afternoonEndTime = afternoonEndTime;
        this.nightStartTime = nightStartTime;
        this.nightEndTime = nightEndTime;
    }

    public BasicMode(int id, String medicineName,
                     Time morningStartTime, Time morningEndTime,
                     Time afternoonStartTime, Time afternoonEndTime,
                     Time nightStartTime, Time nightEndTime) {
        this.id = id;
        this.medicineName = medicineName;
        this.morningStartTime = morningStartTime;
        this.morningEndTime = morningEndTime;
        this.afternoonStartTime = afternoonStartTime;
        this.afternoonEndTime = afternoonEndTime;
        this.nightStartTime = nightStartTime;
        this.nightEndTime = nightEndTime;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public Time getMorningStartTime() { return morningStartTime; }
    public void setMorningStartTime(Time morningStartTime) { this.morningStartTime = morningStartTime; }

    public Time getMorningEndTime() { return morningEndTime; }
    public void setMorningEndTime(Time morningEndTime) { this.morningEndTime = morningEndTime; }

    public Time getAfternoonStartTime() { return afternoonStartTime; }
    public void setAfternoonStartTime(Time afternoonStartTime) { this.afternoonStartTime = afternoonStartTime; }

    public Time getAfternoonEndTime() { return afternoonEndTime; }
    public void setAfternoonEndTime(Time afternoonEndTime) { this.afternoonEndTime = afternoonEndTime; }

    public Time getNightStartTime() { return nightStartTime; }
    public void setNightStartTime(Time nightStartTime) { this.nightStartTime = nightStartTime; }

    public Time getNightEndTime() { return nightEndTime; }
    public void setNightEndTime(Time nightEndTime) { this.nightEndTime = nightEndTime; }
}
package Models.ScheduleModes;

import java.io.Serializable;
import java.util.Date;

public class SingleMode implements Serializable {
    private int id;
    private String medicineName;
    private Date dispensingDate;

    public SingleMode(String medicineName, Date dispensingDate) {
        this.medicineName = medicineName;
        this.dispensingDate = dispensingDate;
    }
    public SingleMode(int id, String medicineName, Date dispensingDate) {
        this.id = id;
        this.medicineName = medicineName;
        this.dispensingDate = dispensingDate;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
    public Date getDispensingDate() { return dispensingDate; }
    public void setDispensingDate(Date dispensingDate) { this.dispensingDate = dispensingDate; }
}
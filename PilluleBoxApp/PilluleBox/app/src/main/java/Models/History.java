package Models;

public class History {
    private int id;
    private String macDispenser;
    private String medicineName;
    private String consumptionStatus;
    private String dateConsumption;
    private String reason;

    public History() {}

    public History(int id, String macDispenser, String medicineName, String consumptionStatus,
                   String dateConsumption, String reason) {
        this.id = id;
        this.macDispenser = macDispenser;
        this.medicineName = medicineName;
        this.consumptionStatus = consumptionStatus;
        this.dateConsumption = dateConsumption;
        this.reason = reason;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMacDispenser() {
        return macDispenser;
    }

    public void setMacDispenser(String macDispenser) {
        this.macDispenser = macDispenser;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getConsumptionStatus() {
        return consumptionStatus;
    }

    public void setConsumptionStatus(String consumptionStatus) {
        this.consumptionStatus = consumptionStatus;
    }

    public String getDateConsumption() {
        return dateConsumption;
    }

    public void setDateConsumption(String dateConsumption) {
        this.dateConsumption = dateConsumption;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "History{" +
                "id=" + id +
                ", macDispenser='" + macDispenser + '\'' +
                ", medicineName='" + medicineName + '\'' +
                ", consumptionStatus='" + consumptionStatus + '\'' +
                ", dateConsumption='" + dateConsumption + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}
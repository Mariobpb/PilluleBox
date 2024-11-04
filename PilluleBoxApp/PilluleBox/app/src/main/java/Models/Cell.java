package Models;

import java.util.Date;

public class Cell {
    private int id;
    private String macDispenser;
    private int numCell;
    private Date currentMedicineDate;
    private Integer singleModeId;
    private Integer sequentialModeId;
    private Integer basicModeId;


    public Cell(int id, String macDispenser, int numCell, Date currentMedicineDate,
                Integer singleModeId, Integer sequentialModeId, Integer basicModeId) {
        this.id = id;
        this.macDispenser = macDispenser;
        this.numCell = numCell;
        this.currentMedicineDate = currentMedicineDate;
        this.singleModeId = singleModeId;
        this.sequentialModeId = sequentialModeId;
        this.basicModeId = basicModeId;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getMacDispenser() {
        return macDispenser;
    }

    public int getNumCell() {
        return numCell;
    }

    public Date getCurrentMedicineDate() {
        return currentMedicineDate;
    }

    public Integer getSingleModeId() {
        return singleModeId;
    }

    public Integer getSequentialModeId() {
        return sequentialModeId;
    }

    public Integer getBasicModeId() {
        return basicModeId;
    }
}
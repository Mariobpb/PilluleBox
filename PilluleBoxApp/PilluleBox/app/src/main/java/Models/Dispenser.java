package Models;

public class Dispenser {
    private String mac;
    private String name;
    private int context;

    public Dispenser(String mac, String name, int context) {
        this.mac = mac;
        this.name = name;
        this.context = context;
    }

    // Getters
    public String getMac() {
        return mac;
    }

    public String getName() {
        return name;
    }

    public int getContextDispenser() {
        return context;
    }

    // Setters
    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContextDispenser(int context) {
        this.context = context;
    }
}
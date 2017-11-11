package unal.edu.co.controlcar.models;

/**
 * Created by erick on 1/11/2017.
 */

public class Alert {

    private String initHour;
    private String description;
    private double velocity;
    private double latitude;
    private double longitude;
    private int holeLevel;

    public Alert(String initHour,String description, double velocity, double latitude, double longitude, int holeLevel) {
        this.initHour = initHour;
        this.description = description;
        this.velocity = velocity;
        this.latitude = latitude;
        this.longitude = longitude;
        this.holeLevel = holeLevel;
    }

    public String getInitHour() {
        return initHour;
    }

    public void setInitHour(String initHour) {
        this.initHour = initHour;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getHoleLevel() {
        return holeLevel;
    }

    public void setHoleLevel(int holeLevel) {
        this.holeLevel = holeLevel;
    }
}

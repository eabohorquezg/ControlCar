package unal.edu.co.controlcar.models;

/**
 * Created by erick on 1/11/2017.
 */

public class Alert {

    private String id;
    private String description;
    private double velocity;
    private double latitude;
    private double longitude;
    private int holeLevel;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

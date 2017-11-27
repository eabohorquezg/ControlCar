package unal.edu.co.controlcar.models;

/**
 * Created by erick on 26/10/2017.
 */

public class Travel {

    private String id;
    private String initHour;
    private String endTime;
    private double initLatitude;
    private double initLongitude;
    private String plate;
    private String driverName;
    private int requestLocation;
    private String curLong;
    private String curLat;

    public int getRequestLocation() {
        return requestLocation;
    }

    public String getCurLong() {
        return curLong;
    }

    public String getCurLat() {
        return curLat;
    }

    public void setRequestLocation(int requestLocation) {
        this.requestLocation = requestLocation;
    }

    public void setCurLong(String curLong) {
        this.curLong = curLong;
    }

    public void setCurLat(String curLat) {
        this.curLat = curLat;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInitHour() {
        return initHour;
    }

    public void setInitHour(String initHour) {
        this.initHour = initHour;
    }

    public double getInitLatitude() {
        return initLatitude;
    }

    public void setInitLatitude(double initLatitude) {
        this.initLatitude = initLatitude;
    }

    public double getInitLongitude() {
        return initLongitude;
    }

    public void setInitLongitude(double initLongitude) {
        this.initLongitude = initLongitude;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
}

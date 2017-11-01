package unal.edu.co.controlcar.models;

import java.util.ArrayList;

/**
 * Created by erick on 1/11/2017.
 */

public class Report {

    private String id;
    private String idTravel;
    private ArrayList<Alert> alerts;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdTravel() {
        return idTravel;
    }

    public void setIdTravel(String idTravel) {
        this.idTravel = idTravel;
    }

    public ArrayList<Alert> getAlerts() {
        return alerts;
    }

    public void setAlerts(ArrayList<Alert> alerts) {
        this.alerts = alerts;
    }
}

package com.metalarm.model;

/**
 * Created by qtm-purvesh on 18/4/16.
 */
public class tableStationModel {

    public String name;
    public String station_lat;
    public String station_lon;
    public String towards_lat;
    public String towards_lon;
    public String opp_lat;
    public String opp_lon;
    public String line;
    public int id;
    int a_id;

    public tableStationModel(String name,
                             String station_lat,
                             String station_lon,
                             String towards_lat,
                             String towards_lon,
                             String opp_lat,
                             String opp_lon,
                             String line) {
        this.line = line;
        this.name = name;
        this.opp_lat = opp_lat;
        this.opp_lon = opp_lon;
        this.station_lat = station_lat;
        this.towards_lat = towards_lat;
        this.station_lon = station_lon;
        this.towards_lon = towards_lon;

    }

    public tableStationModel() {

    }

    public String getTowardsLon() {
        return towards_lon;
    }

    public void setTowardsLon(String towards_lon) {
        this.towards_lon = towards_lon;
    }

    public String getStationLon() {
        return station_lon;
    }

    public void setStationLon(String station_lon) {
        this.station_lon = station_lon;
    }


    public String getTowardsLat() {
        return towards_lat;
    }

    public void setTowardsLat(String towards_lat) {
        this.towards_lat = towards_lat;
    }


    public String getStationLat() {
        return station_lat;
    }

    public void setStationLat(String station_lat) {
        this.station_lat = station_lat;
    }


    public String getOppLon() {
        return opp_lon;
    }

    public void setOppLon(String opp_lon) {
        this.opp_lon = opp_lon;
    }

    public String getOppLat() {
        return opp_lat;
    }

    public void setOppLat(String opp_lat) {
        this.opp_lat = opp_lat;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAlarmID() {
        return a_id;
    }

    public void setAlarmID(int a_id) {
        this.a_id = a_id;
    }
}

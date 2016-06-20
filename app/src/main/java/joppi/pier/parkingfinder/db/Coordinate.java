package joppi.pier.parkingfinder.db;

/**
 * Created by christian on 29/05/16.
 */

public class Coordinate {

    private int id_parking ;
    private double latitude;
    private double longitude;

    public Coordinate(int id_parking, double latitude, double longitude){
        this.id_parking = id_parking;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public int getId_parking() {
        return id_parking;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}

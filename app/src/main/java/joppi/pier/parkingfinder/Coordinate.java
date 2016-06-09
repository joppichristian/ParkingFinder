package joppi.pier.parkingfinder;

/**
 * Created by christian on 29/05/16.
 */

public class Coordinate {

    private int id_parking ;
    private long latitude;
    private long longitude;

    public Coordinate(int id_parking, long latitude, long longitude){
        this.id_parking = id_parking;
        this.longitude = longitude;
        this.latitude = latitude;
    }


    public int getId_parking() {
        return id_parking;
    }

    public long getLatitude() {
        return latitude;
    }

    public long getLongitude() {
        return longitude;
    }
}

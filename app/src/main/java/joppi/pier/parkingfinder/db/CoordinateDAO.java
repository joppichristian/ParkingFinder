package joppi.pier.parkingfinder.db;

import java.util.ArrayList;

/**
 * Created by christian on 29/05/16.
 */
public interface CoordinateDAO {
    public void open();
    public void close();

    public Coordinate insertCoordinate(Coordinate coordinate);
    public boolean clear();
    public ArrayList<Coordinate> getCoordinateOfParking(int id_parking);
}


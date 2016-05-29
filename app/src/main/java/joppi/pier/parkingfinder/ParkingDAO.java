package joppi.pier.parkingfinder;

import java.util.ArrayList;

/**
 * Created by christian on 29/05/16.
 */
public interface ParkingDAO {
    public void open();
    public void close();

    public ArrayList<Parking> getAllParking();
}


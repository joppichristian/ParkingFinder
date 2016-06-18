package joppi.pier.parkingfinder;

import java.util.ArrayList;

/**
 * Created by christian on 29/05/16.
 */
public interface ParkingDAO {
    public void open();
    public void close();

    public Parking insertParking(Parking parking);
    public boolean clear();
    public ArrayList<Parking> getAllParking();
    public Parking getParking(int id);
}


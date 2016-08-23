package joppi.pier.parkingfinder.db;

import android.location.Location;

import java.util.ArrayList;

public interface ParkingDAO
{
	public void open();

	public void close();

	public Parking insertParking(Parking parking);

	public boolean clear();

	public ArrayList<Parking> getParkingList(Location currLocation, double kmRadius,String vehicle);

	public Parking getParking(int id);
}

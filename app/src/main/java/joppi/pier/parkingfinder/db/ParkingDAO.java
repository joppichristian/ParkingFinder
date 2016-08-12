package joppi.pier.parkingfinder.db;

import java.util.ArrayList;

public interface ParkingDAO
{
	public void open();

	public void close();

	public Parking insertParking(Parking parking);

	public boolean clear();

	public ArrayList<Parking> getParkingList();

	public Parking getParking(int id);
}

package joppi.pier.parkingfinder.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

import java.util.ArrayList;

import joppi.pier.parkingfinder.AppUtils;
import joppi.pier.parkingfinder.ParkingFinderApplication;

public class ParkingDAO_DB_impl implements ParkingDAO
{
	private MySQLiteHelper helper;
	private SQLiteDatabase database;

	@Override
	public void open()
	{
		if(helper == null)
			helper = new MySQLiteHelper(ParkingFinderApplication.getAppContext());
		database = helper.getWritableDatabase();
	}

	@Override
	public void close()
	{
		helper.close();
	}

	@Override
	public Parking insertParking(Parking parking)
	{
		long insertId = database.insert(MySQLiteHelper.TABLE_NAME, null, parking.getContentValues());
		Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME, null, MySQLiteHelper.COLUMN_ID + " = ?", new String[]{"" + insertId}, null, null, null);
		cursor.moveToFirst();
		Parking p = Parking.Parse(cursor);
		cursor.close();
		return p;
	}

	@Override
	public boolean clear()
	{
		int retVal = database.delete(MySQLiteHelper.TABLE_NAME, null, null);
		return (retVal != 0);
	}

	@Override
	public ArrayList<Parking> getParkingList(Location currLocation, double kmRadius,String vehicle){
		// filter parkinglist (show only near ones)
		// (filter by query first (square area latlng)

		double geoDegDist = AppUtils.getGeoDegDistance((kmRadius/2.0) * 1000.0);

		String latMin = String.valueOf(currLocation.getLatitude() - geoDegDist);
		String latMax = String.valueOf(currLocation.getLatitude() + geoDegDist);
		String lngMin = String.valueOf(currLocation.getLongitude() - geoDegDist);
		String lngMax = String.valueOf(currLocation.getLongitude() + geoDegDist);

		// Search radius filter
		String whereClause = MySQLiteHelper.COLUMN_LATITUDE+">? AND "+MySQLiteHelper.COLUMN_LATITUDE+"<? AND "+MySQLiteHelper.COLUMN_LONGITUDE+">? AND "+MySQLiteHelper.COLUMN_LONGITUDE+"<?";


        Log.w("VEHICLE",vehicle);
        // Vehicle filter
        switch (vehicle){
            case "Automobile":whereClause += " AND "+ MySQLiteHelper.COLUMN_CAR + " > 0";break;
            case "Moto":whereClause += " AND "+ MySQLiteHelper.COLUMN_MOTO + " > 0";break;
            case "Caravan":whereClause += " AND "+ MySQLiteHelper.COLUMN_CARAVAN + " > 0";break;
        }

		String[] whereArgs = new String[] {
				latMin,latMax,
				lngMin, lngMax
		};

		ArrayList<Parking> parking = new ArrayList<>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME, null, whereClause, whereArgs, null, null, null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast())
		{
			Parking park = Parking.Parse(cursor);
			parking.add(park);
			cursor.moveToNext();
		}
		cursor.close(); // Remember to always close the cursor!
		return parking;
	}

	@Override
	public Parking getParking(int id)
	{
		Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME, null, MySQLiteHelper.COLUMN_ID + " = ? ", new String[]{"" + id}, null, null, null);
		cursor.moveToFirst();

		Parking parking = Parking.Parse(cursor);
		cursor.close(); // Remember to always close the cursor!
		return parking;
	}
}

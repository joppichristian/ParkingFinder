package joppi.pier.parkingfinder.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import joppi.pier.parkingfinder.ParkingFinderApplication;

public class ParkingDAO_DB_impl implements ParkingDAO
{
	private MySQLiteHelper helper;
	private SQLiteDatabase database;
	private String[] allColumns = {
			MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_NAME,
			MySQLiteHelper.COLUMN_COST,
			MySQLiteHelper.COLUMN_TIME_LIMIT,
			MySQLiteHelper.COLUMN_NOTES,
			MySQLiteHelper.COLUMN_TYPE,
			MySQLiteHelper.COLUMN_LOCATION,
			MySQLiteHelper.COLUMN_AREA,
			MySQLiteHelper.COLUMN_TIME_FRAME,
			MySQLiteHelper.COLUMN_CAR,
			MySQLiteHelper.COLUMN_MOTO,
			MySQLiteHelper.COLUMN_CARAVAN
	};

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
		Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME, allColumns, MySQLiteHelper.COLUMN_ID + " = ?", new String[]{"" + insertId}, null, null, null);
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
	public ArrayList<Parking> getParkingList()
	{
		ArrayList<Parking> parking = new ArrayList<Parking>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME, allColumns, null, null, null, null, null);
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
		Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME,
				allColumns, MySQLiteHelper.COLUMN_ID + " = ? ", new String[]{"" + id}, null, null, null);
		cursor.moveToFirst();

		Parking parking = Parking.Parse(cursor);
		cursor.close(); // Remember to always close the cursor!
		return parking;
	}
}

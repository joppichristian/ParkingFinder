package joppi.pier.parkingfinder.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

//    Parking
//    {
//        -id
//        -name
//        -cost (string)
//        -time_limit (string)
//        -notes (string)
//        -places -> Places
//        -type (Surface, Structure, Surveiled, ...)
//        -location (LatLng)
//        -area (LatLng,LatLng)
//        -time_frame ( Open,Close )
//    }
//
public class Parking
{
	public static int TYPE_SURFACE = 0x00000001;
	public static int TYPE_STRUCTURE = 0x0000002;
	public static int TYPE_ROAD = 0x0000004;
	public static int TYPE_SUBTERRANEAN = 0x00000008;
	public static int TYPE_SURVEILED = 0x00000010;

	private int id;
	private String name;
	//cost = costo:durata:fascia:giorni
	private String cost;

	private String timeLimit;
	private int type;
	private String notes;
	private String latitude;
	private String longitude;
	private String area;
	private String timeFrame;
	private int car;
	private int moto;
	private int caravan;

	private int currDistance;
	private double currRank;

	public Parking(int id, String name, String cost, String timeLimit, int type, String notes, String latitude, String longitude, String area, String timeFrame, int car, int moto, int caravan)
	{
		this.id = id;
		this.name = name;
		this.cost = cost;
		this.timeLimit = timeLimit;
		this.type = type;
		this.notes = notes;
		this.latitude = latitude;
		this.longitude = longitude;
		this.area = area;
		this.timeFrame = timeFrame;
		this.car = car;
		this.moto = moto;
		this.caravan = caravan;
		this.currDistance = -1;
		this.currRank = -1.0;
	}

	public int getId()
	{
		return this.id;
	}

	public String getName()
	{
		return this.name;
	}

	public String getCostRaw(){return cost;}

	// TODO: temporary implementation
	public double getCost()
	{
		try{
			return Double.parseDouble(cost);
		}catch(Exception e){}
		return 0.0;
	}

	public String getTimeLimit(){return timeLimit;}

	public int getType()
	{
		return type;
	}

	public String getNotes()
	{
		return notes;
	}

	public String getLatitudeRaw(){return latitude;}

	public String getLongitudeRaw(){return longitude;}

	public LatLng getLocation()
	{
		try{
			return new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
		}catch(Exception e){}
		return null;
	}

	public String getAreaRaw()
	{
		return area;
	}

	public String getTimeFrame()
	{
		return timeFrame;
	}

	public int getCar()
	{
		return car;
	}

	public int getMoto()
	{
		return moto;
	}

	public int getCaravan()
	{
		return caravan;
	}

	public int getCurrDistance()
	{
		return currDistance;
	}

	public double getCurrRank(){ return currRank; }

	public void setCurrDistance(int currDistance)
	{
		this.currDistance = currDistance;
	}

	public void setCurrRank(double rank){ currRank = rank; }

	@Override
	public String toString()
	{
		return name;
	}

	public ContentValues getContentValues()
	{
		ContentValues content = new ContentValues();

		content.put(MySQLiteHelper.COLUMN_NAME, name);
		content.put(MySQLiteHelper.COLUMN_COST, cost);
		content.put(MySQLiteHelper.COLUMN_TIME_LIMIT, timeLimit);
		content.put(MySQLiteHelper.COLUMN_NOTES, notes);
		content.put(MySQLiteHelper.COLUMN_TYPE, type);
		content.put(MySQLiteHelper.COLUMN_LATITUDE, latitude);
		content.put(MySQLiteHelper.COLUMN_LONGITUDE, longitude);
		content.put(MySQLiteHelper.COLUMN_AREA, area);
		content.put(MySQLiteHelper.COLUMN_TIME_FRAME, timeFrame);
		content.put(MySQLiteHelper.COLUMN_CAR, car);
		content.put(MySQLiteHelper.COLUMN_MOTO, moto);
		content.put(MySQLiteHelper.COLUMN_CARAVAN, caravan);
		return content;
	}

	public static Parking Parse(Cursor cursor)
	{
		int i=0;
		int id = cursor.getInt(i++);
		String name = cursor.getString(i++);
		String cost = cursor.getString(i++);
		String timeLimit = cursor.getString(i++);
		String notes = cursor.getString(i++);
		int type = cursor.getInt(i++);
		String latitude = cursor.getString(i++);
		String longitude = cursor.getString(i++);
		String area = cursor.getString(i++);
		String timeFrame = cursor.getString(i++);
		int car = cursor.getInt(i++);
		int moto = cursor.getInt(i++);
		int caravan = cursor.getInt(i++);

		return new Parking(id, name, cost, timeLimit, type, notes, latitude, longitude, area, timeFrame, car, moto, caravan);
	}

	public static ArrayList<LatLng> parseCoordinates(String coordinates)
	{
		ArrayList<LatLng> ar = new ArrayList<>();
		for(String s : coordinates.split(";")){
			ar.add(new LatLng(Double.parseDouble(s.split(",")[0]), Double.parseDouble(s.split(",")[1])));
		}
		return ar;
	}
}

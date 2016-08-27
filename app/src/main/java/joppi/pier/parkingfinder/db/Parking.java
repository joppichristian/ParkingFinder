package joppi.pier.parkingfinder.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

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
    public static int TYPE_MASK = 0xFFFF;
	public static int TYPE_SURFACE = 0x0001;
	public static int TYPE_STRUCTURE = 0x0002;
	public static int TYPE_ROAD = 0x0004;
	public static int TYPE_SUBTERRANEAN = 0x0008;

    public static int SPEC_MASK = 0xFFFF0000;
	public static int SPEC_SURVEILED = 0x10000;
    public static int SPEC_TIME_LIMIT = 0x20000;

	private int id;
	private String name;
	//cost = costo:durata:fascia:giorni;
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

	private int currDistByCar;
	private int currDurationCar;
	private int currDistByFoot;
	private double currRank;
	private String address;

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
		this.currDistByCar = -1;
		this.currDurationCar = -1;
		this.currDistByFoot = -1;
		this.currRank = -1.0;
		this.address = "";
	}

	public int getId()
	{
		return this.id;
	}

	public String getName()
	{
		return this.name;
	}

	public String getCostRaw()
	{
		return cost;
	}

	// TODO: temporary implementation
	public double getCost(String start, String stop, int today_number)
	{
		ArrayList<String> costsList = new ArrayList<>();
		costsList.addAll(Arrays.asList(cost.split(";")));
		double res = 0.0;
		SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
		SimpleDateFormat dateFormat2 = new SimpleDateFormat("hh.mm");

		for(String c : costsList)
		{
			try
			{
				Date date_start = dateFormat.parse(start);
				Date date_stop = dateFormat.parse(stop);
				if(date_start.after(date_stop))
					date_stop.setDate(date_stop.getDate() + 1);
				long tmp_diff = (date_stop.getTime() - date_start.getTime()) / 60000;
				int diff = tmp_diff % 60 != 0 ? (int) (tmp_diff + 60) / 60 : (int) (tmp_diff / 60);
				ArrayList<String> days = new ArrayList<>();
				days.addAll(Arrays.asList(c.split(":")[3].split(",")));
				Date fascia_start = dateFormat2.parse(c.split(":")[2].split(",")[0]);
				Date fascia_stop = dateFormat2.parse(c.split(":")[2].split(",")[1]);

				if(days.contains(today_number + "") && diff > Integer.parseInt(c.split(":")[1]))
				{
					double costPerHour = Double.parseDouble(c.split(":")[0]);
					if(date_start.getTime() - fascia_start.getTime() <= 0 && date_stop.getTime() - fascia_start.getTime() <= 0)
						res += 0.0;
					else if(date_start.getTime() - fascia_start.getTime() <= 0 && date_stop.getTime() - fascia_stop.getTime() <= 0)
						res += costPerHour * diff - ((fascia_start.getTime() - date_start.getTime()) / (1000 * 60 * 60)) * costPerHour;
					else if(date_start.getTime() - fascia_start.getTime() > 0 && date_stop.getTime() - fascia_stop.getTime() < 0)
						res += costPerHour * diff;
					else if(date_start.getTime() - fascia_start.getTime() > 0 && date_stop.getTime() - fascia_stop.getTime() >= 0)
						res += costPerHour * diff - ((date_stop.getTime() - fascia_stop.getTime()) / (1000 * 60 * 60)) * costPerHour;
					else if(date_start.getTime() - fascia_stop.getTime() >= 0 && date_stop.getTime() - fascia_stop.getTime() >= 0)
						res += 0.0;
				}


			}catch(Exception ex){
				Log.e("ParseCostError", ex.toString());
			}
		}

		return (double) Math.round(res * 100) / 100;
	}

	public String getTimeLimit()
	{
		return timeLimit;
	}

	public int getType()
	{
		return type;
	}

	public String getNotes()
	{
		return notes;
	}

	public String getLatitudeRaw()
	{
		return latitude;
	}

	public String getLongitudeRaw()
	{
		return longitude;
	}

	public LatLng getLocation()
	{
		try{
			return new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
		}catch(Exception e){
		}
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

	public int getCurrDistByCar()
	{
		return currDistByCar;
	}

	public int getCurrDistByFoot() { return currDistByFoot; }

	public int getCurrDurationCar() { return currDurationCar; }

	public double getCurrRank()
	{
		return currRank;
	}

	public void setCurrDistByCar(int currDistByCar)
	{
		this.currDistByCar = currDistByCar;
	}

	public void setCurrDistByFoot(int duration)
	{
		this.currDistByFoot = duration;
	}

	public void setCurrDurationCar(int duration)
	{
		this.currDurationCar = duration;
	}

	public void setCurrRank(double rank)
	{
		currRank = rank;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

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
		int i = 0;
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

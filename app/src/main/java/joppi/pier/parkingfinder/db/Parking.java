package joppi.pier.parkingfinder.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
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
	public static int TYPE_SURFACE = 0x00000001;
	public static int TYPE_STRUCTURE = 0x0000002;
	public static int TYPE_ROAD = 0x0000004;
	public static int TYPE_SUBTERRANEAN = 0x00000008;
	public static int TYPE_SURVEILED = 0x00000010;

	private int id;
	private String name;
	//cost = costo:durata:fascia:giorni;
	private String cost;

	private String timeLimit;
	private int type;
	private String notes;
	private String location;
	private String area;
	private String timeFrame;
	private int car;
	private int moto;
	private int caravan;

	private int currDistance;

	private double currPriceRank;
	private double currDistanceRank;

	public Parking(int id, String name, String cost, String timeLimit, int type, String notes, String location, String area, String timeFrame, int car, int moto, int caravan)
	{
		this.id = id;
		this.name = name;
		this.cost = cost;
		this.timeLimit = timeLimit;
		this.type = type;
		this.notes = notes;
		this.location = location;
		this.area = area;
		this.timeFrame = timeFrame;
		this.car = car;
		this.moto = moto;
		this.caravan = caravan;
		this.currDistance = -1;
		this.currPriceRank = -1.0;
		this.currDistanceRank = -1.0;
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
	public double getCost(String start,String stop, int today_number )
	{
		ArrayList<String> costsList = new ArrayList<>();
		costsList.addAll(Arrays.asList(cost.split(";")));
        double res = 0.0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("hh.mm");
        Log.w("FINEEE:", stop);
		for (String c: costsList ) {
			try {
				Date date_start = dateFormat.parse(start);
				Date date_stop = dateFormat.parse(stop);
                if(date_start.after(date_stop))
                    date_stop.setDate(date_stop.getDate()+1);
                long tmp_diff = (date_stop.getTime() - date_start.getTime())/60000;
				int diff = tmp_diff % 60 != 0 ? (int)(tmp_diff+60)/60 : (int)(tmp_diff/60);
				ArrayList<String> days = new ArrayList<>();
				days.addAll(Arrays.asList(c.split(":")[3].split(",")));
                Date fascia_start = dateFormat2.parse(c.split(":")[2].split(",")[0]);
                Date fascia_stop = dateFormat2.parse(c.split(":")[2].split(",")[1]);


				if(days.contains(today_number+"") && diff>Integer.parseInt(c.split(":")[1]))
				{
                    double costPerHour = Double.parseDouble(c.split(":")[0]);
                    if(date_start.getTime() - fascia_start.getTime() <= 0  && date_stop.getTime() - fascia_start.getTime() <= 0)
                        res += 0.0;
                    else if(date_start.getTime() - fascia_start.getTime() <= 0  && date_stop.getTime() - fascia_stop.getTime() <= 0)
                        res += costPerHour *diff - ((fascia_start.getTime() - date_start.getTime())/(1000 * 60 * 60))*costPerHour;
                    else if(date_start.getTime() - fascia_start.getTime() > 0  && date_stop.getTime() - fascia_stop.getTime() < 0)
                        res += costPerHour *diff;
                    else if(date_start.getTime() - fascia_start.getTime() > 0  && date_stop.getTime() - fascia_stop.getTime() >= 0)
                        res += costPerHour *diff - ((date_stop.getTime() - fascia_stop.getTime())/(1000 * 60 * 60))*costPerHour;
                    else if(date_start.getTime() - fascia_stop.getTime() >= 0  && date_stop.getTime() - fascia_stop.getTime() >= 0)
                        res += 0.0;
				}


			}catch (Exception ex){
                Log.e("ParseCostError",ex.toString());
			}
		}
		return (double)Math.round(res * 100) / 100;
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

	public String getLocationRaw(){return location;}

	public LatLng getLocation()
	{
		try{
			String[] locStr = location.split(",");
			return new LatLng(Double.parseDouble(locStr[0]), Double.parseDouble(locStr[1]));
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

	public double getCurrentPriceRank(){ return currPriceRank; }

	public double getCurrentDistanceRank(){ return currDistanceRank; }

	public void setCurrDistance(int currDistance)
	{
		this.currDistance = currDistance;
	}

	public void setCurrPriceRank(double rank){ currPriceRank = rank; }

	public void setCurrDistRank(double rank){ currDistanceRank = rank; }

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
		content.put(MySQLiteHelper.COLUMN_LOCATION, location);
		content.put(MySQLiteHelper.COLUMN_AREA, area);
		content.put(MySQLiteHelper.COLUMN_TIME_FRAME, timeFrame);
		content.put(MySQLiteHelper.COLUMN_CAR, car);
		content.put(MySQLiteHelper.COLUMN_MOTO, moto);
		content.put(MySQLiteHelper.COLUMN_CARAVAN, caravan);
		return content;
	}

	public static Parking Parse(Cursor cursor)
	{
		int id = cursor.getInt(0);
		String name = cursor.getString(1);
		String cost = cursor.getString(2);
		String timeLimit = cursor.getString(3);
		String notes = cursor.getString(4);
		int type = cursor.getInt(5);
		String location = cursor.getString(6);
		String area = cursor.getString(7);
		String timeFrame = cursor.getString(8);
		int car = cursor.getInt(9);
		int moto = cursor.getInt(10);
		int caravan = cursor.getInt(11);

		return new Parking(id, name, cost, timeLimit, type, notes, location, area, timeFrame, car, moto, caravan);
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

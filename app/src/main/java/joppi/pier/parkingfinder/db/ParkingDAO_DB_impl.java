package joppi.pier.parkingfinder.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import joppi.pier.parkingfinder.ParkingFinderApplication;

/**
 * Created by christian on 30/05/16.
 */

public class ParkingDAO_DB_impl implements ParkingDAO {


    private MySQLiteHelper helper;
    private SQLiteDatabase database;
    private String [] allColumns = {
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
    public void open() {
        if(helper == null)
            helper = new MySQLiteHelper(ParkingFinderApplication.getAppContext());
        database = helper.getWritableDatabase();
    }

    @Override
    public void close() {
        helper.close();
    }

    @Override
    public Parking insertParking(Parking parking) {
        long insertId = database.insert(MySQLiteHelper.TABLE_NAME,null,parkingToValues(parking));
        Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME, allColumns, MySQLiteHelper.COLUMN_ID + " = ?" , new String[] {""+insertId}, null, null, null);
        cursor.moveToFirst();
        Parking p=valuesToParking(cursor);
        cursor.close();
        return p;
    }

    @Override
    public boolean clear() {
        database.delete(MySQLiteHelper.TABLE_NAME,null,null);
        return true;
    }


    private ContentValues parkingToValues(Parking parking){
        ContentValues content = new ContentValues();



        content.put(MySQLiteHelper.COLUMN_NAME,parking.getName());
        content.put(MySQLiteHelper.COLUMN_COST, parking.getCost());
        content.put(MySQLiteHelper.COLUMN_TIME_LIMIT, parking.getTime_limit());
        content.put(MySQLiteHelper.COLUMN_NOTES, parking.getNotes());
        content.put(MySQLiteHelper.COLUMN_TYPE, parking.getType());
        content.put(MySQLiteHelper.COLUMN_LOCATION, parking.getLocation());
        content.put(MySQLiteHelper.COLUMN_AREA, parking.getArea());
        content.put(MySQLiteHelper.COLUMN_TIME_FRAME, parking.getTime_frame());
        content.put(MySQLiteHelper.COLUMN_CAR, parking.getCar());
        content.put(MySQLiteHelper.COLUMN_MOTO, parking.getMoto());
        content.put(MySQLiteHelper.COLUMN_CARAVAN, parking.getCaravan());
        return content;
    }

    private Parking valuesToParking(Cursor cursor){
        int id = cursor.getInt(0);
        String name = cursor.getString(1);
        String cost = cursor.getString(2);
        String time_limit = cursor.getString(3);
        String notes = cursor.getString(4);
        int type = cursor.getInt(5);
        String location = cursor.getString(6);
        String area = cursor.getString(7);
        String time_frame = cursor.getString(8);
        int car = cursor.getInt(9);
        int moto = cursor.getInt(10);
        int caravan = cursor.getInt(11);

        return  new Parking(id,name,cost,time_limit,type,notes,location,area,time_frame,car,moto,caravan);
    }
    @Override
    public ArrayList<Parking> getAllParking() {
        ArrayList<Parking> parking = new ArrayList<Parking>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Parking park = valuesToParking(cursor);
            parking.add(park);
            cursor.moveToNext();
        }
        cursor.close(); // Remember to always close the cursor!
        return parking;
    }

    @Override
    public Parking getParking(int id) {
        Parking parking = null;
        Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME,
                allColumns, MySQLiteHelper.COLUMN_ID + " = ? ",  new String[]{""+id},null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            parking = valuesToParking(cursor);
            cursor.moveToNext();
        }
        cursor.close(); // Remember to always close the cursor!
        return parking;    }
}

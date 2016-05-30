package joppi.pier.parkingfinder;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by christian on 30/05/16.
 */

public class ParkingDAO_DB_impl implements ParkingDAO {

    private MySQLiteHelper helper;
    private SQLiteDatabase database;
    private String [] allColumns = {
            MySQLiteHelper.COLUMN_COST,
            MySQLiteHelper.COLUMN_NAME,
            MySQLiteHelper.COLUMN_LONGITUDE,
            MySQLiteHelper.COLUMN_LATIDUTE,
            MySQLiteHelper.COLUMN_ID
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

    private ContentValues parkingToValues(Parking parking){
        ContentValues content = new ContentValues();
        content.put(MySQLiteHelper.COLUMN_NAME,parking.getName());
        content.put(MySQLiteHelper.COLUMN_LATIDUTE,parking.getLatitude());
        content.put(MySQLiteHelper.COLUMN_LONGITUDE,parking.getLongitude());
        content.put(MySQLiteHelper.COLUMN_COST, parking.getCost());
        return content;
    }

    private Parking valuesToParking(Cursor cursor){
        int id = cursor.getInt(0);
        String name = cursor.getString(1);
        long latidute = cursor.getLong(2);
        long longitude = cursor.getLong(3);
        double cost = cursor.getDouble(4);
        return  new Parking(id,name,latidute,longitude,new ArrayList<Parking_Type>(),cost);
    }
    @Override
    public ArrayList<Parking> getAllParking() {
        ArrayList<Parking> parking = new ArrayList<Parking>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Parking person = valuesToParking(cursor);
            parking.add(person);
            cursor.moveToNext();
        }
        cursor.close(); // Remember to always close the cursor!
        return parking;
    }
}


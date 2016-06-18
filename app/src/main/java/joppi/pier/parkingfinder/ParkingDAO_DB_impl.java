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
            MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_NAME,
            MySQLiteHelper.COLUMN_COST,
            MySQLiteHelper.COLUMN_DISCO,
            MySQLiteHelper.COLUMN_CAR,
            MySQLiteHelper.COLUMN_MOTO,
            MySQLiteHelper.COLUMN_CARAVAN,
            MySQLiteHelper.COLUMN_INDOOR
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
        content.put(MySQLiteHelper.COLUMN_DISCO, parking.getDisco());
        content.put(MySQLiteHelper.COLUMN_CAR, parking.isCar());
        content.put(MySQLiteHelper.COLUMN_MOTO, parking.isMoto());
        content.put(MySQLiteHelper.COLUMN_CARAVAN, parking.isCaravan());
        content.put(MySQLiteHelper.COLUMN_INDOOR, parking.isIndoor());
        return content;
    }

    private Parking valuesToParking(Cursor cursor){
        int id = cursor.getInt(0);
        String name = cursor.getString(1);
        double cost = cursor.getDouble(7);
        int disco = cursor.getInt(2);
        boolean car = cursor.getInt(3)>0;
        boolean moto = cursor.getInt(5)>0;
        boolean caravan = cursor.getInt(4)>0;
        boolean indoor = cursor.getInt(6)>0;
        return  new Parking(id,name,cost,disco,car,moto,caravan,indoor);
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


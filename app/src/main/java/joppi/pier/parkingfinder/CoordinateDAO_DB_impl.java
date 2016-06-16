package joppi.pier.parkingfinder;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by christian on 30/05/16.
 */

public class CoordinateDAO_DB_impl implements CoordinateDAO {

    private MySQLiteHelper helper;
    private SQLiteDatabase database;
    private String [] allColumns = {
            MySQLiteHelper.COLUMN_PARKING,
            MySQLiteHelper.COLUMN_LATITUDE,
            MySQLiteHelper.COLUMN_LONGITUDE

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
    public Coordinate insertCoordinate(Coordinate coordinate) {
        database.insert(MySQLiteHelper.TABLE2_NAME, null, coordinateToValues(coordinate));
        Cursor cursor = database.query(MySQLiteHelper.TABLE2_NAME, allColumns, MySQLiteHelper.COLUMN_PARKING + " = ?" , new String[] {""+coordinate.getId_parking()}, null, null, null);
        cursor.moveToFirst();
        Coordinate c=valuesToCoordinate(cursor);
        cursor.close();
        return c;
    }



    @Override
    public boolean clear() {
        database.delete(MySQLiteHelper.TABLE2_NAME, null, null);
        return true;
    }

    @Override
    public ArrayList<Coordinate> getCoordinateOfParking(int id_parking) {
        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE2_NAME,
                allColumns, MySQLiteHelper.COLUMN_PARKING + " = ? ",  new String[]{""+id_parking}, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Coordinate coordinate = valuesToCoordinate(cursor);
            coordinates.add(coordinate);
            cursor.moveToNext();
        }
        cursor.close(); // Remember to always close the cursor!
        return coordinates;
    }


    private ContentValues coordinateToValues(Coordinate coordinate){
        ContentValues content = new ContentValues();
        content.put(MySQLiteHelper.COLUMN_PARKING,coordinate.getId_parking());
        content.put(MySQLiteHelper.COLUMN_LATITUDE, coordinate.getLatitude());
        content.put(MySQLiteHelper.COLUMN_LONGITUDE, coordinate.getLongitude());
        return content;
    }

    private Coordinate valuesToCoordinate(Cursor cursor){
        int id_parking = cursor.getInt(0);
        long latitude = cursor.getLong(1);
        long longitude = cursor.getLong(2);
        return  new Coordinate(id_parking,latitude,longitude);
    }

}


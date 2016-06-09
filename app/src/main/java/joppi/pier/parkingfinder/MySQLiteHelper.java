package joppi.pier.parkingfinder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by christian on 29/05/16.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {


    public static final String TABLE_NAME = "parking";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_COST = "cost";
    public static final String COLUMN_DISCO = "disco";
    public static final String COLUMN_CAR = "car";
    public static final String COLUMN_CARAVAN = "caravan";
    public static final String COLUMN_MOTO = "moto";
    public static final String COLUMN_INDOOR = "indoor";

    public static final String TABLE2_NAME = "coordinates";
    public static final String COLUMN_PARKING = "id_parking";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";

    private static final String DATABASE_NAME = "parking.db";
    private static final int DATABASE_VERSION = 1;
    // Database creation sql statement
    private static final String DATABASE_CREATE_1 = "create table "
            + TABLE_NAME + "( "
            + COLUMN_ID + " integer primary key autoincrement, " + COLUMN_NAME + " text not null, " +COLUMN_COST + " double not null, "+
            COLUMN_DISCO + " integer default 0, " + COLUMN_CAR + " boolean default true, " + COLUMN_MOTO + " boolean default true, "
            + COLUMN_CARAVAN + " boolean default false, "  + COLUMN_INDOOR + " boolean default false );";
    private static final String DATABASE_CREATE_2 =
            "create table " + TABLE2_NAME + "( "
            +  COLUMN_PARKING + " integer not null, "
            + COLUMN_LATITUDE + " long not null, "+ COLUMN_LONGITUDE + " long not null );";


    public MySQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_1);
        db.execSQL(DATABASE_CREATE_2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + "; DROP TABLE IF EXISTS " + TABLE2_NAME);
        onCreate(db);
    }
}

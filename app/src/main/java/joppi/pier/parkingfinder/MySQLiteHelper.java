package joppi.pier.parkingfinder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by christian on 29/05/16.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {


    public static final String TABLE_NAME = "parking";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_LATIDUTE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_COST = "cost";
    private static final String DATABASE_NAME = "parking.db";
    private static final int DATABASE_VERSION = 1;
    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME + "( "
            + COLUMN_ID + " integer primary key autoincrement, " + COLUMN_NAME + " text not null, "
            + COLUMN_LATIDUTE + " long not null, "+ COLUMN_LONGITUDE + " long not null, "+COLUMN_COST + " double not null );";


    public MySQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

package joppi.pier.parkingfinder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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


    private static boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = "data/data/joppi.pier.parkingfinder/databases/" + MySQLiteHelper.DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

            //database does't exist yet.

        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    public static void copyDataBase(Context myContext) throws IOException {

        if(checkDataBase()) {
            Log.w("LOG","ESISTE!");
            return;
        }

        InputStream myInput = myContext.getAssets().open(MySQLiteHelper.DATABASE_NAME);

        String outFileName = "data/data/joppi.pier.parkingfinder/databases/" + MySQLiteHelper.DATABASE_NAME;

        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();

    }
}

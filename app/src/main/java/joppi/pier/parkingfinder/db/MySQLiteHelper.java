package joppi.pier.parkingfinder.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
    public static final String COLUMN_TIME_LIMIT = "time_limit";
    public static final String COLUMN_NOTES = "notes";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_AREA = "area";
    public static final String COLUMN_TIME_FRAME = "time_frame";
    public static final String COLUMN_CAR = "car";
    public static final String COLUMN_MOTO = "moto";
    public static final String COLUMN_CARAVAN = "caravan";

    private static final String DATABASE_NAME = "parking.db";
    private static final int DATABASE_VERSION = 1;

    public MySQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public static void copyDataBase(Context myContext) throws IOException
    {
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

package joppi.pier.parkingfinder;

import android.app.Application;
import android.content.Context;

public class ParkingFinderApplication extends Application
{
    private static Context context;
    public void onCreate(){
        super.onCreate();
        ParkingFinderApplication.context = getApplicationContext(); }
    public static Context getAppContext() {
        return ParkingFinderApplication.context; }
}

package joppi.pier.parkingfinder;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;

import static android.provider.Settings.Global.getString;

/**
 * Created by christian on 11/08/16.
 */
public class SharedPreferencesManager {
    private static SharedPreferences sharedPreferences;
    private static SharedPreferencesManager manager;
    private static Activity activity;

    public static String PREF_VEHICLE = "Vehicle";
    public static String PREF_COST_WEIGHT = "CostWeight";
    public static String PREF_DISTANCE_WEIGHT = "DistanceWeight";
    public static String PREF_TIME = "Time";
    public static String PREF_TYPE_SURFACE = "Surface";
    public static String PREF_TYPE_STRUCTURE = "Structure";
    public static String PREF_TYPE_ROAD = "Road";
    public static String PREF_TYPE_SUBTERRANEAN = "Subterranean";
    public static String PREF_TYPE_SURVEILED = "Surviled";
    public static String PREF_TYPE_TIME_LIMITATED = "TimeLimitated";

    private SharedPreferencesManager() {
        sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
    }

    public static SharedPreferencesManager getInstance(Activity activity) {
        manager.activity = activity;
        if (manager == null)
            manager = new SharedPreferencesManager();
        return manager;
    }

    public void setPreference(String key, String var) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, var);
        editor.commit();

    }

    public void setPreference(String key, int var) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, var);
        editor.commit();

    }

    public void setPreference(String key, Boolean var) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, var);
        editor.commit();

    }

    public void setPreference(String key, Float var) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, var);
        editor.commit();

    }

    public String getStringPreference(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String var = sharedPreferences.getString(key, "no-preference");
        return var;
    }


    public int getIntPreference(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int var = sharedPreferences.getInt(key, 0);
        return var;
    }

    public Float getFloatPreference(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Float var = sharedPreferences.getFloat(key, 0.5f);
        return var;
    }
    public Boolean getBooleanPreference(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Boolean var = sharedPreferences.getBoolean(key, false);
        return var;
    }



}

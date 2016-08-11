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
    private SharedPreferencesManager(){
        sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
    }
    public static SharedPreferencesManager getInstance(Activity activity){
        if(manager == null)
            manager = new SharedPreferencesManager();
        manager.activity = activity;
        return manager;
    }
    public void setPreference(String key ,String var){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, var);
        editor.commit();

    }

    public void setPreference(String key ,int var){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, var);
        editor.commit();

    }

    public String getStringPreference(String key){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String var = sharedPreferences.getString(key, "no-preference");
        return var;
    }


    public int getIntPreference(String key){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int var = sharedPreferences.getInt(key,-1);
        return var;
    }


}

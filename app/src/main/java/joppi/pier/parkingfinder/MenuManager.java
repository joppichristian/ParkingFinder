package joppi.pier.parkingfinder;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * Created by christian on 11/08/16.
 */
public class MenuManager extends Activity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Activity activity;
    private Dialog dialog;

    public MenuManager(DrawerLayout drawerLayout, NavigationView navigationView, Activity activity){
        this.drawerLayout = drawerLayout;
        this.navigationView = navigationView;
        this.activity = activity;
        this.navigationView.setNavigationItemSelectedListener(this);

    }
    public void openMenu(){
        drawerLayout.openDrawer(Gravity.LEFT);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        dialog = new Dialog(activity);
        if (id == R.id.vehicleOption) {
            dialog.setContentView(R.layout.dialog_layout_vehicle);

            String vehicle = SharedPreferencesManager.getInstance(activity).getStringPreference(SharedPreferencesManager.PREF_VEHICLE);
            switch (vehicle){
                case "Car":
                    ((RadioButton)dialog.findViewById(R.id.radio_car_option)).setChecked(true);
                    break;
                case "Moto":
                    ((RadioButton)dialog.findViewById(R.id.radio_motor_option)).setChecked(true);
                    break;
                case "Caravan":
                    ((RadioButton)dialog.findViewById(R.id.radio_caravan_option)).setChecked(true);
                    break;
            }

            dialog.show();
            dialog.findViewById(R.id.confirmVehicleOption).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id_checked = ((RadioGroup)dialog.findViewById(R.id.optionVehicleGroup)).getCheckedRadioButtonId();
                    String vehicle = ((RadioButton)dialog.findViewById(id_checked)).getText().toString();
                    SharedPreferencesManager.getInstance(activity).setPreference(SharedPreferencesManager.PREF_VEHICLE,vehicle);
                    dialog.dismiss();
                }
            });
            return true;
        }
        if (id == R.id.timeOption) {
            dialog.setContentView(R.layout.dialog_layout_time);
            TimePicker timePicker = (TimePicker)dialog.findViewById(R.id.timePickerOption);
            String time = SharedPreferencesManager.getInstance(activity).getStringPreference(SharedPreferencesManager.PREF_TIME);
            timePicker.setCurrentHour(Integer.parseInt(time.split(":")[0]));
            timePicker.setCurrentMinute(Integer.parseInt(time.split(":")[1]));

            dialog.show();
            dialog.findViewById(R.id.confirmTimeOption).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimePicker timePicker = (TimePicker)dialog.findViewById(R.id.timePickerOption);
                    String time = timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute();
                    SharedPreferencesManager.getInstance(activity).setPreference(SharedPreferencesManager.PREF_TIME,time);
                    dialog.dismiss();
                }
            });
            return true;
        }
        if (id == R.id.orderOption) {
            dialog.setContentView(R.layout.dialog_layout_cost);

            SeekBar seekBar = (SeekBar)dialog.findViewById(R.id.seekBarOption);
            int progress = (int)(SharedPreferencesManager.getInstance(activity).getFloatPreference(SharedPreferencesManager.PREF_COST_WEIGHT)*10.0f);
            seekBar.setProgress(progress);

            dialog.show();
            dialog.findViewById(R.id.confirmCostOption).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SeekBar seekBar = (SeekBar)dialog.findViewById(R.id.seekBarOption);
                    float cost_weight = seekBar.getProgress()/10.0f;
                    SharedPreferencesManager.getInstance(activity).setPreference(SharedPreferencesManager.PREF_COST_WEIGHT,cost_weight);
                    SharedPreferencesManager.getInstance(activity).setPreference(SharedPreferencesManager.PREF_DISTANCE_WEIGHT,(1-cost_weight));
                    dialog.dismiss();

                }
            });
            return true;
        }
        if (id == R.id.typeOption) {
            dialog.setContentView(R.layout.dialog_layout_type);

            boolean disco = SharedPreferencesManager.getInstance(activity).getBooleanPreference(SharedPreferencesManager.PREF_TYPE_TIME_LIMITATED);
            boolean surface = SharedPreferencesManager.getInstance(activity).getBooleanPreference(SharedPreferencesManager.PREF_TYPE_SURFACE);
            boolean structure = SharedPreferencesManager.getInstance(activity).getBooleanPreference(SharedPreferencesManager.PREF_TYPE_STRUCTURE);
            boolean road = SharedPreferencesManager.getInstance(activity).getBooleanPreference(SharedPreferencesManager.PREF_TYPE_ROAD);
            boolean subterranean = SharedPreferencesManager.getInstance(activity).getBooleanPreference(SharedPreferencesManager.PREF_TYPE_SUBTERRANEAN);
            boolean surveiled = SharedPreferencesManager.getInstance(activity).getBooleanPreference(SharedPreferencesManager.PREF_TYPE_SURVEILED);

            ((CheckBox)dialog.findViewById(R.id.chkDisco)).setChecked(disco);
            ((CheckBox)dialog.findViewById(R.id.chkSurface)).setChecked(surface);
            ((CheckBox)dialog.findViewById(R.id.chkStructure)).setChecked(structure);
            ((CheckBox)dialog.findViewById(R.id.chkRoad)).setChecked(road);
            ((CheckBox)dialog.findViewById(R.id.chkSubterranean)).setChecked(subterranean);
            ((CheckBox)dialog.findViewById(R.id.chkSurveiled)).setChecked(surveiled);


            dialog.show();


            dialog.findViewById(R.id.confirmTypeOption).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean chkDisco = ((CheckBox)dialog.findViewById(R.id.chkDisco)).isChecked();
                    boolean chkSurface = ((CheckBox)dialog.findViewById(R.id.chkSurface)).isChecked();
                    boolean chkStructure = ((CheckBox)dialog.findViewById(R.id.chkStructure)).isChecked();
                    boolean chkRoad = ((CheckBox)dialog.findViewById(R.id.chkRoad)).isChecked();
                    boolean chkSubterranean = ((CheckBox)dialog.findViewById(R.id.chkSubterranean)).isChecked();
                    boolean chkSurveiled = ((CheckBox)dialog.findViewById(R.id.chkSurveiled)).isChecked();
                    SharedPreferencesManager.getInstance(activity).setPreference(SharedPreferencesManager.PREF_TYPE_TIME_LIMITATED,chkDisco);
                    SharedPreferencesManager.getInstance(activity).setPreference(SharedPreferencesManager.PREF_TYPE_SURFACE,chkSurface);
                    SharedPreferencesManager.getInstance(activity).setPreference(SharedPreferencesManager.PREF_TYPE_STRUCTURE,chkStructure);
                    SharedPreferencesManager.getInstance(activity).setPreference(SharedPreferencesManager.PREF_TYPE_ROAD,chkRoad);
                    SharedPreferencesManager.getInstance(activity).setPreference(SharedPreferencesManager.PREF_TYPE_SUBTERRANEAN,chkSubterranean);
                    SharedPreferencesManager.getInstance(activity).setPreference(SharedPreferencesManager.PREF_TYPE_SURVEILED,chkSurveiled);
                    dialog.dismiss();
                }
            });
            return true;
        }

        return true;
    }

}

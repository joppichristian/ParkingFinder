package joppi.pier.parkingfinder;

import android.app.Activity;
import android.app.Dialog;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import joppi.pier.parkingfinder.db.ParkingMgr;

public class MenuManager extends Activity implements NavigationView.OnNavigationItemSelectedListener, SeekBar.OnSeekBarChangeListener
{
	private DrawerLayout drawerLayout;
	private NavigationView navigationView;
	private MapsActivity mapsActivity;
	private Dialog dialog;
	private ParkingMgr parkingMgr;
	private TextView seek_value;

	public MenuManager(DrawerLayout drawerLayout, NavigationView navigationView, MapsActivity activity)
	{
		this.drawerLayout = drawerLayout;
		this.navigationView = navigationView;
		this.mapsActivity = activity;
		this.navigationView.setNavigationItemSelectedListener(this);

	}

	public void openMenu()
	{
		drawerLayout.openDrawer(Gravity.LEFT);
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item)
	{
		dialog = new Dialog(mapsActivity);
		final SharedPreferencesManager prefManager = SharedPreferencesManager.getInstance(mapsActivity);

		switch(item.getItemId())
		{
			case R.id.vehicleOption:
			{
				dialog.setContentView(R.layout.dialog_layout_vehicle);

				String vehicle = prefManager.getStringPreference(SharedPreferencesManager.PREF_VEHICLE);
				switch(vehicle){
					case "Automobile":
						((RadioButton) dialog.findViewById(R.id.radio_car_option)).setChecked(true);
						break;
					case "Moto":
						((RadioButton) dialog.findViewById(R.id.radio_motor_option)).setChecked(true);
						break;
					case "Caravan":
						((RadioButton) dialog.findViewById(R.id.radio_caravan_option)).setChecked(true);
						break;
				}

				dialog.show();
				dialog.findViewById(R.id.confirmVehicleOption).setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						int id_checked = ((RadioGroup) dialog.findViewById(R.id.optionVehicleGroup)).getCheckedRadioButtonId();
						String vehicle = ((RadioButton) dialog.findViewById(id_checked)).getText().toString();
						prefManager.setPreference(SharedPreferencesManager.PREF_VEHICLE, vehicle);
						dialog.dismiss();
						mapsActivity.triggerParkingListUpdate();
					}
				});
			}break;

			case R.id.timeOption:
			{
				dialog.setContentView(R.layout.dialog_layout_time);
				TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.timePickerOption);
				timePicker.setIs24HourView(DateFormat.is24HourFormat(mapsActivity));
				String time = prefManager.getStringPreference(SharedPreferencesManager.PREF_TIME);
				timePicker.setCurrentHour(Integer.parseInt(time.split(":")[0]));
				timePicker.setCurrentMinute(Integer.parseInt(time.split(":")[1]));

				dialog.show();
				dialog.findViewById(R.id.confirmTimeOption).setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.timePickerOption);
						String time = timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute();
						prefManager.setPreference(SharedPreferencesManager.PREF_TIME, time);
						dialog.dismiss();
						mapsActivity.triggerParkingListUpdate();
					}
				});
			}break;

			case R.id.orderOption:
			{
				dialog.setContentView(R.layout.dialog_layout_cost);

				SeekBar seekBar = (SeekBar) dialog.findViewById(R.id.seekBarOption);
				int progress = (int) (prefManager.getFloatPreference(SharedPreferencesManager.PREF_COST_WEIGHT) * 10.0f);
				seekBar.setProgress(progress);

				dialog.show();
				dialog.findViewById(R.id.confirmCostOption).setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						SeekBar seekBar = (SeekBar) dialog.findViewById(R.id.seekBarOption);
						float cost_weight = seekBar.getProgress() / 10.0f;
						prefManager.setPreference(SharedPreferencesManager.PREF_COST_WEIGHT, cost_weight);
						prefManager.setPreference(SharedPreferencesManager.PREF_DISTANCE_WEIGHT, (1 - cost_weight));
						dialog.dismiss();
						mapsActivity.triggerParkingListUpdate();
					}
				});
			}break;

			case R.id.typeOption:
			{
				dialog.setContentView(R.layout.dialog_layout_type);

				boolean surface = prefManager.getBooleanPreference(SharedPreferencesManager.PREF_TYPE_SURFACE);
				boolean structure = prefManager.getBooleanPreference(SharedPreferencesManager.PREF_TYPE_STRUCTURE);
				boolean road = prefManager.getBooleanPreference(SharedPreferencesManager.PREF_TYPE_ROAD);
				boolean subterranean = prefManager.getBooleanPreference(SharedPreferencesManager.PREF_TYPE_SUBTERRANEAN);

				((CheckBox) dialog.findViewById(R.id.chkSurface)).setChecked(surface);
				((CheckBox) dialog.findViewById(R.id.chkStructure)).setChecked(structure);
				((CheckBox) dialog.findViewById(R.id.chkRoad)).setChecked(road);
				((CheckBox) dialog.findViewById(R.id.chkSubterranean)).setChecked(subterranean);

				dialog.show();

				dialog.findViewById(R.id.confirmTypeOption).setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						boolean chkSurface = ((CheckBox) dialog.findViewById(R.id.chkSurface)).isChecked();
						boolean chkStructure = ((CheckBox) dialog.findViewById(R.id.chkStructure)).isChecked();
						boolean chkRoad = ((CheckBox) dialog.findViewById(R.id.chkRoad)).isChecked();
						boolean chkSubterranean = ((CheckBox) dialog.findViewById(R.id.chkSubterranean)).isChecked();
						prefManager.setPreference(SharedPreferencesManager.PREF_TYPE_SURFACE, chkSurface);
						prefManager.setPreference(SharedPreferencesManager.PREF_TYPE_STRUCTURE, chkStructure);
						prefManager.setPreference(SharedPreferencesManager.PREF_TYPE_ROAD, chkRoad);
						prefManager.setPreference(SharedPreferencesManager.PREF_TYPE_SUBTERRANEAN, chkSubterranean);
						dialog.dismiss();
						mapsActivity.triggerParkingListUpdate();
					}
				});
			}break;

			case R.id.timeLimitOption:
			{
				dialog.setContentView(R.layout.dialog_layout_disco);

				Boolean disco = prefManager.getBooleanPreference(SharedPreferencesManager.PREF_TYPE_TIME_LIMITATED);
				if(disco)
					((RadioButton) dialog.findViewById(R.id.radio_disco_yes)).setChecked(true);
				else
					((RadioButton) dialog.findViewById(R.id.radio_disco_no)).setChecked(true);


				dialog.show();
				dialog.findViewById(R.id.confirmDiscoOption).setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						int id_checked = ((RadioGroup) dialog.findViewById(R.id.optionDiscoGroup)).getCheckedRadioButtonId();
						if(id_checked == R.id.radio_disco_yes)
							prefManager.setPreference(SharedPreferencesManager.PREF_TYPE_TIME_LIMITATED, true);
						else
							prefManager.setPreference(SharedPreferencesManager.PREF_TYPE_TIME_LIMITATED, false);
						dialog.dismiss();
						mapsActivity.triggerParkingListUpdate();
					}
				});
			}break;

			case R.id.surveiledOption:
			{
				dialog.setContentView(R.layout.dialog_layout_surveiled);

				Boolean surveiled = prefManager.getBooleanPreference(SharedPreferencesManager.PREF_TYPE_SURVEILED);
				if(surveiled)
					((RadioButton) dialog.findViewById(R.id.radio_surveiled_yes)).setChecked(true);
				else
					((RadioButton) dialog.findViewById(R.id.radio_surveiled_no)).setChecked(true);

				dialog.show();
				dialog.findViewById(R.id.confirmSurveiledOption).setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						int id_checked = ((RadioGroup) dialog.findViewById(R.id.optionSurveiledGroup)).getCheckedRadioButtonId();
						if(id_checked == R.id.radio_surveiled_yes)
							prefManager.setPreference(SharedPreferencesManager.PREF_TYPE_SURVEILED, true);
						else
							prefManager.setPreference(SharedPreferencesManager.PREF_TYPE_SURVEILED, false);
						dialog.dismiss();
						mapsActivity.triggerParkingListUpdate();
					}
				});
			}break;

			case R.id.radiusOption:
			{
				dialog.setContentView(R.layout.dialog_layout_radius);

				SeekBar seekBar = (SeekBar) dialog.findViewById(R.id.seekBarRadiusOption);
				int progress = (prefManager.getIntPreference(SharedPreferencesManager.PREF_RADIUS));
				seekBar.setProgress(progress);
				seek_value = (TextView) dialog.findViewById(R.id.seekValue);
				seek_value.setText(progress + " Km");
				seekBar.setOnSeekBarChangeListener(this);

				dialog.show();
				dialog.findViewById(R.id.confirmRadiusOption).setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						SeekBar seekBar = (SeekBar) dialog.findViewById(R.id.seekBarRadiusOption);
						int radius = seekBar.getProgress();
						prefManager.setPreference(SharedPreferencesManager.PREF_RADIUS, radius);
						dialog.dismiss();
						Log.w("RADIUS", radius + "");
						mapsActivity.triggerParkingListUpdate();
					}
				});
			}break;
		}

		return true;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		seek_value.setText(progress + " km");
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar)
	{

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar)
	{

	}
}

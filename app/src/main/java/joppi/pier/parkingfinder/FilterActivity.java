package joppi.pier.parkingfinder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TimePicker;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;

import java.io.IOException;

import joppi.pier.parkingfinder.db.MySQLiteHelper;

public class FilterActivity extends AppCompatActivity
{
	CarouselView customCarouselView;
	int NUMBER_OF_PAGES = 3;

	class ShiftViewHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			int viewNum = (int) msg.obj;
			customCarouselView.setCurrentItem(viewNum);
		}
	}
	Handler shiftViewHandler = new ShiftViewHandler();

	@Override
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setContentView(R.layout.activity_filter);

		customCarouselView = (CarouselView) findViewById(R.id.carouselView);
		customCarouselView.setPageCount(NUMBER_OF_PAGES);
		// set ViewListener for custom view
		customCarouselView.setViewListener(viewListener);

		try {
			MySQLiteHelper.copyDataBase(ParkingFinderApplication.getAppContext());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onStartResearchClick (View v)
	{
		Intent go = new Intent(FilterActivity.this, MapsActivity.class);
		int id_checked = ((RadioGroup)findViewById(R.id.filterVehicleGroup)).getCheckedRadioButtonId();
		String vehicle = ((RadioButton)findViewById(id_checked)).getText().toString();

		TimePicker timePicker = (TimePicker)findViewById(R.id.timePicker);
		String time = timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute();

		SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);
		float cost_weight = seekBar.getProgress()/10.0f;


		SharedPreferencesManager.getInstance(FilterActivity.this).setPreference(SharedPreferencesManager.PREF_VEHICLE,vehicle);
		SharedPreferencesManager.getInstance(FilterActivity.this).setPreference(SharedPreferencesManager.PREF_TIME,time);
		SharedPreferencesManager.getInstance(FilterActivity.this).setPreference(SharedPreferencesManager.PREF_COST_WEIGHT,cost_weight);
		SharedPreferencesManager.getInstance(FilterActivity.this).setPreference(SharedPreferencesManager.PREF_DISTANCE_WEIGHT,(1-cost_weight));
		SharedPreferencesManager.getInstance(FilterActivity.this).setPreference(SharedPreferencesManager.PREF_RADIUS,10);
		startActivity(go);
		finish();
	}


	public void onTimePickDoneClick (View v)
	{
		Message viewNum = new Message();
		viewNum.obj = 2;
		shiftViewHandler.sendMessageDelayed(viewNum, 200);
	}

	ViewListener viewListener = new ViewListener()
	{
		@Override
		public View setViewForPosition(int position)
		{
			View customView = null;
            SharedPreferencesManager preferencesManager = SharedPreferencesManager.getInstance(FilterActivity.this);

			switch(position){
				case 0:
					customView = getLayoutInflater().inflate(R.layout.carousel_layout_vehicle, null);
					break;
				case 1:
					customView = getLayoutInflater().inflate(R.layout.carousel_layout_time, null);
					break;
				default:
					customView = getLayoutInflater().inflate(R.layout.carousel_layout_cost, null);
                    SeekBar seekBar = (SeekBar)customView.findViewById(R.id.seekBar);
                    int progress = (int)(preferencesManager.getFloatPreference(SharedPreferencesManager.PREF_COST_WEIGHT)*10.0f);
                    if(seekBar != null)
                        seekBar.setProgress(progress);
                    break;
			}

			//set view attributes here
			return customView;
		}
	};

	public void onRadioButtonClicked(View view)
	{
		// Is the button now checked?
		boolean checked = ((RadioButton) view).isChecked();

		// Check which radio button was clicked
		switch(view.getId()){
			case R.id.radio_car:
				if(checked)
				break;
			case R.id.radio_motor:
				if(checked)
				break;
			case R.id.radio_caravan:
				if(checked)
					break;


		}

		// If checked shift to next page (delayed)
		if(checked){
			Message viewNum = new Message();
			viewNum.obj = 1;
			shiftViewHandler.sendMessageDelayed(viewNum, 200);
		}
	}
}
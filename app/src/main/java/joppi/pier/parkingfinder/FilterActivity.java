package joppi.pier.parkingfinder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TimePicker;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;

public class FilterActivity extends AppCompatActivity
{
	CarouselView customCarouselView;
	int NUMBER_OF_PAGES = 3;
	String vehicle;
    String time;

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
		vehicle = "car";
        time="";
	}

	public void onStartResearchClick (View v)
	{
		Intent go = new Intent(FilterActivity.this, MapsActivity.class);
		SharedPreferencesManager.getInstance(FilterActivity.this).setPreference(SharedPreferencesManager.PREF_VEHICLE,vehicle);
		SharedPreferencesManager.getInstance(FilterActivity.this).setPreference(SharedPreferencesManager.PREF_TIME,time);
		startActivity(go);
		finish();
	}


	public void onTimePickDoneClick (View v)
	{
		Message viewNum = new Message();
		viewNum.obj = 2;
		shiftViewHandler.sendMessageDelayed(viewNum, 200);
        TimePicker time_picker = (TimePicker)findViewById(R.id.timePicker);
        time_picker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                time = hourOfDay+":"+minute;
            }
        });

	}

	ViewListener viewListener = new ViewListener()
	{
		@Override
		public View setViewForPosition(int position)
		{
			View customView = null;
			switch(position){
				case 0:
					customView = getLayoutInflater().inflate(R.layout.carousel_layout_vehicle, null);
					break;
				case 1:
					customView = getLayoutInflater().inflate(R.layout.carousel_layout_time, null);
					break;
				default:
					customView = getLayoutInflater().inflate(R.layout.carousel_layout_cost, null);
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
					vehicle = "car";
				break;
			case R.id.radio_motor:
				if(checked)
					vehicle = "moto";
				break;
			case R.id.radio_caravan:
				if(checked)
					vehicle = "caravan";
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
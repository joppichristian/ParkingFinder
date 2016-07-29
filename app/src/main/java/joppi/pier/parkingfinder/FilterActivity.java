package joppi.pier.parkingfinder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;

public class FilterActivity extends AppCompatActivity
{
	CarouselView customCarouselView;
	int NUMBER_OF_PAGES = 3;

	@Override
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setContentView(R.layout.activity_filter);

		customCarouselView = (CarouselView) findViewById(R.id.carouselView);
		customCarouselView.setPageCount(NUMBER_OF_PAGES);
		// set ViewListener for custom view
		customCarouselView.setViewListener(viewListener);

		Button startApp = (Button) findViewById(R.id.startMap);
		startApp.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//Define Intent to go to map activity ( TO DO )

				Intent go = new Intent(FilterActivity.this, MapsActivity.class);
				startActivity(go);
				finish();
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

	Runnable shiftNextPage = new Runnable()
	{
		public void run()
		{	// Get current item not available???
			customCarouselView.setCurrentItem(1);
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
		if(checked)
		{
			Handler handler = new Handler();
			handler.postDelayed(shiftNextPage, 200);
		}
	}
}
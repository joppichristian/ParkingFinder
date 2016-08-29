package joppi.pier.parkingfinder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;

import joppi.pier.parkingfinder.db.MySQLiteHelper;

public class SplashActivity extends AppCompatActivity
{
	private static int SPLASH_TIME_OUT = 1000; //1 sec

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		try {
			MySQLiteHelper.copyDataBase(ParkingFinderApplication.getAppContext());
		} catch (IOException e) {
			e.printStackTrace();
		}
		new Handler().postDelayed(new Runnable()
		{

			@Override
			public void run()
			{
				// This method will be executed once the timer is over
				// Start your app main activity
				Intent i = new Intent(SplashActivity.this, FilterActivity.class);
				startActivity(i);
				finish();
			}
		}, SPLASH_TIME_OUT);
	}
}

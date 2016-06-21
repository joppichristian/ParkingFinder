package joppi.pier.parkingfinder;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Pierluigi on 21/06/2016.
 */

public class MyLocationProvider extends Service implements LocationListener
{
	private final Context context;

	boolean isGPSEnabled = false;
	boolean isNetworkEnabled = false;
	public boolean canGetLocation = false;

	Location location;

	double latitude;
	double longitude;

	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

	protected LocationManager locationManager;
	protected Activity currentActivity;

	public MyLocationProvider(Activity currActivity, Context context)
	{
		currentActivity = currActivity;

		this.context = context;
		getLocation();
	}

	public LatLng getLatLng()
	{
		Location myLoc = getLocation();
		if(myLoc != null)
			return new LatLng(myLoc.getLatitude(), myLoc.getLongitude());
		else return null;
	}

	public Location getLocation()
	{
		if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
			ActivityCompat.requestPermissions(currentActivity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
			ActivityCompat.requestPermissions(currentActivity,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
		}

		try{
			locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

			isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if(!isGPSEnabled && !isNetworkEnabled){

			} else{
				this.canGetLocation = true;

				if(isNetworkEnabled){
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

					if(locationManager != null){
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

						if(location != null){

							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}

				if(isGPSEnabled){
					if(location == null){
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						if(locationManager != null){
							location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

							if(location != null){
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
				}
			}

		}catch(Exception e){
			e.printStackTrace();
		}

		return location;
	}


	public void stopUsingGPS()
	{
		if(locationManager != null){
			if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
				ActivityCompat.requestPermissions(currentActivity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
				ActivityCompat.requestPermissions(currentActivity,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
			}
			locationManager.removeUpdates(MyLocationProvider.this);
		}
	}

	public double getLatitude()
	{
		if(location != null){
			latitude = location.getLatitude();
		}
		return latitude;
	}

	public double getLongitude()
	{
		if(location != null){
			longitude = location.getLongitude();
		}

		return longitude;
	}

	public boolean canGetLocation()
	{
		return this.canGetLocation;
	}

	public void showSettingsAlert()
	{
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

		alertDialog.setTitle("GPS is settings");

		alertDialog.setMessage("Turn on your GPS to find nearby helpers");

		alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				context.startActivity(intent);
			}
		});

		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.cancel();
			}
		});

		alertDialog.show();
	}

	@Override
	public void onLocationChanged(Location arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
package joppi.pier.parkingfinder;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.ArrayList;
import java.util.List;

public class LocationProvider implements GoogleApiClient.ConnectionCallbacks,
										 GoogleApiClient.OnConnectionFailedListener,
										 LocationListener
{
	private final Context context;
	protected Activity currActivity;

	Location mLocation;
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;

	private List<OnLocationChangedListener> mLocationChangedListeners = new ArrayList<>();

	public interface OnLocationChangedListener
	{
		/**
		 * Called when a sliding pane's position changes.
		 *
		 * @param loc The new location
		 */
		void onLocationChanged(Location loc);
	}

	public LocationProvider(Activity activity, Context context)
	{
		currActivity = activity;

		this.context = context;
		buildGoogleApiClient();
	}

	@Override
	public void onConnected(Bundle bundle)
	{
		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setInterval(4000); // Update location every second
		mLocationRequest.setSmallestDisplacement(10); // Update every 2 meters

		if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
			ActivityCompat.requestPermissions(currActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
		}

		LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
		mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

		LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
		builder.setAlwaysShow(true); //this is the key ingredient

		PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
		result.setResultCallback(new ResultCallback<LocationSettingsResult>()
		{
			@Override
			public void onResult(LocationSettingsResult result)
			{
				final Status status = result.getStatus();
				final LocationSettingsStates state = result.getLocationSettingsStates();
				switch(status.getStatusCode()){
					case LocationSettingsStatusCodes.SUCCESS:
						// All location settings are satisfied. The client can initialize location
						// requests here.
						break;
					case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
						// Location settings are not satisfied. But could be fixed by showing the user
						// a dialog.
						try{
							// Show the dialog by calling startResolutionForResult(),
							// and check the result in onActivityResult().
							status.startResolutionForResult(currActivity, 1000);
						}catch(IntentSender.SendIntentException e){
							// Ignore the error.
						}
						break;
					case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
						// Location settings are not satisfied. However, we have no way to fix the
						// settings so we won't show the dialog.
						break;
				}
			}
		});
	}

	@Override
	public void onConnectionSuspended(int i)
	{
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult)
	{
		buildGoogleApiClient();
	}

	void buildGoogleApiClient()
	{
		mGoogleApiClient = new GoogleApiClient.Builder(context)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();
	}

	public void onStart()
	{
		mGoogleApiClient.connect();
	}

	public void onDestroy()
	{
		mGoogleApiClient.disconnect();
	}

	@Override
	public void onLocationChanged(Location arg0)
	{
		mLocation = arg0;
		dispatchOnLocationChanged(arg0);
	}

	public Location getCurrentLocation()
	{
		return mLocation;
	}

	public void addLocationChangedListener(OnLocationChangedListener listener)
	{
		mLocationChangedListeners.add(listener);
	}

	void dispatchOnLocationChanged(Location loc)
	{
		for(OnLocationChangedListener l : mLocationChangedListeners){
			l.onLocationChanged(loc);
		}
	}
}
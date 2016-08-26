package joppi.pier.parkingfinder;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DistanceMatrixAPI
{
	public static String MODE_DRIVING = "mode=driving";
	public static String MODE_WALKING = "mode=walking";
	public static String MODE_BICYCLING = "mode=bicycling";
	public static String MODE_TRANSIT = "mode=transit";

	public static String AVOID_TOLLS = "avoid=tolls";
	public static String AVOID_HIGHWAYS = "avoid=highways";
	public static String AVOID_FERRIES = "avoid=ferries";
	public static String AVOID_INDOOR = "avoid=indoor";

	private String mAppKey = "";
	private String mTravelMode = "";
	private String mAvoid = "";

	private LatLng[] mOrigins;
	private LatLng[] mDestinations;

	public DistanceMatrixAPI(String key)
	{
		mAppKey = key;
		mOrigins = null;
		mDestinations = null;
	}

	public DistanceMatrixAPI setTravelMode(String mTravelMode)
	{
		this.mTravelMode = mTravelMode;
		return this;
	}

	public DistanceMatrixAPI setRestriction(String restriction)
	{
		mAvoid = restriction;
		return this;
	}

	public DistanceMatrixAPI setOrigins(LatLng origin)
	{
		return setOrigins(new LatLng[]{origin});
	}

	public DistanceMatrixAPI setOrigins(LatLng[] origins)
	{
		mOrigins = origins;
		return this;
	}

	public DistanceMatrixAPI setDestinations(LatLng destinations)
	{
		return setDestinations(new LatLng[]{destinations});
	}

	public DistanceMatrixAPI setDestinations(LatLng[] destinations)
	{
		mDestinations = destinations;
		return this;
	}

	public DistanceMatrixResult exec()
	{
		if(mOrigins == null || mDestinations == null || mOrigins.length == 0 || mDestinations.length == 0)
			return null;

		StringBuilder urlString = new StringBuilder();
		urlString.append("https://maps.googleapis.com/maps/api/distancematrix/json?");

		urlString.append("origins=");
		for(int i=0; i<mOrigins.length; i++)
		{
			urlString.append( Double.toString(mOrigins[i].latitude));
			urlString.append(",");
			urlString.append( Double.toString(mOrigins[i].longitude));

			// If there is another element
			if((i + 1) < mOrigins.length)
				urlString.append("|");
		}

		urlString.append("&destinations=");
		for(int i=0; i<mDestinations.length; i++)
		{
			urlString.append( Double.toString(mDestinations[i].latitude));
			urlString.append(",");
			urlString.append( Double.toString(mDestinations[i].longitude));

			// If there is another element
			if((i + 1) < mDestinations.length)
				urlString.append("|");
		}

		if(mTravelMode != "")
			urlString.append("&" + mTravelMode);
		else urlString.append("&mode=driving");

		if(mAvoid != "")
			urlString.append("&" + mAvoid);

		if(mAppKey != "")
		urlString.append("&key=" + mAppKey);

		// Get the JSON And parse it to get the directions data.
		HttpURLConnection urlConnection= null;
		URL url = null;

		try
		{
			url = new URL(urlString.toString());
			urlConnection=(HttpURLConnection)url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.connect();

			InputStream inStream = urlConnection.getInputStream();
			BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));

			String temp, response = "";
			while((temp = bReader.readLine()) != null){
				//Parse data
				response += temp;
			}

			//Close the reader, stream & connection
			bReader.close();
			inStream.close();
			urlConnection.disconnect();

			return new DistanceMatrixResult(response);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}

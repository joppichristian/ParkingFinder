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


	private String appKey = "";
	private String travelMode = "";
	private String avoid = "";

	public DistanceMatrixAPI(String key)
	{
		appKey = key;
	}

	public DistanceMatrixAPI setTravelMode(String travelMode )
	{
		this.travelMode = travelMode;
		return this;
	}

	public DistanceMatrixAPI setRestriction(String restriction)
	{
		avoid = restriction;
		return this;
	}

	public DistanceMatrixResult exec(LatLng origin, LatLng destination)
	{
		StringBuilder urlString = new StringBuilder();
		urlString.append("https://maps.googleapis.com/maps/api/distancematrix/json?");
		urlString.append("origins=");
		urlString.append( Double.toString(origin.latitude));
		urlString.append(",");
		urlString.append( Double.toString(origin.longitude));
		urlString.append("&destinations=");
		urlString.append( Double.toString(destination.latitude));
		urlString.append(",");
		urlString.append( Double.toString(destination.longitude));
//		urlString.append("&key=" + appKey);
		urlString.append("&mode=driving");

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

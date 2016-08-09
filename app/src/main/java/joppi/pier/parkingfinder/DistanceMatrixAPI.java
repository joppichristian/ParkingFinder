package joppi.pier.parkingfinder;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// TODO: create a quite complete class for DistanceMatrix requests (vehicle, duration, traffic etc...)
public class DistanceMatrixAPI
{
	public DistanceMatrixAPI()
	{
	}

	public int getDistanceMatrix(LatLng... params)
	{
		int iDistance = 0;
		StringBuilder urlString = new StringBuilder();
		urlString.append("http://maps.googleapis.com/maps/api/directions/json?");
		urlString.append("origin=");
		urlString.append( Double.toString(params[0].latitude));
		urlString.append(",");
		urlString.append( Double.toString(params[0].longitude));
		urlString.append("&destination=");
		urlString.append( Double.toString(params[1].latitude));
		urlString.append(",");
		urlString.append( Double.toString(params[1].longitude));
		urlString.append("&mode=driving");
		//Log.d("xxx","URL="+urlString.toString());

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

			//Sortout JSONresponse
			JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
			JSONArray array = object.getJSONArray("routes");

			//Routes is a combination of objects and arrays
			JSONObject routes = array.getJSONObject(0);
			String summary = routes.getString("summary");
			JSONArray legs = routes.getJSONArray("legs");
			JSONObject steps = legs.getJSONObject(0);
			JSONObject distance = steps.getJSONObject("distance");

			iDistance = distance.getInt("value");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iDistance;
	}
}

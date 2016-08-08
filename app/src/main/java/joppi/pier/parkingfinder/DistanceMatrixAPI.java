package joppi.pier.parkingfinder;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by christian on 01/08/16.
 */
public class DistanceMatrixAPI extends AsyncTask<LatLng,Void,Integer> {

    @Override
    protected Integer doInBackground(LatLng... params) {

        int iDistance = 0;
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/directions/json?");
        urlString.append("origin=");//from
        urlString.append( Double.toString(params[0].latitude));
        urlString.append(",");
        urlString.append( Double.toString(params[0].longitude));
        urlString.append("&destination=");//to
        urlString.append( Double.toString(params[1].latitude));
        urlString.append(",");
        urlString.append( Double.toString(params[1].longitude));
        urlString.append("&mode=walking&sensor=true");
        Log.d("xxx","URL="+urlString.toString());

        // get the JSON And parse it to get the directions data.
        HttpURLConnection urlConnection= null;
        URL url = null;

        try {
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
            //Log.d("JSON","array: "+array.toString());

            //Routes is a combination of objects and arrays
            JSONObject routes = array.getJSONObject(0);
            //Log.d("JSON","routes: "+routes.toString());

            String summary = routes.getString("summary");
            //Log.d("JSON","summary: "+summary);

            JSONArray legs = routes.getJSONArray("legs");
            //Log.d("JSON","legs: "+legs.toString());

            JSONObject steps = legs.getJSONObject(0);
            //Log.d("JSON","steps: "+steps.toString());

            JSONObject distance = steps.getJSONObject("distance");
            //Log.d("JSON","distance: "+distance.toString());

            iDistance = distance.getInt("value");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iDistance;
    }
}

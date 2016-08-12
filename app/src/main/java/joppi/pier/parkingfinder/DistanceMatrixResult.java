package joppi.pier.parkingfinder;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class DistanceMatrixResult
{
	private String mStatus = "";
	private String mInnerStatus = "";
	private JSONObject mDistance = null;
	private JSONObject mDuration = null;

	public DistanceMatrixResult(String jsonReply)
	{
		try
		{
			// Sortout JSONresponse
			JSONObject object = (JSONObject) new JSONTokener(jsonReply).nextValue();

			// Get status info
			mStatus = object.getString("status");

			// Get row 0
			JSONArray rows = object.getJSONArray("rows");
			JSONObject row0 = rows.getJSONObject(0);

			// Get elem 0
			JSONArray elements = row0.getJSONArray("elements");
			JSONObject elem0 = elements.getJSONObject(0);

			mDistance = elem0.getJSONObject("distance");
			mDuration = elem0.getJSONObject("duration");

			mInnerStatus = elem0.getString("status");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean getStatusOk()
	{
		return (mStatus == "OK");
	}

	public String getStatusText()
	{
		return mStatus;
	}

	public String getDistanceText()
	{
		try{
			return mDistance.getString("text");
		}catch(Exception e)
		{}
		return null;
	}

	public int getDistance()
	{
		try{
			return mDistance.getInt("value");
		}catch(Exception e)
		{}
		return -1;
	}

	public String getDurationText()
	{
		try{
			return mDuration.getString("text");
		}catch(Exception e)
		{}
		return null;
	}

	public int getDuration()
	{
		try{
			return mDuration.getInt("value");
		}catch(Exception e)
		{}
		return -1;
	}
}

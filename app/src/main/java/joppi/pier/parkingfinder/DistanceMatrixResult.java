package joppi.pier.parkingfinder;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

public class DistanceMatrixResult
{
	private String mStatus = "";
	private ArrayList<ResultElement> mResultElements;

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

			mResultElements = new ArrayList<>();

			for(int i=0; i<elements.length(); i++)
				mResultElements.add(new ResultElement(elements.getJSONObject(i)));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<ResultElement> getElementsList()
	{
		return mResultElements;
	}

	public ResultElement getElement(int index)
	{
		if(mResultElements != null)
			return mResultElements.get(index);
		return null;
	}

	public boolean getStatusOk()
	{
		return mStatus.equals("OK");
	}

	public String getStatusText()
	{
		return mStatus;
	}

	public class ResultElement
	{
		private JSONObject mDistance = null;
		private JSONObject mDuration = null;
		private String mInnerStatus = "";

		public ResultElement(JSONObject element)
		{
			try{
				mDistance = element.getJSONObject("distance");
				mDuration = element.getJSONObject("duration");
				mInnerStatus = element.getString("status");
			}catch(Exception e)
			{e.printStackTrace();}
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
}

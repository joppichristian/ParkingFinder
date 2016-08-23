package joppi.pier.parkingfinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import joppi.pier.parkingfinder.db.Parking;
import joppi.pier.parkingfinder.db.ParkingMgr;

public class ParkingListAdapter extends BaseAdapter
{
	Context mAppContext;
	ParkingMgr mParkingMgr;
	static LayoutInflater inflater = null;

	public ParkingListAdapter(Context context, ParkingMgr parkingMgr)
	{
		mAppContext = context;
		mParkingMgr = parkingMgr;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount()
	{
		ArrayList<Parking> list = mParkingMgr.getParkingList();
		if(list != null)
			return list.size();
		else return 0;
	}

	@Override
	public Object getItem(int position)
	{
		return mParkingMgr.getParkingList().get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		View vi = convertView;
		if(vi == null)
			vi = inflater.inflate(R.layout.parking_list_item, null);

		ArrayList<Parking> parkingList = mParkingMgr.getParkingList();
		if(parkingList != null)
		{
			Parking currParking = parkingList.get(position);

			TextView text_name = (TextView) vi.findViewById(R.id.park_name);
			text_name.setText(currParking.toString());
			TextView text_details = (TextView) vi.findViewById(R.id.park_distance);

			double distance = currParking.getCurrDistance();
			long distM = Math.round(distance);
			CharSequence text = "NA"; // TODO: replace with loading image or something
			if(distM > 1100)
				text = "" + String.format("%.1f", (distM / 1000.0)) + " km";
			else if(distM >= 0)
				text = "" + distM + " m";

			text_details.setText(text);
			TextView text_price = (TextView) vi.findViewById(R.id.park_price);
			text_price.setText("" + currParking.getCost() + " €/h");

			// From GREEN (0x30e0c0) to YELLOW@0.5 (0xffc280) to RED (0xff7080)
			int color = AppUtils.generateColorFromRank(0x30e0c0, 0xffc280, 0xff7080, currParking.getCurrRank());

			View tmp = vi.findViewById(R.id.rightColor);
			tmp.setBackgroundColor(color);

			tmp = vi.findViewById(R.id.bottomColor);
			tmp.setBackgroundColor(color);

			text_price.setTextColor(color);

			TextView tmp2 = (TextView) vi.findViewById(R.id.park_dist_by_foot);
			tmp2.setTextColor(color);

		}

		return vi;
	}
}

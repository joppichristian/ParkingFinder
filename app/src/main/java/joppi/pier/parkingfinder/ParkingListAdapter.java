package joppi.pier.parkingfinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import joppi.pier.parkingfinder.db.Parking;

/**
 * Created by christian on 18/06/16.
 */
public class ParkingListAdapter extends BaseAdapter {

    Context context;
    ArrayList<Parking> parkingList;
    private static LayoutInflater inflater = null;

    public ParkingListAdapter(Context context, ArrayList<Parking> parkingList) {
        this.context = context;
        this.parkingList = parkingList;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return parkingList.size();
    }

    @Override
    public Object getItem(int position) {
        return parkingList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.parking_list_item, null);

        TextView text_name = (TextView) vi.findViewById(R.id.park_name);
        text_name.setText(parkingList.get(position).toString());
        TextView text_details = (TextView) vi.findViewById(R.id.park_distance);

        // TODO: show km if more than x (1100m)
		double distance = parkingList.get(position).getDistance();
		CharSequence text = "" + Math.round(distance) + " m";
		if(distance == -1.0)
			text = "... m";

        text_details.setText(text);
        TextView text_price = (TextView) vi.findViewById(R.id.park_price);
        text_price.setText("" + parkingList.get(position).getCost() + " €/h");

        return vi;
    }
}

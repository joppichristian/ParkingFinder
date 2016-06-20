package joppi.pier.parkingfinder;

import android.content.Context;
import android.util.Log;
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
public class MyListAdapter extends BaseAdapter {

    Context context;
    ArrayList<Parking> data;
    private static LayoutInflater inflater = null;

    public MyListAdapter(Context context, ArrayList<Parking> data) {
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
        {
            if(data.get(position).getDistance() > 1500 && data.get(position).getDistance() <= 2000) {
                vi = inflater.inflate(R.layout.row_green, null);
                Log.w("TYPE_COLOR_GREEN",data.get(position).getDistance()+" " +data.get(position).getId());

            }
            else if(data.get(position).getDistance() > 2000 ) {
                vi = inflater.inflate(R.layout.row_red, null);
                Log.w("TYPE_COLOR_RED",data.get(position).getDistance()+" " +data.get(position).getId());
            }
            else if(data.get(position).getDistance() < 1500) {
                Log.w("TYPE_COLOR_BLUE",data.get(position).getDistance()+" " +data.get(position).getId());
                vi = inflater.inflate(R.layout.row_blue, null);
            }
        }
        TextView text_name = (TextView) vi.findViewById(R.id.park_name);
        text_name.setText(data.get(position).toString());
        TextView text_details = (TextView) vi.findViewById(R.id.park_distance);
        text_details.setText("" + Math.round(data.get(position).getDistance()) + " metri");
        TextView text_price = (TextView) vi.findViewById(R.id.park_price);
        text_price.setText("" + data.get(position).getCost() + " €");
        TextView text_capacity = (TextView) vi.findViewById(R.id.park_capacity);
        text_capacity.setText("?");

        return vi;
    }


}

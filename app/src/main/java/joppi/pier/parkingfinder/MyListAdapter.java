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
            vi = position%2==0 ? inflater.inflate(R.layout.row_green, null) : inflater.inflate(R.layout.row_red, null);
        TextView text_name = (TextView) vi.findViewById(R.id.park_name);
        text_name.setText(data.get(position).toString());
        TextView text_details = (TextView) vi.findViewById(R.id.park_details);
        text_details.setText("" + data.get(position).getCost());

        return vi;
    }


}

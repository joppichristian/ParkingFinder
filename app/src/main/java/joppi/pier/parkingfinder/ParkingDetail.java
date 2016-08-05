package joppi.pier.parkingfinder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import joppi.pier.parkingfinder.db.Coordinate;
import joppi.pier.parkingfinder.db.CoordinateDAO;
import joppi.pier.parkingfinder.db.CoordinateDAO_DB_impl;
import joppi.pier.parkingfinder.db.Parking;
import joppi.pier.parkingfinder.db.ParkingDAO;
import joppi.pier.parkingfinder.db.ParkingDAO_DB_impl;

public class ParkingDetail extends Activity {


    double lat=0,lon=0;
    String name="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_detail);
        name = getIntent().getExtras().getString("name");
        Double cost = getIntent().getExtras().getDouble("cost");
        Double dist = getIntent().getExtras().getDouble("dist");
        lat = getIntent().getExtras().getDouble("lat");
        lon = getIntent().getExtras().getDouble("long");

        TextView tx_name = (TextView)findViewById(R.id.det_name);
        tx_name.setText(name);

        TextView tx_cost = (TextView)findViewById(R.id.det_cost);
        tx_cost.setText(cost +" €");

        TextView tx_distance = (TextView)findViewById(R.id.det_dist);
        tx_distance.setText(dist.intValue() + " metri");


        Button goMap = (Button)findViewById(R.id.go_toMap);
        goMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:"+lat+","+lon+"?q="+lat+","+lon+"("+name+")");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW,gmmIntentUri );
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });


    }
}

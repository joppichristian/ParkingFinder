package joppi.pier.parkingfinder;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

import joppi.pier.parkingfinder.db.Coordinate;
import joppi.pier.parkingfinder.db.CoordinateDAO;
import joppi.pier.parkingfinder.db.CoordinateDAO_DB_impl;
import joppi.pier.parkingfinder.db.Parking;
import joppi.pier.parkingfinder.db.ParkingDAO;
import joppi.pier.parkingfinder.db.ParkingDAO_DB_impl;

public class ParkingDetail extends Activity {


    private CoordinateDAO coordinateDAO;
    private ParkingDAO parkingDAO;
    private ArrayList<Coordinate> coordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_detail);
        int id = getIntent().getExtras().getInt("Key");


        coordinateDAO = new CoordinateDAO_DB_impl();
        parkingDAO = new ParkingDAO_DB_impl();

        parkingDAO.open();
        Parking p = parkingDAO.getParking(id);
        coordinateDAO.open();

        coordinateDAO.getCoordinateOfParking(id);

        TextView name = (TextView)findViewById(R.id.det_name);
        name.setText(p.getName());


    }
}

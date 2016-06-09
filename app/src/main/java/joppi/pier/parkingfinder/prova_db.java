package joppi.pier.parkingfinder;

import android.app.Activity;
import android.app.Application;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class prova_db extends ListActivity  {
    private ParkingDAO parkingDAO;
    private CoordinateDAO coordinateDAO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova_db);
        //ParkingFinderApplication.getAppContext().deleteDatabase("parking.db");
        parkingDAO = new ParkingDAO_DB_impl();
        parkingDAO.open();
        parkingDAO.clear();
        parkingDAO.insertParking(new Parking("Autosilo", 0, 2, true, true, true, false));
        ArrayList<Parking> parking = parkingDAO.getAllParking();
        parkingDAO.close();


        coordinateDAO = new CoordinateDAO_DB_impl();
        coordinateDAO.open();
        coordinateDAO.clear();
        coordinateDAO.insertCoordinate(new Coordinate(parking.get(0).getId(), 11, 41));
        ArrayList<Coordinate> coordinates = coordinateDAO.getCoordinateOfParking(parking.get(0).getId());

        ArrayAdapter<Coordinate> adapter = new ArrayAdapter<Coordinate>(this,android.R.layout.simple_list_item_1,coordinates);
        setListAdapter(adapter);

    }
}

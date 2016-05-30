package joppi.pier.parkingfinder;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class prova_db extends ListActivity {
    private ParkingDAO parkingDAO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova_db);
        parkingDAO = new ParkingDAO_DB_impl();
        parkingDAO.open();
        parkingDAO.insertParking(new Parking("Autosilo",11,41,new ArrayList<Parking_Type>(),0));
        ArrayList<Parking> parking = parkingDAO.getAllParking();
        ArrayAdapter<Parking> adapter = new ArrayAdapter<Parking>(this,android.R.layout.simple_list_item_1,parking);
        setListAdapter(adapter);
    }
}

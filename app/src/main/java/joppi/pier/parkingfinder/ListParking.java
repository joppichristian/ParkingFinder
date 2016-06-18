package joppi.pier.parkingfinder;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ListParking extends Activity {

    private ParkingDAO parkingDAO;
    private CoordinateDAO coordinateDAO;
    private ArrayList<Parking> parking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_parking);


        parkingDAO = new ParkingDAO_DB_impl();
        parkingDAO.open();
        ArrayList<Parking> parkings = parkingDAO.getAllParking();
        parkingDAO.close();
        ListView list = (ListView)findViewById(R.id.list);
        final MyAdapter myAdapter = new MyAdapter(this,parkings);
        list.setAdapter(myAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Parking clicked = (Parking)myAdapter.getItem(position);

                Intent intent = new Intent(ListParking.this,ParkingDetail.class);
                intent.putExtra("Key",clicked.getId());
                startActivity(intent);
            }
        });


    }
}

package joppi.pier.parkingfinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import joppi.pier.parkingfinder.db.Coordinate;
import joppi.pier.parkingfinder.db.CoordinateDAO;
import joppi.pier.parkingfinder.db.CoordinateDAO_DB_impl;
import joppi.pier.parkingfinder.db.Parking;
import joppi.pier.parkingfinder.db.ParkingDAO;
import joppi.pier.parkingfinder.db.ParkingDAO_DB_impl;

import static android.location.Location.distanceBetween;

public class ListParking extends Activity {

    private ParkingDAO parkingDAO;
    private CoordinateDAO coordinateDAO;
    private ArrayList<Parking> parking;
    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_parking);

        latitude = 46.076200;
        longitude = 11.111455;


        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }*/


        parkingDAO = new ParkingDAO_DB_impl();
        parkingDAO.open();
        parking = parkingDAO.getAllParking();
        parkingDAO.close();

        final float res [] = new float[1];
        LatLng tmp = null;
        for(Parking p : parking){
            tmp = searchClosestPoint(p);
            if(tmp!=null)
            {
                distanceBetween(tmp.latitude,tmp.longitude,latitude,longitude,res);
                p.setDistance(res[0]);
                Log.w("DIST: ", p.getName() + "-"+ res[0] + "");

            }
            else{
                p.setDistance(Float.MAX_VALUE);
            }
        }

        Collections.sort(parking, new Comparator<Parking>() {
            @Override
            public int compare(Parking lhs, Parking rhs) {
                return lhs.getDistance() >= rhs.getDistance() ? 1 : -1 ;
            }
        });

        ListView list = (ListView)findViewById(R.id.list);
        final MyListAdapter myListAdapter = new MyListAdapter(this,parking);
        list.setAdapter(myListAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Parking clicked = (Parking) myListAdapter.getItem(position);

                Intent intent = new Intent(ListParking.this,ParkingDetail.class);
                intent.putExtra("name",clicked.getName());
                intent.putExtra("cost",clicked.getCost());
                intent.putExtra("dist",clicked.getDistance());
                intent.putExtra("lat",searchClosestPoint(clicked).latitude);
                intent.putExtra("long",searchClosestPoint(clicked).longitude);
                startActivity(intent);
            }
        });
    }

    /**
     * Ricerca il punto del parcheggio più vicino a te
     * @param p
     * @return Coordinate del punto
     */
    private LatLng searchClosestPoint (Parking p){
        coordinateDAO = new CoordinateDAO_DB_impl();
        coordinateDAO.open();
        ArrayList<Coordinate> coordinates = coordinateDAO.getCoordinateOfParking(p.getId());
        coordinateDAO.close();
        float distance [] = new float[1];


        LatLng tmp = null;
        double tmp_dist= Double.MAX_VALUE;
        for(Coordinate c: coordinates){
            distanceBetween(c.getLatitude(), c.getLongitude(), latitude, longitude, distance);
            if(tmp_dist > distance[0]){
                tmp_dist = distance[0];
                tmp = new LatLng(c.getLatitude(),c.getLongitude());
            }
        }
        return tmp;

    }

}


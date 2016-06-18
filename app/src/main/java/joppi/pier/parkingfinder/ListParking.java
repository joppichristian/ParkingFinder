package joppi.pier.parkingfinder;

import android.Manifest;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.location.Location.*;

public class ListParking extends Activity  {

    private ParkingDAO parkingDAO;
    private CoordinateDAO coordinateDAO;
    private ArrayList<Parking> parking;
    double latitude;
    double longitude;
    Map<Integer,Float> parking_point = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_parking);
        latitude = 46.076200;
        longitude = 11.111455;



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
        final MyAdapter myAdapter = new MyAdapter(this,parking);
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


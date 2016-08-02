package joppi.pier.parkingfinder;

import android.Manifest;
import android.app.LauncherActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import joppi.pier.parkingfinder.db.Coordinate;
import joppi.pier.parkingfinder.db.CoordinateDAO;
import joppi.pier.parkingfinder.db.CoordinateDAO_DB_impl;
import joppi.pier.parkingfinder.db.Parking;
import joppi.pier.parkingfinder.db.ParkingDAO;
import joppi.pier.parkingfinder.db.ParkingDAO_DB_impl;

import static android.location.Location.distanceBetween;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
{
	private GoogleMap mMap;

	static ArrayList<LatLng> drawPolyPts = new ArrayList<>();
	static Polygon drawPoly = null;
    private Map<String,Integer> POLYGON_CACHE = new HashMap<>();
	private ParkingDAO parkingDAO;
	private CoordinateDAO coordinateDAO;
	private ArrayList<Parking> parking;
	LatLng trento = new LatLng(46.076200, 11.111455);
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);


		parkingDAO = new ParkingDAO_DB_impl();
		parkingDAO.open();
		parking = parkingDAO.getAllParking();

		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
        ((ListView)findViewById(R.id.list)).setEnabled(false);
        SlidingUpPanelLayout slidingLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        slidingLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if(newState == SlidingUpPanelLayout.PanelState.COLLAPSED)
                    ((ListView)findViewById(R.id.list)).setEnabled(false);
                if(newState == SlidingUpPanelLayout.PanelState.EXPANDED)
                    ((ListView)findViewById(R.id.list)).setEnabled(true);
                if(newState == SlidingUpPanelLayout.PanelState.DRAGGING) {
					sortList(parking);
                    ListView list = (ListView)findViewById(R.id.list);
                    ((MyListAdapter)list.getAdapter()).notifyDataSetChanged();
				}
            }
        });
	}


	/**
	 * Manipulates the map once available.
	 * This callback is triggered when the map is ready to be used.
	 * This is where we can add markers or lines, add listeners or move the camera.
	 * If Google Play services is not installed on the device, the user will be prompted to install
	 * it inside the SupportMapFragment. This method will only be triggered once the user has
	 * installed Google Play services and returned to the app.
	 */
	@Override
	public void onMapReady(GoogleMap googleMap)
	{
		mMap = googleMap;

		// Add a marker and move the camera

		mMap.addMarker(new MarkerOptions().position(trento).title("This is Trento"));
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((trento), 16.0f));

		coordinateDAO = new CoordinateDAO_DB_impl();
		coordinateDAO.open();

		ArrayList<Coordinate> coordinates;
		ArrayList<LatLng> polygonCoordinates = new ArrayList<LatLng>();

        int cont = 0;
		for(final Parking p : parking){
			coordinates = coordinateDAO.getCoordinateOfParking(p.getId());
			polygonCoordinates.clear();
			for(Coordinate c : coordinates){
				Log.w("COORD", c.getLatitude() + ":" + c.getLongitude());
				polygonCoordinates.add(new LatLng(c.getLatitude(), c.getLongitude()));
			}

			Log.w("NUMERO:", polygonCoordinates.size() + "");
			if(polygonCoordinates.size() > 2){
                PolygonOptions pol = new PolygonOptions()
                        .addAll(polygonCoordinates)
                        .strokeColor(0x660000ff)
                        .fillColor(0x220000ff)
                        .clickable(true);
				mMap.addPolygon(pol);
                POLYGON_CACHE.put("pg"+cont,p.getId());
			} else if(polygonCoordinates.size() == 1){
				mMap.addMarker(new MarkerOptions().position(polygonCoordinates.get(0)).title(p.getName()));
			}
            cont++;
		}



		// TEST FOR ADDING PARKINGS DYNAMICALLY
		mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
		{
			@Override
			public void onMapClick(LatLng latLng)
			{
				drawPolyPts.add(latLng);
				if(drawPolyPts.size() > 1){
					if(drawPoly == null){
						drawPoly = mMap.addPolygon(new PolygonOptions()
								.addAll(drawPolyPts)
								.strokeColor(0x66ff0000)
								.fillColor(0x22ff0000)
								.clickable(true));
					} else{
						drawPoly.setPoints(drawPolyPts);
						if(isPolyComplex(drawPolyPts))
							Toast.makeText(MapsActivity.this, "Invalid selection", Toast.LENGTH_LONG).show();
					}
				}
			}
		});

		MyLocationProvider myLocProvider = new MyLocationProvider(this, getApplicationContext());
		LatLng myLoc = myLocProvider.getLatLng();
		if(myLoc != null){
			mMap.addMarker(new MarkerOptions().position(myLoc).title("My Location"));
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 16.0f));
		}

		if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
			ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
			ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
		}
		mMap.setMyLocationEnabled(true);
		mMap.getUiSettings().setZoomControlsEnabled(true);

		LatLng tmp = null;
        Integer result = 0;
		for(Parking p : parking){
			tmp = searchClosestPoint(p);
			if(tmp!=null)
			{
                try {
                    result = new RetrieveDistance().execute(trento,tmp).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                p.setDistance(result);
				Log.w("DIST: ", p.getName() + "-"+ result + "");

			}
			else{
				p.setDistance(Integer.MAX_VALUE);
			}
		}



        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener()
        {
            @Override
            public void onPolygonClick(Polygon polygon)
            {

                LatLng pt = PolygonCenter(polygon.getPoints());

				String tmp = polygon.getId();
				int id = POLYGON_CACHE.get(tmp);
                ListView list = ((ListView)findViewById(R.id.list));
                Parking p = parking.get(0);
                Parking p1 = null;
                for(Parking p_tmp: parking ){
                    if(p_tmp.getId() == id)
                        p1=p_tmp;
                }
                int index=0;
                parking.set(0,p1);
                int cont=0;
                for(Parking p_tmp: parking ){
                    if(p_tmp.getId() == id)
                        index = cont;
                    cont++;
                }
                parking.set(index,p);
                Log.w("CHANGE POS:",p.getName()+": 0"+ p1.getName() + ": "+index);
                ((MyListAdapter)list.getAdapter()).notifyDataSetChanged();
                list.setSelection(0);
                //Marker mrk = mMap.addMarker(new MarkerOptions().position(pt).title(index + " " +parking.get(0).getName()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(pt));
                //mrk.showInfoWindow();

            }
        });
        sortList(parking);
		ListView list = (ListView)findViewById(R.id.list);
		final MyListAdapter myListAdapter = new MyListAdapter(this,parking);
		list.setAdapter(myListAdapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Parking clicked = (Parking) myListAdapter.getItem(position);

				Intent intent = new Intent(MapsActivity.this,ParkingDetail.class);
				intent.putExtra("name",clicked.getName());
				intent.putExtra("cost",clicked.getCost());
				intent.putExtra("dist",clicked.getDistance());
				intent.putExtra("lat",searchClosestPoint(clicked).latitude);
				intent.putExtra("long",searchClosestPoint(clicked).longitude);
				startActivity(intent);
			}
		});
        //list.setEnabled(false);
	}

	public LatLng PolygonCenter(List<LatLng> points)
	{
		double longitude = 0;
		double latitude = 0;
		double maxlat = 0, minlat = 0, maxlon = 0, minlon = 0;
		int i = 0;
		for (LatLng p : points) {
			latitude = p.latitude;
			longitude = p.longitude;
			if (i == 0) {
				maxlat = latitude;
				minlat = latitude;
				maxlon = longitude;
				minlon = longitude;
			} else {
				if (maxlat < latitude)
					maxlat = latitude;
				if (minlat > latitude)
					minlat = latitude;
				if (maxlon < longitude)
					maxlon = longitude;
				if (minlon > longitude)
					minlon = longitude;
			}
			i++;
		}
		latitude = (maxlat + minlat) / 2;
		longitude = (maxlon + minlon) / 2;
		return new LatLng(latitude, longitude);
	}

	public static boolean isPolyComplex(List<LatLng> points)
	{
		double[] x = new double[points.size()];
		double[] y = new double[points.size()];

		for(int i=0; i< points.size(); i++)
		{
			x[i] = points.get(i).latitude;
			y[i] = points.get(i).longitude;
		}

		int i = 0, j;
		for( j = i + 2; j < x.length - 1; j++ )
			if( intersect( x, y, i, j ) )
				return true;
		for( i = 1; i < x.length; i++ )
			for( j = i + 2; j < x.length; j++ )
				if( intersect( x, y, i, j ) )
					return true;
		return false;
	}

	public static boolean intersect( final double[] x, final double[] y, int i1, int i2 )
	{
		System.out.println( "i: " + i1 + ", j: " + i2 );
		int s1 = ( i1 > 0 ) ? i1 -1 : x.length - 1;
		int s2 = ( i2 > 0 ) ? i2 -1 : x.length - 1;
		return ccw( x[ s1 ], y[ s1 ], x[ i1 ], y[ i1 ], x[ s2 ], y[ s2 ] )
				!= ccw( x[ s1 ], y[ s1 ], x[ i1 ], y[ i1 ], x[ i2 ], y[ i2 ] )
				&& ccw( x[ s2 ], y[ s2 ], x[ i2 ], y[ i2 ], x[ s1 ], y[ s1 ] )
				!= ccw( x[ s2 ], y[ s2 ], x[ i2 ], y[ i2 ], x[ i1 ], y[ i1 ] );
	}

	// Check counterclockwise
	public static boolean ccw( double p1x, double p1y, double p2x, double p2y, double p3x, double p3y )
	{
		double dx1 = p2x - p1x;
		double dy1 = p2y - p1y;
		double dx2 = p3x - p2x;
		double dy2 = p3y - p2y;
		return dy1 * dx2 < dy2 * dx1;
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
			distanceBetween(c.getLatitude(), c.getLongitude(),trento.latitude , trento.longitude, distance);
			if(tmp_dist > distance[0]){
				tmp_dist = distance[0];
				tmp = new LatLng(c.getLatitude(),c.getLongitude());
			}
		}
		return tmp;

	}
    private void sortList(ArrayList<Parking> al){
        Collections.sort(parking, new Comparator<Parking>() {
            @Override
            public int compare(Parking lhs, Parking rhs) {
                return lhs.getDistance() >= rhs.getDistance() ? 1 : -1 ;
            }
        });
    }




}

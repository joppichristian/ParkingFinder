package joppi.pier.parkingfinder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import joppi.pier.parkingfinder.db.Parking;
import joppi.pier.parkingfinder.db.ParkingMgr;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
{
	private GoogleMap mMap;

	private ParkingMgr parkingMgr;

    ListView list;
	LatLng trento = new LatLng(46.076200, 11.111455);
    private double cost_weight = 0.5;
    private double distance_weight = 0.5;
    private Parking clicked;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);

		// Obtain the SupportMapFragment and get notified when the map is ready to be used (onMapReady).
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		SlidingUpPanelLayout slidingLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
		slidingLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
			@Override
			public void onPanelSlide(View panel, float slideOffset) {

			}

			@Override
			public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
				if(newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
					list.setEnabled(false);
				}
				if(newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
					list.setEnabled(true);
				}
				if(newState == SlidingUpPanelLayout.PanelState.DRAGGING) {
					// TODO: check if executed only once!
					sortList(parkingMgr.getParkingList(),cost_weight,distance_weight);
					ListView list = (ListView)findViewById(R.id.list);
					((ParkingListAdapter)list.getAdapter()).notifyDataSetChanged();
				}
			}
		});

		// Load parking DB
		parkingMgr = new ParkingMgr(this);
		parkingMgr.loadDbAsync();

		// Disable Parking listView
		list = (ListView)findViewById(R.id.list);
		//lock_unlock_scroll(true);
		list.setEnabled(false);
	}

	/**
	 * Manipulates the map once available.
	 * This callback is triggered when the map is ready to be used.
	 */
	@Override
	public void onMapReady(GoogleMap googleMap)
	{
		mMap = googleMap;

		// DEBUG: Add a marker and move the camera
		mMap.addMarker(new MarkerOptions().position(trento).title("This is Trento"));
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((trento), 16.0f));

		// Add all parking zones to Map
		parkingMgr.addParkingListOnMap(mMap);

		// Click handler to let user add parking zones
		mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
		{
			@Override
			public void onMapClick(LatLng latLng)
			{
				parkingMgr.drawPolyClickHandler(latLng);
			}
		});

		mMap.setOnPolygonClickListener(parkingMgr);

		if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
			ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
			ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
		}
		mMap.setMyLocationEnabled(true);
		mMap.getUiSettings().setZoomControlsEnabled(true);

		// TODO: to be called inside 'LocationChanged' callback
		parkingMgr.updateDistancesAsync();

        sortList(parkingMgr.getParkingList(),cost_weight,distance_weight);

		final ParkingListAdapter myListAdapter = new ParkingListAdapter(this,parkingMgr.getParkingList());
		list.setAdapter(myListAdapter);

		//region List itemClickListener
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Parking clicked = (Parking) myListAdapter.getItem(position);
				Intent intent = new Intent(MapsActivity.this,ParkingDetail.class);
				intent.putExtra("name",clicked.getName());
				intent.putExtra("cost",clicked.getCost());
				intent.putExtra("dist",clicked.getDistance());
//				intent.putExtra("lat",searchClosestPoint(clicked).latitude);
//				intent.putExtra("long",searchClosestPoint(clicked).longitude);
				startActivity(intent);
			}
		});

        clicked = parkingMgr.getParkingList().get(0);
        ((RelativeLayout)findViewById(R.id.view_list)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this,ParkingDetail.class);
                intent.putExtra("name",clicked.getName());
                intent.putExtra("cost",clicked.getCost());
                intent.putExtra("dist",clicked.getDistance());
//                intent.putExtra("lat",searchClosestPoint(clicked).latitude);
//                intent.putExtra("long",searchClosestPoint(clicked).longitude);
                startActivity(intent);
            }
        });

		//endregion
	}

    private void sortList(ArrayList<Parking> al, final double cost_weight, final double distance_weight){
        Collections.sort(parkingMgr.getParkingList(), new Comparator<Parking>() {
            @Override
            public int compare(Parking lhs, Parking rhs) {
                return lhs.getDistance()*distance_weight+lhs.getCost()*cost_weight >= rhs.getDistance()*distance_weight+rhs.getCost()*cost_weight ? 1 : -1 ;
            }
        });
    }

	private void lock_unlock_scroll(final boolean interrupt){
		list.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_MOVE)
					return interrupt;
				return true;

			}
		});
	}
}

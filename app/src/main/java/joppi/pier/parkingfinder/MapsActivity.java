package joppi.pier.parkingfinder;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

import joppi.pier.parkingfinder.db.Parking;
import joppi.pier.parkingfinder.db.ParkingMgr;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
															  LocationProvider.OnLocationChangedListener,
															  ParkingMgr.OnDistUpdateCompleteListener,
															  SlidingUpPanelLayout.PanelSlideListener
{
	private GoogleMap mMap;
	private ParkingMgr parkingMgr;
	private LocationProvider locationProvider;
    private MenuManager menuManager;

    ListView parkingListView;
	View mSelectedParkingView;
	ParkingListAdapter mParkingListAdapter;
	LatLng trento = new LatLng(46.076200, 11.111455);

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);

		// Obtain the SupportMapFragment and get notified when the map is ready to be used (onMapReady).
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		// Add Sliding Up panel layout
		SlidingUpPanelLayout slidingLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
		slidingLayout.addPanelSlideListener(this);

		// Load parking DB
		parkingMgr = new ParkingMgr(this);
		parkingMgr.loadDbAsync();
		parkingMgr.addDistUpdateCompleteListener(this);
		parkingMgr.registerListSortComparator(new Comparator<Parking>()
		{
			@Override
			public int compare(Parking lhs, Parking rhs)
			{
				// TODO: get info from FilterActivity
				double distance_weight = 0.5;
				double cost_weight = 0.5;

				return 1;
				//lhs.getDistance() * distance_weight + lhs.getCost() * cost_weight >= rhs.getDistance() * distance_weight + rhs.getCost() * cost_weight ? 1 : -1;
			}
		});

		// Set-up parking listView
		parkingListView = (ListView)findViewById(R.id.parkingListView);
		parkingListView.setOverScrollMode(View.OVER_SCROLL_NEVER);

		// Create location provider
		locationProvider = new LocationProvider(this, getApplicationContext());
		locationProvider.addLocationChangedListener(this);

        menuManager = new MenuManager((DrawerLayout)findViewById(R.id.drawer_layout),(NavigationView)findViewById(R.id.menu),MapsActivity.this);
        SharedPreferencesManager preferencesManager = SharedPreferencesManager.getInstance(MapsActivity.this);
        Float prova = preferencesManager.getFloatPreference(SharedPreferencesManager.PREF_DISTANCE_WEIGHT);
		String prova2 = preferencesManager.getStringPreference(SharedPreferencesManager.PREF_TIME);
		String prova3 = preferencesManager.getStringPreference(SharedPreferencesManager.PREF_VEHICLE);
        Log.w("PROVA PREF: ", prova+"/"+prova2+"/"+prova3);

        ArrayList<LatLng> ar = new ArrayList<>();
        ar = Parking.parseCoordinates(parkingMgr.getParkingList().get(0).getArea());
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

		// TODO: implement parking markers and show polygons only for user reference
		mMap.setOnPolygonClickListener(parkingMgr);

		if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
			ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
		}

		mMap.setMyLocationEnabled(true);
		mMap.getUiSettings().setZoomControlsEnabled(true);

		// Sort parking list
		parkingMgr.sortList();

		mParkingListAdapter = new ParkingListAdapter(this, parkingMgr);
		parkingListView.setAdapter(mParkingListAdapter);
		parkingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				openParkingDetailActivity(position);
			}
		});

		FrameLayout layout = (FrameLayout)findViewById(R.id.frameLayout);
		mSelectedParkingView = mParkingListAdapter.getView(0, null, null);
		mSelectedParkingView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				openParkingDetailActivity(0);
			}
		});
		layout.addView(mSelectedParkingView);
	}

	private void openParkingDetailActivity(int itemPos)
	{
		Parking clicked = (Parking) mParkingListAdapter.getItem(itemPos);
		Intent intent = new Intent(MapsActivity.this,ParkingDetail.class);
		intent.putExtra("name",clicked.getName());
		intent.putExtra("cost",clicked.getCost());
		intent.putExtra("dist",clicked.getDistance());
		//				intent.putExtra("lat",searchClosestPoint(clicked).latitude);
		//				intent.putExtra("long",searchClosestPoint(clicked).longitude);
		startActivity(intent);
	}

	@Override
	public void onPanelSlide(View panel, float slideOffset)
	{
		float alpha = 1.0f - slideOffset*1.5f;
		if(alpha < 0.0f)
			alpha = 0.0f;
		mSelectedParkingView.setAlpha(alpha);
	}

	@Override
	public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
		if(newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
			mSelectedParkingView.setClickable(true);
		}
		else if(newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
			mSelectedParkingView.setClickable(false);
		}
		else if(newState == SlidingUpPanelLayout.PanelState.DRAGGING) {
			parkingMgr.sortList();
			ListView list = (ListView)findViewById(R.id.parkingListView);
			((ParkingListAdapter)list.getAdapter()).notifyDataSetChanged();
			parkingListView.setSelection(0);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		locationProvider.onStart();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		locationProvider.onDestroy();
	}

	@Override
	public void onLocationChanged(Location newLoc)
	{
		parkingMgr.updateDistancesAsync();
	}

	@Override
	public void onDistUpdateComplete()
	{
		mSelectedParkingView = mParkingListAdapter.getView(0, mSelectedParkingView, null);
		ListView list = (ListView)findViewById(R.id.parkingListView);
		((ParkingListAdapter)list.getAdapter()).notifyDataSetChanged();
	}


    public void showMenu(View v){
        menuManager.openMenu();
    }

}

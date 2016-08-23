package joppi.pier.parkingfinder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.Comparator;

import joppi.pier.parkingfinder.db.Parking;
import joppi.pier.parkingfinder.db.ParkingMgr;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
															  LocationProvider.OnLocationChangedListener,
															  ParkingMgr.UiRefreshHandler,
															  SlidingUpPanelLayout.PanelSlideListener
{
	private GoogleMap mMap;
	private ParkingMgr mParkingMgr;
	private LocationProvider locationProvider;
    private MenuManager menuManager;

    ListView parkingListView;
	View mSelectedParkingView;
	ParkingListAdapter mParkingListAdapter;

	// TODO: remove & implement on current location
	LatLng trento = new LatLng(46.062228, 11.112906);

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

		// Set-up parking listView
		parkingListView = (ListView)findViewById(R.id.parkingListView);
		parkingListView.setOverScrollMode(View.OVER_SCROLL_NEVER);

		// Create location provider
		locationProvider = new LocationProvider(this, getApplicationContext());
		locationProvider.addLocationChangedListener(this);

		// Crate options menu manager
        menuManager = new MenuManager((DrawerLayout)findViewById(R.id.drawer_layout),(NavigationView)findViewById(R.id.menu),MapsActivity.this);
    }

	/**
	 * Manipulates the map once available.
	 * This callback is triggered when the map is ready to be used.
	 */
	@Override
	public void onMapReady(GoogleMap googleMap)
	{
		mMap = googleMap;

		// Load parking DB
		mParkingMgr = new ParkingMgr(this, mMap);

		mParkingMgr.addUiRefreshHandler(this);
		mParkingMgr.registerListSortComparator(new Comparator<Parking>()
		{
			@Override
			public int compare(Parking lhs, Parking rhs)
			{
				// TODO: get info from FilterActivity
				double distance_weight = 0.5;
				double cost_weight = 0.5;

				return lhs.getCurrDistance() * distance_weight + lhs.getCost() * cost_weight >= rhs.getCurrDistance() * distance_weight + rhs.getCost() * cost_weight ? 1 : -1;
			}
		});

		// TODO: implement on current location
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((trento), 13.0f));

		// Click handler to let user add parking zones
		mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
		{
			@Override
			public void onMapClick(LatLng latLng)
			{
				mParkingMgr.drawPolyClickHandler(latLng);
			}
		});

		mMap.setOnMarkerClickListener(mParkingMgr);

		// TODO: implement permissions callback (this may crash if permission is not granted)
		if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
			ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
		}
		mMap.setMyLocationEnabled(true);
		mMap.getUiSettings().setZoomControlsEnabled(true);

		mParkingListAdapter = new ParkingListAdapter(this, mParkingMgr);
		parkingListView.setAdapter(mParkingListAdapter);
		parkingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				openParkingDetailActivity(position);
			}
		});

		FrameLayout layout = (FrameLayout)findViewById(R.id.frameLayout);
		mSelectedParkingView = mParkingListAdapter.getView(0, null, null);
		mSelectedParkingView.setVisibility(View.INVISIBLE);
		mSelectedParkingView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				openParkingDetailActivity(0);
			}
		});
		layout.addView(mSelectedParkingView);

		// TODO: delete, for DEBUG purpose only
		if(Build.FINGERPRINT.startsWith("generic"))
		{
			Location tmp = new Location("tmp");
			tmp.setLatitude(trento.latitude);
			tmp.setLongitude(trento.longitude);
			onCoarseLocationChanged(tmp);
			onFineLocationChanged(tmp);
		}
	}

	private void openParkingDetailActivity(int itemPos)
	{
		Parking clicked = (Parking) mParkingListAdapter.getItem(itemPos);
		Intent intent = new Intent(MapsActivity.this,ParkingDetail.class);
		intent.putExtra("name",clicked.getName());
		intent.putExtra("cost",clicked.getCost());
		intent.putExtra("dist",clicked.getCurrDistance());
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
	public void onFineLocationChanged(Location newLoc)
	{
		mParkingMgr.updateDistancesAsync();
	}

	@Override
	public void onCoarseLocationChanged(Location newLoc)
	{
		mParkingMgr.updateParkingListAsync(newLoc);
	}

	@Override
	public void uiRefreshHandler()
	{
		int selectedItem = mParkingMgr.getSelectedParkingIndex();
		if(selectedItem < 0){
			mSelectedParkingView.setVisibility(View.INVISIBLE);
		}
		else
		{
			mSelectedParkingView.setVisibility(View.VISIBLE);
			mSelectedParkingView = mParkingListAdapter.getView(selectedItem, mSelectedParkingView, null);
		}

		ListView list = (ListView)findViewById(R.id.parkingListView);
		((ParkingListAdapter)list.getAdapter()).notifyDataSetChanged();
	}

	// Open side options menu
    public void optionsMenuButtonClick(View v)
	{
        menuManager.openMenu();
    }

}

package joppi.pier.parkingfinder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.Calendar;

import joppi.pier.parkingfinder.db.Parking;
import joppi.pier.parkingfinder.db.ParkingMgr;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
															  LocationProvider.OnLocationChangedListener,
															  ParkingMgr.UiRefreshHandler,
															  SlidingUpPanelLayout.PanelSlideListener,
															  PlaceSelectionListener
{
	private static float PANEL_HEIGHT = 103.0f; // dp

	private GoogleMap mMap;
	private ParkingMgr mParkingMgr;
	private LocationProvider locationProvider;
	private MenuManager menuManager;

	ListView parkingListView;
	View mSelectedParkingView;
	ProgressBar mProgressBar;
	SlidingUpPanelLayout mSlidingLayout;
	ParkingListAdapter mParkingListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);

		// Obtain the SupportMapFragment and get notified when the map is ready to be used (onMapReady).
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		// Add Sliding Up panel layout
		mSlidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
		mSlidingLayout.addPanelSlideListener(this);
		mSlidingLayout.setPanelHeight((int)AppUtils.convertDpToPixel(PANEL_HEIGHT, this));

		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		mProgressBar.setVisibility(View.VISIBLE);

		// Create location provider (Slow for some reason)
		locationProvider = new LocationProvider(this);
		locationProvider.addLocationChangedListener(this);

		// Set-up parking listView
		parkingListView = (ListView) findViewById(R.id.parkingListView);
		parkingListView.setOverScrollMode(View.OVER_SCROLL_NEVER);

		// Crate options menu manager
		menuManager = new MenuManager((DrawerLayout) findViewById(R.id.drawer_layout), (NavigationView) findViewById(R.id.menu), MapsActivity.this);

		// Retrieve the PlaceAutocompleteFragment.
		PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
				getFragmentManager().findFragmentById(R.id.autocomplete_fragment);

		// Register a listener to receive callbacks when a place has been selected or an error has
		// occurred.
		autocompleteFragment.setOnPlaceSelectedListener(this);
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

		// Click handler to let user select destination
		mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
		{
			@Override
			public void onMapClick(LatLng latLng)
			{
//				mMap.addMarker(new MarkerOptions()
//						.position(latLng)
//						.title("Mia Destinazione")
//						.icon(BitmapDescriptorFactory.fromResource(R.drawable.dest_marker)));
//				mParkingMgr.setUserDestination(latLng);
			}
		});

		mMap.setOnMarkerClickListener(mParkingMgr);

		// TODO: implement permissions callback (this may crash if permission is not granted)
		if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
		}

		mMap.setMyLocationEnabled(true);
		mMap.getUiSettings().setZoomControlsEnabled(true);
		mMap.getUiSettings().setCompassEnabled(false);
		mMap.getUiSettings().setMyLocationButtonEnabled(false);

		mParkingListAdapter = new ParkingListAdapter(this, mParkingMgr);
		parkingListView.setAdapter(mParkingListAdapter);
		parkingListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				openParkingDetailActivity(position);
			}
		});

		FrameLayout layout = (FrameLayout) findViewById(R.id.frameLayout);
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
		LatLng trento = new LatLng(46.062228, 11.112906);
//		Location tmp = new Location("tmp");
//		tmp.setLatitude(trento.latitude);
//		tmp.setLongitude(trento.longitude);
//		mParkingMgr.setCurrentLocation(tmp);
//		triggerParkingListUpdate();
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((trento), 13.0f));
	}

	private void openParkingDetailActivity(int itemPos)
	{
		Parking clicked = (Parking) mParkingListAdapter.getItem(itemPos);
		Intent intent = new Intent(MapsActivity.this, ParkingDetail.class);
		intent.putExtra("name", clicked.getName());

		SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(MapsActivity.this);
		String stop = sharedPreferencesManager.getStringPreference(SharedPreferencesManager.PREF_TIME);
		String start = Calendar.getInstance().get(Calendar.HOUR) + ":" + Calendar.getInstance().get(Calendar.MINUTE);
		int today_number = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

		intent.putExtra("cost", clicked.getCost(start, stop, today_number));
		intent.putExtra("dist", clicked.getCurrDistance());
		//				intent.putExtra("lat",searchClosestPoint(clicked).latitude);
		//				intent.putExtra("long",searchClosestPoint(clicked).longitude);
		startActivity(intent);
	}

	@Override
	public void onPanelSlide(View panel, float slideOffset)
	{
		float alpha = 1.0f - slideOffset * 1.5f;
		if(alpha < 0.0f)
			alpha = 0.0f;
		mSelectedParkingView.setAlpha(alpha);
	}

	@Override
	public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState)
	{
		if(newState == SlidingUpPanelLayout.PanelState.COLLAPSED){
			mSelectedParkingView.setClickable(true);
		} else if(newState == SlidingUpPanelLayout.PanelState.EXPANDED){
			mSelectedParkingView.setClickable(false);
		} else if(newState == SlidingUpPanelLayout.PanelState.DRAGGING){
			parkingListView.setSelection(0);
		}
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		locationProvider.onStart();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		locationProvider.onDestroy();
	}

	@Override
	public void onFineLocationChanged(Location newLoc)
	{
		if(!mParkingMgr.isUserDestDefined())
			mParkingMgr.setCurrentLocation(newLoc);

		mParkingMgr.updateDistancesAsync();
	}

	@Override
	public void onCoarseLocationChanged(Location newLoc)
	{
		if(!mParkingMgr.isUserDestDefined())
			mParkingMgr.setCurrentLocation(newLoc);

        triggerParkingListUpdate();
	}

	@Override
	public void uiRefreshHandler()
	{
		// TODO: temporary implementation, not even working correctly...
		int selectedItem = mParkingMgr.getSelectedParkingIndex();
		if(selectedItem < 0){
			mSlidingLayout.setPanelHeight(0);
			mProgressBar.setVisibility(View.VISIBLE);
		} else{
			mSlidingLayout.setPanelHeight((int)AppUtils.convertDpToPixel(PANEL_HEIGHT, this));
			mProgressBar.setVisibility(View.INVISIBLE);
			mSelectedParkingView = mParkingListAdapter.getView(selectedItem, mSelectedParkingView, null);
		}
		
		ListView list = (ListView) findViewById(R.id.parkingListView);
		((ParkingListAdapter) list.getAdapter()).notifyDataSetChanged();
	}

	// Open side options menu
	public void optionsMenuButtonClick(View v)
	{
		menuManager.openMenu();
	}

	// Set current location
    public void triggerParkingListUpdate()
    {
        mParkingMgr.updateParkingListAsync();
    }
	
	public void setCurrLocationBtnClick(View v)
	{
		Location loc = mMap.getMyLocation();
		if(loc != null)
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(),
					loc.getLongitude()), 13.0f));
	}

	/**
	 * Callback invoked when a place has been selected from the PlaceAutocompleteFragment.
	 */
	@Override
	public void onPlaceSelected(Place place)
	{
		LatLng loc = place.getLatLng();
		if(loc != null)
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 13.0f));
	}

	/**
	 * Callback invoked when PlaceAutocompleteFragment encounters an error.
	 */
	@Override
	public void onError(Status status)
	{
		Toast.makeText(this, "Place selection failed: " + status.getStatusMessage(),
				Toast.LENGTH_SHORT).show();
	}
}

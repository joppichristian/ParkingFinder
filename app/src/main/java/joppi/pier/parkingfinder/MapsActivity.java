package joppi.pier.parkingfinder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
	private static float PANEL_HEIGHT = 104.0f; // dp

	private GoogleMap mMap;
	private ParkingMgr mParkingMgr;
	private LocationProvider mLocProvider;
	private MenuManager menuManager;

	Marker mDestMarker;
	Marker mDestMarkerTmp;

	ListView parkingListView;
	View mSelectedParkingView;
	ProgressBar mProgressBar;
	View mNewDestinationDialog;
	SlidingUpPanelLayout mSlidingLayout;
	ParkingListAdapter mParkingListAdapter;

	boolean mStartupCameraSet;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);

		mStartupCameraSet = true;
		mNewDestinationDialog = null;

		// Obtain the SupportMapFragment and get notified when the map is ready to be used (onMapReady).
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		// Add Sliding Up panel layout
		mSlidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
		mSlidingLayout.addPanelSlideListener(this);
		mSlidingLayout.setPanelHeight(0);

		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		//mProgressBar.setVisibility(View.VISIBLE);

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
		mDestMarker = null;
		mDestMarkerTmp = null;

		// Create location provider (Slow for some reason)
		mLocProvider = new LocationProvider(this);
		mLocProvider.addLocationChangedListener(this);

		// Load parking DB
		mParkingMgr = new ParkingMgr(this, mMap);
		mParkingMgr.addUiRefreshHandler(this);

		// Click handler to let user select destination
		mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
		{
			@Override
			public void onMapClick(LatLng latLng)
			{
				// Reset selection
				mParkingMgr.setSelection(null);
				showNewDestinationDialog(latLng);
				uiRefreshHandler();
			}
		});

		mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener()
		{
			@Override
			public void onMapLongClick(LatLng latLng)
			{
				// Reset selection
				mParkingMgr.setSelection(null);
				showNewParkingDialog(latLng);
				uiRefreshHandler();
			}
		});

		mMap.setOnMarkerClickListener(mParkingMgr);

		ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);

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
		mSelectedParkingView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
                int selectedItem = mParkingMgr.getSelectedParkingIndex();
                if(selectedItem < 0)
                {
                    selectedItem = 0;
                }
				openParkingDetailActivity(selectedItem);
			}
		});
		layout.addView(mSelectedParkingView);

		// TODO: remove, for emulator only
		mMap.getUiSettings().setZoomControlsEnabled(true);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		switch (requestCode)
		{
			case 123:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
				{
					// Permission Granted
					if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
						mMap.setMyLocationEnabled(true);
					mLocProvider.onStart();
				} else {
					// Permission Denied
					Toast.makeText(MapsActivity.this, "L'applicazione necessita dei permessi per accedere all tua locazione per funzionare correttamente", Toast.LENGTH_SHORT)
							.show();
					ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
				}
				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	private void openParkingDetailActivity(int itemPos)
	{
		Parking clicked = (Parking) mParkingListAdapter.getItem(itemPos);
		Intent intent = new Intent(MapsActivity.this, ParkingDetail.class);
		intent.putExtra("name", clicked.getName());

		SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(MapsActivity.this);
		String stop = sharedPreferencesManager.getStringPreference(SharedPreferencesManager.PREF_TIME);
		String start = Calendar.getInstance().get(Calendar.HOUR) + ":" + Calendar.getInstance().get(Calendar.MINUTE);

		intent.putExtra("cost", clicked.getCost(start, stop));
		intent.putExtra("dist", (double)clicked.getCurrDistByCar());
		intent.putExtra("lat", clicked.getLatitudeRaw());
		intent.putExtra("long",clicked.getLongitudeRaw());
        intent.putExtra("color",AppUtils.generateColorFromRank(0x30e0c0, 0xffc280, 0xff7080,clicked.getCurrRank()));
        intent.putExtra("type",clicked.getType());
        intent.putExtra("time_limit",clicked.getTimeLimit());
        intent.putExtra("time_frame",clicked.getTimeFrame());
        intent.putExtra("notes",clicked.getNotes());
		intent.putExtra("dist_foot",(double)clicked.getCurrDistByFoot());
        switch (sharedPreferencesManager.getStringPreference(SharedPreferencesManager.PREF_VEHICLE)){
            case "Automobile":intent.putExtra("places",clicked.getCar());break;
            case "Moto":intent.putExtra("places",clicked.getMoto());break;
            case "Caravan":intent.putExtra("places",clicked.getCaravan());break;
        }

		intent.putExtra("duration",clicked.getCurrDurationCar());
		intent.putExtra("address",clicked.getAddress());
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
			parkingListView.setSelection(0);
		} else if(newState == SlidingUpPanelLayout.PanelState.EXPANDED){
			mSelectedParkingView.setClickable(false);
		} else if(newState == SlidingUpPanelLayout.PanelState.DRAGGING){
			if(mParkingMgr.getSelectedParkingIndex() >= 0)
				parkingListView.setSelection(mParkingMgr.getSelectedParkingIndex());
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		mLocProvider.onDestroy();
	}

	@Override
	public void onFineLocationChanged(Location newLoc)
	{
		mParkingMgr.setCurrentLocation(newLoc);
		mParkingMgr.updateDistancesAsync();

		if(mStartupCameraSet)
		{
			setCurrLocationBtnClick(null);
			mStartupCameraSet = false;
		}
	}

	@Override
	public void onCoarseLocationChanged(Location newLoc)
	{
		mParkingMgr.setCurrentLocation(newLoc);
        triggerParkingListUpdate();
	}

	@Override
	public void uiRefreshHandler()
	{
		int selectedItem = mParkingMgr.getSelectedParkingIndex();
		if(selectedItem < 0)
		{
			if(mParkingMgr.getParkingList() != null && mParkingMgr.getParkingList().size() > 0){
				mSelectedParkingView = mParkingListAdapter.getView(0, mSelectedParkingView, null);
				mSlidingLayout.setPanelHeight((int)AppUtils.convertDpToPixel(PANEL_HEIGHT, this));
			}
			else mSlidingLayout.setPanelHeight(0);

			if(mParkingMgr.getParkingList() != null && mParkingMgr.getParkingList().size() == 0)
				Toast.makeText(this, "Nessun parcheggio nelle vicinanze",Toast.LENGTH_LONG).show();
		} else
		{
			mSlidingLayout.setPanelHeight((int)AppUtils.convertDpToPixel(PANEL_HEIGHT, this));
			mSelectedParkingView = mParkingListAdapter.getView(selectedItem, mSelectedParkingView, null);
		}
		
		ListView list = (ListView) findViewById(R.id.parkingListView);
		((ParkingListAdapter) list.getAdapter()).notifyDataSetChanged();

		mProgressBar.setVisibility(View.INVISIBLE);
	}

	// Open side options menu
	public void optionsMenuButtonClick(View v)
	{
		menuManager.openMenu();
	}

	private void showNewDestinationDialog(LatLng latLng)
	{
		mDestMarkerTmp = mMap.addMarker(new MarkerOptions()
				.position(latLng)
				.title("Mia Destinazione")
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.dest_marker)));

		Projection projection = mMap.getProjection();
		Point pt = projection.toScreenLocation(latLng);

		final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.mapsFrameLayout);
		mNewDestinationDialog = getLayoutInflater().inflate(R.layout.map_custom_dialog, null);

		TextView txtView = (TextView) mNewDestinationDialog.findViewById(R.id.mapCustomDialogText);
		txtView.setText(getResources().getString(R.string.newDestText));
		View btnOk = mNewDestinationDialog.findViewById(R.id.mapCustomDialogOk);
		btnOk.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onNewDestinationDialogClick();
			}
		});

		frameLayout.addView(mNewDestinationDialog);

		View back = findViewById(R.id.newDestBackView);
		back.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Cancel new destination dialog
				frameLayout.removeView(mNewDestinationDialog);
				v.setClickable(false);
				mDestMarkerTmp.remove();
			}
		});

		int width = (int)AppUtils.convertDpToPixel(210, MapsActivity.this);
		mNewDestinationDialog.setPadding(pt.x - width/2, pt.y - 350, 0, 0);
	}

	// Set new user destination
	private void  onNewDestinationDialogClick()
	{
		if(mDestMarker != null)
			mDestMarker.remove();

		mDestMarker = mMap.addMarker(new MarkerOptions()
				.position(mDestMarkerTmp.getPosition())
				.title("Mia Destinazione")
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.dest_marker)));
		mDestMarkerTmp.remove();
		mDestMarker.showInfoWindow();

		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.mapsFrameLayout);
		frameLayout.removeView(mNewDestinationDialog);
		View back = findViewById(R.id.newDestBackView);
		back.setClickable(false);

		mParkingMgr.setUserDestination(mDestMarker.getPosition());
		triggerParkingListUpdate();
	}

	private void showNewParkingDialog(LatLng latLng)
	{
		final Marker newParkMarker = mMap.addMarker(new MarkerOptions()
				.position(latLng)
				.title("")
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.dest_marker)));

		Projection projection = mMap.getProjection();
		Point pt = projection.toScreenLocation(latLng);

		final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.mapsFrameLayout);
		mNewDestinationDialog = getLayoutInflater().inflate(R.layout.map_custom_dialog, null);

		TextView txtView = (TextView) mNewDestinationDialog.findViewById(R.id.mapCustomDialogText);
		txtView.setText(getResources().getString(R.string.newParkingText));
		View btnOk = mNewDestinationDialog.findViewById(R.id.mapCustomDialogOk);
		btnOk.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				newParkMarker.remove();
				onNewParkingDialogClick();
			}
		});

		frameLayout.addView(mNewDestinationDialog);

		View back = findViewById(R.id.newDestBackView);
		back.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Cancel new destination dialog
				frameLayout.removeView(mNewDestinationDialog);
				v.setClickable(false);
				newParkMarker.remove();
			}
		});

		int width = (int)AppUtils.convertDpToPixel(210, MapsActivity.this);
		mNewDestinationDialog.setPadding(pt.x - width/2, pt.y - 350, 0, 0);
	}

	private void onNewParkingDialogClick()
	{
		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.mapsFrameLayout);
		frameLayout.removeView(mNewDestinationDialog);
		View back = findViewById(R.id.newDestBackView);
		back.setClickable(false);

		// Open new parking view
        Intent newIntent = new Intent(MapsActivity.this,AddParking.class);
        startActivity(newIntent);
	}

	// Set current location
    public void triggerParkingListUpdate()
	{
		mProgressBar.setVisibility(View.VISIBLE);

        mParkingMgr.updateParkingListAsync();
    }
	
	public void setCurrLocationBtnClick(View v)
	{
		if(mLocProvider.getCurrentLatLng() != null)
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLocProvider.getCurrentLatLng(), 13.0f));
	}

	/**
	 * Callback invoked when a place has been selected from the PlaceAutocompleteFragment.
	 */
	@Override
	public void onPlaceSelected(Place place)
	{
		final LatLng loc = place.getLatLng();
		if(loc != null)
		{
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 14.0f), new GoogleMap.CancelableCallback() {
				@Override
				public void onFinish() {
					showNewDestinationDialog(loc);
				}

				@Override
				public void onCancel() {

				}
			});
		}
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

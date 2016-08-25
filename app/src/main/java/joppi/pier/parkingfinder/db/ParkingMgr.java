package joppi.pier.parkingfinder.db;

import android.app.Activity;
import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import joppi.pier.parkingfinder.AppUtils;
import joppi.pier.parkingfinder.DistanceMatrixAPI;
import joppi.pier.parkingfinder.DistanceMatrixResult;
import joppi.pier.parkingfinder.SharedPreferencesManager;

public class ParkingMgr implements GoogleMap.OnMarkerClickListener
{
	GoogleMap mMap;
	Activity mapsActivity;
	ArrayList<Parking> mParkingList;
	Map<Marker, Parking> parkingMarkersHashMap;

	Location mCurrLocation;
	boolean mUserDefLocation;
	Parking mSelectedParking;
	Thread mUpdateDistancesThread;
	SharedPreferencesManager mPrefManager;
	private final Semaphore listAccessSema = new Semaphore(1, true);

	public interface UiRefreshHandler
	{
		void uiRefreshHandler();
	}

	// Called when Ui refresh is needed.
	private List<UiRefreshHandler> mUiRefreshHandlers = new ArrayList<>();

	private Comparator<Parking> mParkingListComparator;

	public ParkingMgr(Activity activity, GoogleMap map)
	{
		mapsActivity = activity;
		mMap = map;

		mParkingList = null;
		mParkingListComparator = null;
		mSelectedParking = null;
		parkingMarkersHashMap = new HashMap<>();
		mCurrLocation = null;
		mUserDefLocation = false;

		mPrefManager = SharedPreferencesManager.getInstance(mapsActivity);

		mParkingListComparator = new Comparator<Parking>()
		{
			@Override
			public int compare(Parking lhs, Parking rhs)
			{
				// TODO: move prefs elsewhere
				String stop = mPrefManager.getStringPreference(SharedPreferencesManager.PREF_TIME);
				String start = Calendar.getInstance().get(Calendar.HOUR)+":"+Calendar.getInstance().get(Calendar.MINUTE);
				double cost_weight = mPrefManager.getFloatPreference(SharedPreferencesManager.PREF_COST_WEIGHT);
				double distance_weight = mPrefManager.getFloatPreference(SharedPreferencesManager.PREF_DISTANCE_WEIGHT);

				int today_number = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
				return lhs.getCurrDistance() * distance_weight + lhs.getCost(start,stop,today_number) * cost_weight >= rhs.getCurrDistance() * distance_weight + rhs.getCost(start,stop,today_number) * cost_weight ? 1 : -1;
			}
		};
	}

	public Parking getSelectedParking()
	{
		return mSelectedParking;
	}

	public int getSelectedParkingIndex()
	{
		try{
			return mParkingList.indexOf(mSelectedParking);
		}catch(Exception e)
		{e.printStackTrace();}
		return -1;
	}

	public ArrayList<Parking> getParkingList()
	{
		// We might incur in concurrent modifications on the list
		try{
			listAccessSema.acquire();
		}catch(InterruptedException e){
			return null;
		}

		ArrayList<Parking> list = mParkingList;
		listAccessSema.release();

		return list;
	}

	public void setSelection(int index)
	{
		if(mParkingList != null && index >= 0 && index < mParkingList.size())
			mSelectedParking = mParkingList.get(index);
	}

	public void setCurrentLocation(Location loc)
	{
		if(loc != null)
			mCurrLocation = loc;
	}

	public void setUserDestination(LatLng loc)
	{
		Location tmp = null;
		if(loc != null){
			tmp = new Location("");
			tmp.setLatitude(loc.latitude);
			tmp.setLongitude(loc.longitude);
		}
		setUserDestination(tmp);
	}

	public void setUserDestination(Location loc)
	{
		if(loc == null)
			mUserDefLocation = false;
		else
		{
			mUserDefLocation = true;
			mCurrLocation = loc;
		}
	}

	public boolean isUserDestDefined()
	{
		return mUserDefLocation;
	}

	public void updateParkingListAsync()
	{
		// Start separated thread
		(new Thread(mUpdateParkingListTask)).start();
	}

	public void updateDistancesAsync()
	{
		// If task is already running discard request
		if(mUpdateDistancesThread != null && mUpdateDistancesThread.isAlive())
			return;

		// Start separated thread (if not already running)
		mUpdateDistancesThread = new Thread(mUpdateDistancesTask);
		mUpdateDistancesThread.start();
	}

	// TODO: temporary implementation
	static Polygon drawPoly = null;
	static ArrayList<LatLng> drawPolyPts = new ArrayList<>();

	public void drawPolyClickHandler(LatLng latLng)
	{
		//		drawPolyPts.add(latLng);
		//		if(drawPolyPts.size() > 1){
		//			if(drawPoly == null){
		//				drawPoly = mMap.addPolygon(new PolygonOptions()
		//						.addAll(drawPolyPts)
		//						.strokeColor(0x66ff0000)
		//						.fillColor(0x22ff0000)
		//						.clickable(true));
		//			} else{
		//				drawPoly.setPoints(drawPolyPts);
		//				if(isPolyComplex(drawPolyPts))
		//					Toast.makeText(mapsActivity, "Invalid selection", Toast.LENGTH_LONG).show();
		//			}
		//		}
	}

	// TODO: Area is displayed to user only after click on parking and just for reference
	@Override
	public boolean onMarkerClick(Marker marker)
	{
		// Set current selection
		mSelectedParking = parkingMarkersHashMap.get(marker);

		// Force UI refresh
		mapsActivity.runOnUiThread(mDispatchUiRefreshHandlers);
		return false;
	}

	public void addUiRefreshHandler(UiRefreshHandler handler)
	{
		mUiRefreshHandlers.add(handler);
	}

	public void sortList()
	{
		if(mParkingListComparator != null)
		{
			// We might incur in concurrent modifications on the list
			try{
				listAccessSema.acquire();

				Collections.sort(mParkingList, mParkingListComparator);
			}catch(Exception e)
			{e.printStackTrace();}

			listAccessSema.release();
		}
	}

	private void updateRanks()
	{
		try{
			listAccessSema.acquire();
		}catch(InterruptedException e)
		{e.printStackTrace();}

		for(int i = 0; i < mParkingList.size(); i++){
			Parking p = mParkingList.get(i);

			double rank = 1.0 / (mParkingList.size() - 1) * i;
			p.setCurrRank(rank);
		}

		listAccessSema.release();
	}

	private void updateParkingMarkers()
	{
		mMap.clear();
		parkingMarkersHashMap.clear();

		// Add parking markers
		for(Parking parking : mParkingList)
		{
			BitmapDescriptor bd = AppUtils.getCustomParkingMarker(parking.getCurrRank(), parking);

			Marker newMarker = mMap.addMarker(new MarkerOptions()
					.position(parking.getLocation())
					.title(parking.getName())
					.icon(bd));
			parkingMarkersHashMap.put(newMarker, parking);
		}
	}

	private Runnable mUpdateParkingListTask = new Runnable()
	{
		@Override
		public void run()
		{
			// Load parking DB
			ParkingDAO parkingDAO = new ParkingDAO_DB_impl();
			parkingDAO.open();

			try{
				listAccessSema.acquire();
			}catch(InterruptedException e)
			{e.printStackTrace();}

			if(mCurrLocation != null)
			{
				String vehicle = mPrefManager.getStringPreference(SharedPreferencesManager.PREF_VEHICLE);
				int radius = mPrefManager.getIntPreference(SharedPreferencesManager.PREF_RADIUS);

				mParkingList = parkingDAO.getParkingList(mCurrLocation, radius, vehicle);

				int type_mask = AppUtils.getPerfTypeMask(mPrefManager);
				int spec_mask = AppUtils.getPrefSpecMask(mPrefManager);
				for(int i = 0; i < mParkingList.size(); i++)
				{
					int type = mParkingList.get(i).getType();
					if(((type & type_mask) == 0)){
						mParkingList.remove(i);
						i--;
					} else if(((type & Parking.SPEC_TIME_LIMIT & spec_mask) == 0) && ((type & Parking.SPEC_TIME_LIMIT) != 0)){
						mParkingList.remove(i);
						i--;
					} else if(((type & Parking.SPEC_SURVEILED & spec_mask) == 0) && ((spec_mask & Parking.SPEC_SURVEILED) != 0)){
						mParkingList.remove(i);
						i--;
					}
				}
			}

			parkingDAO.close();

			listAccessSema.release();

			// Force distance update (with subseq UI refresh etc...)
			mUpdateDistancesThread = new Thread(mUpdateDistancesTask);
			mUpdateDistancesThread.start();
		}
	};

	private Runnable mUpdateDistancesTask = new Runnable()
	{
		@Override
		public void run()
		{
			try{
				listAccessSema.acquire();
			}catch(InterruptedException e)
			{e.printStackTrace();}

			if(mParkingList == null){
				listAccessSema.release();
				return;
			}

			for(Parking parking : mParkingList)
			{
				try
				{
					LatLng tmp = new LatLng(mCurrLocation.getLatitude(), mCurrLocation.getLongitude());

					DistanceMatrixResult queryResult = new DistanceMatrixAPI("").exec(tmp, parking.getLocation());
					if(queryResult.getStatusOk())
						parking.setCurrDistance(queryResult.getDistance());
					else
						Toast.makeText(mapsActivity, "DistanceMatrix error: " + queryResult.getStatusText(), Toast.LENGTH_LONG).show();
				}catch(Exception e){
					e.printStackTrace();
				}
			}

			listAccessSema.release();

			// Sort parking list
			sortList();

			updateRanks();

			// If nothing is selected
			if(getSelectedParkingIndex() < 0)
				setSelection(0);

			mapsActivity.runOnUiThread(mDispatchUiRefreshHandlers);
		}
	};

	private Runnable mDispatchUiRefreshHandlers = new Runnable()
	{
		@Override
		public void run()
		{
			for(UiRefreshHandler l : mUiRefreshHandlers){
				l.uiRefreshHandler();
			}

			updateParkingMarkers();
		}
	};
}

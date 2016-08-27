package joppi.pier.parkingfinder.db;

import android.app.Activity;
import android.location.Location;
import android.os.Looper;
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
import joppi.pier.parkingfinder.R;
import joppi.pier.parkingfinder.SharedPreferencesManager;

public class ParkingMgr implements GoogleMap.OnMarkerClickListener
{
	GoogleMap mMap;
	Activity mapsActivity;
	ArrayList<Parking> mParkingList;
	Map<Marker, Parking> parkingMarkersHashMap;

	Location mCurrLocation;
	Location mUserDefLocation;
	Parking mSelectedParking;
	Thread mUpdateDistancesThread;
	DistanceMatrixResult mQueryResult;
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
		mUserDefLocation = null;
		mQueryResult = null;

		mPrefManager = SharedPreferencesManager.getInstance(mapsActivity);

		// TODO: include distance by foot
		mParkingListComparator = new Comparator<Parking>()
		{
			@Override
			public int compare(Parking lhs, Parking rhs)
			{
				String stop = mPrefManager.getStringPreference(SharedPreferencesManager.PREF_TIME);
				String start = Calendar.getInstance().get(Calendar.HOUR)+":"+Calendar.getInstance().get(Calendar.MINUTE);
				double cost_weight = mPrefManager.getFloatPreference(SharedPreferencesManager.PREF_COST_WEIGHT);
				double distance_weight = mPrefManager.getFloatPreference(SharedPreferencesManager.PREF_DISTANCE_WEIGHT);

				int today_number = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
				return lhs.getCurrDistByCar() * distance_weight + lhs.getCost(start,stop,today_number) * cost_weight >= rhs.getCurrDistByCar() * distance_weight + rhs.getCost(start,stop,today_number) * cost_weight ? 1 : -1;
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

	public void setSelection(Parking parking)
	{
		mSelectedParking = parking;
	}

	public void setSelection(int index)
	{
		if(mParkingList != null && index >= 0 && index < mParkingList.size())
			mSelectedParking = mParkingList.get(index);
		else if (index == -1)
			mSelectedParking = null;
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
		mUserDefLocation = loc;
	}

	public boolean isUserDestDefined()
	{
		return (mUserDefLocation == null);
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
			try
			{
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
		for(Marker m : parkingMarkersHashMap.keySet())
			m.remove();
		parkingMarkersHashMap.clear();

		try{
			listAccessSema.acquire();
		}catch(InterruptedException e)
		{e.printStackTrace();}

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

		listAccessSema.release();
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

				// Search location radius based on user destination if provided
				Location searchLoc = mCurrLocation;
				if(mUserDefLocation != null)
					searchLoc = mUserDefLocation;
				mParkingList = parkingDAO.getParkingList(searchLoc, radius, vehicle);

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

			// Build destinations list
			LatLng[] destinations = new LatLng[mParkingList.size()];

			for(int i=0; i<mParkingList.size(); i++)
				destinations[i] = mParkingList.get(i).getLocation();

			listAccessSema.release();

			mQueryResult = null;
			DistanceMatrixResult queryResultFoot = null;

			try
			{
				LatLng currLoc = new LatLng(mCurrLocation.getLatitude(), mCurrLocation.getLongitude());
				mQueryResult = new DistanceMatrixAPI(mapsActivity.getResources().getString(R.string.google_server_key)).setOrigins(currLoc).setDestinations(destinations).exec();

				if(mUserDefLocation != null)
				{
					LatLng destLoc = new LatLng(mUserDefLocation.getLatitude(), mUserDefLocation.getLongitude());
					queryResultFoot = new DistanceMatrixAPI(mapsActivity.getResources().getString(R.string.google_server_key)).setOrigins(destLoc).setDestinations(destinations).setTravelMode(DistanceMatrixAPI.MODE_WALKING).exec();
				}
			}catch(Exception e){
				e.printStackTrace();
			}

			if(mQueryResult != null)
			{
				if(mQueryResult.getStatusOk())
				{
					try{
						listAccessSema.acquire();
					}catch(InterruptedException e)
					{e.printStackTrace();}

					// Set updated information
					for(int i=0; i<mParkingList.size(); i++)
					{
						Parking parking = mParkingList.get(i);
						DistanceMatrixResult.ResultElement elem = mQueryResult.getElement(i);

						parking.setCurrDistByCar(elem.getDistance());
						parking.setCurrDurationCar(elem.getDuration());

						if(queryResultFoot != null)
						{
							DistanceMatrixResult.ResultElement elemFoot = queryResultFoot.getElement(i);
							parking.setCurrDistByFoot(elemFoot.getDistance());
						}
						else parking.setCurrDistByFoot(-1); // Not available
					}

					listAccessSema.release();
				} else{
					Looper.prepare();
					mapsActivity.runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							Toast.makeText(mapsActivity, "DistanceMatrix error: " + mQueryResult.getStatusText(), Toast.LENGTH_LONG).show();
						}
					});
				}

				// Sort parking list
				sortList();

				updateRanks();
			}

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

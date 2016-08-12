package joppi.pier.parkingfinder.db;

import android.app.Activity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import joppi.pier.parkingfinder.AppUtils;
import joppi.pier.parkingfinder.DistanceMatrixAPI;
import joppi.pier.parkingfinder.DistanceMatrixResult;

public class ParkingMgr implements GoogleMap.OnMarkerClickListener
{
	GoogleMap mMap;
	Activity mapsActivity;
	ArrayList<Parking> mParkingList;
	Map<Marker, Parking> parkingMarkersHashMap;

	Parking mSelectedParking;

	Thread mUpdateDistancesThread;

	private final Semaphore listAccessSema = new Semaphore(1, true);

	public interface UiRefreshHandler
	{
		void onUiRefreshHandler();
	}

	// Called when Ui refresh is needed.
	private List<UiRefreshHandler> mUiRefreshHandlers = new ArrayList<>();

	private Comparator<Parking> mParkingListComparator;

	public ParkingMgr(Activity activity)
	{
		mapsActivity = activity;
		mParkingList = null;
		mParkingListComparator = null;
		mSelectedParking = null;
		parkingMarkersHashMap = new HashMap<>();
	}

	public Parking getSelectedParking()
	{
		return mSelectedParking;
	}

	public int getSelectedParkingIndex()
	{
		try{
			return mParkingList.indexOf(mSelectedParking);
		}catch(Exception e){}
		return -1;
	}

	public ArrayList<Parking> getParkingList()
	{
		if(!mLoadDbTask.isAlive())
			return mParkingList;
		return null;
	}

	public void setSelection(int index)
	{
		if(mParkingList != null && index >= 0 && index < mParkingList.size())
			mSelectedParking = mParkingList.get(index);
	}

	public void loadDbAsync()
	{
		// Start separated thread
		mLoadDbTask.start();
	}

	// TODO: Area is displayed to user only after click on parking and just for reference
	// TODO: Markers color should depend only on price (distance is already shown right?)
	public void addParkingListOnMap(GoogleMap map)
	{
		mMap = map;

		// Have to do this on the UI thread
		// mapsActivity.runOnUiThread(mUpdateParkingMarkers);
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

	@Override
	public boolean onMarkerClick(Marker marker)
	{
		// Set current selection
		mSelectedParking = parkingMarkersHashMap.get(marker);
		mapsActivity.runOnUiThread(mDispatchOnUiRefreshHandlers);
		return false;
	}

	public void addUiRefreshHandler(UiRefreshHandler handler)
	{
		mUiRefreshHandlers.add(handler);
	}

	public void registerListSortComparator(Comparator<Parking> sortComparator)
	{
		mParkingListComparator = sortComparator;
	}

	public void sortList()
	{
		if(mParkingListComparator != null)
		{
			// We might incur in concurrent modifications on the list
			try{
				listAccessSema.acquire();

				Collections.sort(mParkingList, mParkingListComparator);

				listAccessSema.release();
			}catch(Exception e){}
		}
	}

	private void updateParkingMarkers()
	{
		mMap.clear();
		parkingMarkersHashMap.clear();

		try{
			listAccessSema.acquire();
		}catch(InterruptedException e){}

		// Add parkings markers
		for(Parking parking : mParkingList)
		{
			// Check price rank only?
			BitmapDescriptor bd = AppUtils.getCustomParkingMarker(parking.getCurrentPriceRank());


			Marker newMarker = mMap.addMarker(new MarkerOptions()
					.position(parking.getLocation())
					.title(parking.getName())
					.icon(bd));
			parkingMarkersHashMap.put(newMarker, parking);
		}
		listAccessSema.release();
	}

	private Thread mLoadDbTask = new Thread()
	{
		@Override
		public void run()
		{
			// Load parking DB
			ParkingDAO parkingDAO = new ParkingDAO_DB_impl();
			parkingDAO.open();

			try{
				listAccessSema.acquire();
			}catch(InterruptedException e){}

			mParkingList = parkingDAO.getParkingList();

			parkingDAO.close();

			listAccessSema.release();
		}
	};

	private Runnable mUpdateDistancesTask = new Runnable()
	{
		@Override
		public void run()
		{
			try{
				listAccessSema.acquire();
			}catch(InterruptedException e){}

			for(Parking parking: mParkingList)
			{
				try
				{
					// TODO: remove this and implement methods on current location
					LatLng trento = new LatLng(46.062228, 11.112906);

					DistanceMatrixResult queryResult = new DistanceMatrixAPI("").exec(trento, parking.getLocation());
					if(queryResult.getStatusOk())
						parking.setCurrDistance(queryResult.getDistance());
				}catch(Exception e){
					e.printStackTrace();
				}
			}

			listAccessSema.release();

			// Sort parking list
			sortList();

			// If nothing is selected
			if(getSelectedParkingIndex() < 0)
				setSelection(0);

			mapsActivity.runOnUiThread(mDispatchOnUiRefreshHandlers);
		}
	};

	private Runnable mDispatchOnUiRefreshHandlers = new Runnable()
	{
		@Override
		public void run()
		{
			for(UiRefreshHandler l : mUiRefreshHandlers){
				l.onUiRefreshHandler();
			}
			updateParkingMarkers();
		}
	};
}

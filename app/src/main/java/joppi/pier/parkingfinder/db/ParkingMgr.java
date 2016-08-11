package joppi.pier.parkingfinder.db;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import joppi.pier.parkingfinder.DistanceMatrixAPI;

import static android.location.Location.distanceBetween;

public class ParkingMgr implements GoogleMap.OnPolygonClickListener
{
	GoogleMap mMap;
	Activity mapsActivity;
	ParkingDAO parkingDAO;
	CoordinateDAO coordinateDAO;
	ArrayList<Parking> parkingList;

	Thread mUpdateDistancesThread;
	Map<String, Integer> POLYGON_CACHE = new HashMap<>();

	private final Semaphore listAccessSema = new Semaphore(1, true);

	public interface OnDistUpdateCompleteListener
	{
		void onDistUpdateComplete();
	}

	// Called when distance update is completed.
	private List<OnDistUpdateCompleteListener> mDistUpdateCompleteListeners = new ArrayList<>();

	private Comparator<Parking> mParkingListComparator;

	static Polygon drawPoly = null;
	static ArrayList<LatLng> drawPolyPts = new ArrayList<>();

	// TODO: remove this and implement methods on current location
	LatLng trento = new LatLng(46.076200, 11.111455);


	public ParkingMgr(Activity activity)
	{
		mapsActivity = activity;
		parkingList = null;
		mParkingListComparator = null;
	}

	public ArrayList<Parking> getParkingList()
	{
		if(!mLoadDbTask.isAlive())
			return parkingList;
		return null;
	}

	public void loadDbAsync()
	{
		// Start separated thread
		mLoadDbTask.start();
	}

	// TODO: Show markers only as parking
	// TODO: Markers color should depend only on price (distance is already shown right?)
	public void addParkingListOnMap(GoogleMap map)
	{
		mMap = map;

		// Have to do this on the UI thread
		mapsActivity.runOnUiThread(mAddParkingToMapTask);
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
	public void drawPolyClickHandler(LatLng latLng)
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
					Toast.makeText(mapsActivity, "Invalid selection", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onPolygonClick(Polygon polygon)
	{
		//		LatLng pt = PolygonCenter(polygon.getPoints());
		//
		//		String tmp = polygon.getId();
		//		int id = POLYGON_CACHE.get(tmp);
		//		Parking p = parking.get(0);
		//		Parking p1 = null;
		//		for(Parking p_tmp: parking ){
		//			if(p_tmp.getId() == id)
		//				p1=p_tmp;
		//		}
		//		int index=0;
		//		parking.set(0,p1);
		//		int cont=0;
		//		for(Parking p_tmp: parking ){
		//			if(p_tmp.getId() == id)
		//				index = cont;
		//			cont++;
		//		}
		//		parking.set(index,p);
		//		clicked = parking.get(0);
		//		Log.w("CHANGE POS:",p.getName()+": 0"+ p1.getName() + ": "+index);
		//		((MyListAdapter)list.getAdapter()).notifyDataSetChanged();
		//		list.setSelection(0);
		//		//Marker mrk = mMap.addMarker(new MarkerOptions().position(pt).title(index + " " +parking.get(0).getName()));
		//		mMap.moveCamera(CameraUpdateFactory.newLatLng(pt));
		//		//mrk.showInfoWindow();

	}

	public void addDistUpdateCompleteListener(OnDistUpdateCompleteListener listener)
	{
		mDistUpdateCompleteListeners.add(listener);
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

				Collections.sort(parkingList, mParkingListComparator);

				listAccessSema.release();
			}catch(Exception e){}
		}
	}

	private Thread mLoadDbTask = new Thread()
	{
		@Override
		public void run()
		{
			// Load parking DB
			parkingDAO = new ParkingDAO_DB_impl();
			parkingDAO.open();
			try{
				listAccessSema.acquire();
			}catch(InterruptedException e){}
			parkingList = parkingDAO.getAllParking();
			listAccessSema.release();

			coordinateDAO = new CoordinateDAO_DB_impl();
			coordinateDAO.open();
		}
	};

	private Thread mAddParkingToMapTask = new Thread()
	{
		@Override
		public void run()
		{
			try{
				listAccessSema.acquire();
			}catch(InterruptedException e){}

			// Add parkings markers
			int count = 0;
			ArrayList<Coordinate> coordinates;
			ArrayList<LatLng> polygonCoordinates = new ArrayList<LatLng>();
			for(final Parking p : parkingList){
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
					POLYGON_CACHE.put("pg" + count, p.getId());
				} else if(polygonCoordinates.size() == 1){
					mMap.addMarker(new MarkerOptions().position(polygonCoordinates.get(0)).title(p.getName()));
				}
				count++;
			}
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

			for(int i = 0; i < parkingList.size(); i++){
				Parking p = parkingList.get(i);
				LatLng tmp = searchClosestPoint(p);
				if(tmp != null){
					int result = 0;
					try{
						result = new DistanceMatrixAPI().getDistanceMatrix(trento, tmp);
					}catch(Exception e){
						e.printStackTrace();
					}

					if(result != 0)
						p.setDistance(result);
					Log.w("DIST. UPDATE: ", p.getName() + "-" + result + "");
				}
			}
			listAccessSema.release();

			sortList();

			mapsActivity.runOnUiThread(mDispatchOnDistUpdateCompleteTask);
		}
	};

	private Runnable mDispatchOnDistUpdateCompleteTask = new Runnable()
	{
		@Override
		public void run()
		{
			for(OnDistUpdateCompleteListener l : mDistUpdateCompleteListeners){
				l.onDistUpdateComplete();
			}
		}
	};

	// TODO: All parkings should have one or more "entry points",
	// TODO: area is displayed to user only after click on parking and just for reference

	/**
	 * Ricerca il punto del parcheggio più vicino a te
	 *
	 * @param p
	 * @return Coordinate del punto
	 */
	private LatLng searchClosestPoint(Parking p)
	{
		coordinateDAO = new CoordinateDAO_DB_impl();
		coordinateDAO.open();
		ArrayList<Coordinate> coordinates = coordinateDAO.getCoordinateOfParking(p.getId());
		coordinateDAO.close();
		float distance[] = new float[1];


		LatLng tmp = null;
		double tmp_dist = Double.MAX_VALUE;
		for(Coordinate c : coordinates){
			distanceBetween(c.getLatitude(), c.getLongitude(), trento.latitude, trento.longitude, distance);
			if(tmp_dist > distance[0]){
				tmp_dist = distance[0];
				tmp = new LatLng(c.getLatitude(), c.getLongitude());
			}
		}
		return tmp;
	}

	//region Polygon rel. methods

	public LatLng PolygonCenter(List<LatLng> points)
	{
		double longitude = 0;
		double latitude = 0;
		double maxlat = 0, minlat = 0, maxlon = 0, minlon = 0;
		int i = 0;
		for(LatLng p : points){
			latitude = p.latitude;
			longitude = p.longitude;
			if(i == 0){
				maxlat = latitude;
				minlat = latitude;
				maxlon = longitude;
				minlon = longitude;
			} else{
				if(maxlat < latitude)
					maxlat = latitude;
				if(minlat > latitude)
					minlat = latitude;
				if(maxlon < longitude)
					maxlon = longitude;
				if(minlon > longitude)
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

		for(int i = 0; i < points.size(); i++){
			x[i] = points.get(i).latitude;
			y[i] = points.get(i).longitude;
		}

		int i = 0, j;
		for(j = i + 2; j < x.length - 1; j++)
			if(intersect(x, y, i, j))
				return true;
		for(i = 1; i < x.length; i++)
			for(j = i + 2; j < x.length; j++)
				if(intersect(x, y, i, j))
					return true;
		return false;
	}

	public static boolean intersect(final double[] x, final double[] y, int i1, int i2)
	{
		System.out.println("i: " + i1 + ", j: " + i2);
		int s1 = (i1 > 0) ? i1 - 1 : x.length - 1;
		int s2 = (i2 > 0) ? i2 - 1 : x.length - 1;
		return ccw(x[s1], y[s1], x[i1], y[i1], x[s2], y[s2])
				!= ccw(x[s1], y[s1], x[i1], y[i1], x[i2], y[i2])
				&& ccw(x[s2], y[s2], x[i2], y[i2], x[s1], y[s1])
				!= ccw(x[s2], y[s2], x[i2], y[i2], x[i1], y[i1]);
	}

	// Check counterclockwise
	public static boolean ccw(double p1x, double p1y, double p2x, double p2y, double p3x, double p3y)
	{
		double dx1 = p2x - p1x;
		double dy1 = p2y - p1y;
		double dx2 = p3x - p2x;
		double dy2 = p3y - p2y;
		return dy1 * dx2 < dy2 * dx1;
	}

	//endregion
}

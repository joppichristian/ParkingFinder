package joppi.pier.parkingfinder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

import joppi.pier.parkingfinder.db.Coordinate;
import joppi.pier.parkingfinder.db.CoordinateDAO;
import joppi.pier.parkingfinder.db.CoordinateDAO_DB_impl;
import joppi.pier.parkingfinder.db.Parking;
import joppi.pier.parkingfinder.db.ParkingDAO;
import joppi.pier.parkingfinder.db.ParkingDAO_DB_impl;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
{
	private GoogleMap mMap;

	static ArrayList<LatLng> drawPolyPts = new ArrayList<>();
	static Polygon drawPoly = null;


	private ParkingDAO parkingDAO;
	private CoordinateDAO coordinateDAO;
	private ArrayList<Parking> parking;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);

		parkingDAO = new ParkingDAO_DB_impl();
		parkingDAO.open();
		parking = parkingDAO.getAllParking();
		parkingDAO.close();

		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
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
		LatLng trento = new LatLng(46.076200, 11.111455);
		mMap.addMarker(new MarkerOptions().position(trento).title("This is Trento"));
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((trento), 16.0f));

		if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
//			return;
		}
		else{
			mMap.setMyLocationEnabled(true);
		}
		mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
			@Override
			public boolean onMyLocationButtonClick() {
				return false;
			}
		});


		coordinateDAO = new CoordinateDAO_DB_impl();
		coordinateDAO.open();


		Polygon parkingLot = null;
		ArrayList<Coordinate> coordinates;
		ArrayList<LatLng> polygonCoordinates = new ArrayList<LatLng>();

		for(final Parking p : parking){
			coordinates = coordinateDAO.getCoordinateOfParking(p.getId());
			polygonCoordinates.clear();
			for(Coordinate c: coordinates){
				Log.w("COORD",c.getLatitude() + ":"+c.getLongitude());
				polygonCoordinates.add(new LatLng(c.getLatitude(), c.getLongitude()));
			}
			Log.w("NUMERO:", polygonCoordinates.size() + "");
			if(polygonCoordinates.size() > 2) {
				parkingLot = mMap.addPolygon(new PolygonOptions()
						.addAll(polygonCoordinates)
						.strokeColor(0x660000ff)
						.fillColor(0x220000ff)
						.clickable(true));


				mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
					@Override
					public void onPolygonClick(Polygon polygon) {
						LatLng pt = PolygonCenter(polygon.getPoints());
						Marker mrk = mMap.addMarker(new MarkerOptions().position(pt).title(p.getName()).flat(false));
						mMap.moveCamera(CameraUpdateFactory.newLatLng(pt));

						mrk.showInfoWindow();
					}
				});
			}
			else if(polygonCoordinates.size() == 1)
			{
				mMap.addMarker(new MarkerOptions().position(polygonCoordinates.get(0)).title(p.getName()));
			}
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
						if(isPolyComplex(drawPolyPts)) Toast.makeText(MapsActivity.this, "Invalid selection", Toast.LENGTH_LONG).show();
					}
				}
			}
		});
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
}

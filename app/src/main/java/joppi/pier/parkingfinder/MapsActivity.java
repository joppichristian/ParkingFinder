package joppi.pier.parkingfinder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
{
	private GoogleMap mMap;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);
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
		mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener()
		{
			@Override
			public boolean onMyLocationButtonClick()
			{
				return false;
			}
		});

		// Add some parking lots
		Polygon polygon = mMap.addPolygon(new PolygonOptions()
		.add(new LatLng(46.076479, 11.110441), new LatLng(46.076650, 11.109615), new LatLng(46.076637, 11.109221)
				, new LatLng(46.076151, 11.109243), new LatLng(46.076110, 11.108448), new LatLng(46.075526, 11.108459)
				, new LatLng(46.075511, 11.108969), new LatLng(46.075094, 11.109033), new LatLng(46.075165, 11.110385)
				, new LatLng(46.075868, 11.110653), new LatLng(46.076136, 11.110637), new LatLng(46.076170, 11.110452))
		.strokeColor(0x660000ff)
		.fillColor(0x220000ff));

		Polygon polygon2 = mMap.addPolygon(new PolygonOptions()
				.add(new LatLng(46.076665, 11.110522), new LatLng(46.076721, 11.111091), new LatLng(46.076334, 11.111198)
						, new LatLng(46.076196, 11.110881), new LatLng(46.076203, 11.110602))
				.strokeColor(0x6600ff00)
				.fillColor(0x2200ff00));
	}
}

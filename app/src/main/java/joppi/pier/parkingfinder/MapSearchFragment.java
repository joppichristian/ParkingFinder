package joppi.pier.parkingfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLngBounds;

public class MapSearchFragment extends PlaceAutocompleteFragment
{
	private TextView txtSearch;
	private View zzaRh;
	@Nullable
	private LatLngBounds bounds;
	@Nullable
	private AutocompleteFilter filter;
	@Nullable
	private PlaceSelectionListener listener;

	public MapSearchFragment()
	{
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.map_searchbar, container, false);

		txtSearch = (TextView) view.findViewById(R.id.txtSearchLocation);
		txtSearch.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onTxtViewClick();
			}
		});

		ImageView searchImg = (ImageView) view.findViewById(R.id.searchLocation);
		searchImg.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onTxtViewClick();
			}
		});

		return view;
	}


	public void onDestroyView()
	{
		this.zzaRh = null;
		this.txtSearch = null;
		super.onDestroyView();
	}

	public void setBoundsBias(@Nullable LatLngBounds bounds)
	{
		this.bounds = bounds;
	}

	public void setFilter(@Nullable AutocompleteFilter filter)
	{
		this.filter = filter;
	}

	public void setText(CharSequence text)
	{
		this.txtSearch.setText(text);
	}

	public void setHint(CharSequence hint)
	{
		this.txtSearch.setHint(hint);
		this.zzaRh.setContentDescription(hint);
	}

	public void setOnPlaceSelectedListener(PlaceSelectionListener listener)
	{
		this.listener = listener;
	}

	private void onTxtViewClick()
	{
		int errorCode = -1;

		try{
			Intent intent = (new PlaceAutocomplete.IntentBuilder(2)).setBoundsBias(this.bounds).setFilter(this.filter).zzeq(this.txtSearch.getText().toString()).zzig(1).build(this.getActivity());
			this.startActivityForResult(intent, 1);
		}catch(GooglePlayServicesRepairableException e){
			errorCode = e.getConnectionStatusCode();
			Log.e("Places", "Could not open autocomplete activity", e);
		}catch(GooglePlayServicesNotAvailableException e){
			errorCode = e.errorCode;
			Log.e("Places", "Could not open autocomplete activity", e);
		}

		if(errorCode != -1){
			GoogleApiAvailability avail = GoogleApiAvailability.getInstance();
			avail.showErrorDialogFragment(this.getActivity(), errorCode, 2);
		}

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == 1){
			if(resultCode == -1){
				Place place = PlaceAutocomplete.getPlace(this.getActivity(), data);
				if(this.listener != null){
					this.listener.onPlaceSelected(place);
				}

				this.setText(place.getName().toString());
			} else if(resultCode == 2){
				Status status = PlaceAutocomplete.getStatus(this.getActivity(), data);
				if(this.listener != null){
					this.listener.onError(status);
				}
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}
}
package joppi.pier.parkingfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;

public class FilterActivity extends AppCompatActivity
{
    CarouselView customCarouselView;
    int NUMBER_OF_PAGES = 3;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_filter);

        customCarouselView = (CarouselView) findViewById(R.id.carouselView);
        customCarouselView.setPageCount(NUMBER_OF_PAGES);
        // set ViewListener for custom view 
        customCarouselView.setViewListener(viewListener);
		
        Button startApp = (Button)findViewById(R.id.startMap);
        startApp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Define Intent to go to map activity ( TO DO )

                Intent go = new Intent(FilterActivity.this, MapsActivity.class);
                startActivity(go);
                finish();
            }
        });

        startApp = (Button)findViewById(R.id.startList);
        startApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Define Intent to go to map activity ( TO DO )

                Intent go = new Intent(FilterActivity.this,ListParking.class);
                startActivity(go);
                finish();
            }
        });
    }
	
	ViewListener viewListener = new ViewListener() {

        @Override
        public View setViewForPosition(int position) {
            View customView = getLayoutInflater().inflate(R.layout.activity_parking_detail, null);
            //set view attributes here

            return customView;
        }
    };
}
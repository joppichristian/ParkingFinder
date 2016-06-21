package joppi.pier.parkingfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class FilterActivity extends AppCompatActivity
{
    //DateFormat fmtDateAndTime=DateFormat.getDateTimeInstance();

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_filter);

        //dateAndTimeLabel=(TextView)findViewById(R.id.dateAndTime);

        //updateLabel();

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

    private void updateLabel() {
        //dateAndTimeLabel.setText(fmtDateAndTime.format(dateAndTime.getTime()));
    }
}

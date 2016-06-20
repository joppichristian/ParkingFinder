package joppi.pier.parkingfinder;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class FilterActivity extends AppCompatActivity
{
    //DateFormat fmtDateAndTime=DateFormat.getDateTimeInstance();
    TextView dateAndTimeLabel;
    Calendar dateAndTime=Calendar.getInstance();
    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };
    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener()
    {
        public void onTimeSet(TimePicker view, int hourOfDay,
                              int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            updateLabel();
        }
    };

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_filter);

        Button btn=(Button)findViewById(R.id.setTime);

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TimePickerDialog tpd = new TimePickerDialog(FilterActivity.this,
                        t,
                        dateAndTime.get(Calendar.HOUR_OF_DAY),
                        dateAndTime.get(Calendar.MINUTE),
                        true);
                tpd.setTitle("When are you planning...");
                tpd.show();
            }
        });

        //dateAndTimeLabel=(TextView)findViewById(R.id.dateAndTime);

        updateLabel();

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

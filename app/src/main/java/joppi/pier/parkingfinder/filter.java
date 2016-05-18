package joppi.pier.parkingfinder;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.util.Calendar;

public class filter extends AppCompatActivity {
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

        TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setIs24HourView(DateFormat.is24HourFormat(this));
        timePicker.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));

        ToggleButton btn=(ToggleButton)findViewById(R.id.toggleButton);

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new DatePickerDialog(filter.this,
                        d,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btn=(ToggleButton)findViewById(R.id.toggleButton2);

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TimePickerDialog tpd = new TimePickerDialog(filter.this,
                        t,
                        dateAndTime.get(Calendar.HOUR_OF_DAY),
                        dateAndTime.get(Calendar.MINUTE),
                        true);
                tpd.setTitle("When are you planning...");
                tpd.show();
            }
        });

        dateAndTimeLabel=(TextView)findViewById(R.id.dateAndTime);

        updateLabel();
    }

    private void updateLabel() {
        //dateAndTimeLabel.setText(fmtDateAndTime.format(dateAndTime.getTime()));
    }
}

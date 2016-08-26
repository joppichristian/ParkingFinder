package joppi.pier.parkingfinder;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.Shape;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Circle;

import joppi.pier.parkingfinder.db.Parking;

public class ParkingDetail extends Activity {


    String lat="",lon="";
    String name="",notes="";
    String time_limit ="";
    String time_frame ="";
    int color=0x0,type=0x0;
    int posti;
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_parking_detail);
        name = getIntent().getExtras().getString("name");
        Double cost = getIntent().getExtras().getDouble("cost");
        Double dist = getIntent().getExtras().getDouble("dist");
        lat = getIntent().getExtras().getString("lat");
        lon = getIntent().getExtras().getString("long");
        color = getIntent().getExtras().getInt("color");
        type =  getIntent().getExtras().getInt("type");
        time_limit = getIntent().getExtras().getString("time_limit");
        posti = getIntent().getExtras().getInt("places");
        notes = getIntent().getExtras().getString("notes");
        time_frame = getIntent().getExtras().getString("time_frame");
        Double dist_foot = getIntent().getExtras().getDouble("dist_foot");
        int duration  = getIntent().getExtras().getInt("duration");
        String address  = getIntent().getExtras().getString("address");

        // SET NAME
        TextView tx_name = (TextView)findViewById(R.id.park_name);
        tx_name.setText(name);

        // SET PLACES
        TextView tx_places = (TextView)findViewById(R.id.park_places);
        if(posti == 1)
            tx_places.setText("# posti non disponibile");
        else
            tx_places.setText(posti + " posti");

        // SET COST
        TextView tx_cost = (TextView)findViewById(R.id.park_price);
        tx_cost.setText(cost +" €");

        // SET DISTANCE
        TextView tx_distance = (TextView)findViewById(R.id.park_distance);
        if(dist>1000)
            tx_distance.setText(String.format("%.1f", (dist / 1000.0)) + " km");
        else
            tx_distance.setText(dist.intValue() + " metri");

        // SET DURATA VIAGGIO
        String udm = "sec";
        if(duration > 60)
        {
            duration /= 60;
            udm = "min";
        }
        if(duration > 60)
        {
            duration /=60;
            udm= "h";
        }
        tx_distance.setText(tx_distance.getText() + " , "+ duration + " " + udm);

        // SET DISTANCE BY FOOT
        TextView tx_distance_foot = (TextView)findViewById(R.id.park_distance_foot);
        if(dist_foot < 0)
            tx_distance_foot.setText("Destinazione non definita");
        else {
            if(dist_foot>1000)
                tx_distance_foot.setText(String.format("%.1f", (dist_foot/ 1000.0)) + " km alla destinazione segnata");
            else
                tx_distance_foot.setText(dist_foot.intValue() + " metri alla destinazione segnata");
        }
        // SET TIME FRAME
        TextView tx_time_frame = (TextView)findViewById(R.id.park_time_frame);
        tx_time_frame.setText(time_frame);

        // SET ADDRESS
        TextView tx_address = (TextView)findViewById(R.id.park_address);
        tx_address.setText(address);

        // SET NOTES
        TextView tx_notes = (TextView)findViewById(R.id.park_notes);
        tx_notes.setText(notes);


        // SET IMAGE AND TYPE PARAMETERS
        if((type & Parking.SPEC_SURVEILED) != Parking.SPEC_SURVEILED )
        {
            TextView tx_surveiled = (TextView)findViewById(R.id.park_surveiled);
            tx_surveiled.setText("Non sorvegliato");
        }
        if(time_limit.compareTo("0")==0)
        {
            TextView tx_time_limit = (TextView)findViewById(R.id.park_time_limit);
            tx_time_limit.setText("No Disco Orario");
        }

        ImageView type_image = (ImageView)findViewById(R.id.parkingDetailsTypeImage);
        TextView text_type = (TextView)findViewById(R.id.park_type);
        switch (type & Parking.TYPE_MASK){
            case 1:
                type_image.setImageDrawable(getResources().getDrawable(R.drawable.parking_surface));
                text_type.setText("Superficie");
                break;
            case 2:
                type_image.setImageDrawable(getResources().getDrawable(R.drawable.parking_structure));
                text_type.setText("Struttura");
                break;
            case 4:
                type_image.setImageDrawable(getResources().getDrawable(R.drawable.parking_road));
                text_type.setText("Lato Strada");
                break;
            case 8:
                type_image.setImageDrawable(getResources().getDrawable(R.drawable.parking_covered));
                text_type.setText("Sotterraneo");
                break;
        }

        int standard_color = getResources().getColor(R.color.colorPrimaryDark);

        ImageView imageView_disco = (ImageView)findViewById(R.id.icon_time_limit);
        Drawable icon_disco = getResources().getDrawable(R.drawable.ic_timelapse_details);
        icon_disco.clearColorFilter();
        icon_disco.setColorFilter(standard_color, PorterDuff.Mode.SRC_ATOP);
        imageView_disco.setImageDrawable(icon_disco);

        ImageView imageView_time_frame = (ImageView)findViewById(R.id.icon_time_frame);
        Drawable icon_time_frame = getResources().getDrawable(R.drawable.ic_action_clock_details);
        icon_time_frame.clearColorFilter();
        icon_time_frame.setColorFilter(standard_color, PorterDuff.Mode.SRC_ATOP);
        imageView_time_frame.setImageDrawable(icon_time_frame);

        ImageView imageView_surveiled = (ImageView)findViewById(R.id.icon_surveiled);
        Drawable icon_surveiled = getResources().getDrawable(R.drawable.parking_surveiled_details);
        icon_surveiled.clearColorFilter();
        icon_surveiled.setColorFilter(standard_color, PorterDuff.Mode.SRC_ATOP);
        imageView_surveiled.setImageDrawable(icon_surveiled);

        ImageView imageView_distance_by_car = (ImageView)findViewById(R.id.icon_distance_by_car);
        Drawable icon_distance_by_car = getResources().getDrawable(R.drawable.distance_by_car_details);
        icon_distance_by_car.clearColorFilter();
        icon_distance_by_car.setColorFilter(standard_color, PorterDuff.Mode.SRC_ATOP);
        imageView_distance_by_car.setImageDrawable(icon_distance_by_car);

        ImageView imageView_distance_by_foot = (ImageView)findViewById(R.id.icon_distance_by_foot);
        Drawable icon_distance_by_foot = getResources().getDrawable(R.drawable.distance_by_foot_details);
        icon_distance_by_foot.clearColorFilter();
        icon_distance_by_foot.setColorFilter(standard_color, PorterDuff.Mode.SRC_ATOP);
        imageView_distance_by_foot.setImageDrawable(icon_distance_by_foot);

        ImageView imageView_price = (ImageView)findViewById(R.id.icon_price);
        Drawable icon_price = getResources().getDrawable(R.drawable.price_icon);
        icon_price.clearColorFilter();
        icon_price.setColorFilter(standard_color, PorterDuff.Mode.SRC_ATOP);
        imageView_price.setImageDrawable(icon_price);

        ImageView imageView_feedback_up = (ImageView)findViewById(R.id.icon_feedback_up);
        Drawable icon_feedback_up = getResources().getDrawable(R.drawable.ic_thumb_up);
        icon_feedback_up.clearColorFilter();
        icon_feedback_up.setColorFilter(standard_color, PorterDuff.Mode.SRC_ATOP);
        imageView_feedback_up.setImageDrawable(icon_feedback_up);

        ImageView imageView_feedback_down = (ImageView)findViewById(R.id.icon_feedback_down);
        Drawable icon_feedback_down = getResources().getDrawable(R.drawable.ic_thumb_down);
        icon_feedback_down.clearColorFilter();
        icon_feedback_down.setColorFilter(standard_color, PorterDuff.Mode.SRC_ATOP);
        imageView_feedback_down.setImageDrawable(icon_feedback_down);

        /*ImageView imageView_address = (ImageView)findViewById(R.id.icon_address);
        Drawable icon_address = getResources().getDrawable(R.drawable.ic_action_location_details);
        icon_address.clearColorFilter();
        icon_address.setColorFilter(standard_color, PorterDuff.Mode.SRC_ATOP);
        imageView_address.setImageDrawable(icon_address);
*/
        // SET VIEW COLOR
        View right_view = findViewById(R.id.rightColorDetail);
        right_view.setBackgroundColor(color);
        View bottom_view = findViewById(R.id.bottomColorDetail);
        bottom_view.setBackgroundColor(color);
        View divisor = findViewById(R.id.firstDivisor);
        divisor.setBackgroundColor(color);
        divisor = findViewById(R.id.secondDivisor);
        divisor.setBackgroundColor(color);
        divisor = findViewById(R.id.thirdDivisor);
        divisor.setBackgroundColor(color);
        divisor = findViewById(R.id.fourthDivisor);
        divisor.setBackgroundColor(color);
        divisor = findViewById(R.id.fiftDivisor);
        divisor.setBackgroundColor(color);
        divisor = findViewById(R.id.sixthDivisor);
        divisor.setBackgroundColor(color);


        GradientDrawable shape_blue = (GradientDrawable)getResources().getDrawable(R.drawable.button_circle_background);
        shape_blue.setColor(getResources().getColor(R.color.blueButton));
        ImageButton goMap = (ImageButton)findViewById(R.id.goMap);
        goMap.setBackground(shape_blue);


        GradientDrawable shape_green = (GradientDrawable)getResources().getDrawable(R.drawable.button_circle_background);
        shape_green.setColor(getResources().getColor(R.color.greenButton));
        ImageButton feedback_up = (ImageButton)findViewById(R.id.postFeedbackUp);
        feedback_up.setBackground(shape_green);

        GradientDrawable shape_red = (GradientDrawable)getResources().getDrawable(R.drawable.button_circle_background);
        shape_red.setColor(Color.RED);
        ImageButton feedback_down = (ImageButton)findViewById(R.id.postFeedbackDown);
        feedback_down.setBackground(shape_red);


    }

    public void goNavigator(View v){
        Uri gmmIntentUri = Uri.parse("geo:"+lat+","+lon+"?q="+lat+","+lon+"("+name+")");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW,gmmIntentUri );
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }


    public void postFeedback(View v){
        int id = v.getId();
        Toast toast;
        if(id == R.id.postFeedbackUp)
            toast=Toast.makeText(this,"La ringraziamo per il feedback inviato!",Toast.LENGTH_LONG);
        else
            toast=Toast.makeText(this,"La ringraziamo per il feedback inviato! La preghiamo inoltre di contattarci per eventuali informazioni errate!",Toast.LENGTH_LONG);
        toast.show();
    }
}

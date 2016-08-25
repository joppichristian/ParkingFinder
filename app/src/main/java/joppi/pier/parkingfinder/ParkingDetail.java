package joppi.pier.parkingfinder;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

        // SET NAME
        TextView tx_name = (TextView)findViewById(R.id.park_name);
        tx_name.setText(name);

        // SET PLACES
        TextView tx_places = (TextView)findViewById(R.id.park_places);
        if(posti == 1)
            tx_places.setText("Posti non definiti");
        else
            tx_places.setText(posti + " posti");

        // SET COST
        TextView tx_cost = (TextView)findViewById(R.id.park_price);
        tx_cost.setText("Prezzo : " + cost +" €");

        // SET DISTANCE
        TextView tx_distance = (TextView)findViewById(R.id.park_distance);
        tx_distance.setText("Distanza : "+dist.intValue() + " metri");

        // SET TIME FRAME
        TextView tx_time_frame = (TextView)findViewById(R.id.park_time_frame);
        tx_time_frame.setText(time_frame);

        // SET NOTES
        TextView tx_notes = (TextView)findViewById(R.id.park_notes);
        tx_notes.setText(notes);


        // SET IMAGE AND TYPE PARAMETERS
        if((type & Parking.SPEC_SURVEILED) == Parking.SPEC_SURVEILED )
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



        ImageView icon_disco = (ImageView)findViewById(R.id.icon_time_limit);
        if(icon_disco != null) {
            icon_disco.getDrawable().setColorFilter(0x00ff9900, PorterDuff.Mode.SRC_ATOP);
            icon_disco.postInvalidate();
        }


        // SET HEADER VIEW COLOR
        View right_view = findViewById(R.id.rightColorDetail);
        right_view.setBackgroundColor(color);
        View bottom_view = findViewById(R.id.bottomColorDetail);
        bottom_view.setBackgroundColor(color);

        // SET BUTTON COLOR
        TextView goMap = (TextView)findViewById(R.id.goMap);

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[] { 8, 8, 8, 8, 8, 8, 8, 8 });
        shape.setColor(color);
        shape.setStroke(30, color);

        goMap.setBackground(shape);





    }

    public void goNavigator(View v){
        Uri gmmIntentUri = Uri.parse("geo:"+lat+","+lon+"?q="+lat+","+lon+"("+name+")");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW,gmmIntentUri );
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}

package joppi.pier.parkingfinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class AddParking extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_parking);
    }

    public void addPark(View v){
        Toast toast= Toast.makeText(this,"Il parcheggio è stato aggiunto!",Toast.LENGTH_LONG);
        toast.show();
        Intent myIntent = new Intent(AddParking.this,MapsActivity.class);
        startActivity(myIntent);
    }
}

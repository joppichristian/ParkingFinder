package joppi.pier.parkingfinder;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by christian on 11/08/16.
 */
public class MenuManager extends Activity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Activity activity;
    private Dialog dialog;

    public MenuManager(DrawerLayout drawerLayout, NavigationView navigationView, Activity activity){
        this.drawerLayout = drawerLayout;
        this.navigationView = navigationView;
        this.activity = activity;
        this.navigationView.setNavigationItemSelectedListener(this);
    }
    public void openMenu(){
        drawerLayout.openDrawer(Gravity.LEFT);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        dialog = new Dialog(activity);

        if (id == R.id.vehicleOption) {
            dialog.setContentView(R.layout.dialog_layout_vehicle);
            dialog.show();
            dialog.findViewById(R.id.confirmVehicleOption).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Save parameters
                    dialog.dismiss();
                }
            });
            return true;
        }
        if (id == R.id.timeOption) {
            dialog.setContentView(R.layout.dialog_layout_time);
            dialog.show();
            dialog.findViewById(R.id.confirmTimeOption).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Save parameters
                    dialog.dismiss();
                }
            });
            return true;
        }
        if (id == R.id.orderOption) {
            dialog.setContentView(R.layout.dialog_layout_cost);
            dialog.show();
            dialog.findViewById(R.id.confirmCostOption).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Save parameters
                    dialog.dismiss();
                }
            });
            return true;
        }
        if (id == R.id.typeOption) {
            dialog.setContentView(R.layout.dialog_layout_type);
            dialog.show();
            dialog.findViewById(R.id.confirmTypeOption).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Save parameters
                    dialog.dismiss();
                }
            });
            return true;
        }

        return true;
    }

}

package joppi.pier.parkingfinder;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by christian on 11/08/16.
 */



public class MenuManager extends Activity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayList<MenuItem> menuItems;
    public MenuManager(){

        menuItems.add(new MenuItem("Titolo1","ic_action_creditcard"));

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        MenuAdapter adapter = new MenuAdapter(ParkingFinderApplication.getAppContext(),menuItems);
        mDrawerList.setAdapter(adapter);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

    }

    public void openMenu(){
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }


    public class MenuAdapter extends BaseAdapter {

        Context context;
        ArrayList<MenuItem> items;
        public MenuAdapter(Context context, ArrayList<MenuItem> items) {
            this.context = context;
            this.items = items;
        }
        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater mInflater = (LayoutInflater)
                        context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.menu_adapter, null);

            }
            //TextView tx = (TextView)convertView.findViewById(R.id.title_item);
            //tx.setText(items.get(position).getTitle());
            //tx.drawable
            return convertView;
        }
    }


    public class MenuItem {
        private String title;
        private String iconName;
        public MenuItem(String title,String iconName){
            this.title = title;
            this.iconName = iconName;

        }

        public String getTitle() {
            return title;
        }

        public String getIconName() {
            return iconName;
        }
    }
}

package joppi.pier.parkingfinder;

import java.util.ArrayList;

/**
 * Created by christian on 29/05/16.
 */

public class Parking {

    private int id;
    private String name;
    private long latitude;
    private long longitude;
    //Tutte le varie tipologie le gestiamo con una lista di flag.
    private ArrayList<Parking_Type> flags = new ArrayList<Parking_Type>();
    private double cost;

    public Parking(){}
    public Parking(String name,long latitude,long longitude,ArrayList<Parking_Type> flags,double cost){
        this.id = -1;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.flags.addAll(flags);
        this.cost = cost;
    }
    public int getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public long getLatitude(){
        return this.latitude;
    }

    public long getLongitude(){
        return this.longitude;
    }

    public ArrayList<Parking_Type> getAllsFlags(){
        return this.flags;
    }

    public double getCost(){
        return this.cost;
    }

}

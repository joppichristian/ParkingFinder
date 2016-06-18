package joppi.pier.parkingfinder;

import java.util.ArrayList;

/**
 * Created by christian on 29/05/16.
 */

public class Parking {

    private int id;
    private String name;

    private double cost;
    private int disco;
    private boolean car;
    private boolean moto;
    private boolean caravan;
    private boolean indoor;

    private float distance;

    public Parking(String name,double cost,int disco,boolean car,boolean moto, boolean caravan, boolean indoor){
        this.id = -1;
        this.name = name;
        this.cost = cost;
        this.disco = disco;
        this.car = car;
        this.moto = moto;
        this.caravan = caravan;
        this.indoor = indoor;
        this.distance = 0;
    }
    public Parking(int id,String name,double cost,int disco,boolean car,boolean moto, boolean caravan, boolean indoor){
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.disco = disco;
        this.car = car;
        this.moto = moto;
        this.caravan = caravan;
        this.indoor = indoor;
        this.distance = 0;
    }
    public int getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }



    public double getCost(){
        return this.cost;
    }


    public int getDisco() {
        return disco;
    }

    public boolean isCar() {
        return car;
    }

    public boolean isMoto() {
        return moto;
    }

    public boolean isCaravan() {
        return caravan;
    }

    public boolean isIndoor() {
        return indoor;
    }

    @Override
    public String toString(){
        return name;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}

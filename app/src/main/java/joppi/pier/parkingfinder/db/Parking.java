package joppi.pier.parkingfinder.db;

/**
 * Created by christian on 29/05/16.
 */

public class Parking {

//    Parking
//    {
//        -id
//        -name
//        -cost (string)
//        -time_limit (string)
//        -notes (string)
//        -places -> Places
//        -type (Surface, Structure, Surveiled, ...)
//        -location (LatLng)
//        -area (LatLng,LatLng)
//    }
//
//    Places
//    {
//        -car
//        -moto
//        -caravan
//    }

    private int id;
    private String name;

    private double cost;
    private int disco;
    private boolean car;
    private boolean moto;
    private boolean caravan;
    private boolean indoor;

    private double distance;

    public Parking(String name,double cost,int disco,boolean car,boolean moto, boolean caravan, boolean indoor){
        this(-1, name, cost, disco, car, moto, caravan, indoor);
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
        this.distance = -1.0;
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

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

}

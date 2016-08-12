package joppi.pier.parkingfinder.db;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by christian on 29/05/16.
 */

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
//        -time_frame ( Open,Close )
//    }
//
public class Parking {

    public static int TYPE_SURFACE = 0x00000001;
    public static int TYPE_STRUCTURE = 0x0000002;
    public static int TYPE_ROAD = 0x0000004;
    public static int TYPE_SUBTERRANEAN = 0x80000008;
    public static int TYPE_SURVEILED = 0x80000010;

    private int id;
    private String name;
    //cost = costo:durata:fascia:giorni
    private String cost;

    private String time_limit;
    private int type;
    private String notes;
    private String location;
    private String area;
    private String time_frame;
    private int car;
    private int moto;
    private int caravan;

    private double distance;

    public Parking(int id,String name,String cost,String time_limit,int type,String notes, String location, String area,String time_frame,int car,int moto,int caravan){
        this.id = id;
        this.name = name;
        this.setCost(cost);
        this.setTime_limit(time_limit);
        this.setType(type);
        this.setNotes(notes);
        this.setLocation(location);
        this.setArea(area);
        this.setTime_frame(time_frame);
        this.car = car;
        this.moto = moto;
        this.caravan = caravan;
        this.distance = -1.0;
    }
    public int getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
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

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getTime_limit() {
        return time_limit;
    }

    public void setTime_limit(String time_limit) {
        this.time_limit = time_limit;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getTime_frame() {
        return time_frame;
    }

    public void setTime_frame(String time_frame) {
        this.time_frame = time_frame;
    }

    public int getCar() {
        return car;
    }

    public void setCar(int car) {
        this.car = car;
    }

    public int getMoto() {
        return moto;
    }

    public void setMoto(int moto) {
        this.moto = moto;
    }

    public int getCaravan() {
        return caravan;
    }

    public void setCaravan(int caravan) {
        this.caravan = caravan;
    }

    public static ArrayList<LatLng> parseCoordinates(String coordinates){
        ArrayList<LatLng> ar = new ArrayList<>();
        for(String s : coordinates.split(";")){
            ar.add(new LatLng(Double.parseDouble(s.split(",")[0]),Double.parseDouble(s.split(",")[1])));
        }
        return ar;
    }
}

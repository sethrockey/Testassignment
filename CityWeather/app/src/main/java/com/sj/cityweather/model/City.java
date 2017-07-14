package com.sj.cityweather.model;

/**
 * Created by HP on 7/11/2017.
 */

public class City {
    int id;
    String city;
    public City(){

    }

    public City(int id,String city){
        this.id = id;
        this.city = city;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


}

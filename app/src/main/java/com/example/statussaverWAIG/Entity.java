package com.example.statussaverWAIG;

import androidx.room.PrimaryKey;

@androidx.room.Entity(tableName = "number_table")
public class Entity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private double phone_number;
    private String name,datetime,iso;
    private int country_code;

    public Entity(int id, int country_code, Double phone_number,String iso, String name, String datetime) {
        this.id = id;
        this.country_code = country_code;
        this.phone_number = phone_number;
        this.iso = iso;
        this.name = name;
        this.datetime = datetime;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public void setPhone_number(double phone_number) {
        this.phone_number = phone_number;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public void setCountry_code(int country_code) {
        this.country_code = country_code;
    }

    public int getId() {
        return id;
    }

    public int getCountry_code() {
        return country_code;
    }

    public double getPhone_number() {
        return phone_number;
    }

    public String getName() {
        return name;
    }

    public String getDatetime() {
        return datetime;
    }
}

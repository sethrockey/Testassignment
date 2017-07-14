package com.sj.cityweather;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.sj.cityweather.adapter.DataBaseHandler;

public class WeatherApplication extends Application {

    private static WeatherApplication weatherApplication;
    public static DataBaseHandler helper;

    @Override
    public void onCreate() {
        super.onCreate();
        weatherApplication = this;
        helper = new DataBaseHandler(getApplicationContext());
    }

    public static WeatherApplication get() {
        return weatherApplication;
    }

    public static SQLiteDatabase getDb() {
        return helper.getDatabase();
    }
}

package com.sj.cityweather.api;

import com.sj.cityweather.model.WeatherInfo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by rk on 11/07/2017.
 */

public interface WeatherService {

	@GET("forecast?units=metric")
	Call<WeatherInfo> getCityForecast(@Query("q") String cityName, @Query("appid") String apiKey);
}

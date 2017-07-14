package com.sj.cityweather.api;

import com.sj.cityweather.model.WeatherInfo;
import com.sj.cityweather.util.Utils;

import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Class handles all the API calling work using retrofit library.
 */
public class WeatherApiClient {

	// Base usrl to query weather info from openweathermap
	private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";

	// URL to download the weather icon from OWM
	private static String IMG_URL = "http://openweathermap.org/img/w/";

	// API KEY to be used with apis of OWN
	private static final String API_KEY = "d19303234b7c5b8dbc9ff0f1f0c571f9";

	private static Retrofit retrofit;
	private static WeatherApiClient apiClient;

	private WeatherApiClient() {
		retrofit = new Retrofit.Builder()
				.baseUrl(BASE_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.build();
	}

	public static WeatherApiClient getApiClient() {
		if (apiClient == null) {
			apiClient = new WeatherApiClient();
		}
		return apiClient;
	}

	public void getCityForecastWeather(String cityName, Callback<WeatherInfo> callback) {
		Utils.log(this, "getCityForecastWeather: for city - " + cityName);
		retrofit.create(WeatherService.class).getCityForecast(cityName, API_KEY).enqueue(callback);
	}

	public static String getWeatherIconUrl(String iconId) {
		return WeatherApiClient.IMG_URL + iconId + ".png";
	}
}

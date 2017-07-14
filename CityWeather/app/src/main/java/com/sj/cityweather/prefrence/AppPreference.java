package com.sj.cityweather.prefrence;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.sj.cityweather.WeatherApplication;
import com.sj.cityweather.model.WeatherInfo;
import com.sj.cityweather.util.Utils;

/**
 * Manages the preference for the app.
 * We basically use this to store the weather
 * data for the user preferred city.
 */
public class AppPreference {

	public static String CITY_DATA = "cityData";

	private static final String FILE_NAME = "app_preference";
	private static int MODE_PRIVATE = 0;
	private static AppPreference preference;

	private SharedPreferences sharedPreferences;

	private AppPreference() {
		sharedPreferences = WeatherApplication.get().getSharedPreferences(FILE_NAME, MODE_PRIVATE);
	}

	public static AppPreference getPreference(){
		if(preference == null){
			preference = new AppPreference();
		}
		return preference;
	}

	public void saveCityWeather(WeatherInfo weatherInfo){
		SharedPreferences.Editor editor = sharedPreferences.edit();
		String weather = new Gson().toJson(weatherInfo);
		Utils.log(this, "saveCityWeather: saving " + weather);
		editor.putString(CITY_DATA, weather);
		editor.commit();
	}

	public WeatherInfo getCityWeatherForecast(){
		String weather = sharedPreferences.getString(CITY_DATA, null);
		return weather != null ? new Gson().fromJson(weather, WeatherInfo.class) : null;
	}

	public boolean shouldUpdateCityData(WeatherInfo newData) {
		WeatherInfo originalData = getCityWeatherForecast();
		return originalData == null
				||  (newData != null && originalData.getCity().getId() == newData.getCity().getId());
	}
}

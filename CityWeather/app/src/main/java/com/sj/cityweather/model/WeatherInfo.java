package com.sj.cityweather.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class WeatherInfo implements Serializable {

	@SerializedName("city")
	private City city;
	@SerializedName("list")
	private List<CityWeather> weatherList;

	private List<CityWeather> uniqueDaysList;

	public City getCity() {
		return city;
	}

	public List<CityWeather> getWeatherList() {
		return weatherList;
	}

	public List<CityWeather> getUniqueDaysWeatherForCast() {
		if (uniqueDaysList == null) {
			uniqueDaysList = getUniqueDays();
		}
		return uniqueDaysList;
	}

	private List<CityWeather> getUniqueDays() {
		List<CityWeather> cityWeathers = new ArrayList<>();
		Date currentDate = Calendar.getInstance().getTime();
		for (CityWeather cityWeather : weatherList) {
			long lastSavedDate = cityWeathers.isEmpty()
					? currentDate.getTime()
					: cityWeathers.get(cityWeathers.size()-1).getDate().getTime();

			Date dateToCheck =  new Date(lastSavedDate + (24 * 60 * 60 * 1000));

			if (cityWeather.getDate().compareTo(dateToCheck)>0) {
				cityWeathers.add(cityWeather);
			}
		}

		return cityWeathers;
	}

	public class City implements Serializable {

		@SerializedName("id")
		private Long id;
		@SerializedName("name")
		private String name;
		@SerializedName("country")
		private String country;

		public Long getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getCountry() {
			return country;
		}
	}

	public class CityWeather implements Serializable {

		@SerializedName("weather")
		private List<Weather> weather = null;
		@SerializedName("main")
		private Main main;
		@SerializedName("dt")
		private long date;

		public Weather getWeather() {
			return weather.get(0);
		}

		public Main getMain() {
			return main;
		}

		public Date getDate() {
			return new Date(date * 1000);
		}

		public class Main implements Serializable {

			@SerializedName("temp")
			private Double temp;
			@SerializedName("humidity")
			private Double humidity;
			@SerializedName("pressure")
			private Double pressure;
			@SerializedName("temp_min")
			private Double tempMin;
			@SerializedName("temp_max")
			private Double tempMax;

			public Double getTemp() {
				return temp;
			}

			public Double getHumidity() {
				return humidity;
			}

			public Double getPressure() {
				return pressure;
			}

			public Double getTempMin() {
				return tempMin;
			}

			public Double getTempMax() {
				return tempMax;
			}
		}

		public class Weather implements Serializable {

			@SerializedName("id")
			private Long id;
			@SerializedName("main")
			private String main;
			@SerializedName("description")
			private String description;
			@SerializedName("icon")
			private String icon;

			public Long getId() {
				return id;
			}

			public String getWeather() {
				return main;
			}

			public String getDescription() {
				return description;
			}

			public String getIcon() {
				return icon;
			}

		}
	}
}

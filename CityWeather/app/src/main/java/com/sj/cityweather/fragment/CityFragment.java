package com.sj.cityweather.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sj.cityweather.R;
import com.sj.cityweather.adapter.WeatherForecastAdapter;
import com.sj.cityweather.api.WeatherApiClient;
import com.sj.cityweather.model.WeatherInfo;
import com.squareup.picasso.Picasso;

/**
 * Fragments handle the display of views show and also updating the content.
 * The fragment is initialized with the required data (WeatherInfo)
 */
public class CityFragment extends Fragment {

	private static final String EXTRA_CITY_WEATHER = "weatherInfo";

	private WeatherInfo weatherInfo;

	private TextView cityField;
	private TextView detailsField;
	private TextView currentTemperatureField;
	private ImageView weatherIcon;

	private RecyclerView recyclerView;

	/**
	 * Helper method to get a new instance of CityFragment
	 * @param weatherInfo
	 * @return
	 */
	public static CityFragment newInstance(WeatherInfo weatherInfo) {
		CityFragment fragment = new CityFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(EXTRA_CITY_WEATHER, weatherInfo);
		fragment.setArguments(bundle);

		return fragment;
	}

	/**
	 * Helper method to get the current city weather data
	 * @return
	 */
	public WeatherInfo getWeatherInfo() {
		return weatherInfo;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		weatherInfo = (WeatherInfo) getArguments().getSerializable(EXTRA_CITY_WEATHER);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_city, container, false);
		cityField = (TextView)rootView.findViewById(R.id.city_field);
		detailsField = (TextView)rootView.findViewById(R.id.details_field);
		currentTemperatureField = (TextView)rootView.findViewById(R.id.current_temperature_field);
		weatherIcon = (ImageView) rootView.findViewById(R.id.weatherIcon);

		recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
		return rootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setData();
	}

	private void setData() {
		if (weatherInfo == null) {
			return;
		}

		WeatherInfo.City city = weatherInfo.getCity();
		cityField.setText(city.getName() + ", " + city.getCountry());

		WeatherInfo.CityWeather weather = weatherInfo.getWeatherList().get(0);

		detailsField.setText(weather.getWeather().getDescription());

		currentTemperatureField.setText(getString(R.string.degree_celsius, weather.getMain().getTemp()));

		Picasso.with(getContext()).load(WeatherApiClient.getWeatherIconUrl(weather.getWeather().getIcon())).into(weatherIcon);

		recyclerView.setAdapter(new WeatherForecastAdapter(weatherInfo.getUniqueDaysWeatherForCast()));
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		cityField = null;
		detailsField = null;
		currentTemperatureField = null;
		weatherIcon = null;
	}
}

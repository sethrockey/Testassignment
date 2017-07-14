package com.sj.cityweather.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import com.sj.cityweather.R;
import com.sj.cityweather.api.WeatherApiClient;
import com.sj.cityweather.model.WeatherInfo;
import com.squareup.picasso.Picasso;

/**
 * Adapter used to show the upcoming days weather forecast
 */
public class WeatherForecastAdapter extends  RecyclerView.Adapter<WeatherForecastAdapter.ViewHolder> {

	List<WeatherInfo.CityWeather> weatherList;

	public WeatherForecastAdapter(List<WeatherInfo.CityWeather> weatherList) {
		this.weatherList = weatherList;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_item_view, parent, false));
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.update(weatherList.get(position));
	}

	@Override
	public int getItemCount() {
		return weatherList.size();
	}


	public class ViewHolder extends RecyclerView.ViewHolder {
		public ImageView icon;
		public TextView tempTV, weekDayTV;

		public ViewHolder(View v) {
			super(v);
			icon = (ImageView) v.findViewById(R.id.icon);
			tempTV = (TextView) v.findViewById(R.id.tempRange);
			weekDayTV = (TextView) v.findViewById(R.id.weekDay);
			updateViewDimensions();
		}

		private void updateViewDimensions() {
			int dimension = itemView.getContext().getResources().getDisplayMetrics().widthPixels / weatherList.size();
			RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) itemView.getLayoutParams();
			layoutParams.width = dimension;
			itemView.setLayoutParams(layoutParams);
		}

		public void update(WeatherInfo.CityWeather cityWeather) {
			Context context = itemView.getContext();
			WeatherInfo.CityWeather.Weather weather = cityWeather.getWeather();
			//Picasso.with(context).load(WeatherApiClient.getWeatherIconUrl(weather.getIcon())).into(icon);

			Resources resources = context.getResources();
			StringBuilder builder = new StringBuilder();
			builder.append(resources.getString(R.string.degree_celsius, cityWeather.getMain().getTempMax()));
			builder.append("\n\n");
			builder.append(resources.getString(R.string.degree_celsius, cityWeather.getMain().getTempMin()));
			tempTV.setText(builder.toString());

			String week = new SimpleDateFormat("EE").format(cityWeather.getDate());
			weekDayTV.setText(week);
		}
	}
}

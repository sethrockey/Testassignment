package com.sj.cityweather;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.sj.cityweather.adapter.CustomAdapter;
import com.sj.cityweather.adapter.DataBaseHandler;
import com.sj.cityweather.api.WeatherApiClient;
import com.sj.cityweather.dialog.DialogFactory;
import com.sj.cityweather.fragment.CityFragment;
import com.sj.cityweather.model.City;
import com.sj.cityweather.model.WeatherInfo;
import com.sj.cityweather.prefrence.AppPreference;
import com.sj.cityweather.util.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements SearchView.OnCloseListener, SearchView.OnQueryTextListener, AdapterView.OnItemClickListener {

    private SearchView searchView;
    View containerView, errorView;
    CustomAdapter mAdapter;
    DataBaseHandler dbHandler;
    City city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        containerView = findViewById(R.id.container);
        errorView = findViewById(R.id.errorMsg);

        if (savedInstanceState == null) {
            // Set the initial data
            setUpData();
        }
        dbHandler = new DataBaseHandler(this);
        dbHandler.getWritableDatabase();
        city = new City();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.weather_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);
        MenuItem searchMenuItem = menu.findItem(R.id.actionCitySearch);
        searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);

        // set up the layout of the action view part of the search menu item
        final ViewGroup.LayoutParams layoutParams = searchView.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        searchView.setQueryHint(getString(R.string.search_city_hint));
         Cursor c = dbHandler.getAllCity();
        if (c.getCount() < 0) {
            return false;
        }
         mAdapter = new CustomAdapter(this, c,onClickListener, onLongClickListener);
        searchView.setSuggestionsAdapter(mAdapter);
        searchView.setOnCloseListener(this);
        searchView.setOnQueryTextListener(this);

        return true;
    }



    // Showing Delete the list

    private void showDeleteDialog(final String cityValue) {

        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(this);
        deleteDialog.setTitle("CityWeather");

        deleteDialog.setMessage("Do you really want to delete this city");
        deleteDialog.setNegativeButton("No", null);
        deleteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                city.setCity(cityValue);
                dbHandler.deleteCity(city);
                updateList();
            }});
        deleteDialog.create();
        deleteDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.actionRefresh) {
            Utils.log(this, "onOptionsItemSelected: action refresh");
            CityFragment fragment = (CityFragment) getSupportFragmentManager().findFragmentById(R.id.container);
            if (fragment != null) {
                WeatherInfo weatherInfo = fragment.getWeatherInfo();
                if (weatherInfo != null) {
                    getWeatherForCity(weatherInfo.getCity().getName());
                }
            }
            return true;
        }else if(item.getItemId() == R.id.contactus){
            WebView wb = new WebView(this);
            wb.loadUrl("file:///android_asset/Contact.html");
            wb.getSettings().setJavaScriptEnabled(true);
            setContentView(wb);
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpData() {
        WeatherInfo weatherInfo = AppPreference.getPreference().getCityWeatherForecast();

        if (weatherInfo == null) {
            Utils.log(this, "setUpData: first visit, ask for city.");
            showCityInputDialog();
        } else {
            getWeatherForCity(weatherInfo.getCity().getName());
        }
    }

    private void setCityFragment(WeatherInfo weatherInfo) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, CityFragment.newInstance(weatherInfo));
        fragmentTransaction.commit();
    }

    private void showCityInputDialog() {
        DialogFactory.showCityInputDialog(this, new DialogFactory.DialogListener<String>() {
            @Override
            public void onSuccess(String cityName) {
                if (TextUtils.isEmpty(cityName)) {
                    showCityInputDialog();
                } else {
                    getWeatherForCity(cityName);
                }
            }

            @Override
            public void onFailure() {
                finish();
            }
        });
    }

    /**
     * Method to get weather information about a city.
     * Using the WeatherApiClient we call the api method
     * passing the callback, in callback we handle the response.
     *
     * @param cityName
     */
    private void getWeatherForCity(String cityName) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.getting_update));
        dialog.show();
        WeatherApiClient.getApiClient().getCityForecastWeather(cityName, new Callback<WeatherInfo>() {
            @Override
            public void onResponse(Call<WeatherInfo> call, Response<WeatherInfo> response) {
                dialog.dismiss();
                WeatherInfo weatherInfo = response.body();
                Log.i("WEATHER", weatherInfo + "");

                AppPreference appPreference = AppPreference.getPreference();
                // If the city is same as the user's preferred city then
                // update it in preference.
                if (appPreference.shouldUpdateCityData(weatherInfo)) {
                    appPreference.saveCityWeather(weatherInfo);
                }
                setCityFragment(weatherInfo);
                errorView.setVisibility(View.GONE);
                containerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<WeatherInfo> call, Throwable t) {
                dialog.dismiss();
                Log.i("WEATHER", t.getMessage());
                errorView.setVisibility(View.VISIBLE);
                containerView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onClose() {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        city.setCity(query);/*
        Cursor cursor = dbHandler.getAllCity();
        for(int i =0;i<=cursor.getCount();i++){
            String cityName = cursor.getString(cursor.getColumnIndex("city"));
        }*/


        dbHandler.addCity(city);
        updateList();
        getWeatherForCity(query);
        Utils.hideSoftKeypad(this);
        return true;
    }


    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    // Onclick Method for fetching weather Information

    private View.OnClickListener onClickListener =  new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TextView textView = (TextView) view;
            String name = textView.getText().toString();
            getWeatherForCity(name);
            Utils.hideSoftKeypad(MainActivity.this);
        }

    };

    // OnLongclick Method for fetching weather Information

     private View.OnLongClickListener onLongClickListener  = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            TextView textView = (TextView) v;
            String cityName = textView.getText().toString();
            showDeleteDialog(cityName);
            return true;
        }
    };

    // Update list for  fetching data

    private void updateList(){
        Cursor c = dbHandler.getAllCity();
        if (c.getCount() < 0) {
            return ;
        }
        mAdapter = new CustomAdapter(this, c, onClickListener,onLongClickListener);
        searchView.setSuggestionsAdapter(mAdapter);
    }
}

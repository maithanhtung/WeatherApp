package com.example.tom.weatherapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.tom.weatherapp.models.Response;

import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity
        implements LocationListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    private WeatherAppAPI mainApi;
    private Location currentLocation;
    private LocationManager locationManager;
    private TextView tv_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainApi = API.get().create(WeatherAppAPI.class);
        init();
    }

    private void init() {
        tv_icon = (TextView) findViewById(R.id.tv_icon);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (currentLocation != null){
            updateWeather(currentLocation);
        }
        try{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
        } catch (Exception e) {
            Log.e(TAG, "Error", e);
        }
    }

    private void updateWeather(Location location) {
        String lat = String.valueOf(location.getLatitude());
        String lon = String.valueOf(location.getLongitude());
        mainApi.getWeatherWithLL(lat, lon)
                .enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        response.body();
                        if (response.body() != null) {
                            String id = response.body().id;
                            String main = response.body().main;
                            String description = response.body().description;
                            String icon = response.body().icon;
                            Log.d(TAG, "onResponse: " + "icon: " + icon + " description: " + description + " main: " + main + " id: " + id );
                            tv_icon.setText(icon);
                        }
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {

                    }
                });


    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        if (currentLocation != null) {
            updateWeather(currentLocation);
        }
        Log.d(TAG, "onLocationChanged: " + currentLocation.getLatitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}

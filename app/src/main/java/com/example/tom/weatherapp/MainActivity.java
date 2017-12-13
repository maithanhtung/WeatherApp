package com.example.tom.weatherapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.PictureDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.caverock.androidsvg.SVG;
import com.example.tom.weatherapp.models.Response;

import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity
        implements LocationListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    private WeatherAppAPI mainApi;
    private Location currentLocation;
    private LocationManager locationManager;
    private TextView tv_Noti;
    private ImageView imageViewNet;
    private TextView tv_Des;
    private GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainApi = API.get().create(WeatherAppAPI.class);
        init();
    }

    private void init() {
        tv_Noti = (TextView) findViewById(R.id.tv_Noti);
        tv_Des = (TextView) findViewById(R.id.tv_Des);
        imageViewNet = (ImageView) findViewById(R.id.svg_icon);

        requestBuilder = Glide.with(this)
                .using(Glide.buildStreamModelLoader(Uri.class, this), InputStream.class)
                .from(Uri.class)
                .as(SVG.class)
                .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                .sourceEncoder(new StreamEncoder())
                .cacheDecoder(new FileToStreamDecoder<SVG>(new SvgDecoder()))
                .decoder(new SvgDecoder())
                .animate(android.R.anim.fade_in)
                .listener(new SvgSoftwareLayerSetter<Uri>());

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            tv_Noti.setVisibility(View.VISIBLE);
            imageViewNet.setVisibility(View.GONE);
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (currentLocation != null){
            tv_Noti.setVisibility(View.GONE);
            imageViewNet.setVisibility(View.VISIBLE);
            updateWeather(currentLocation);
        } else {
            tv_Noti.setVisibility(View.VISIBLE);
            imageViewNet.setVisibility(View.GONE);
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
                            loadIcon(icon);
                            loadDescription(id);
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
            tv_Noti.setVisibility(View.GONE);
            imageViewNet.setVisibility(View.VISIBLE);
            updateWeather(currentLocation);
        } else {
            tv_Noti.setVisibility(View.VISIBLE);
            imageViewNet.setVisibility(View.GONE);
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

    private void loadDescription(String id) {
        int id_Des = Integer.parseInt(id);
        switch (id_Des) {
            case 200 :
                tv_Des.setText("thunderstorm with light rain");
                break;
            case 201 :
                tv_Des.setText("thunderstorm with rain");
                break;
            case 202 :
                tv_Des.setText("thunderstorm with heavy rain");
                break;
            case 210 :
                tv_Des.setText("light thunderstorm");
                break;
            case 211 :
                tv_Des.setText("thunderstorm");
                break;
            case 212 :
                tv_Des.setText("heavy thunderstorm");
                break;
            case 221 :
                tv_Des.setText("ragged thunderstorm");
                break;
            case 230 :
                tv_Des.setText("thunderstorm with light drizzle");
                break;
            case 231 :
                tv_Des.setText("thunderstorm with drizzle");
                break;
            case 232 :
                tv_Des.setText("thunderstorm with heavy drizzle");
                break;
            case 300 :
                tv_Des.setText("light intensity drizzle");
                break;
            case 301 :
                tv_Des.setText("drizzle");
                break;
            case 302 :
                tv_Des.setText("heavy intensity drizzle");
                break;
            case 310 :
                tv_Des.setText("light intensity drizzle rain");
                break;
            case 311 :
                tv_Des.setText("drizzle rain");
                break;
            case 312 :
                tv_Des.setText("heavy intensity drizzle rain");
                break;
            case 313 :
                tv_Des.setText("shower rain and drizzle");
                break;
            case 314 :
                tv_Des.setText("heavy shower rain and drizzle");
                break;
            case 321 :
                tv_Des.setText("shower drizzle");
                break;
            case 500 :
                tv_Des.setText("shower drizzle");
                break;
            case 501 :
                tv_Des.setText("moderate rain");
                break;
            case 502 :
                tv_Des.setText("heavy intensity rain");
                break;
            case 503 :
                tv_Des.setText("very heavy rain");
                break;
            case 504 :
                tv_Des.setText("extreme rain");
                break;
            case 511 :
                tv_Des.setText("freezing rain");
                break;
            case 520 :
                tv_Des.setText("light intensity shower rain");
                break;
            case 521 :
                tv_Des.setText("shower rain");
                break;
            case 522 :
                tv_Des.setText("heavy intensity shower rain");
                break;
            case 531 :
                tv_Des.setText("ragged shower rain");
                break;
            case 600 :
                tv_Des.setText("light snow");
                break;
            case 601 :
                tv_Des.setText("snow");
                break;
            case 602 :
                tv_Des.setText("heavy snow");
                break;
            case 611 :
                tv_Des.setText("sleet");
                break;
            case 612 :
                tv_Des.setText("shower sleet");
                break;
            case 615 :
                tv_Des.setText("light rain and snow");
                break;
            case 616 :
                tv_Des.setText("rain and snow");
                break;
            case 620 :
                tv_Des.setText("light shower snow");
                break;
            case 621 :
                tv_Des.setText("shower snow");
                break;
            case 622 :
                tv_Des.setText("heavy shower snow");
                break;
            case 701 :
                tv_Des.setText("mist");
                break;
            case 711 :
                tv_Des.setText("smoke");
                break;
            case 721 :
                tv_Des.setText("haze");
                break;
            case 731 :
                tv_Des.setText("sand, dust whirls");
                break;
            case 741 :
                tv_Des.setText("fog");
                break;
            case 751 :
                tv_Des.setText("sand");
                break;
            case 761 :
                tv_Des.setText("dust");
                break;
            case 762 :
                tv_Des.setText("volcanic ash");
                break;
            case 771 :
                tv_Des.setText("squalls");
                break;
            case 781 :
                tv_Des.setText("tornado");
                break;
            case 800 :
                tv_Des.setText("clear sky");
                break;
            case 801 :
                tv_Des.setText("few clouds");
                break;
            case 802 :
                tv_Des.setText("scattered clouds");
                break;
            case 803 :
                tv_Des.setText("broken clouds");
                break;
            case 804 :
                tv_Des.setText("overcast clouds");
                break;
            case 900 :
                tv_Des.setText("tornado");
                break;
            case 901 :
                tv_Des.setText("tropical storm");
                break;
            case 902 :
                tv_Des.setText("hurricane");
                break;
            case 903 :
                tv_Des.setText("cold");
                break;
            case 904 :
                tv_Des.setText("hot");
                break;
            case 905 :
                tv_Des.setText("windy");
                break;
            case 906 :
                tv_Des.setText("hail");
                break;
            case 951 :
                tv_Des.setText("calm");
                break;
            case 952 :
                tv_Des.setText("light breeze");
                break;
            case 953 :
                tv_Des.setText("gentle breeze");
                break;
            case 954 :
                tv_Des.setText("moderate breeze");
                break;
            case 955 :
                tv_Des.setText("fresh breeze");
                break;
            case 956 :
                tv_Des.setText("strong breeze");
                break;
            case 957 :
                tv_Des.setText("high wind, near gale");
                break;
            case 958 :
                tv_Des.setText("gale");
                break;
            case 959 :
                tv_Des.setText("severe gale");
                break;
            case 960 :
                tv_Des.setText("storm");
                break;
            case 961 :
                tv_Des.setText("violent storm");
                break;
            case 962 :
                tv_Des.setText("hurricane");
                break;
            default:
                tv_Des.setText("Location not found");
        }
    }

    private void loadIcon(String icon) {
        Uri uri = Uri.parse("https://weatherapp.eficode.fi/img/"+ icon +".svg");
        requestBuilder
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                // SVG cannot be serialized so it's not worth to cache it
                .load(uri)
                .into(imageViewNet);
    }
}

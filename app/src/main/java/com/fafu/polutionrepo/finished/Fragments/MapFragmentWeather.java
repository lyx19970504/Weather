package com.fafu.polutionrepo.finished.Fragments;

import android.content.Intent;

import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.util.Calendar;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.VisibleRegion;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.fafu.polutionrepo.finished.Activities.AddCityActivity;
import com.fafu.polutionrepo.finished.Activities.CityListActivity;
import com.fafu.polutionrepo.finished.Activities.MainActivity;
import com.fafu.polutionrepo.finished.Adapter.Hour_Adapter;
import com.fafu.polutionrepo.finished.Adapter.InfoWindowDayAdapter;
import com.fafu.polutionrepo.finished.Adapter.InfoWindowHourAdapter;
import com.fafu.polutionrepo.finished.Areas.Capital;
import com.fafu.polutionrepo.finished.Areas.City;
import com.fafu.polutionrepo.finished.Areas.County;
import com.fafu.polutionrepo.finished.Beans.Air;
import com.fafu.polutionrepo.finished.Beans.Forecast;
import com.fafu.polutionrepo.finished.Beans.Hour_item;
import com.fafu.polutionrepo.finished.Beans.Weather;
import com.fafu.polutionrepo.finished.Beans.Weather10;
import com.fafu.polutionrepo.finished.Beans.Weather_industrial;
import com.fafu.polutionrepo.finished.R;
import com.fafu.polutionrepo.finished.Util.Util;
import com.fafu.polutionrepo.finished.Util.WeatherUtil;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;

import org.litepal.LitePal;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import kotlin.collections.IntIterator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

public class MapFragmentWeather extends Fragment
        implements AMap.OnCameraChangeListener,
        AMap.OnMarkerClickListener,View.OnClickListener, AMap.OnMyLocationChangeListener {

    final static String url = "https://free-api.heweather.net/s6/weather?key=3470750de86f49f08ea4b7472bde0bb0&location=";
    final static String Hour24 = "https://api.heweather.net/s6/weather?key=a9e489cf72bb4d5981def08c9258a267&location=";
    final static String Day10 = "https://api.heweather.net/s6/weather/forecast?key=291d884650704b08bf46b3f1dd23c747&location=";
    public static final String TAG = "MapFragmentWeather";
    MapView mapView;
    @InjectView(R.id.map_layout)
    FrameLayout frameLayout;

    private AMap aMap;
    private AlertDialog alertDialog;
    public static final int CITY_REQUEST = 0;
    public static final int POSITION_NAME_REQUEST = 1;
    public static final String PROVINCE_NAME = "province_name";
    public static final String CITY_NAME = "city_name";
    public static final String LATITUDE = "position_latitude";
    public static final String LONGITUDE = "position_longitude";
    ImageView imageView1,imageView2;
    TextView textView1,textView2;



    private ImageView searchView;
    private TextView mLocationView;

    private boolean weatherCameraChanged = true;
    private boolean temperatureCameraChanged = false;
    private boolean isMaxWeatherRegion = true;
    private boolean isMaxTemperatureRegion = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_map_weather, container,false);
        Injector.injectInto(this,view);
        mapView = new MapView(getContext(), mapOptions());
        mapView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mapView.onCreate(savedInstanceState);
        frameLayout.addView(mapView);
        addInfoWindowButton();
        if(aMap == null) {
            aMap = mapView.getMap();
        }

        initViews();
        return view;
    }

    public void addInfoWindowButton(){
        int density = Util.getDensity(getContext());
        View weatherView = LayoutInflater.from(getContext()).inflate(R.layout.map_button_layout,null,false);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); 
        params.gravity = Gravity.END;
        params.rightMargin = 10 * density;
        params.topMargin = 100 * density;
        weatherView.setLayoutParams(params);
        textView1 = weatherView.findViewById(R.id.text_info);
        textView1.setText("预报");
        textView1.setTextColor(Color.BLUE);
        imageView1 = weatherView.findViewById(R.id.image_info);
        imageView1.setImageDrawable(getContext().getDrawable(R.drawable.weather_forecast));
        imageView1.setOnClickListener(this);
        frameLayout.addView(weatherView);

        View tempView = LayoutInflater.from(getContext()).inflate(R.layout.map_button_layout,null,false);
        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params2.gravity = Gravity.END;
        params2.rightMargin = 10 * density;
        params2.topMargin = 200 * density;
        tempView.setLayoutParams(params2);
        textView2 = tempView.findViewById(R.id.text_info);
        textView2.setText("温度");
        textView2.setTextColor(Color.GRAY);
        imageView2 = tempView.findViewById(R.id.image_info);
        imageView2.setOnClickListener(this);
        imageView2.setImageDrawable(getContext().getDrawable(R.drawable.temperature_forecast_dark));
        frameLayout.addView(tempView);
    }

    public AMapOptions mapOptions(){
        LatLng centerFuzhouPoint = new LatLng(26.15000,119.28333);
        AMapOptions mapOptions = new AMapOptions();
        mapOptions.camera(new CameraPosition(centerFuzhouPoint,5f,0,0));
        return mapOptions;
    }

    public void initViews(){
        searchView.setOnClickListener(this);
        mLocationView.setOnClickListener(this);
        getMyLocation();
        setUiWidgets();
        aMap.setOnCameraChangeListener(this);
        aMap.setOnMarkerClickListener(this);
        aMap.setOnMyLocationChangeListener(this);
    }

    public void getMyLocation(){
        aMap.setMyLocationEnabled(true);
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        myLocationStyle.strokeColor(Color.TRANSPARENT);
        myLocationStyle.radiusFillColor(Color.TRANSPARENT);
        aMap.setMyLocationStyle(myLocationStyle);
    }

    public void setUiWidgets(){
        UiSettings settings = aMap.getUiSettings();
        settings.setCompassEnabled(true);
        settings.setMyLocationButtonEnabled(true);
        settings.setScaleControlsEnabled(true);
        settings.setRotateGesturesEnabled(false);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        //do nothing
    }


    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        VisibleRegion region = aMap.getProjection().getVisibleRegion();
        String latitudeBottom = String.valueOf(region.nearLeft.latitude);
        String longitudeRight = String.valueOf(region.farRight.longitude);
        String latitudeTop = String.valueOf(region.farRight.latitude);
        String longitudeLeft = String.valueOf(region.nearLeft.longitude);
        Log.d(TAG, "onCameraChangeFinish: "+latitudeBottom+","+latitudeTop);
        Log.d(TAG, "onCameraChangeFinish: "+longitudeLeft+","+longitudeRight);
        if(weatherCameraChanged) {  //显示天气时才能缩放，显示温度时不能缩放
            if (cameraPosition.zoom < 7.5) {
                //不显示 地级市、县
                if(isMaxWeatherRegion) {    //省会城市只加载一次
                    aMap.clear();
                    queryCapitals();
                    isMaxWeatherRegion = false;
                }
            } else if (cameraPosition.zoom >= 7.5 && cameraPosition.zoom < 10.5) {
                //显示地级市、省会
                isMaxWeatherRegion = true;
                aMap.clear();
                List<City> cities = LitePal.where("longitude < ? and longitude > ? and latitude < ? and latitude > ?", longitudeRight, longitudeLeft, latitudeTop, latitudeBottom).find(City.class);
                if (cities.size() != 0) {
                    for (final City city : cities) {
                        Util.sendHttpOkRequest(url + city.getCityName(), new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.d(TAG, "onFailure: " + "获取天气数据失败");
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String responseText = response.body().string();
                                if (responseText != null) {
                                    Weather weather = WeatherUtil.handleWeatherResponse(responseText);
                                    if (weather != null && weather.status.equals("ok")) {
                                        createWeatherMarker(city, weather);
                                    }
                                }
                            }
                        });
                    }
                }
            } else if (cameraPosition.zoom >= 10.5) {
                //显示所有
                aMap.clear();
                isMaxWeatherRegion = true;
                List<County> counties = LitePal.where("longitude < ? and longitude > ? and latitude < ? and latitude > ?", longitudeRight, longitudeLeft, latitudeTop, latitudeBottom).find(County.class);
                if (counties.size() != 0) {
                    for (final County county : counties) {
                        Util.sendHttpOkRequest(url + county.getCountyName(), new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.d(TAG, "onFailure: " + "获取天气数据失败");
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String responseText = response.body().string();
                                if (responseText != null) {
                                    Weather weather = WeatherUtil.handleWeatherResponse(responseText);
                                    if (weather != null && weather.status.equals("ok")) {
                                        createWeatherMarker(county, weather);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        }


        if(temperatureCameraChanged){    //显示温度时
            if (cameraPosition.zoom < 7.5) {
                //不显示 地级市、县
                if(isMaxTemperatureRegion) {
                    aMap.clear();
                    addTemperatureMarkers();
                    isMaxTemperatureRegion = false;
                }
            } else if (cameraPosition.zoom >= 7.5 && cameraPosition.zoom < 10.5) {
                //显示地级市、省会
                isMaxTemperatureRegion = true;
                aMap.clear();
                List<City> cities = LitePal.where("longitude < ? and longitude > ? and latitude < ? and latitude > ?", longitudeRight, longitudeLeft, latitudeTop, latitudeBottom).find(City.class);
                if (cities.size() != 0) {
                    for (final City city : cities) {
                        Util.sendHttpOkRequest(url + city.getCityName(), new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.d(TAG, "onFailure: " + "获取天气数据失败");
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String responseText = response.body().string();
                                if (responseText != null) {
                                    Weather weather = WeatherUtil.handleWeatherResponse(responseText);
                                    if (weather != null && weather.status.equals("ok")) {
                                        createTemperatureMarker(city, weather);
                                    }
                                }
                            }
                        });
                    }
                }
            } else if (cameraPosition.zoom >= 10.5) {
                //显示所有
                isMaxTemperatureRegion = true;
                aMap.clear();
                List<County> counties = LitePal.where("longitude < ? and longitude > ? and latitude < ? and latitude > ?", longitudeRight, longitudeLeft, latitudeTop, latitudeBottom).find(County.class);
                if (counties.size() != 0) {
                    for (final County county : counties) {
                        Util.sendHttpOkRequest(url + county.getCountyName(), new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.d(TAG, "onFailure: " + "获取天气数据失败");
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String responseText = response.body().string();
                                if (responseText != null) {
                                    Weather weather = WeatherUtil.handleWeatherResponse(responseText);
                                    if (weather != null && weather.status.equals("ok")) {
                                        createTemperatureMarker(county, weather);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        }
    }


    public void queryCapitals(){
        final List<Capital> capitals = LitePal.findAll(Capital.class);
        for(final Capital capital : capitals){
            Util.sendHttpOkRequest(url + capital.getCityName(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if(getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "获取天气失败!!!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String responseText = response.body().string();
                    if (response != null) {
                        final Weather weather = WeatherUtil.handleWeatherResponse(responseText);
                        if(getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (weather != null && weather.status.equals("ok")) {
                                        createWeatherMarker(capital, weather);
                                    } else {
                                        Toast.makeText(getContext(), "获取天气信息失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    private Marker createWeatherMarker(Capital capital,Weather weather) {
        View iconView = LayoutInflater.from(getContext()).inflate(R.layout.icon_layout,null,false);
        ImageView iconImage = iconView.findViewById(R.id.icon_image);
        WeatherFragment.judgeWeatherImage(iconImage, weather.forecasts.get(0).dayConf, null,false);
        TextView iconText = iconView.findViewById(R.id.icon_text);
        iconText.setText(weather.forecasts.get(0).tmp_min+"～"+weather.forecasts.get(0).tmp_max+"°");
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.infoWindowEnable(false);
        markerOptions.position(new LatLng(capital.getLatitude(),capital.getLongitude()));
        markerOptions.title(capital.getCityName());
        markerOptions.icon(BitmapDescriptorFactory.fromView(iconView));
        Marker marker = aMap.addMarker(markerOptions);
        marker.setDraggable(false);
        addAnimation(marker);
        return marker;
    }

    private Marker createWeatherMarker(City city,Weather weather) {
        View iconView = LayoutInflater.from(getContext()).inflate(R.layout.icon_layout,null,false);
        ImageView iconImage = iconView.findViewById(R.id.icon_image);
        WeatherFragment.judgeWeatherImage(iconImage, weather.forecasts.get(0).dayConf, null,false);
        TextView iconText = iconView.findViewById(R.id.icon_text);
        iconText.setText(weather.forecasts.get(0).tmp_min+"～"+weather.forecasts.get(0).tmp_max+"°");
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.infoWindowEnable(false);
        markerOptions.position(new LatLng(city.getLatitude(),city.getLongitude()));
        markerOptions.title(city.getCityName());
        markerOptions.icon(BitmapDescriptorFactory.fromView(iconView));
        Marker marker = aMap.addMarker(markerOptions);
        marker.setDraggable(false);
        addAnimation(marker);
        return marker;
    }

    private Marker createWeatherMarker(County county,Weather weather) {
        View iconView = LayoutInflater.from(getContext()).inflate(R.layout.icon_layout,null,false);
        ImageView iconImage = iconView.findViewById(R.id.icon_image);
        WeatherFragment.judgeWeatherImage(iconImage, weather.forecasts.get(0).dayConf, null,false);
        TextView iconText = iconView.findViewById(R.id.icon_text);
        iconText.setText(weather.forecasts.get(0).tmp_min+"～"+weather.forecasts.get(0).tmp_max+"°");
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.infoWindowEnable(false);
        markerOptions.position(new LatLng(county.getLatitude(),county.getLongitude()));
        markerOptions.title(county.getCountyName());
        markerOptions.icon(BitmapDescriptorFactory.fromView(iconView));
        Marker marker = aMap.addMarker(markerOptions);
        marker.setDraggable(false);
        addAnimation(marker);
        return marker;
    }

    public void addTemperatureMarkers(){
        final List<Capital> capitals =  LitePal.findAll(Capital.class);
        if(capitals.size() != 0){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < capitals.size(); i++) {
                        final Capital capital = capitals.get(i);
                        Util.sendHttpOkRequest(url + capital.getCityName(), new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                if(getActivity() != null) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getContext(), "无法获取温度数据!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String responseText = response.body().string();
                                Weather weather = WeatherUtil.handleWeatherResponse(responseText);
                                if (weather != null && weather.status.equals("ok")) {
                                    createTemperatureMarker(capital, weather);
                                }
                            }
                        });
                    }
                }
            }).start();

        }
    }

    public void createTemperatureMarker(Capital capital,Weather weather) {
        View iconView = LayoutInflater.from(getContext()).inflate(R.layout.temp_view,null,false);
        iconView.setLayoutParams(new ViewGroup.LayoutParams(41 * Util.getDensity(getContext()), ViewGroup.LayoutParams.WRAP_CONTENT));
        ((TextView) iconView).setText(weather.now.tmp + "°");
        iconView.setBackgroundColor(judgeBackgroundColor(weather.now.tmp));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.infoWindowEnable(false);
        markerOptions.title(capital.getCityName()+"?");
        markerOptions.position(new LatLng(capital.getLatitude(),capital.getLongitude()));
        markerOptions.icon(BitmapDescriptorFactory.fromView(iconView));
        Marker marker = aMap.addMarker(markerOptions);
        marker.setDraggable(false);
        addAnimation(marker);
    }

    public void createTemperatureMarker(City city,Weather weather) {
        View iconView = LayoutInflater.from(getContext()).inflate(R.layout.temp_view,null,false);
        iconView.setLayoutParams(new ViewGroup.LayoutParams(41 * Util.getDensity(getContext()), ViewGroup.LayoutParams.WRAP_CONTENT));
        ((TextView) iconView).setText(weather.now.tmp + "°");
        iconView.setBackgroundColor(judgeBackgroundColor(weather.now.tmp));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.infoWindowEnable(false);
        markerOptions.title(city.getCityName()+"?");
        markerOptions.position(new LatLng(city.getLatitude(),city.getLongitude()));
        markerOptions.icon(BitmapDescriptorFactory.fromView(iconView));
        Marker marker = aMap.addMarker(markerOptions);
        marker.setDraggable(false);
        addAnimation(marker);
    }

    public void createTemperatureMarker(County county,Weather weather) {
        View iconView = LayoutInflater.from(getContext()).inflate(R.layout.temp_view,null,false);
        iconView.setLayoutParams(new ViewGroup.LayoutParams(41 * Util.getDensity(getContext()), ViewGroup.LayoutParams.WRAP_CONTENT));
        ((TextView) iconView).setText(weather.now.tmp + "°");
        iconView.setBackgroundColor(judgeBackgroundColor(weather.now.tmp));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.infoWindowEnable(false);
        markerOptions.title(county.getCountyName()+"?");
        markerOptions.position(new LatLng(county.getLatitude(),county.getLongitude()));
        markerOptions.icon(BitmapDescriptorFactory.fromView(iconView));
        Marker marker = aMap.addMarker(markerOptions);
        marker.setDraggable(false);
        addAnimation(marker);
    }

    private int judgeBackgroundColor(String temp){
        int tmp = Integer.valueOf(temp);
        switch (tmp / 5){
            case 7:   //35 - 40
                return Color.rgb(156,39,176);
            case 6:   //30 -35
                return Color.rgb(91,12,12);
            case 5:   //25 - 30
                return Color.rgb(255,87,34);
            case 4:   //20 - 25
                return Color.rgb(255,152,0);
            case 3:   //15 - 20
                return Color.rgb(255,235,89);
            case 2:
                return Color.rgb(92,196,252);
            case 1:
                return Color.rgb(3,169,244);
            case 0:
                return Color.rgb(34,95,128);
            default:
                return 0;
        }
    }

    private static void addAnimation(Marker marker){
        //添加动画
        Animation markerAnimation = new ScaleAnimation(0, 1, 0, 1); //初始化生长效果动画
        markerAnimation.setDuration(1000);  //设置动画时间 单位毫秒
        marker.setAnimation(markerAnimation);
        marker.startAnimation();
    }


    @Override
    public boolean onMarkerClick(final Marker marker) {
        if(!marker.getTitle().contains("?")) {
            openDialog("未来天气数据加载中...");
            //获得天气数据
            Util.sendHttpOkRequest(Hour24 + marker.getTitle(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if(getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "获取天气数据失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    if (response != null) {
                        final Weather_industrial weather = WeatherUtil.handleWeatherResponse2(responseText);
                        if(getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (weather != null && weather.status.equals("ok")) {
                                        Util.sendHttpOkRequest(Day10 + marker.getTitle(), new Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                closeDialog();
                                                if(getActivity() != null) {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(getContext(), "获取天气数据失败", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onResponse(Call call, Response response) throws IOException {
                                                closeDialog();
                                                String responseText = response.body().string();
                                                if (responseText != null) {
                                                    final Weather10 weather10 = WeatherUtil.handle10DaysResponse(responseText);
                                                    if(getActivity() != null) {
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if (weather10 != null && weather10.status.equals("ok")) {
                                                                    openDialog(weather, weather10);
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }else{
            openDialog("今日逐小时温度加载中...");
            final String cityName = marker.getTitle().replace("?","");
            Util.sendHttpOkRequest(Hour24 + cityName, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if(getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "获取天气数据失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    closeDialog();
                    String responseText = response.body().string();
                    if (response != null) {
                        final Weather_industrial weather = WeatherUtil.handleWeatherResponse2(responseText);
                        if(getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    openTempDialog(weather, cityName);
                                }
                            });
                        }
                    }
                }
            });
        }
        return false;
    }

    public void openTempDialog(Weather_industrial weather,String cityName){
        if(weather != null){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View view = LayoutInflater.from(getContext()).inflate(R.layout.temp_infowindow_layout, null, false);
            TextView textView = view.findViewById(R.id.city_name);
            textView.setText(cityName);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(layoutManager);

            Hour_item item;
            List<Hour_item> hourItems = new ArrayList<>();
            //设置24天气情况
            for(int i=0;i<weather.hours_24.size();i++){
                item = new Hour_item();
                item.setTime(weather.hours_24.get(i).time);
                item.setTemperature(weather.hours_24.get(i).temperature);
                item.setWeather_image(weather.hours_24.get(i).condition);
                hourItems.add(item);
            }
            recyclerView.setAdapter(new Hour_Adapter(hourItems, getContext(),true));
            builder.setView(view);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void openDialog(Weather_industrial weather_industrial,Weather10 weather10){
        if(weather_industrial != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View view = LayoutInflater.from(getContext()).inflate(R.layout.inforwindow_layout, null, false);
            TextView popView = view.findViewById(R.id.pop);
            popView.setText("未来2小时内下雨的概率为:"+ weather_industrial.hours_24.get(1).pop+"%");
            ((TextView) view.findViewById(R.id.city_name)).setText(weather_industrial.basic.cityName);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(layoutManager);
            InfoWindowHourAdapter adapter = new InfoWindowHourAdapter(weather_industrial);
            recyclerView.setAdapter(adapter);
            if (weather10 != null) {
                setData(view, weather10.forecasts.get(0), 0);     //设置今天的天气
                RecyclerView daysRecyclerView = view.findViewById(R.id.days_recyclerView);
                LinearLayoutManager manager = new LinearLayoutManager(getContext());
                daysRecyclerView.setLayoutManager(manager);
                InfoWindowDayAdapter dayAdapter = new InfoWindowDayAdapter(weather10.forecasts);
                daysRecyclerView.setAdapter(dayAdapter);
            }
            builder.setView(view);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public static void setData(View view,Forecast forecast,int i){
        TextView dateView = view.findViewById(R.id.weather_item_date);
        TextView weekView = view.findViewById(R.id.weather_item_week);
        ImageView imageView = view.findViewById(R.id.weather_item_image);
        TextView conditionView = view.findViewById(R.id.weather_item_condition);
        TextView tempView = view.findViewById(R.id.weather_item_temp);
        dateView.setText(forecast.date.split("-")[2]+"日");
        Calendar calendar = Calendar.getInstance();
        int weekValue = calendar.get(Calendar.DAY_OF_WEEK);
        String week = WeatherFragment.judgeWeek((weekValue+i) % 7);
        weekView.setText(i == 0 ? "今天" : week);
        WeatherFragment.judgeWeatherImage(imageView, forecast.dayConf, null,false);
        conditionView.setText(forecast.dayConf);
        tempView.setText(forecast.tmp_min+"～"+forecast.tmp_max+"°");
    }



    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    public void openDialog(String msg){
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        ProgressBar progressBar = new ProgressBar(getContext());
        progressBar.setIndeterminateDrawable(getContext().getDrawable(R.drawable.progress_bar));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(120 * Util.getDensity(getContext()),120 * Util.getDensity(getContext()));
        params.gravity = Gravity.START;
        progressBar.setLayoutParams(params);
        linearLayout.addView(progressBar);
        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params2.gravity = Gravity.CENTER_VERTICAL;
        textView.setLayoutParams(params2);
        textView.setTextSize(32);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setText(msg);
        linearLayout.addView(textView);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(linearLayout);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void closeDialog(){
        alertDialog.cancel();
        alertDialog = null;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.search:
                intent = new Intent(getContext(), AddCityActivity.class);
                intent.putExtra("from_activity_type", TAG);
                startActivityForResult(intent,CITY_REQUEST);
                break;
            case R.id.present_location:
                intent = new Intent(getActivity(), CityListActivity.class);
                startActivityForResult(intent,POSITION_NAME_REQUEST);
                break;
            case R.id.image_info:
                aMap.clear();
                imageView1.setImageDrawable(getContext().getDrawable(v == imageView1 ? R.drawable.weather_forecast : R.drawable.weather_forecast_dark));
                imageView2.setImageDrawable(getContext().getDrawable(v == imageView1 ? R.drawable.temperature_forecast_dark : R.drawable.temperature_forecast));
                textView1.setTextColor(v == imageView1 ? Color.BLUE : Color.BLACK);
                textView2.setTextColor(v == imageView1 ? Color.BLACK : Color.BLUE);
                if(v == imageView1){
                    aMap.clear();
                    queryCapitals();
                    weatherCameraChanged = true;
                    temperatureCameraChanged = false;
                }else if(v == imageView2){
                    aMap.clear();
                    addTemperatureMarkers();
                    weatherCameraChanged = false;
                    temperatureCameraChanged = true;
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case CITY_REQUEST:
                    String cityName = data.getStringExtra(MainActivity.CITY_NAME);
                    if(cityName.length() == 4 || cityName.length() == 3) {
                        cityName = cityName.trim();
                    }
                    List<County> counties = LitePal.where("countyname like ?", cityName + "%").find(County.class);
                    if(counties.size() != 0){
                        County county = counties.get(0);
                        mLocationView.setText(county.getCountyName());
                        double latitude = county.getLatitude();
                        double longitude = county.getLongitude();
                        aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(latitude,longitude),12f,0,0)));
                    }
                    break;
                case POSITION_NAME_REQUEST:
                    aMap.clear();
                    //获取经纬度，名字，根据名字判断为省还是市，省的话缩放级别消协，市的话缩放级别大些
                    String name = data.getStringExtra(PROVINCE_NAME);
                    double latitude = data.getDoubleExtra(LATITUDE,0);
                    double longitude = data.getDoubleExtra(LONGITUDE,0);
                    if(name == null){  //市
                        name = data.getStringExtra(CITY_NAME);
                        aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(latitude,longitude),11f,0,0)));
                        final List<City> cities = LitePal.where("cityname like ?",name).find(City.class);
                        if(cities.size() != 0){
                            Util.sendHttpOkRequest(url + cities.get(0).getCityName(), new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.d(TAG, "onFailure: "+"获取天气失败!");
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    if(response.body() != null) {
                                        final String responseText = response.body().string();
                                        final Weather weather = WeatherUtil.handleWeatherResponse(responseText);
                                        if (getActivity() != null) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (weather != null && weather.status.equals("ok")) {
                                                        createWeatherMarker(cities.get(0), weather);
                                                    } else {
                                                        Toast.makeText(getContext(), "获取天气信息失败", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                        }
                    } else{             //省
                        aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(latitude,longitude),7f,0,0)));
                        List<City> cities =  LitePal.where("provincename like ?",name).find(City.class);
                        if(cities.size() != 0){
                            for(final City city : cities){
                                Util.sendHttpOkRequest(url + city.getCityName(), new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        Log.d(TAG, "onFailure: "+"获取天气失败!");
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        if(response.body() != null) {
                                            final String responseText = response.body().string();
                                            final Weather weather = WeatherUtil.handleWeatherResponse(responseText);
                                            if (getActivity() != null) {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (weather != null && weather.status.equals("ok")) {
                                                            createWeatherMarker(city, weather);
                                                        } else {
                                                            Toast.makeText(getContext(), "获取天气信息失败", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                    mLocationView.setText(name);
                    break;
            }
        }
    }

    public static MapFragmentWeather newWeatherFragment(ImageView imageView,TextView textView){
        MapFragmentWeather mapFragmentWeather = new MapFragmentWeather();
        mapFragmentWeather.searchView = imageView;
        mapFragmentWeather.mLocationView = textView;
        return mapFragmentWeather;
    }


    @Override
    public void onMyLocationChange(Location location) {

    }
}

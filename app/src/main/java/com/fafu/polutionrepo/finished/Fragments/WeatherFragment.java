package com.fafu.polutionrepo.finished.Fragments;

import android.app.ActivityOptions;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.fafu.polutionrepo.finished.Activities.ChooseCityActivity;
import com.fafu.polutionrepo.finished.Activities.MainActivity;
import com.fafu.polutionrepo.finished.Beans.Air;
import com.fafu.polutionrepo.finished.Beans.City_selected;
import com.fafu.polutionrepo.finished.Beans.Day_item;
import com.fafu.polutionrepo.finished.Beans.Forecast;
import com.fafu.polutionrepo.finished.Beans.Hour_item;
import com.fafu.polutionrepo.finished.Beans.LifeStyleIndex;
import com.fafu.polutionrepo.finished.Beans.Weather;
import com.fafu.polutionrepo.finished.Beans.Weather10;
import com.fafu.polutionrepo.finished.Beans.Weather_industrial;
import com.fafu.polutionrepo.finished.Adapter.*;
import com.fafu.polutionrepo.finished.R;
import com.fafu.polutionrepo.finished.Util.*;
import com.fafu.polutionrepo.finished.View.Air_pic_view;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;


import org.litepal.LitePal;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherFragment extends Fragment implements OnRefreshListener,View.OnClickListener {

    private static final String TAG = "WeatherFragment";
    private static View main_view;
    private static Context mContext;
    private SmartRefreshLayout smartRefreshLayout;
    public static String mLocationInfo;
    private String countyName;
    private String cityName;

    private FrameLayout airLayout;
    private TextView mTempView;
    private TextView mWeatherConditionView;
    private ImageView mWeatherImageView;
    private TextView mMaxMinTemp;
    private TextView mHumView;
    private TextView mFeelTempView;
    private TextView mVisibleView;
    private TextView mAirPressureView;
    private ImageView mWindDirectionView;
    private TextView mWindDirectionTextView;
    private TextView mUvindexView;
    private TextView mSuggestion;
    //明天天气预报----------------------------------
    private TextView mTempView1;
    private TextView mAirCondition1View;
    private ImageView mWeather1View;
    private TextView mWeatherCondition1View;
    //后天天气预报----------------------------------
    private TextView mTempView2;
    private ImageView mWeather2View;
    private TextView mWeatherCondition2View;
    //----------------------------------------------------------------------
    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerView_days;
    public static final String WEATHER_URL = "https://free-api.heweather.net/s6/weather?key=3470750de86f49f08ea4b7472bde0bb0&location=";
    public static final String AIR_URL = "https://free-api.heweather.net/s6/air?key=d1c68adc7e25438485cc5f2fcce41e7e&location=";
    public static final String WEATHER_URL2 = "https://api.heweather.net/s6/weather?key=a9e489cf72bb4d5981def08c9258a267&location=";
    public static final String WEATHER_10DAYS = "https://api.heweather.net/s6/weather/forecast?key=291d884650704b08bf46b3f1dd23c747&location=";
    public static final String LIFESTYLE_URL = "https://api.heweather.net/s6/weather?key=ffa239d6e48446948da8e6e1dcb300e0&location=";

    public static final String WEATHER_INFO = "weather_info";
    public static final String AIR_INFO = "air_info";
    public static final String WEATHER10_INFO = "weather_10days";
    public static final String WEATHER_INDUSTRIAL_INFO = "weather_industrial_info";
    public static final String LIFESTYLE_INFO = "lifestyle_info";
    public static final String AIR = "air";

    private TextView mPm10View;
    private TextView mPm2_5View;
    private TextView mO3View;
    private TextView mNO2View;
    private TextView mSO2View;
    private TextView mCOView;
    private TableLayout mTableLayout;
    public static String accurateInfo;
    private int[] mResId = new int[]{
            R.drawable.comfortable,R.drawable.coats,R.drawable.flu,R.drawable.sport,
            R.drawable.travel,R.drawable.uv,R.drawable.washing_car,R.drawable.air_pollution,
            R.drawable.air_conditioner,R.drawable.allergy,R.drawable.sunglasses,R.drawable.makeup,
            R.drawable.dry_coat,R.drawable.transport,R.drawable.fish,R.drawable.sunscreen};
    private String[] mResNameId;
    private NotificationManager mManager;
    private static final String LocationInfo = "LocationInfo";
    private static final String AccurateInfo = "AccurateInfo";

    public static WeatherFragment getWeatherFragment(String locationInfo,String accurateInfo){
        WeatherFragment weatherFragment = new WeatherFragment();
        WeatherFragment.mLocationInfo = locationInfo;
        WeatherFragment.accurateInfo = accurateInfo;
        return weatherFragment;
    }

    public static WeatherFragment getWeatherFragment(String locationInfo){
        WeatherFragment weatherFragment = new WeatherFragment();
        WeatherFragment.mLocationInfo = locationInfo;
        return weatherFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            if(savedInstanceState.getString(LocationInfo) != null) {
                mLocationInfo = savedInstanceState.getString(LocationInfo);
            }
            if(savedInstanceState.getString(AccurateInfo) != null) {
                accurateInfo = savedInstanceState.getString(AccurateInfo);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        main_view = inflater.inflate(R.layout.activity_weather, container,false);
        mContext = getContext();
        mResNameId = Util.readAssetsTxtFile("lifestyle_index", mContext);
        if(mLocationInfo != null) {
            if(mLocationInfo.split(" ").length == 2) {
                countyName = mLocationInfo.split(" ")[0];
                cityName = mLocationInfo.split(" ")[1];
                Log.d(TAG, "onCreateView: "+countyName + "," + cityName);
            }else{
                countyName = mLocationInfo;
                cityName = mLocationInfo;
            }
        }
        initView();
        return main_view;
    }

    public void initView(){

        smartRefreshLayout = main_view.findViewById(R.id.smart_refresh);
        smartRefreshLayout.setOnRefreshListener(this);         //656
        Button mJumpButton = main_view.findViewById(R.id.select_city);
        mJumpButton.setOnClickListener(this);
        TextView mLocationView = main_view.findViewById(R.id.location);
        mLocationView.setText(mLocationInfo);
        airLayout = main_view.findViewById(R.id.frameLayout);
        mSuggestion = main_view.findViewById(R.id.suggestion);
        mTempView = main_view.findViewById(R.id.temp);
        mWeatherConditionView = main_view.findViewById(R.id.weather_condition);
        mWeatherImageView = main_view.findViewById(R.id.weather_image);
        mMaxMinTemp = main_view.findViewById(R.id.max_min_temp);
        mHumView = main_view.findViewById(R.id.humidity);
        mFeelTempView = main_view.findViewById(R.id.feel_temp);
        mAirPressureView = main_view.findViewById(R.id.air_pressure);
        mWindDirectionView = main_view.findViewById(R.id.wind_direction);
        mWindDirectionTextView = main_view.findViewById(R.id.wind_direction_text);
        mUvindexView = main_view.findViewById(R.id.uv_index);
        mTempView1 = main_view.findViewById(R.id.temp_1);
        mVisibleView = main_view.findViewById(R.id.visible);
        mAirCondition1View = main_view.findViewById(R.id.air_condition_1);
        mWeather1View = main_view.findViewById(R.id.weather_image_1);
        mWeatherCondition1View = main_view.findViewById(R.id.weather_condition_1);
        mTempView2 = main_view.findViewById(R.id.temp_2);
        mWeather2View = main_view.findViewById(R.id.weather_image_2);
        mWeatherCondition2View = main_view.findViewById(R.id.weather_condition_2);
        mRecyclerView = main_view.findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView_days = main_view.findViewById(R.id.days_recycler_view);
        LinearLayoutManager manager2 = new LinearLayoutManager(getContext());
        manager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView_days.setLayoutManager(manager2);
        mPm10View = main_view.findViewById(R.id.pm10);
        mPm2_5View = main_view.findViewById(R.id.pm2_5);
        mO3View = main_view.findViewById(R.id.o3);
        mNO2View = main_view.findViewById(R.id.no2);
        mSO2View = main_view.findViewById(R.id.so2);
        mCOView = main_view.findViewById(R.id.co);
        initialTableLayout(main_view);         //初始化Table布局

        if(mLocationInfo == null){
            mLocationInfo = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("CITY_NAME", null);
        }
        if(mLocationInfo.split(" ").length == 2) {
            initialData();     //初始化数据

            // 判断如果是新的城市，那么直接请求数据
        }else{
            request_industrial_Weather(countyName);
            requestAir(cityName);
            request10days(countyName);
            requestLifeStyle(countyName);
            requestWeather(countyName);
        }
    }

    public void initialTableLayout(View view){
        mTableLayout = view.findViewById(R.id.table_layout);
        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.weight = 1;
        params.topMargin = 15 * Util.getDensity(getContext());
        params.bottomMargin = 10 * Util.getDensity(getContext());
        LinearLayout linearLayout;
        TextView textView;
        ImageView imageView;
        TableRow row;
        //------------------------------------------------------------------------------------------------------------------------
        View viewLine;
        //------------------------------------------------------------------------------------------------------------------------
        for(int i = 0;i<4;i++){
            row = new TableRow(getContext());
            for(int j=0;j<4;j++) {
                linearLayout = new LinearLayout(getContext());
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setGravity(Gravity.CENTER);

                imageView = new ImageView(getContext());
                imageView.setLayoutParams(new LinearLayout.LayoutParams(50 * Util.getSPDensity(getContext()), 50 * Util.getSPDensity(getContext())));
                imageView.setImageResource(mResId[i * 4+j]);

                textView = new TextView(getContext());
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(7 * Util.getSPDensity(getContext()));
                textView.setGravity(Gravity.CENTER);
                textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                textView.setMaxEms(2 * Util.getDensity(getContext()));
                textView.setTypeface(Typeface.DEFAULT_BOLD);
                textView.setText(mResNameId[i*4+j]);

                linearLayout.addView(imageView);
                linearLayout.addView(textView);
                row.addView(linearLayout,params);
            }
            viewLine = new View(getContext());
            viewLine.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));     //添加分割线
            viewLine.setBackgroundColor(Color.BLACK);
            viewLine.setAlpha(0.77f);
            mTableLayout.addView(viewLine);
            mTableLayout.addView(row);
        }
    }

    /**
     * 先从DefaultSharedPreference中取出数据，如果不存在就发送网络请求
     */
    public void initialData(){
        String weather_info = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(WEATHER_INFO, null);
        String weather_industrial_info = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(WEATHER_INDUSTRIAL_INFO, null);
        String air_info = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(AIR_INFO, null);
        String weather10_info = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(WEATHER10_INFO, null);
        String lifestyle = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(LIFESTYLE_INFO, null);
        if(weather_info == null) {
            requestWeather(countyName);
        }else {
            Weather weather = WeatherUtil.handleWeatherResponse(weather_info);
            showWeatherInfo(weather);
        }
        if(weather_industrial_info == null) {
            request_industrial_Weather(countyName);
        }else{
            Weather_industrial weather_industrial = WeatherUtil.handleWeatherResponse2(weather_industrial_info);
            show24Hours(weather_industrial);
        }
        if(weather_industrial_info == null) {
            requestAir(cityName);
        }else{
            Air air = WeatherUtil.handleAirResponse(air_info);
            showAirInfo(air);
        }
        if(weather10_info == null){
            request10days(countyName);
        }else{
            Weather10 weather10 = WeatherUtil.handle10DaysResponse(weather10_info);
            show10daysInfo(weather10);
        }
        if(lifestyle == null){
            requestLifeStyle(countyName);
        }else{
            LifeStyleIndex index = WeatherUtil.handleLifeStyleResponse(lifestyle);
            showLifeStyleInfo(index);
        }
    }



    //请求天气数据
    public void requestWeather(String countyName){
        Util.sendHttpOkRequest(WEATHER_URL + countyName, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getContext(),"获取天气信息失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                if(response != null){
                    final Weather weather = WeatherUtil.handleWeatherResponse(responseText);
                    if(getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (weather != null && weather.status.equals("ok")) {
                                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                                    editor.putString(WEATHER_INFO, responseText);
                                    editor.apply();
                                    showWeatherInfo(weather);

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

    //显示24小时天气情况  付费内容
    public void request_industrial_Weather(String countyName){
        Util.sendHttpOkRequest(WEATHER_URL2 + countyName, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),"获取天气信息失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                if(response != null){
                    final Weather_industrial weather = WeatherUtil.handleWeatherResponse2(responseText);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(weather != null && weather.status.equals("ok")){
                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                                editor.putString(WEATHER_INDUSTRIAL_INFO, responseText);
                                editor.apply();
                                show24Hours(weather);
                            }
                        }
                    });
                }
            }
        });
    }

    //请求空气数据
    public void requestAir(String cityName){
        Util.sendHttpOkRequest(AIR_URL+cityName, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(getActivity() != null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),"无法获取环境数据",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Air air = WeatherUtil.handleAirResponse(responseText);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(air != null && air.status.equals("ok")){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                            editor.putString(AIR_INFO, responseText);
                            editor.putString(AIR,air.air_now_city.air_quality + "   " + air.air_now_city.aqi);
                            editor.apply();
                            showAirInfo(air);
                        }
                    }
                });
            }
        });
    }

    //请求10天天气数据
    public void request10days(String countyName){
        Util.sendHttpOkRequest(WEATHER_10DAYS+countyName, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),"无法获取天气数据",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather10 weather10 = WeatherUtil.handle10DaysResponse(responseText);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather10 != null && weather10.status.equals("ok")){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                            editor.putString(WEATHER10_INFO, responseText);
                            editor.apply();
                            show10daysInfo(weather10);
                        }
                    }
                });
            }
        });
    }

    //请求生活方式数据
    public void requestLifeStyle(String countyName){
        Util.sendHttpOkRequest(LIFESTYLE_URL + countyName, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),"无法获取生活方式数据",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final LifeStyleIndex index = WeatherUtil.handleLifeStyleResponse(responseText);
                if(getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (index != null && index.status.equals("ok")) {
                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                                editor.putString(LIFESTYLE_INFO, responseText);
                                editor.apply();
                                showLifeStyleInfo(index);
                                smartRefreshLayout.finishRefresh();
                            }
                        }
                    });
                }
            }
        });
    }


    public void showWeatherInfo(Weather weather){
        addCityIntoDatabase(weather);       //将城市添加到数据库中
        mTempView.setText(weather.now.tmp);    //设置温度
        mWeatherConditionView.setText(weather.now.weather_condition);   //设置天气状况
        mMaxMinTemp.setText(weather.forecasts.get(0).tmp_min+"℃/"+weather.forecasts.get(0).tmp_max+"℃");   //设置最高最低温度
        mHumView.setText("湿度:"+weather.now.humidity+"%");   //设置湿度
        mVisibleView.setText("能见度:"+weather.now.visible+"千米");                                 //设置能见度
        mFeelTempView.setText("体感温度:"+weather.now.feel_temp+"℃");          //设置体感温度
        mAirPressureView.setText("气压:"+weather.now.pres+"hPa");        //设置大气压强
        mWindDirectionTextView.setText(weather.now.wind_direction+","+"风力:"+weather.now.wind_sc+"级"+" "+"风速:"+weather.now.wind_spd+"m/s");   //设置风向
        mUvindexView.setText("紫外线强度:"+weather.forecasts.get(0).uv_index);        //设置紫外线强度指数
        judgeWindDirection(weather.now.wind_direction);     //设置风向图标
        judgeWeatherImage(mWeatherImageView,weather.now.weather_condition,null,true);       //设置天气图标
        //明天的天气预测--------------------------------------------------------------------------------------------------------------
        mTempView1.setText(weather.forecasts.get(1).tmp_min+"℃"+"/"+weather.forecasts.get(1).tmp_max+"℃");
        String day_con1 = weather.forecasts.get(1).dayConf;
        String night_con1 = weather.forecasts.get(1).nightConf;
        if(day_con1.equals(night_con1)){
            mWeatherCondition1View.setText(day_con1);
        }else{
            mWeatherCondition1View.setText(day_con1+"转"+night_con1);
        }
        judgeWeatherImage(mWeather1View,night_con1,null,false);
        //后天的天气预测--------------------------------------------------------------------------------------------------------------
        mTempView2.setText(weather.forecasts.get(2).tmp_min+"℃"+"/"+weather.forecasts.get(2).tmp_max+"℃");
        String day_con2 = weather.forecasts.get(2).dayConf;
        String night_con2 = weather.forecasts.get(2).nightConf;
        if(day_con2.equals(night_con2)){
            mWeatherCondition2View.setText(day_con2);
        }else{
            mWeatherCondition2View.setText(day_con2+"转"+night_con2);
        }
        judgeWeatherImage(mWeather2View,night_con2,null,false);
        createNotification(weather);
    }

    public void addCityIntoDatabase(Weather weather){
        //save into database
        List<City_selected> items = LitePal.select("*").where("location = ?",weather.basic.cityName).find(City_selected.class);
        if(items.size() == 0) {
            City_selected city_selected = new City_selected();
            city_selected.setLocation(weather.basic.cityName);
            city_selected.setTemperature(weather.now.tmp);
            city_selected.setWeather(weather.now.weather_condition);
            city_selected.save();
        }else{
            City_selected city_selected = items.get(0);
            city_selected.setTemperature(weather.now.tmp);
            city_selected.setWeather(weather.now.weather_condition);
            city_selected.updateAll("location = ?",weather.basic.cityName);
        }
    }
    //判断风向
    public void judgeWindDirection(String wind_direction){
        if(wind_direction.equals("北风")){
            mWindDirectionView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wind_north));
        }else if(wind_direction.equals("东北风")){
            mWindDirectionView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wind_northeast));
        }
        else if(wind_direction.equals("东风")){
            mWindDirectionView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wind_east));
        }
        else if(wind_direction.equals("东南风")){
            mWindDirectionView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wind_southeast));
        }
        else if(wind_direction.equals("南风")){
            mWindDirectionView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wind_south));
        }
        else if(wind_direction.equals("西南风")){
            mWindDirectionView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wind_southwest));
        }
        else if(wind_direction.equals("西风")){
            mWindDirectionView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wind_east));
        }else if(wind_direction.equals("西北风")){
            mWindDirectionView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wind_northwest));
        }
    }

    public void showAirInfo(Air air){
        mAirCondition1View.setText(air.air_now_city.air_quality);
        judgeAirQuality(mAirCondition1View,air,getContext());

        Air_pic_view air_pic_view = new Air_pic_view(getContext());
        air_pic_view.setAQIvalue(air.air_now_city.aqi);
        air_pic_view.setAirCondition(air.air_now_city.air_quality);

        airLayout.addView(air_pic_view, new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        mPm10View.setText(air.air_now_city.pm10);
        mPm2_5View.setText(air.air_now_city.pm25);
        mO3View.setText(air.air_now_city.o3);
        mNO2View.setText(air.air_now_city.no2);
        mSO2View.setText(air.air_now_city.so2);
        mCOView.setText(air.air_now_city.co);
    }

    public void showLifeStyleInfo(LifeStyleIndex index){
        TextView dataView;
        LinearLayout linearLayout;
        TableRow row;
        for(int i=0;i<mTableLayout.getChildCount()/2;i++){
            row = (TableRow) mTableLayout.getChildAt(i * 2 + 1);
            for(int j=0;j<row.getChildCount();j++){
                linearLayout = (LinearLayout) row.getChildAt(j);
                dataView = new TextView(getContext());
                dataView.setTextColor(Color.WHITE);
                dataView.setTextSize(6 * Util.getSPDensity(getContext()));
                dataView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                dataView.setTypeface(Typeface.DEFAULT_BOLD);
                dataView.setText(index.lifestyles.get(i*4+j).feeling);
                linearLayout.addView(dataView,1);
                final String msg1 = mResNameId[i*4+j];
                final String msg2 = index.lifestyles.get(i*4+j).feeling;
                final String msg3 = index.lifestyles.get(i*4+j).suggesstion;
                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_view, null);
                        TextView textView1 = view.findViewById(R.id.msg1);
                        textView1.setGravity(Gravity.CENTER);
                        textView1.setText(msg1);
                        TextView textView2 = view.findViewById(R.id.msg2);
                        textView2.setGravity(Gravity.CENTER);
                        textView2.setText(msg2);
                        TextView textView3 = view.findViewById(R.id.msg3);
                        textView3.setText(msg3);
                        builder.setView(view);
                        builder.create().show();
                    }
                });
            }
        }
    }

    public void show24Hours(Weather_industrial weather){
        mSuggestion.setText("未来2 个小时下雨的概率为:"+weather.hours_24.get(1).pop+"%,你自己看着办!");
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
        mRecyclerView.setAdapter(new Hour_Adapter(hourItems, getContext(),false));
    }

    public static void judgeAirQuality(TextView textView,Air air,Context context){
        if(air.air_now_city.air_quality.equals("优")){
            textView.setBackgroundColor(Color.GREEN);
        }else if(air.air_now_city.air_quality.equals("良")){
            textView.setBackgroundColor(Color.YELLOW);
        }else if(air.air_now_city.air_quality.equals("轻度污染")){
            textView.setBackgroundColor(context.getResources().getColor(R.color.mild_pollution, null));
        }else if(air.air_now_city.air_quality.equals("中度污染")){
            textView.setBackgroundColor(context.getResources().getColor(R.color.moderate_pollution, null));
        }else if(air.air_now_city.air_quality.equals("重度污染")){
            textView.setBackgroundColor(context.getResources().getColor(R.color.severe_pollution, null));
        }else if(air.air_now_city.air_quality.equals("严重污染")){
            textView.setBackgroundColor(context.getResources().getColor(R.color.serious_pollution, null));
        }
    }

    public static void judgeWeatherImage(ImageView view,String weather_condition,String hourNum,boolean is_today_condition){
        int hourNumber = 0;
        if(hourNum != null) {
            hourNumber = Integer.parseInt(hourNum);
        }
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if(is_today_condition) {
            AnimatorUtil.clearLastAnimation(main_view);
        }
        if((hour >= 18 || hour <= 6) && (hourNumber <= 6 || hourNumber >= 18)){
            if(weather_condition.equals("多云")){
                view.setImageResource(R.drawable.cloud_moon);
                if(is_today_condition) {
                    AnimatorUtil.startCloudyAnimation(main_view, mContext, true);
                }
            }else if(weather_condition.equals("晴")){
                view.setImageResource(R.drawable.moon);
                if(is_today_condition) {
                    AnimatorUtil.startSunAnimation(main_view, mContext, true);
                }
            }else if(weather_condition.equals("阴")){
                if(is_today_condition) {
                    main_view.setBackgroundResource(R.drawable.bg_haze);
                }
                view.setImageResource(R.drawable.clouds);
            }
        }else{
            if(weather_condition.equals("多云")){
                view.setImageResource(R.drawable.cloudtosun);
                if(is_today_condition) {
                    AnimatorUtil.startCloudyAnimation(main_view, mContext, false);
                }
            }else if(weather_condition.equals("晴")){
                view.setImageResource(R.drawable.sun);
                if(is_today_condition) {
                    AnimatorUtil.startSunAnimation(main_view, mContext, false);
                }
            }else if(weather_condition.equals("阴")){
                if(is_today_condition) {
                    main_view.setBackgroundResource(R.drawable.bg_haze);
                }
                view.setImageResource(R.drawable.clouds);
            }
        }
        if(weather_condition.equals("大雨")){
            if(is_today_condition) {
                AnimatorUtil.startlRainAnimation(main_view, mContext);
            }
            view.setImageResource(R.drawable.rain_severe);
        }else if(weather_condition.equals("中雨")){
            if(is_today_condition) {
                AnimatorUtil.startmRainAnimation(main_view, mContext);
            }
            view.setImageResource(R.drawable.rain_moderate);
        }else if(weather_condition.equals("小雨")){
            if(is_today_condition) {
                AnimatorUtil.startsRainAnimation(main_view, mContext);
            }
            view.setImageResource(R.drawable.rain_mlid);
        }else if(weather_condition.equals("阵雪")){
            if(is_today_condition) {
                AnimatorUtil.startSnowAnimation(main_view, mContext);
            }
            view.setImageResource(R.drawable.zhenxue);
        }else if(weather_condition.equals("雨夹雪")){
            if(is_today_condition) {
                AnimatorUtil.startSnowAndRainAnimation(main_view, mContext);
            }
            view.setImageResource(R.drawable.rain_snow);
        }else if(weather_condition.equals("阵雨")){
            if(is_today_condition) {
                AnimatorUtil.startmRainAnimation(main_view, mContext);
            }
            view.setImageResource(R.drawable.zhenyu);
        }else if(weather_condition.equals("中到大雨")){
            if(is_today_condition) {
                AnimatorUtil.startmRainAnimation(main_view, mContext);
            }
            view.setImageResource(R.drawable.rain_serious);
        }else if(weather_condition.equals("小到中雨")){
            if(is_today_condition) {
                AnimatorUtil.startmRainAnimation(main_view, mContext);
            }
            view.setImageResource(R.drawable.rain_moderate);
        }else if(weather_condition.equals("雷阵雨")){
            if(is_today_condition) {
                AnimatorUtil.startRainLightAnimation(main_view, mContext);
            }
            view.setImageResource(R.drawable.rain_light);
        }
    }

    public void show10daysInfo(Weather10 weather10){
        List<Forecast> forecasts = weather10.forecasts;
        List<Day_item> day_items = new ArrayList<>();
        Day_item item;
        Calendar calendar = Calendar.getInstance();
        int weekValue = calendar.get(Calendar.DAY_OF_WEEK);
        String week;
        for(int i =0;i<forecasts.size();i++){
            Forecast forecast = forecasts.get(i);
            week = judgeWeek((weekValue+i) % 7);           //判断星期
            item = new Day_item();
            item.setWeek(week);
            item.setDate(forecast.date.split("-")[1]+"/"+forecast.date.split("-")[2]);
            item.setConditionDay(forecast.dayConf);
            item.setMaxTemp(forecast.tmp_max);
            item.setMinTemp(forecast.tmp_min);
            item.setConditionNight(forecast.nightConf);
            item.setWind(forecast.wind_direction);
            item.setWind_speed(forecast.wnid_speed);
            day_items.add(item);
        }
        day_items.get(0).setWeek("今天");
        Day_Adapter day_adapter = new Day_Adapter(day_items, getContext());
        mRecyclerView_days.setAdapter(day_adapter);
    }

    public static String judgeWeek(int weekValue){
        switch (weekValue){
            case 1:
                return "星期日";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 0:
                return "星期六";
        }
        return null;
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getMyLocation();
    }

    public void getMyLocation(){
        final AMapLocationClient client = new AMapLocationClient(getContext());
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setOnceLocation(true);
        client.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(final AMapLocation aMapLocation) {
                if(aMapLocation != null){
                    String locationInfo = aMapLocation.getDistrict()+" "+aMapLocation.getCity();
                    countyName = locationInfo.split(" ")[0];
                    cityName = locationInfo.split(" ")[1];
                    requestWeather(countyName);
                    request_industrial_Weather(countyName);
                    requestAir(cityName);
                    request10days(countyName);
                    requestLifeStyle(countyName);
                }
            }
        });
        client.setLocationOption(option);
        client.startLocation();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.select_city:
                Intent intent = new Intent(getActivity(),ChooseCityActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void createNotification(Weather weather){
        if(getContext() != null){
            if(mManager == null) {
                mManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            }
            mManager.cancelAll();
            NotificationChannel channel = new NotificationChannel("233","weather", NotificationManager.IMPORTANCE_HIGH);
            mManager.createNotificationChannel(channel);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(),channel.getId());
            builder.setOngoing(true);
            builder.setSmallIcon(R.drawable.sun);
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
            RemoteViews remoteViews = new RemoteViews(getContext().getPackageName(),R.layout.notification_info_layout);
            remoteViews.setTextViewText(R.id.weather_condition,weather.now.weather_condition);
            if(judgeWeatherImage(weather.now.weather_condition) == 0){
                remoteViews.setImageViewResource(R.id.weather_image,R.drawable.sun);
            }else{
                remoteViews.setImageViewResource(R.id.weather_image,judgeWeatherImage(weather.now.weather_condition));
            }
            remoteViews.setTextViewText(R.id.max_min_temperature,weather.forecasts.get(0).tmp_min+"°/"+weather.forecasts.get(0).tmp_max+"°");
            remoteViews.setTextViewText(R.id.position,accurateInfo != null ? accurateInfo : "福州市福建农林大学");
            remoteViews.setTextViewText(R.id.date_and_time,new SimpleDateFormat("yyyy/MM/dd hh: mm: ss",new Locale("en")).format(new Date()));
            remoteViews.setTextViewText(R.id.temperature,weather.now.tmp+"°");
            String air = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(AIR,null);
            remoteViews.setTextViewText(R.id.air_condition, air != null ? air : " ");
            builder.setCustomContentView(remoteViews);
            Intent intent = new Intent(getContext(), MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getContext(),0,intent,0);
            builder.setContentIntent(pendingIntent);
            Notification notification = builder.build();
            mManager.notify(1,notification);
        }
    }

    public static int judgeWeatherImage(String weather_condition){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if((hour >= 18 || hour <= 6)) {
            switch (weather_condition) {
                case "多云":
                    return R.drawable.cloud_moon;
                case "晴":
                    return R.drawable.moon;
            }
        }else{
            switch (weather_condition) {
                case "多云":
                    return R.drawable.cloudtosun;
                case "晴":
                    return R.drawable.sun;
            }
        }

        switch (weather_condition){
            case "阴":
                return R.drawable.clouds;
            case "大雨":
                return R.drawable.rain_severe;
            case "中雨":
                return R.drawable.rain_moderate;
            case "小雨":
                return R.drawable.rain_mlid;
            case "阵雪":
                return R.drawable.zhenxue;
            case "雨夹雪":
                return R.drawable.rain_snow;
            case "阵雨":
                return R.drawable.zhenyu;
            case "中到大雨":
                return R.drawable.rain_serious;
            case "小到中雨":
                return R.drawable.rain_moderate;
            case "雷阵雨":
                return R.drawable.rain_light;
        }
        return 0;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(LocationInfo,mLocationInfo);
        outState.putString(AccurateInfo,accurateInfo);
    }
}

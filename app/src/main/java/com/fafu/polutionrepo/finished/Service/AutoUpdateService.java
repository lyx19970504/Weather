package com.fafu.polutionrepo.finished.Service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.fafu.polutionrepo.finished.Activities.MainActivity;
import com.fafu.polutionrepo.finished.Beans.Air;
import com.fafu.polutionrepo.finished.Beans.LifeStyleIndex;
import com.fafu.polutionrepo.finished.Beans.Weather;
import com.fafu.polutionrepo.finished.Beans.Weather10;
import com.fafu.polutionrepo.finished.Beans.Weather_industrial;
import com.fafu.polutionrepo.finished.Fragments.WeatherFragment;
import com.fafu.polutionrepo.finished.R;
import com.fafu.polutionrepo.finished.Util.Util;
import com.fafu.polutionrepo.finished.Util.WeatherUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {

    private static final String TAG = "AutoUpdateService";
    private NotificationManager mManager;
    private static String positionInfo;
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();   //更新天气数据 下同
        updateAir();
        updateIndustrialWeather();   //更新付费类天气数据
        update10DaysWeather();
        updateLifeStyle();
        updateNotification();
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int hour = 1000 * 60 * 60;
        long triggerAtTime = SystemClock.elapsedRealtime() + hour;
        Intent intentTrigger = new Intent(this,AutoUpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentTrigger, 0);
        alarmManager.cancel(pendingIntent);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    public void updateWeather(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weather_info = preferences.getString(WeatherFragment.WEATHER_INFO, null);
        if(weather_info != null){
            Weather weather = WeatherUtil.handleWeatherResponse(weather_info);
            if(weather != null) {
                String weatherId = weather.basic.id;
                String weatherUrl = WeatherFragment.WEATHER_URL + weatherId;
                Util.sendHttpOkRequest(weatherUrl, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: " + "获取天气信息失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseText = response.body().string();
                        Weather weather = WeatherUtil.handleWeatherResponse(responseText);
                        if (weather != null && weather.status.equals("ok")) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                            editor.putString("CITY_NAME", weather.basic.cityName);
                            editor.putString(WeatherFragment.WEATHER_INFO, responseText);
                            editor.apply();
                        }
                    }
                });
            }
        }
    }

    public void updateAir(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String air_info = preferences.getString(WeatherFragment.AIR_INFO, null);
        if(air_info != null){
            Air air = WeatherUtil.handleAirResponse(air_info);
            if(air != null) {
                String airId = air.basic.id;
                String airUrl = WeatherFragment.AIR_URL + airId;
                Util.sendHttpOkRequest(airUrl, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: " + "获取空气信息失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseText = response.body().string();
                        Air air = WeatherUtil.handleAirResponse(responseText);
                        if (air != null && air.status.equals("ok")) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                            editor.putString(WeatherFragment.AIR_INFO, responseText);
                            editor.putString(WeatherFragment.AIR, air.air_now_city.air_quality + "   " + air.air_now_city.aqi);
                            editor.apply();
                        }
                    }
                });
            }
        }
    }

    public void updateIndustrialWeather(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weather_info = preferences.getString(WeatherFragment.WEATHER_INDUSTRIAL_INFO, null);
        if(weather_info != null){
            Weather_industrial weatherIndustrial = WeatherUtil.handleWeatherResponse2(weather_info);
            if(weatherIndustrial != null) {
                String weatherId = weatherIndustrial.basic.id;
                String weatherUrl = WeatherFragment.WEATHER_URL2 + weatherId;
                Util.sendHttpOkRequest(weatherUrl, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: " + "获取付费天气信息失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseText = response.body().string();
                        Weather_industrial weatherIndustrial = WeatherUtil.handleWeatherResponse2(responseText);
                        if (weatherIndustrial != null && weatherIndustrial.status.equals("ok")) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                            editor.putString(WeatherFragment.WEATHER_INDUSTRIAL_INFO, responseText);
                            editor.apply();
                        }
                    }
                });
            }
        }
    }

    public void update10DaysWeather(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weather10days_info = preferences.getString(WeatherFragment.WEATHER10_INFO, null);
        if(weather10days_info != null){
            Weather10 weather10 = WeatherUtil.handle10DaysResponse(weather10days_info);
            if(weather10 != null) {
                String weatherId = weather10.basic.id;
                String weatherUrl = WeatherFragment.WEATHER_10DAYS + weatherId;
                Util.sendHttpOkRequest(weatherUrl, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: " + "获取付费天气信息失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseText = response.body().string();
                        Weather10 weather10 = WeatherUtil.handle10DaysResponse(responseText);
                        if (weather10 != null && weather10.status.equals("ok")) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                            editor.putString(WeatherFragment.WEATHER10_INFO, responseText);
                            editor.apply();
                        }
                    }
                });
            }
        }
    }

    public void updateLifeStyle(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lifestyle_info = preferences.getString(WeatherFragment.LIFESTYLE_INFO, null);
        if(lifestyle_info != null){
            LifeStyleIndex lifeStyle = WeatherUtil.handleLifeStyleResponse(lifestyle_info);
            if(lifeStyle != null) {
                String cityId = lifeStyle.basic.id;
                String lifestyleUrl = WeatherFragment.LIFESTYLE_URL + cityId;
                Util.sendHttpOkRequest(lifestyleUrl, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: " + "获取付费天气信息失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseText = response.body().string();
                        LifeStyleIndex lifeStyle = WeatherUtil.handleLifeStyleResponse(responseText);
                        if (lifeStyle != null && lifeStyle.status.equals("ok")) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                            editor.putString(WeatherFragment.LIFESTYLE_INFO, responseText);
                            editor.apply();
                        }
                    }
                });
            }
        }
    }

    public void updateNotification(){
        getMyLocation();
        String weatherText = PreferenceManager.getDefaultSharedPreferences(this).getString(WeatherFragment.WEATHER_INFO,null);
        if(weatherText != null) {
            Weather weather = WeatherUtil.handleWeatherResponse(weatherText);
            if (mManager == null) {
                mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            }
            mManager.cancelAll();
            NotificationChannel channel = new NotificationChannel("233", "weather", NotificationManager.IMPORTANCE_HIGH);
            mManager.createNotificationChannel(channel);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channel.getId());
            builder.setOngoing(true);
            builder.setSmallIcon(R.drawable.sun);
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
            RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.notification_info_layout);
            remoteViews.setTextViewText(R.id.weather_condition, weather.now.weather_condition);
            if (WeatherFragment.judgeWeatherImage(weather.now.weather_condition) == 0) {
                remoteViews.setImageViewResource(R.id.weather_image, R.drawable.sun);
            } else {
                remoteViews.setImageViewResource(R.id.weather_image, WeatherFragment.judgeWeatherImage(weather.now.weather_condition));
            }
            remoteViews.setTextViewText(R.id.max_min_temperature, weather.forecasts.get(0).tmp_min + "°/" + weather.forecasts.get(0).tmp_max + "°");
            remoteViews.setTextViewText(R.id.position, positionInfo != null ? positionInfo : "福州市福建农林大学");
            remoteViews.setTextViewText(R.id.date_and_time, new SimpleDateFormat("yyyy/MM/dd HH: mm: ss", new Locale("en")).format(new Date()));
            remoteViews.setTextViewText(R.id.temperature, weather.now.tmp + "°");
            String air = PreferenceManager.getDefaultSharedPreferences(this).getString(WeatherFragment.AIR, null);
            remoteViews.setTextViewText(R.id.air_condition, air != null ? air : " ");
            builder.setCustomContentView(remoteViews);
            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
            builder.setContentIntent(pendingIntent);
            Notification notification = builder.build();
            mManager.notify(1, notification);
        }
    }

    public void getMyLocation(){
        final AMapLocationClient client = new AMapLocationClient(this);
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setOnceLocation(true);
        client.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(final AMapLocation aMapLocation) {
                if(aMapLocation != null){
                    String locationInfo = aMapLocation.getDistrict()+" "+aMapLocation.getCity();
                    positionInfo = locationInfo + aMapLocation.getStreet();
                }
            }
        });
        client.setLocationOption(option);
        client.startLocation();
    }
}

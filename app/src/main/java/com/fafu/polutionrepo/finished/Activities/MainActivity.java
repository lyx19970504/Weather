package com.fafu.polutionrepo.finished.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import com.fafu.polutionrepo.finished.Application.MyApplication;
import com.fafu.polutionrepo.finished.Beans.User;
import com.fafu.polutionrepo.finished.Fragments.*;
import com.fafu.polutionrepo.finished.R;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    FragmentManager fragmentManager;
    private BottomNavigationView mBottomNavigationView;
    private static final int PERMISSION_REQUEST = 0;
    private MapFragment mMapFragment;
    private MyInfoFragment myInfoFragment;
    private TopicFragment topicFragment;


    private static String locationInfo;

    public static final String CITY_NAME = "city_name";

    private long mExitTime;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        initView();    //初始化布局
        RequestPermissions();  //请求权限
    }

    public void initView(){
        fragmentManager = getSupportFragmentManager();
        mBottomNavigationView = findViewById(R.id.bottom_nav);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
        topicFragment = new TopicFragment();

        mMapFragment = new MapFragment();
        myInfoFragment = new MyInfoFragment();

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.weather:
                if(fragmentManager.getFragments().get(0) != null && fragmentManager.getFragments().get(0) instanceof WeatherFragment){
                    return true;
                }
                fragmentManager.beginTransaction().replace(R.id.fragment_container, WeatherFragment.getWeatherFragment(locationInfo)).commit();
                break;
            case R.id.map:
                fragmentManager.beginTransaction().replace(R.id.fragment_container, mMapFragment).commit();
                break;
            case R.id.myInfo:
                fragmentManager.beginTransaction().replace(R.id.fragment_container, myInfoFragment).commit();
                break;
            case R.id.topic:
                fragmentManager.beginTransaction().replace(R.id.fragment_container, topicFragment).commit();
                break;
            default:
                break;
        }
        return false;
    }

    //request the danger permissions
    private void RequestPermissions(){
        if(Build.VERSION.SDK_INT >= 24){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_PHONE_STATE},PERMISSION_REQUEST);
            }else{
                String cityName = getIntent().getStringExtra(CITY_NAME);
                if(cityName != null) {
                    if(!cityName.equals("当前位置")) {
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, WeatherFragment.getWeatherFragment(cityName)).commit();
                    }else{
                        getMyLocation();
                    }
                }else{
                    getMyLocation();
                }
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_REQUEST:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getMyLocation();   //定位
                    return;
                }else{
                    finish();
                }
                break;
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
                    locationInfo = aMapLocation.getDistrict()+" "+aMapLocation.getCity();
                    Log.d(TAG, "onLocationChanged: "+locationInfo);
                    fragmentManager.beginTransaction().add(R.id.fragment_container,WeatherFragment.getWeatherFragment(locationInfo,locationInfo + aMapLocation.getStreet())).commit();
                }
            }
        });
        client.setLocationOption(option);
        client.startLocation();
    }

    public static Intent newIntent(Context context,String cityName){
        Intent intent = new Intent(context,MainActivity.class);
        intent.putExtra(CITY_NAME, cityName);
        return intent;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if((System.currentTimeMillis() - mExitTime) > 2000){
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            }else{
                System.exit(0);
            }
        }
        return true;
    }
}

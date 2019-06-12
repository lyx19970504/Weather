package com.fafu.polutionrepo.finished.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.fafu.polutionrepo.finished.Activities.PollutionActivity;
import com.fafu.polutionrepo.finished.Adapter.CustomInfoWindowAdapter;
import com.fafu.polutionrepo.finished.Application.MyApplication;
import com.fafu.polutionrepo.finished.Beans.Pollution;
import com.fafu.polutionrepo.finished.Beans.User;
import com.fafu.polutionrepo.finished.R;
import com.fafu.polutionrepo.finished.Util.Util;
import com.fafu.polutionrepo.finished.Util.WeatherUtil;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MapFragmentPollution extends Fragment implements AMap.OnMarkerClickListener, AMap.OnMapClickListener {

    private static final String TAG = "MapFragmentPollution";
    public static final String GET_POLLUTION_URL = Util.URL_PREFIX + "/green/getPollutions";
    private AMap aMap;
    MapView mapView;
    @InjectView(R.id.map_layout)
    FrameLayout frameLayout;
    private static boolean isAddMarker = true;
    private ImageView addMarkerImageView;
    private TextView addMarkerTextView;
    public static final int PUBLISH_REQUEST = 0;
    private Marker currentMarker;
    public User user;
    private boolean infoWindowShown = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_map_river, container,false);
        Injector.injectInto(this,view);

        mapView = new MapView(getContext(), mapOptions());
        mapView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mapView.onCreate(savedInstanceState);
        frameLayout.addView(mapView);

        if(aMap == null) {
            aMap = mapView.getMap();
        }

        initViews();
        return view;
    }



    public AMapOptions mapOptions(){
        LatLng centerFuzhouPoint = new LatLng(26.15000,119.28333);
        AMapOptions mapOptions = new AMapOptions();
        mapOptions.camera(new CameraPosition(centerFuzhouPoint,5f,0,0));
        return mapOptions;
    }

    public void initViews(){
        if(getActivity() != null){
            user = MyApplication.getUser();
        }
        getMyLocation();
        setUiWidgets();
        addMarkerIcon();
        aMap.setOnMapClickListener(this);
        aMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(getContext()));
        aMap.setOnMarkerDragListener(new AMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                marker.destroy();
            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
            }
        });
        aMap.setOnMarkerClickListener(this);

        Util.getPollutionRequest(GET_POLLUTION_URL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: "+"获取污染数据失败!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.body() != null){
                    final String responseText = response.body().string();
                    if(getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (responseText != null) {
                                    List<Pollution> pollutionList = WeatherUtil.handlePublishResponse(responseText);
                                    if(pollutionList.size() > 0){
                                        initMarkers(pollutionList);
                                    }
                                }
                                Toast.makeText(getContext(), "获取污染源数据成功!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
    }

    public void initMarkers(List<Pollution> pollutionList){
        for(Pollution pollution : pollutionList){
            createInitMarker(pollution);
        }
    }

    public void addMarkerIcon(){
        int density = Util.getDensity(getContext());
        View pollutionView = LayoutInflater.from(getContext()).inflate(R.layout.map_button_layout,null,false);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.END;
        params.rightMargin = 10 * density;
        params.topMargin = 100 * density;
        pollutionView.setLayoutParams(params);
        addMarkerImageView = pollutionView.findViewById(R.id.image_info);
        addMarkerImageView.setImageResource(R.drawable.add_marker_dark);
        addMarkerTextView = pollutionView.findViewById(R.id.text_info);
        addMarkerTextView.setText("添加污染");
        addMarkerTextView.setTextColor(Color.BLACK);

        pollutionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user == null){
                    Toast.makeText(getContext(), "您还未登录，请登录后再试!", Toast.LENGTH_SHORT).show();
                    return;
                }
                addMarkerImageView.setImageResource(isAddMarker ? R.drawable.add_marker : R.drawable.add_marker_dark);
                addMarkerTextView.setTextColor(isAddMarker ? Color.BLUE : Color.BLACK);
                isAddMarker = !isAddMarker;
            }
        });
        frameLayout.addView(pollutionView);
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

    public void createInitMarker(Pollution pollution){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.infoWindowEnable(false);
        markerOptions.title("");
        markerOptions.snippet("");
        markerOptions.position(new LatLng(pollution.getLatitude(),pollution.getLongitude()));
        int resourceId = Util.judgePollutionIcon(pollution.getPollutionType()+":"+pollution.getPollutionLevel());
        markerOptions.icon(BitmapDescriptorFactory.fromResource(resourceId));
        Marker marker = aMap.addMarker(markerOptions);
        marker.setObject(pollution);
        marker.setDraggable(false);
        marker.setInfoWindowEnable(true);
        addAnimation(marker);
    }

    public void createRiverMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.infoWindowEnable(false);
        markerOptions.title("");
        markerOptions.snippet("");
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.unknown_level));
        Marker marker = aMap.addMarker(markerOptions);
        marker.setObject(null);
        marker.setDraggable(true);
        marker.setInfoWindowEnable(true);
        addAnimation(marker);
    }

    private static void addAnimation(Marker marker){
        //添加动画
        Animation markerAnimation = new ScaleAnimation(0, 1, 0, 1); //初始化生长效果动画
        markerAnimation.setDuration(1000);  //设置动画时间 单位毫秒
        marker.setAnimation(markerAnimation);
        marker.startAnimation();
    }



    @Override
    public boolean onMarkerClick(Marker marker) {
        infoWindowShown = false;
        currentMarker = marker;
        if(marker.getObject() == null){
            Intent intent = PollutionActivity.newIntent(getContext(),marker.getPosition().latitude,marker.getPosition().longitude);
            startActivityForResult(intent,PUBLISH_REQUEST);
        }else{
            marker.showInfoWindow();
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == PUBLISH_REQUEST){
                if(data != null){
                    Pollution pollution = new Pollution();
                    pollution.setCityName(data.getStringExtra(PollutionActivity.CITY));
                    pollution.setDate(data.getStringExtra(PollutionActivity.DATE));
                    pollution.setPollutionType(data.getStringExtra(PollutionActivity.POLLUTION_TYPE));
                    pollution.setPollutionLevel(data.getStringExtra(PollutionActivity.POLLUTION_LEVEL));
                    pollution.setLatitude(data.getDoubleExtra(PollutionActivity.LATITUDE,-1));
                    pollution.setLongitude(data.getDoubleExtra(PollutionActivity.LONGITUDE,-1));
                    currentMarker.setInfoWindowEnable(true);
                    currentMarker.setObject(pollution);
                    setIcon(pollution.getPollutionType(), pollution.getPollutionLevel(), currentMarker.getOptions());
                }
            }
        }
    }

    public void setIcon(String type,String level,MarkerOptions markerOptions){
        int resourceId = Util.judgePollutionIcon(type+":"+level);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(resourceId));
    }


    @Override
    public void onMapClick(LatLng latLng) {
        if(!isAddMarker) {
            createRiverMarker(latLng);
            addMarkerImageView.setImageResource(R.drawable.add_marker_dark);
            addMarkerTextView.setTextColor(Color.BLACK);
            isAddMarker = !isAddMarker;
        }

        if(currentMarker.isInfoWindowShown() && !infoWindowShown){
            infoWindowShown = true;
            return;
        }

        if(currentMarker.isInfoWindowShown() && infoWindowShown){
            currentMarker.hideInfoWindow();
        }
    }
}

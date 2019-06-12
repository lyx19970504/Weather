package com.fafu.polutionrepo.finished.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Marker;
import com.fafu.polutionrepo.finished.Activities.PollutionDetailedActivity;
import com.fafu.polutionrepo.finished.Areas.County;
import com.fafu.polutionrepo.finished.Beans.Pollution;
import com.fafu.polutionrepo.finished.Beans.User;
import com.fafu.polutionrepo.finished.R;
import com.fafu.polutionrepo.finished.Util.Util;

import org.litepal.LitePal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class CustomInfoWindowAdapter implements AMap.InfoWindowAdapter, View.OnClickListener {

    private static final String TAG = "CustomInfoWindowAdapter";
    private Context mContext;
    private Pollution mPollution;


    public CustomInfoWindowAdapter(Context context){
        mContext = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        mPollution = (Pollution) marker.getObject();
        View view = LayoutInflater.from(mContext).inflate(R.layout.river_infowindow, null);
        TextView cityNameView = view.findViewById(R.id.city_name);
        if(TextUtils.isEmpty(mPollution.getCityName())) {
            String cityName = Util.searchCity(mContext,mPollution.getLatitude(), mPollution.getLongitude(), cityNameView);
            mPollution.setCityName(cityName != null ? cityName : "");
        }else{
            cityNameView.setText(mPollution.getCityName());
        }
        TextView pollutionDate = view.findViewById(R.id.pollution_date);
        pollutionDate.setText(mPollution.getDate());
        TextView pollutionType = view.findViewById(R.id.pollution_type);
        Object[] objects;
        objects = Util.judgePollution(mPollution.getPollutionType());
        pollutionType.setText(pollutionType.getText().toString() + objects[0]);
        TextView pollutionLevel = view.findViewById(R.id.pollution_level);
        objects = Util.judgePollution(mPollution.getPollutionLevel());
        pollutionLevel.setText(pollutionLevel.getText().toString() + objects[0]);
        Button detail = view.findViewById(R.id.detail_button);
        detail.setOnClickListener(this);
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.detail_button:
                intent = PollutionDetailedActivity.newIntent(mContext,mPollution);
                mContext.startActivity(intent);
                break;
        }
    }



}

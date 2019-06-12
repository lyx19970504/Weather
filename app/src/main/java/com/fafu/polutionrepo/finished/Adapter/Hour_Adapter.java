package com.fafu.polutionrepo.finished.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fafu.polutionrepo.finished.Beans.Hour_item;
import com.fafu.polutionrepo.finished.Fragments.WeatherFragment;
import com.fafu.polutionrepo.finished.R;
import com.fafu.polutionrepo.finished.Util.Util;
import com.fafu.polutionrepo.finished.Util.WeatherUtil;
import com.fafu.polutionrepo.finished.View.TemperatureView;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Hour_Adapter extends RecyclerView.Adapter<Hour_Adapter.Hour_Holder> {

    private static final String TAG = "Hour_Adapter";
    private List<Hour_item> itemList;
    private Context mContext;
    private List<Integer> datas = new ArrayList<>();
    private int minValue;
    private int maxValue;
    private boolean isSimple;

    public Hour_Adapter(List<Hour_item> itemList,Context context,boolean isSimple){
        this.itemList = itemList;
        setData(itemList);
        mContext = context;
        this.isSimple = isSimple;
    }

    @NonNull
    @Override
    public Hour_Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        if(isSimple){
            view = LayoutInflater.from(mContext).inflate(R.layout.hour_item_simple, viewGroup,false);
        }else{
            view = LayoutInflater.from(mContext).inflate(R.layout.hour_item, viewGroup,false);
        }
        Hour_Holder hour_holder = new Hour_Holder(view);
        return hour_holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Hour_Holder hour_holder, int i) {
        Hour_item item = itemList.get(i);
        hour_holder.mTimeView.setText(item.getTime().split(" ")[1].split(":")[0]+"时");
        WeatherFragment.judgeWeatherImage(hour_holder.mWeather_image, item.getWeather_image(),item.getTime().split(" ")[1].split(":")[0],false);
        if(i==0){
            hour_holder.mTemperatureView.setDrawLeftLine(false);
        }else{
            hour_holder.mTemperatureView.setDrawLeftLine(true);
            hour_holder.mTemperatureView.setLastValue(datas.get(i-1));
        }

        if(i == datas.size()-1){
            hour_holder.mTemperatureView.setDrawRightLine(false);
        }else{
            hour_holder.mTemperatureView.setDrawRightLine(true);
            hour_holder.mTemperatureView.setNextValue(datas.get(i+1));
        }
        hour_holder.mTemperatureView.setCurrentValue(datas.get(i));

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class Hour_Holder extends RecyclerView.ViewHolder{

        private TextView mTimeView;
        private ImageView mWeather_image;
        private TemperatureView mTemperatureView;

        public Hour_Holder(@NonNull View itemView) {
            super(itemView);
            mTimeView = itemView.findViewById(R.id.time_item);
            mWeather_image = itemView.findViewById(R.id.weather_item);
            mTemperatureView = itemView.findViewById(R.id.temp_view);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100*Util.getDensity(mContext), ViewGroup.LayoutParams.WRAP_CONTENT);
            mTemperatureView.setLayoutParams(params);
            mTemperatureView.setMinValue(minValue);
            mTemperatureView.setMaxValue(maxValue);
        }
    }

    public void setData(List<Hour_item> itemList){
        List<Integer> dataList = new ArrayList<>();

        for(Hour_item item : itemList){
            dataList.add(Integer.parseInt(item.getTemperature()));
        }

        datas.addAll(dataList);
        Collections.sort(dataList);
        minValue = dataList.get(0);
        maxValue = dataList.get(dataList.size()-1);
    }


}
package com.fafu.polutionrepo.finished.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.fafu.polutionrepo.finished.Beans.Day_item;
import com.fafu.polutionrepo.finished.Fragments.WeatherFragment;
import com.fafu.polutionrepo.finished.R;
import com.fafu.polutionrepo.finished.Util.Util;
import com.fafu.polutionrepo.finished.View.TemperatureMaxMinView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Day_Adapter extends RecyclerView.Adapter<Day_Adapter.Day_Holder> {
    private static final String TAG = "Day_Adapter";

    private List<Day_item> day_items;
    private Context mContext;
    private List<Integer> dataMax = new ArrayList<>();
    private List<Integer> dataMin = new ArrayList<>();
    private int MaxMaxValue;
    private int MaxMinValue;
    private int MinMaxValue;
    private int MinMinValue;

    public Day_Adapter(List<Day_item> day_items, Context context){
        this.day_items = day_items;
        setData(day_items);
        mContext = context;
    }

    @NonNull
    @Override
    public Day_Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.day_item, viewGroup,false);
        Day_Holder day_holder = new Day_Holder(view);
        return day_holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Day_Holder day_holder, int i) {
        Day_item day_item = day_items.get(i);

        day_holder.mWeekView.setText(day_item.getWeek());
        day_holder.mDateView.setText(day_item.getDate());
        day_holder.mConditionDayView.setText(day_item.getConditionDay());
        WeatherFragment.judgeWeatherImage(day_holder.mConditionImageDayView, day_item.getConditionDay(),null,false);   //判断白天天气情况并设置图像
        WeatherFragment.judgeWeatherImage(day_holder.mConditionImageNightView, day_item.getConditionNight(),null,false);   //判断晚上天气情况并设置图像
        day_holder.mConditionNightView.setText(day_item.getConditionNight());
        day_holder.mWindDirectionView.setText(day_item.getWind());
        day_holder.mWindSpeedView.setText(day_item.getWind_speed()+"级");

        //---------------------------------设置最高最低温度图-------------------------------------
        if(i==0){
            day_holder.mMaxMinTempView.setDrawLeftLine(false);
        }else{
            day_holder.mMaxMinTempView.setDrawLeftLine(true);
            day_holder.mMaxMinTempView.setLastMaxValue(dataMax.get(i-1));
            day_holder.mMaxMinTempView.setLastMinValue(dataMin.get(i-1));
        }

        if(i == dataMax.size()-1){
            day_holder.mMaxMinTempView.setDrawRightLine(false);
        }else{
            day_holder.mMaxMinTempView.setDrawRightLine(true);
            day_holder.mMaxMinTempView.setNextMaxValue(dataMax.get(i+1));
            day_holder.mMaxMinTempView.setNextMinValue(dataMin.get(i+1));
        }
        day_holder.mMaxMinTempView.setCurrentMaxValue(dataMax.get(i));
        day_holder.mMaxMinTempView.setCurrentMinValue(dataMin.get(i));
    }

    @Override
    public int getItemCount() {
        return day_items.size();
    }



    class Day_Holder extends RecyclerView.ViewHolder{

        private TextView mWeekView;
        private TextView mDateView;
        private TextView mConditionDayView;
        private ImageView mConditionImageDayView;
        private ImageView mConditionImageNightView;
        private TextView mConditionNightView;
        private TextView mWindDirectionView;
        private TextView mWindSpeedView;
        private TemperatureMaxMinView mMaxMinTempView;
        private View mainView;

        public Day_Holder(@NonNull final View itemView) {
            super(itemView);
            mainView = itemView;
            mWeekView = itemView.findViewById(R.id.week_view);
            mDateView = itemView.findViewById(R.id.date_view);
            mConditionDayView = itemView.findViewById(R.id.condition_day);
            mConditionImageDayView = itemView.findViewById(R.id.condition_image_day);
            mConditionImageNightView = itemView.findViewById(R.id.condition_night_image);
            mConditionNightView = (TextView) itemView.findViewById(R.id.condition_night);
            mWindDirectionView = (TextView) itemView.findViewById(R.id.wind_direction_item);
            mWindSpeedView = (TextView) itemView.findViewById(R.id.wind_speed);
            mMaxMinTempView = (TemperatureMaxMinView) itemView.findViewById(R.id.temp_max_min);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100*Util.getDensity(mContext), ViewGroup.LayoutParams.WRAP_CONTENT);
            mMaxMinTempView.setLayoutParams(params);
            mMaxMinTempView.setMaxMaxValue(MaxMaxValue);
            mMaxMinTempView.setMaxMinValue(MaxMinValue);
            mMaxMinTempView.setMinMaxValue(MinMaxValue);
            mMaxMinTempView.setMinMinValue(MinMinValue);
        }
    }

    public void setData(List<Day_item> items){
        List<Integer> dataMax = new ArrayList<>();
        List<Integer> dataMin = new ArrayList<>();
        for(Day_item item : items){
            dataMax.add(Integer.parseInt(item.getMaxTemp()));
            dataMin.add(Integer.parseInt(item.getMinTemp()));
        }
        this.dataMax.addAll(dataMax);
        this.dataMin.addAll(dataMin);
        Collections.sort(dataMax);
        Collections.sort(dataMin);
        MaxMaxValue = dataMax.get(dataMax.size()-1);
        MaxMinValue = dataMax.get(0);
        MinMaxValue = dataMin.get(dataMin.size()-1);
        MinMinValue = dataMin.get(0);
    }

}

package com.fafu.polutionrepo.finished.Adapter;

import android.icu.util.Calendar;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fafu.polutionrepo.finished.Beans.Forecast;
import com.fafu.polutionrepo.finished.Fragments.WeatherFragment;
import com.fafu.polutionrepo.finished.R;

import java.util.List;

public class InfoWindowDayAdapter extends RecyclerView.Adapter<InfoWindowDayAdapter.InfoWindowDayHolder> {

    private List<Forecast> forecastList;

    public InfoWindowDayAdapter(List<Forecast> forecastList){
        this.forecastList = forecastList;
        this.forecastList.remove(0);
    }

    @NonNull
    @Override
    public InfoWindowDayHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.weather_item_layout, viewGroup,false);
        InfoWindowDayHolder holder = new InfoWindowDayHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull InfoWindowDayHolder infoWindowDayHolder, int i) {
        Forecast forecast = forecastList.get(i);
        infoWindowDayHolder.dateView.setText(forecast.date.split("-")[2]+"日");
        Calendar calendar = Calendar.getInstance();
        int weekValue = calendar.get(Calendar.DAY_OF_WEEK);
        String week = WeatherFragment.judgeWeek((weekValue+i) % 7);
        infoWindowDayHolder.weekView.setText(week);
        WeatherFragment.judgeWeatherImage(infoWindowDayHolder.imageView, forecast.dayConf, null,false);
        infoWindowDayHolder.conditionView.setText(forecast.dayConf);
        infoWindowDayHolder.tempView.setText(forecast.tmp_min+"～"+forecast.tmp_max+"°");
    }

    @Override
    public int getItemCount() {
        return forecastList.size()-1;
    }

    class InfoWindowDayHolder extends RecyclerView.ViewHolder{

        private TextView dateView;
        private TextView weekView;
        private ImageView imageView;
        private TextView conditionView;
        private TextView tempView;

        public InfoWindowDayHolder(@NonNull View itemView) {
            super(itemView);
            dateView = itemView.findViewById(R.id.weather_item_date);
            weekView = itemView.findViewById(R.id.weather_item_week);
            imageView = itemView.findViewById(R.id.weather_item_image);
            conditionView = itemView.findViewById(R.id.weather_item_condition);
            tempView = itemView.findViewById(R.id.weather_item_temp);
        }
    }
}

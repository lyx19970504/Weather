package com.fafu.polutionrepo.finished.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fafu.polutionrepo.finished.Beans.Hour_future;
import com.fafu.polutionrepo.finished.Beans.Weather_industrial;
import com.fafu.polutionrepo.finished.Fragments.WeatherFragment;
import com.fafu.polutionrepo.finished.R;

public class InfoWindowHourAdapter extends RecyclerView.Adapter<InfoWindowHourAdapter.InfoWindowHolder> {

    private Weather_industrial weather;

    public InfoWindowHourAdapter(Weather_industrial weather){
        this.weather = weather;
    }

    @NonNull
    @Override
    public InfoWindowHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.infowindow_hour_item, viewGroup,false);
        InfoWindowHolder holder = new InfoWindowHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull InfoWindowHolder infoWindowHolder, int i) {
        Hour_future hour_future = weather.hours_24.get(i);
        infoWindowHolder.hourText.setText(hour_future.time.split(" ")[1].split(":")[0]+"时");
        WeatherFragment.judgeWeatherImage(infoWindowHolder.hourImage, hour_future.condition, hour_future.time.split(" ")[1].split(":")[0],false);
        infoWindowHolder.hourTemp.setText(hour_future.temperature+"℃");
    }

    @Override
    public int getItemCount() {
        return weather.hours_24.size();
    }

    class InfoWindowHolder extends RecyclerView.ViewHolder{

        TextView hourText;
        ImageView hourImage;
        TextView hourTemp;

        public InfoWindowHolder(@NonNull View itemView) {
            super(itemView);
            hourText = itemView.findViewById(R.id.hour);
            hourImage = itemView.findViewById(R.id.hour_image);
            hourTemp = itemView.findViewById(R.id.hour_temp);
        }
    }
}

package com.fafu.polutionrepo.finished.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fafu.polutionrepo.finished.Activities.MainActivity;
import com.fafu.polutionrepo.finished.Beans.City_selected;
import com.fafu.polutionrepo.finished.R;
import com.fafu.polutionrepo.finished.SlideSwapHelper.SlideSwapAction;
import com.fafu.polutionrepo.finished.Util.Util;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class City_selected_Adapter extends RecyclerView.Adapter<City_selected_Adapter.City_list_Holder> {

    private static final String TAG = "City_selected_Adapter";
    private Context mContext;
    List<City_selected> items;
    private DeletedItemListener listener;

    public void setDeletedItemListener(DeletedItemListener listener){
        this.listener = listener;
    }

    public City_selected_Adapter(Context context){
        mContext = context;
        items = LitePal.findAll(City_selected.class);
    }

    public void removeDataByPosition(int position){
        if(position >= 0 && position < items.size()){
            items.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeRemoved(position, items.size()-position);
        }
    }

    @NonNull
    @Override
    public City_list_Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.city_list_item,viewGroup,false);
        City_list_Holder holder = new City_list_Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final City_list_Holder city_list_holder, final int i) {
        final City_selected city_selected = items.get(i);
        city_list_holder.locationView.setText(city_selected.getLocation());
        city_list_holder.temperatureView.setText(city_selected.getTemperature());
        city_list_holder.weatherView.setText(city_selected.getWeather());
        city_list_holder.slideView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.delete(city_list_holder.getAdapterPosition());
                    LitePal.deleteAll(City_selected.class,"location = ?",city_selected.getLocation());
                }
            }
        });
        city_list_holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = MainActivity.newIntent(mContext, city_selected.getLocation());
                mContext.startActivity(intent);
                ((Activity) mContext).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class City_list_Holder extends RecyclerView.ViewHolder implements SlideSwapAction {

        private TextView locationView;
        private TextView temperatureView;
        private TextView weatherView;
        private TextView slideView;
        private RelativeLayout relativeLayout;

        public City_list_Holder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.relative);
            locationView = itemView.findViewById(R.id.location_selected);
            if(Util.getHour() <= 18 & Util.getHour() >= 6){
                relativeLayout.setBackground(mContext.getDrawable(R.drawable.item_bg));
                locationView.setTextColor(Color.BLACK);
            }else{
                relativeLayout.setBackground(mContext.getDrawable(R.drawable.item_bg_night));
                locationView.setTextColor(Color.WHITE);
            }

            temperatureView = itemView.findViewById(R.id.temperature_selected);
            weatherView = itemView.findViewById(R.id.weather_selected);
            slideView = itemView.findViewById(R.id.slide_text);
            slideView.setWidth(relativeLayout.getHeight());
        }

        @Override
        public float getActionWidth() {
            return slideView.getWidth();
        }

        @Override
        public View ItemView() {
            return relativeLayout;
        }
    }

    public interface DeletedItemListener{
        void delete(int position);
    }


}

package com.fafu.polutionrepo.finished.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fafu.polutionrepo.finished.Activities.ChooseCityActivity;
import com.fafu.polutionrepo.finished.Activities.MainActivity;
import com.fafu.polutionrepo.finished.Fragments.MapFragmentWeather;
import com.fafu.polutionrepo.finished.R;

import java.util.List;

public class City_list_Adapter extends RecyclerView.Adapter<City_list_Adapter.City_list_Holder> {

    private static final String TAG = "City_list_Adapter";
    private List<String> cities;
    private Context mContext;
    private String type;

    public City_list_Adapter(List<String> cities,Context context,String type){
        this.cities = cities;
        mContext = context;
        this.type = type;
    }

    @NonNull
    @Override
    public City_list_Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.simple_list_item,viewGroup,false);
        City_list_Holder holder = new City_list_Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final City_list_Holder city_list_holder, final int i) {
        city_list_holder.textView.setText(cities.get(i));
        final String cityName = cities.get(city_list_holder.getAdapterPosition());
        city_list_holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type.equals(ChooseCityActivity.TAG)) {
                    Intent intent = MainActivity.newIntent(mContext, cityName);
                    mContext.startActivity(intent);
                } else if(type.equals(MapFragmentWeather.TAG)){
                    Intent intent = new Intent();
                    intent.putExtra(MainActivity.CITY_NAME, cityName);
                    ((Activity) mContext).setResult(Activity.RESULT_OK,intent);
                }
                ((Activity) mContext).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    class City_list_Holder extends RecyclerView.ViewHolder{

        private TextView textView;

        public City_list_Holder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }
}

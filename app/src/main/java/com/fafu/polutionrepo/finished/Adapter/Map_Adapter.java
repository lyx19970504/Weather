package com.fafu.polutionrepo.finished.Adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fafu.polutionrepo.finished.Fragments.MapFragmentPollution;
import com.fafu.polutionrepo.finished.Fragments.MapFragmentWeather;

public class Map_Adapter extends FragmentPagerAdapter {

    private ImageView searchView;
    private TextView mLocationView;

    public Map_Adapter(FragmentManager fm, ImageView imageView, TextView textView) {
        super(fm);
        searchView = imageView;
        mLocationView = textView;
    }

    @Override
    public Fragment getItem(int i) {
        if(i == 0){
            return MapFragmentWeather.newWeatherFragment(searchView,mLocationView);
        }else{
            return new MapFragmentPollution();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0){
            return "天气";
        }else{
            return "实时污染";
        }
    }
}

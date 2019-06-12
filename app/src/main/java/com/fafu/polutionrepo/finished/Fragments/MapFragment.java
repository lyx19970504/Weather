package com.fafu.polutionrepo.finished.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fafu.polutionrepo.finished.Activities.AddCityActivity;
import com.fafu.polutionrepo.finished.Activities.CityListActivity;
import com.fafu.polutionrepo.finished.Adapter.Map_Adapter;
import com.fafu.polutionrepo.finished.Beans.User;
import com.fafu.polutionrepo.finished.R;
import com.fafu.polutionrepo.finished.View.MyViewPager;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;

import static com.fafu.polutionrepo.finished.Fragments.MapFragmentWeather.CITY_REQUEST;
import static com.fafu.polutionrepo.finished.Fragments.MapFragmentWeather.POSITION_NAME_REQUEST;
import static com.fafu.polutionrepo.finished.Fragments.MapFragmentWeather.TAG;

public class MapFragment extends Fragment {

    @InjectView(R.id.tab_layout)
    TabLayout tabLayout;
    @InjectView(R.id.search)
    ImageView searchView;
    @InjectView(R.id.present_location)
    TextView mLocationView;
    @InjectView(R.id.view_pager)
    MyViewPager contentViewPager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_map, container,false);
        Injector.injectInto(this,view);
        initViews();
        return view;
    }

    public void initViews(){
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        Map_Adapter adapter = new Map_Adapter(getChildFragmentManager(),searchView,mLocationView);
        contentViewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(contentViewPager, true);
    }
}

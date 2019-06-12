package com.fafu.polutionrepo.finished.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fafu.polutionrepo.finished.Areas.City;
import com.fafu.polutionrepo.finished.Areas.Province;
import com.fafu.polutionrepo.finished.R;

import org.litepal.LitePal;

import java.util.List;

import static com.fafu.polutionrepo.finished.Fragments.MapFragmentWeather.LATITUDE;
import static com.fafu.polutionrepo.finished.Fragments.MapFragmentWeather.LONGITUDE;
import static com.fafu.polutionrepo.finished.Fragments.MapFragmentWeather.PROVINCE_NAME;
import static com.fafu.polutionrepo.finished.Fragments.MapFragmentWeather.CITY_NAME;


public class CityListActivity extends AppCompatActivity  {

    private static final String TAG = "CityListActivity";
    private LinearLayout mScrollView;
    private String[] strs = new String[]{"A","B","C","D","E","F","G","H","J","K","L","M","N","P","Q","R","S","T","W","X","Y","Z"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_list__by_letter);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        TabLayout.Tab tab1 = tabLayout.newTab();
        tab1.setText("省");
        tabLayout.addTab(tab1);
        TabLayout.Tab tab2 = tabLayout.newTab();
        tab2.setText("市");
        tabLayout.addTab(tab2);
        mScrollView = findViewById(R.id.scroll_view);
        addProvinceData();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0){
                    mScrollView.removeAllViews();
                    addProvinceData();
                }else if(tab.getPosition() == 1){
                    mScrollView.removeAllViews();
                    addCityDatas();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    public void addProvinceData(){
        TextView view;
        for(int i=0;i<strs.length;i++){
            String letter = strs[i];
            view = (TextView) LayoutInflater.from(CityListActivity.this).inflate(R.layout.letter_layout,null,false);
            view.setText(letter);
            List<Province> provinces = LitePal.where("initial = ?",letter).find(Province.class);
            if(provinces.size() != 0){
                mScrollView.addView(view);
                for(int j=0;j<provinces.size();j++){
                    final Province province = provinces.get(j);
                    View provinceView = LayoutInflater.from(CityListActivity.this).inflate(R.layout.city_layout,null,false);
                    TextView textView = provinceView.findViewById(R.id.city_view);
                    textView.setText(province.getProvinceName());
                    mScrollView.addView(provinceView);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.putExtra(PROVINCE_NAME,((TextView) v).getText());
                            intent.putExtra(LATITUDE,province.getLatitude());
                            intent.putExtra(LONGITUDE,province.getLongitude());
                            setResult(RESULT_OK,intent);
                            finish();
                        }
                    });
                }
            }
        }
    }

    public void addCityDatas(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<strs.length;i++){
                    String letter = strs[i];
                    final TextView view = (TextView) LayoutInflater.from(CityListActivity.this).inflate(R.layout.letter_layout,null,false);
                    view.setText(letter);
                    List<City> cities = LitePal.where("initial like ?",letter+"%").find(City.class);
                    if(cities.size() != 0){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mScrollView.addView(view);
                            }
                        });
                        for(int j=0;j<cities.size();j++){
                            final City city = cities.get(j);
                            final View cityView = LayoutInflater.from(CityListActivity.this).inflate(R.layout.city_layout,null,false);
                            TextView textView = cityView.findViewById(R.id.city_view);
                            textView.setText(city.getCityName());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mScrollView.addView(cityView);
                                }
                            });
                            textView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.putExtra(CITY_NAME,((TextView) v).getText());
                                    intent.putExtra(LATITUDE,city.getLatitude());
                                    intent.putExtra(LONGITUDE,city.getLongitude());
                                    setResult(RESULT_OK,intent);
                                    finish();
                                }
                            });
                        }
                    }
                }
            }
        }).start();

    }

    public void back(View view) {
        finish();
    }
}

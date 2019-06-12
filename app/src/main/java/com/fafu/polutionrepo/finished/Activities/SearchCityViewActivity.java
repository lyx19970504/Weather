package com.fafu.polutionrepo.finished.Activities;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.fafu.polutionrepo.finished.Areas.Capital;
import com.fafu.polutionrepo.finished.Areas.City;
import com.fafu.polutionrepo.finished.Areas.Province;
import com.fafu.polutionrepo.finished.R;
import com.fafu.polutionrepo.finished.Util.Util;
import com.fafu.polutionrepo.finished.Util.WeatherUtil;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;

import org.litepal.LitePal;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SearchCityViewActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private static final String TAG = "SearchCityViewActivity";
    @InjectView(R.id.search_view)
    SearchView mSearchView;
    @InjectView(R.id.province_list)
    ListView mListView;
    List<String> provinceNames;
    @InjectView(R.id.my_location)
    TextView mMyLocation;
    @InjectView(R.id.grid_layout)
    GridLayout mGridLayout;
    @InjectView(R.id.city_container)
    LinearLayout mCityContainer;
    @InjectView(R.id.scroll_view)
    ScrollView mScrollView;
    @InjectView(R.id.back)
    ImageView back;
    public static final String IMAGE_URL = "http://stock.tuchong.com/search?term={0}&page={1}";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_view_list);
        Injector.injectInto(this);
        initViews();
    }

    public void initViews(){
        provinceNames = new ArrayList<>();
        provinceNames.add("推荐");
        mSearchView.post(new Runnable() {
            @Override
            public void run() {
                mSearchView.clearFocus();
            }
        });
        List<Province> provinces = LitePal.findAll(Province.class);
        if(provinces.size() != 0){
            for(Province province : provinces){
                String name = splitText(province.getProvinceName());
                provinceNames.add(name);
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,provinceNames);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
        setCityContainer(mMyLocation,mGridLayout);
        mMyLocation.setOnClickListener(this);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setCityContainer(TextView myLocation,GridLayout gridLayout){
        getMyLocation(myLocation);
        List<Capital> capitals = LitePal.findAll(Capital.class);
        float density = Util.getRealDensity(this);
        for(Capital capital : capitals){
            TextView textView = new TextView(this);
            textView.setText(capital.getCityName().replace("市",""));
            textView.setTextColor(getResources().getColorStateList(R.color.text_color,null));
            ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams((int) (80 * density),(int) (32 * density));
            marginLayoutParams.setMargins((int) (16 * density),(int) (12 * density),0,(int) (16 * density));
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(marginLayoutParams);
            textView.setLayoutParams(params);
            textView.setGravity(Gravity.CENTER);
            textView.setBackgroundColor(Color.WHITE);
            textView.setOnClickListener(this);
            gridLayout.addView(textView);
        }
    }

    public void getMyLocation(final TextView textView){
        final AMapLocationClient client = new AMapLocationClient(this);
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setOnceLocation(true);
        client.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(final AMapLocation aMapLocation) {
                if(aMapLocation != null){
                    textView.setText(aMapLocation.getCity().replace("市",""));
                }
            }
        });
        client.setLocationOption(option);
        client.startLocation();
    }

    public String splitText(String text){
        return text.replace("省","").
                replace("市","").
                replace("自治区","").
                replace("壮族","").
                replace("回族","").
                replace("维吾尔","");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position == 0){
            mScrollView.removeAllViews();
            mCityContainer = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.city_container_layout,null,false);
            mMyLocation = mCityContainer.findViewById(R.id.my_location);
            mMyLocation.setOnClickListener(this);
            GridLayout gridLayout = mCityContainer.findViewById(R.id.grid_layout);

            setCityContainer(mMyLocation,gridLayout);
            mScrollView.addView(mCityContainer);
        }
        TextView clickedView = (TextView) view;
        String provinceName = clickedView.getText().toString();
        List<City> cities = LitePal.where("provincename like ?",provinceName+"%").find(City.class);
        if(cities.size() != 0){
            mScrollView.removeAllViews();
            mCityContainer = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.city_container_layout,null,false);
            LinearLayout linearLayout = mCityContainer.findViewById(R.id.province_info);
            linearLayout.removeAllViews();
            TextView textView = new TextView(this);
            textView.setText(provinceName);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(20,20,0,0);
            textView.setLayoutParams(params);
            linearLayout.addView(textView);
            GridLayout gridLayout = mCityContainer.findViewById(R.id.grid_layout);
            float density = Util.getRealDensity(this);
            for(City city : cities){
                TextView cityView = new TextView(this);
                cityView.setTextColor(getResources().getColorStateList(R.color.text_color,null));
                cityView.setOnClickListener(this);
                cityView.setText(city.getCityName().replace("市",""));
                cityView.setTextColor(getResources().getColorStateList(R.color.text_color,null));
                ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams((int) (80 * density),(int) (40 * density));
                marginLayoutParams.setMargins((int) (16 * density),(int) (12 * density),0,(int) (16 * density));
                GridLayout.LayoutParams cityParams = new GridLayout.LayoutParams(marginLayoutParams);
                cityView.setLayoutParams(cityParams);
                cityView.setGravity(Gravity.CENTER);
                cityView.setBackgroundColor(Color.WHITE);
                gridLayout.addView(cityView);
            }
            mScrollView.addView(mCityContainer);
        }

    }

    @Override
    public void onClick(View v) {
        TextView textView = (TextView) v;
        final String cityName = textView.getText().toString();
        Util.sendImageRequest(MessageFormat.format(IMAGE_URL,cityName + " 风景",1), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: "+"获取图片信息失败!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                if(responseText != null){
                    String newResponse = responseText.substring(responseText.indexOf("[{"),responseText.indexOf("}];")+2);
                    Intent intent = CityViewActivity.newIntent(SearchCityViewActivity.this,cityName,newResponse);
                    startActivity(intent);
                }
            }
        });
    }


}

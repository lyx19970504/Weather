package com.fafu.polutionrepo.finished.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;
import com.fafu.polutionrepo.finished.Application.MyApplication;
import com.fafu.polutionrepo.finished.Areas.City;
import com.fafu.polutionrepo.finished.Areas.County;
import com.fafu.polutionrepo.finished.Beans.User;
import com.fafu.polutionrepo.finished.R;

import com.fafu.polutionrepo.finished.Util.Util;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;

import org.litepal.LitePal;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PollutionActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PollutionActivity";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    @InjectView(R.id.pollution_spinner)
    SmartMaterialSpinner pollution_spinner;
    @InjectView(R.id.level_spinner)
    SmartMaterialSpinner level_spinner;
    @InjectView(R.id.belong_city)
    TextView CityView;
    @InjectView(R.id.publish_date)
    TextView dateView;
    @InjectView(R.id.confirm)
    Button confirmButton;
    @InjectView(R.id.cancel)
    Button cancelButton;

    public static final String[] pollutions_zh = new String[]{"水污染","空气污染","土地污染","其他污染"};
    public static final int[] pollutionIcons = new int[]{R.drawable.water_pollution,R.drawable.air_pollution,R.drawable.soil_pollution,R.drawable.other_pollution};
    public static final String[] pollutions_us = new String[]{"WATER","AIR","SOIL","OTHER"};
    public static final String[] LEVELS = new String[]{"正常","中等","差","严重","非常严重"};
    public static final String[] pollution_levels = new String[]{"NORMAL","MIDDLE","BAD","SERIOUS","SEVERE"};
    public static final int[] levelIcons = new int[]{R.drawable.normal,R.drawable.middle,R.drawable.bad,R.drawable.serious,R.drawable.severe};
    private static final String PUBLISH_URL = Util.URL_PREFIX + "/green/auth/publishPol";
    private static String selected_level;
    private static String selected_pollution_us;
    private static String selected_level_us;
    private String Date;
    public static final String CITY = "city";
    public static final String DATE = "date";
    public static final String POLLUTION_TYPE = "pollution_type";
    public static final String POLLUTION_LEVEL = "pollution_level";
    private User user;
    private String goalCityName = null;
    private double longitude;
    private double latitude;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pollution_public_layout);
        Injector.injectInto(this);
        initViews();

    }

    public void initViews(){
        user = MyApplication.getUser();
        PollutionAdapter adapter = new PollutionAdapter();
        pollution_spinner.setAdapter(adapter);
        pollution_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_pollution_us = pollutions_us[position];
                pollution_spinner.setHint("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        PollutionLevelAdapter levelAdapter = new PollutionLevelAdapter();
        level_spinner.setAdapter(levelAdapter);
        level_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_level = LEVELS[position];
                selected_level_us = pollution_levels[position];
                level_spinner.setHint("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        latitude = getIntent().getDoubleExtra(LATITUDE,-1);
        longitude = getIntent().getDoubleExtra(LONGITUDE,0);
        goalCityName = Util.searchCity(this,latitude,longitude,CityView);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",new Locale("us"));
        Date = format.format(new Date());
        dateView.setText(dateView.getText().toString() + "  "+ Date);

        confirmButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.confirm:
                Intent intent = new Intent();
                if(selected_pollution_us == null){
                    Toast.makeText(this, "请选择污染类型!", Toast.LENGTH_SHORT).show();
                    return;
                }
                intent.putExtra(POLLUTION_TYPE,selected_pollution_us);
                if(selected_level == null){
                    Toast.makeText(this, "请选择受污染程度!", Toast.LENGTH_SHORT).show();
                    return;
                }
                intent.putExtra(POLLUTION_LEVEL,selected_level_us);
                intent.putExtra(DATE,Date);
                intent.putExtra(CITY,goalCityName);
                intent.putExtra(LATITUDE,latitude);
                intent.putExtra(LONGITUDE,longitude);

                if(user != null){
                    Object[] objects = new Object[]{latitude,longitude,selected_pollution_us,selected_level_us};
                    Util.sendPublishRequest(PUBLISH_URL, user, objects, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d(TAG, "onFailure: "+"发布污染源失败!");
                        }

                        @Override
                        public void onResponse(Call call, Response response) {
                            if(response.body() != null){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(PollutionActivity.this, "发布成功!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
                setResult(RESULT_OK,intent);
                finish();
                break;
            case R.id.cancel:
                setResult(RESULT_OK,null);
                finish();
                break;
        }
    }

    private class PollutionAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return pollutions_zh.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_pollution_item,null);
            }
            ImageView PollutionIcon = convertView.findViewById(R.id.pollution_icon);
            PollutionIcon.setImageResource(pollutionIcons[position]);
            TextView Pollution = convertView.findViewById(R.id.pollution_type);
            Pollution.setText(pollutions_zh[position]);
            return convertView;
        }
    }

    private class PollutionLevelAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return LEVELS.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_pollution_item,null);
            }
            ImageView PollutionIcon = convertView.findViewById(R.id.pollution_icon);
            PollutionIcon.setImageResource(levelIcons[position]);
            TextView Pollution = convertView.findViewById(R.id.pollution_type);
            Pollution.setText(LEVELS[position]);
            return convertView;
        }
    }

    public static Intent newIntent(Context context, double latitude, double longitude){
        Intent intent = new Intent(context,PollutionActivity.class);
        intent.putExtra(LATITUDE,latitude);
        intent.putExtra(LONGITUDE,longitude);
        return intent;
    }
}

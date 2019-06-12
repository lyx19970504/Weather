package com.fafu.polutionrepo.finished.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fafu.polutionrepo.finished.Beans.Image;
import com.fafu.polutionrepo.finished.R;
import com.fafu.polutionrepo.finished.Util.Util;
import com.fafu.polutionrepo.finished.Util.WeatherUtil;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.fafu.polutionrepo.finished.Activities.SearchCityViewActivity.IMAGE_URL;

public class CityViewActivity extends AppCompatActivity implements View.OnClickListener, OnRefreshListener, OnLoadMoreListener {

    private static final String TAG = "CityViewActivity";
    @InjectView(R.id.button_back)
    ImageView mButtonBack;
    @InjectView(R.id.city_name)
    TextView mCityName;
    @InjectView(R.id.smart_refresh)
    SmartRefreshLayout mSmartRefreshLayout;
    @InjectView(R.id.tool_bar)
    Toolbar toolbar;
    @InjectView(R.id.city_list1)
    LinearLayout city_list1;
    @InjectView(R.id.city_list2)
    LinearLayout city_list2;
    @InjectView(R.id.city_list)
    LinearLayout city_lit;
    public LinearLayout[] mLinearLayouts;
    public static final String CITY_NAME  = "city_name";
    public static final String RESPONSE_TEXT = "response_text";
    public static final String URL_PREFIX = "http://p3a.pstatp.com/weili/l/";
    public static final int offset = 10;
    public static int imageIndex = 2;
    public static float density;
    public List<Image> images;
    public static final int MAX_PAGE = 50;
    public static int PAGE_INDEX = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_view_layout);
        Injector.injectInto(this);
        mLinearLayouts = new LinearLayout[]{city_list1,city_list2};
        density = Util.getRealDensity(this);
        mButtonBack.setOnClickListener(this);
        setSupportActionBar(toolbar);
        initViews();
    }

    public void initViews(){


        String cityName = getIntent().getStringExtra(CITY_NAME);
        mCityName.setText(cityName);
        String response = getIntent().getStringExtra(RESPONSE_TEXT);
        if(cityName != null && response != null){
            images  =  WeatherUtil.handleImageResponse(response);
            if(images.size() != 0) {
                initialImages();
            }
        }
        mSmartRefreshLayout.setEnableRefresh(true);
        mSmartRefreshLayout.setEnableLoadMore(true);
        mSmartRefreshLayout.setOnRefreshListener(this);
        mSmartRefreshLayout.setOnLoadMoreListener(this);
    }

    public void initialImages(){
        for(int j= 0;j<2;j++){
            LinearLayout linearLayout = mLinearLayouts[j];
            for (int i = j * offset; i < offset * (j+1); i++) {
                final Image image = images.get(i);
                View view = LayoutInflater.from(this).inflate(R.layout.city_image_item, null, false);
                ImageView imageView = (ImageView) view;
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = CityImageDetailActivity.newIntent(CityViewActivity.this,image.getImageId(),mCityName.getText().toString(),image.getTitle());
                        startActivity(intent);
                    }
                });
                Glide.with(this).load(URL_PREFIX + image.getImageId() + ".jpg").into(imageView);
                linearLayout.addView(imageView);
                View emptyView = new View(this);
                emptyView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int) (3 * density)));
                linearLayout.addView(emptyView);
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_back:
                finish();
                break;
        }
    }

    public static Intent newIntent(Context context,String cityName,String responseText){
        Intent intent = new Intent(context,CityViewActivity.class);
        intent.putExtra(CITY_NAME,cityName);
        intent.putExtra(RESPONSE_TEXT,responseText);
        return intent;
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        Random random = new Random();
        PAGE_INDEX = random.nextInt(50);
        Util.sendImageRequest(MessageFormat.format(IMAGE_URL,mCityName.getText() + " 风景",PAGE_INDEX), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: "+"获取图片信息失败!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                if(responseText != null){
                    final String newResponse = responseText.substring(responseText.indexOf("[{"),responseText.indexOf("}];")+2);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            city_list1.removeAllViews();
                            city_list2.removeAllViews();
                            images.clear();
                            images.addAll(WeatherUtil.handleImageResponse(newResponse));
                            if(images.size() != 0) {
                                initialImages();
                            }
                        }
                    });

                }
            }
        });
        refreshLayout.finishRefresh();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if(PAGE_INDEX > MAX_PAGE){
            TextView textView = new TextView(this);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            textView.setTextSize(40);
            textView.setGravity(Gravity.CENTER);
            textView.setText("没有更多内容可以显示了!");
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            city_lit.addView(textView);
            return;
        }
        if(imageIndex != 8) {
            addImages();
        }else{
            images.clear();
            imageIndex = 0;
            PAGE_INDEX ++;
            Util.sendImageRequest(MessageFormat.format(IMAGE_URL,mCityName.getText() + " 风景",PAGE_INDEX), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "onFailure: "+"获取图片信息失败!");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    if(responseText != null){
                        String newResponse = responseText.substring(responseText.indexOf("[{"),responseText.indexOf("}];")+2);
                        images.addAll(WeatherUtil.handleImageResponse(newResponse));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addImages();
                            }
                        });
                    }
                }
            });
        }

        refreshLayout.finishLoadMore();
    }

    public void addImages(){
        for (int i = imageIndex * offset; i < offset * (imageIndex + 2); i++) {
            final Image image = images.get(i);
            View view = LayoutInflater.from(this).inflate(R.layout.city_image_item, null, false);
            ImageView imageView = (ImageView) view;
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = CityImageDetailActivity.newIntent(CityViewActivity.this,image.getImageId(),mCityName.getText().toString(),image.getTitle());
                    startActivity(intent);
                }
            });
            Glide.with(this).load(URL_PREFIX + image.getImageId() + ".jpg").into(imageView);
            View emptyView = new View(this);
            emptyView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (3 * density)));
            if (i % 2 == 0) {
                city_list1.addView(imageView);
                city_list1.addView(emptyView);
            } else {
                city_list2.addView(imageView);
                city_list2.addView(emptyView);
            }
        }
        imageIndex += 2;
    }
}

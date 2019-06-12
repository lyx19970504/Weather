package com.fafu.polutionrepo.finished.Activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.transition.Explode;
import android.transition.Slide;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.fafu.polutionrepo.finished.Adapter.City_selected_Adapter;
import com.fafu.polutionrepo.finished.R;
import com.fafu.polutionrepo.finished.SlideSwapHelper.ItemSlideCallback;
import com.fafu.polutionrepo.finished.SlideSwapHelper.WItemTouchHelperPlus;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;

public class ChooseCityActivity extends AppCompatActivity implements City_selected_Adapter.DeletedItemListener {

    public static final String TAG = "ChooseCityActivity";

    @InjectView(R.id.selected_cities)
    RecyclerView mRecyclerView;
    private City_selected_Adapter adapter;
    @InjectView(R.id.tool_bar)
    Toolbar toolbar;

    public void initToolbar(){
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if(hour <= 18 && hour >= 6){
            getWindow().getDecorView().setBackground(getDrawable(R.drawable.bg));
        }else{
            getWindow().getDecorView().setBackground(getDrawable(R.drawable.bg_night));
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置Activity进入退出动画
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Slide());
        getWindow().setExitTransition(new Explode());
        setContentView(R.layout.choose_city);
        Injector.injectInto(this);
        initToolbar();
        initView();


    }

    public void initView(){

        adapter = new City_selected_Adapter(this);
        adapter.setDeletedItemListener(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
        ItemSlideCallback callback = new ItemSlideCallback();
        WItemTouchHelperPlus extension = new WItemTouchHelperPlus(callback);
        extension.attachToRecyclerView(mRecyclerView);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    public void selected_city(View view) {
        Intent intent = new Intent(this,AddCityActivity.class);
        intent.putExtra("from_activity_type", TAG);
        startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    @Override
    public void delete(int position) {
        adapter.removeDataByPosition(position);
    }
}

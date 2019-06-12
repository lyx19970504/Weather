package com.fafu.polutionrepo.finished.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fafu.polutionrepo.finished.Application.MyApplication;
import com.fafu.polutionrepo.finished.Beans.Pollution;
import com.fafu.polutionrepo.finished.Beans.User;
import com.fafu.polutionrepo.finished.R;
import com.fafu.polutionrepo.finished.Util.Util;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class PollutionDetailedActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PollutionDetailedActivity";
    public static final String POLLUTION = "pollution";

    @InjectView(R.id.location_city)
    TextView mLocationCity;
    @InjectView(R.id.pollution_type)
    TextView mPollutionType;
    @InjectView(R.id.pollution_icon)
    ImageView mPollutionIcon;
    @InjectView(R.id.pollution_level)
    TextView mPollutionLevel;
    @InjectView(R.id.pollution_level_icon)
    ImageView mPollutionLevelIcon;
    @InjectView(R.id.focus_button)
    Button mFocusButton;
    @InjectView(R.id.table_button)
    Button mTableButton;
    @InjectView(R.id.topic_button)
    Button mTopicButton;
    @InjectView(R.id.tool_bar)
    Toolbar mToolbar;
    @InjectView(R.id.longitude)
    TextView longitudeView;
    @InjectView(R.id.latitude)
    TextView latitudeView;
    private Pollution mPollution;
    @InjectView(R.id.table)
    LinearLayout tableLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pollution_detailed_layout);
        setSupportActionBar(mToolbar);
        Injector.injectInto(this);
        mFocusButton.setSelected(false);
        mFocusButton.setOnClickListener(this);
        mTableButton.setSelected(false);
        mTableButton.setOnClickListener(this);
        mTopicButton.setOnClickListener(this);
        mToolbar.findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mPollution = (Pollution) getIntent().getSerializableExtra(POLLUTION);
        if(mPollution != null){
            BigDecimal bigDecimal;
            bigDecimal = new BigDecimal(mPollution.getLongitude()).setScale(2, RoundingMode.DOWN);
            longitudeView.setText("东经 " + bigDecimal.doubleValue()+"°");
            bigDecimal = new BigDecimal(mPollution.getLatitude()).setScale(2, RoundingMode.DOWN);
            latitudeView.setText("北纬 " + bigDecimal.doubleValue() + "°");
            mLocationCity.setText(mLocationCity.getText() + mPollution.getCityName());
            Object[] objects = Util.judgePollution(mPollution.getPollutionType());
            mPollutionType.setText((String) (objects[0]));
            mPollutionIcon.setImageResource((int) (objects[1]));
            Object[] objects2 = Util.judgePollution(mPollution.getPollutionLevel());
            mPollutionLevel.setText((String) (objects2[0]));
            mPollutionLevelIcon.setImageResource((int) (objects2[1]));
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.focus_button:
                mFocusButton.setBackgroundResource(R.drawable.focus_pressed);
                Toast.makeText(PollutionDetailedActivity.this, v.isSelected() ? "取消关注成功!" : "关注成功!", Toast.LENGTH_SHORT).show();
                mFocusButton.setText(v.isSelected() ? "关注" : "已关注");
                mFocusButton.setSelected(!v.isSelected());
                mFocusButton.setBackgroundResource(
                        v.isSelected() ? R.drawable.focus_pressed : R.drawable.focus_no_pressed);
                break;
            case R.id.table_button:
                Toast.makeText(PollutionDetailedActivity.this, v.isSelected() ? "取消申请成功!" : "申请报表成功!", Toast.LENGTH_SHORT).show();
                mTableButton.setText(v.isSelected() ? "申请报表" : "取消申请");
                mTableButton.setSelected(!v.isSelected());
                mTableButton.setBackgroundResource(
                        v.isSelected() ? R.drawable.focus_pressed : R.drawable.focus_no_pressed);

                if(tableLayout.getChildAt(tableLayout.getChildCount()-1).getId() == R.id.like) {
                   tableLayout.removeViewAt(tableLayout.getChildCount() -1);
                }else{
                    TextView textView = new TextView(this);
                    textView.setId(R.id.like);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.topMargin = (int) (48 * Util.getRealDensity(PollutionDetailedActivity.this));
                    params.gravity = Gravity.CENTER;
                    textView.setLayoutParams(params);
                    textView.setTextSize(18 * Util.getSPDensity(PollutionDetailedActivity.this));
                    textView.setText("报表已提交，待审核");
                    tableLayout.addView(textView);
                }
                break;
            case R.id.topic_button:
                Intent intent = TopicDetailedActivity.newIntent(PollutionDetailedActivity.this,mPollution);
                startActivity(intent);
                break;
        }
    }

    public static Intent newIntent(Context context, Pollution pollution){
        Intent intent = new Intent(context,PollutionDetailedActivity.class);
        intent.putExtra(POLLUTION,pollution);
        return intent;
    }
}

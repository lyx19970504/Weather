package com.fafu.polutionrepo.finished.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AnticipateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fafu.polutionrepo.finished.Application.MyApplication;
import com.fafu.polutionrepo.finished.Beans.Comment;
import com.fafu.polutionrepo.finished.Beans.Topic;
import com.fafu.polutionrepo.finished.Beans.User;
import com.fafu.polutionrepo.finished.Fragments.WeatherFragment;
import com.fafu.polutionrepo.finished.R;
import com.fafu.polutionrepo.finished.Util.Util;
import com.fafu.polutionrepo.finished.Util.WeatherUtil;
import com.google.gson.Gson;
import com.rey.material.app.BottomSheetDialog;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.fafu.polutionrepo.finished.Activities.CityViewActivity.URL_PREFIX;
import static com.fafu.polutionrepo.finished.Activities.CityViewActivity.density;

public class CityImageDetailActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TOPIC_DETAIL_URL = Util.URL_PREFIX + "/green/getTopic?topicId=";
    public static final String IMAGE_PATH = Util.URL_PREFIX + "/green/img/";
    public static final String GET_USER_INFO = Util.URL_PREFIX + "/green/getAccountAbbr?id=";
    public static final String ADD_COMMENT_URL = Util.URL_PREFIX + "/green/auth/addComment";
    private static final String TAG = "CityImageDetailActivity";
    public static final String IMAGE_ID = "image_id";
    public static final String CITY_NAME = "city_name";
    public static final String CONTENT = "content";
    public static final String TOPIC = "topic";
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @InjectView(R.id.city_image)
    ImageView mCityImageView;
    @InjectView(R.id.title)
    TextView mTitleInfo;
    @InjectView(R.id.detail)
    TextView mDetailInfo;
    @InjectView(R.id.like)
    TextView mLikeView;
    @InjectView(R.id.comment)
    TextView mCommentView;
    @InjectView(R.id.comment_area)
    LinearLayout mCommentArea;
    @InjectView(R.id.pollution_type)
    TextView mPollutionType;
    @InjectView(R.id.pollution_type_image)
    ImageView mPollutionTypeImage;
    @InjectView(R.id.pollution_level)
    TextView mPollutionLevel;
    @InjectView(R.id.pollution_image)
    ImageView mPollutionImage;
    private BottomSheetDialog mBottomSheetDialog;
    private int SoftInputHeight;
    private static boolean isOpenKeyBoard = false;
    private EditText mEditText;
    private int TopicId;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_detail_layout);
        Injector.injectInto(this);
        initViews();
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
                int screenHeight = getWindow().getDecorView().getRootView().getHeight();
                SoftInputHeight = screenHeight - rect.bottom;
                Log.d(TAG, "onGlobalLayout: "+SoftInputHeight);
                if(SoftInputHeight == 0 && isOpenKeyBoard){
                    //closeBottomDialog();
                }
            }
        });

    }

    public void initViews(){
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        String imageId = getIntent().getStringExtra(IMAGE_ID);
        String cityName = getIntent().getStringExtra(CITY_NAME);
        final String content = getIntent().getStringExtra(CONTENT);
        if(imageId != null){
            mCollapsingToolbarLayout.setTitle("中国 " + cityName);
            Glide.with(this).load(URL_PREFIX + imageId + ".jpg").into(mCityImageView);
            mTitleInfo.setText(content);
            if(TextUtils.isEmpty(content)){
                mTitleInfo.setText("拍摄者没有留下任何讯息!");
            }
        }
        Topic topic = (Topic) getIntent().getSerializableExtra(TOPIC);
        if(topic != null){
            Util.getTopicDetailRequest(TOPIC_DETAIL_URL + topic.getTopicId(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "onFailure: "+"获取话题失败!");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Log.d(TAG, "onResponse: "+responseText);
                    final Topic topic = WeatherUtil.handleDetailTopics(responseText);
                    TopicId = topic.getTopicId();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(topic.getTopicId() != 0){
                                mCollapsingToolbarLayout.setTitle("中国 " + topic.getCityName());
                                Glide.with(CityImageDetailActivity.this).load(IMAGE_PATH+topic.getImagePath()).into(mCityImageView);
                                mTitleInfo.setText(topic.getTitle());
                                mDetailInfo.setText(topic.getShortInfo());
                                Object[] objectsType = Util.judgePollution(topic.getTag());
                                mPollutionType.setText((String) objectsType[0]);
                                mPollutionTypeImage.setImageResource((int) objectsType[1]);
                                if(topic.getPollution() != null) {
                                    Object[] objectsLevel = Util.judgePollution(topic.getPollution().getPollutionLevel());
                                    mPollutionLevel.setText((String) objectsLevel[0]);
                                    mPollutionImage.setImageResource((int) objectsLevel[1]);
                                }

                                List<Comment> commentList = topic.getComments();
                                if(commentList.size() == 0){
                                    float density = Util.getRealDensity(CityImageDetailActivity.this);
                                    ImageView imageView = new ImageView(CityImageDetailActivity.this);
                                    LinearLayout.LayoutParams params= new LinearLayout.LayoutParams((int) (250 * density),(int) (250 * density));
                                    params.gravity = Gravity.CENTER;
                                    imageView.setLayoutParams(params);
                                    imageView.setImageResource(R.drawable.no_comments);
                                    mCommentArea.addView(imageView);
                                }else{
                                    for(Comment comment: commentList){
                                        View view = LayoutInflater.from(CityImageDetailActivity.this).inflate(R.layout.comment_item,null,false);
                                        TextView UserComment = view.findViewById(R.id.user_comment);
                                        UserComment.setText(comment.getContent());
                                        TextView dateView = view.findViewById(R.id.date);
                                        dateView.setText(new SimpleDateFormat("yyyy/MM/dd hh: mm: ss",new Locale("en")).format(new Date()));
                                        TextView likeCountView = view.findViewById(R.id.like_count);
                                        likeCountView.setText(String.valueOf(comment.getHits()));
                                        final ImageView UserPortrait = view.findViewById(R.id.user_portrait);
                                        final TextView UserNickName = view.findViewById(R.id.user_nickname);
                                        Util.getUserInfoRequest(GET_USER_INFO + comment.getAccount_Id(), new Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Log.d(TAG, "onFailure: "+"获取用户信息失败!");
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onResponse(Call call, Response response) throws IOException {
                                                String responseText = response.body().string();
                                                final User user = new Gson().fromJson(responseText,User.class);
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Glide.with(CityImageDetailActivity.this).load(IMAGE_PATH+user.getImageName()).into(UserPortrait);
                                                        UserNickName.setText(user.getNickname());
                                                    }
                                                });
                                            }
                                        });
                                        mCommentArea.addView(view);
                                    }
                                }

                            }
                        }
                    });

                }
            });
        }
        mLikeView.setOnClickListener(this);
        mCommentView.setOnClickListener(this);
    }

    public static Intent newIntent(Context context,String imageId,String cityName,String content){
        Intent intent = new Intent(context,CityImageDetailActivity.class);
        intent.putExtra(IMAGE_ID,imageId);
        intent.putExtra(CITY_NAME,cityName);
        intent.putExtra(CONTENT,content);
        return intent;
    }

    public static Intent newIntent(Context context, Topic topic){
        Intent intent = new Intent(context,CityImageDetailActivity.class);
        intent.putExtra(TOPIC,topic);
        return intent;
    }

    @Override
    public void onClick(View v) {
        switch ((v.getId())){
            case R.id.like:
                Toast.makeText(this, "点赞成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.comment:
                if(MyApplication.getUser() == null){
                    Toast.makeText(this, "你还未登录!", Toast.LENGTH_SHORT).show();
                    return;
                }
                showBottomDialog();
                break;
            case R.id.send_message:
                final View view = LayoutInflater.from(this).inflate(R.layout.comment_item,null,false);
                TextView mUserComment = view.findViewById(R.id.user_comment);
                mUserComment.setText(mEditText.getText());
                TextView mDate = view.findViewById(R.id.date);
                Calendar calendar = Calendar.getInstance();
                int month = calendar.get(Calendar.MONTH) + 1;
                mDate.setText(calendar.get(Calendar.YEAR)+":"+month+":"+calendar.get(Calendar.DAY_OF_MONTH));
                Util.addCommentsRequest(ADD_COMMENT_URL, TopicId, mEditText.getText().toString(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CityImageDetailActivity.this, "评论失败!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String responseText = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(TextUtils.isEmpty(responseText)) {
                                    mCommentArea.addView(view);
                                    Toast.makeText(CityImageDetailActivity.this, "评论成功!", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(CityImageDetailActivity.this, "评论失败!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                closeBottomDialog();
                break;
        }
    }

    public void showBottomDialog(){
        View view = View.inflate(this,R.layout.comment_window,null);
        Button button = view.findViewById(R.id.send_message);
        mEditText = view.findViewById(R.id.comment_message);
        button.setOnClickListener(this);
        mBottomSheetDialog = new BottomSheetDialog(this);
        isOpenKeyBoard = true;
        mBottomSheetDialog.
                contentView(view)
                .heightParam(803 + (int) (density * 180))    //803:软键盘的高度
                .inDuration(200)
                .outDuration(200)
                .inInterpolator(new AnticipateInterpolator())
                .cancelable(true)
                .show();
    }

    public void closeBottomDialog(){
        isOpenKeyBoard = false;
        mBottomSheetDialog.hide();
        if(mBottomSheetDialog != null){
            mBottomSheetDialog = null;
        }
    }
}

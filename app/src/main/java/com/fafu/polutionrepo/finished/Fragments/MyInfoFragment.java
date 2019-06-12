package com.fafu.polutionrepo.finished.Fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fafu.polutionrepo.finished.Activities.LoginActivity;
import com.fafu.polutionrepo.finished.Activities.MyDetailInfoActivity;
import com.fafu.polutionrepo.finished.Activities.RegisterActivity;
import com.fafu.polutionrepo.finished.Activities.UpdateNickNameActivity;
import com.fafu.polutionrepo.finished.Adapter.Content_Adapter;
import com.fafu.polutionrepo.finished.Adapter.Day_Adapter;
import com.fafu.polutionrepo.finished.Application.MyApplication;
import com.fafu.polutionrepo.finished.Beans.User;
import com.fafu.polutionrepo.finished.R;
import com.fafu.polutionrepo.finished.Util.Util;
import com.jkb.slidemenu.OnSlideChangedListener;
import com.jkb.slidemenu.SlideMenuLayout;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

public class MyInfoFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "MyInfoFragment";
    private View view;
    private User user;
    LinearLayout linearLayout;
    private TextView nickName;
    private TextView exitView;
    private CircleImageView mCircleImageView;
    public static final String UPDATE_URL = Util.URL_PREFIX + "/green/auth/updateAccount";
    public static final String IMAGE_PREFIX = Util.URL_PREFIX + "/green/img/";
    private static final int REQUEST_NICKNAME = 0;
    private static final int REQUEST_PORTRAIT = 1;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_info, container,false);
        initView();
        return view;
    }

    public void initView(){
        if(getActivity() != null) {
            user = MyApplication.getUser();
        }
        mCircleImageView = view.findViewById(R.id.portrait);
        linearLayout = view.findViewById(R.id.linear_msg);
        exitView = view.findViewById(R.id.exit);
        if(user == null) {
            view.findViewById(R.id.login_button).setOnClickListener(this);
            view.findViewById(R.id.register_button).setOnClickListener(this);
            mCircleImageView.setEnabled(false);
            exitView.setVisibility(View.GONE);
            Toast.makeText(getContext(), "您还未登录!", Toast.LENGTH_SHORT).show();
        }else{
            linearLayout.removeAllViews();
            nickName = new TextView(getActivity());
            nickName.setMaxEms(6);
            nickName.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            nickName.setTypeface(Typeface.DEFAULT_BOLD);
            nickName.setText(TextUtils.isEmpty(user.getNickname()) ? "还没有设置昵称，点击设置" : user.getNickname());
            nickName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(),UpdateNickNameActivity.class);
                    startActivityForResult(intent, REQUEST_NICKNAME);
                }
            });
            linearLayout.addView(nickName);

            if(user.getImageName() != null) {
                Glide.with(getContext()).load(IMAGE_PREFIX + user.getImageName()).into(mCircleImageView);
            }
            mCircleImageView.setEnabled(true);
            mCircleImageView.setOnClickListener(this);
            exitView.setVisibility(View.VISIBLE);
        }
        ViewPager contentViewPager = view.findViewById(R.id.view_pager);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        Content_Adapter adapter = new Content_Adapter(getChildFragmentManager());
        contentViewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(contentViewPager, true);


        exitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSharedPreference();
                if(getActivity() != null) {
                    getActivity().finish();
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_button:
                Intent loginIntent = new Intent(getActivity(),LoginActivity.class);
                startActivity(loginIntent,ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                break;
            case R.id.register_button:
                Intent registerIntent = new Intent(getActivity(),RegisterActivity.class);
                startActivity(registerIntent,ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                break;
            case R.id.portrait:
                Intent intent = MyDetailInfoActivity.newIntent(getContext(),user.getImageName());
                startActivityForResult(intent,REQUEST_PORTRAIT);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_NICKNAME:
                    String nickname = data.getStringExtra("nickname").trim();
                    Log.d(TAG, "onActivityResult: "+nickname);
                    nickName.setText(nickname);
                    user.setNickname(nickname);
                    Util.updateUserRequest(UPDATE_URL, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "更新失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) {
                            if(response.body() != null) {
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getActivity(), "更新成功!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }
                    });
                    break;
                case REQUEST_PORTRAIT:
                    byte[] bytes = data.getByteArrayExtra("bitmap");
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    mCircleImageView.setImageBitmap(bitmap);
                    break;
            }
        }
    }

    public void clearSharedPreference(){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        editor.putString(Util.UserInfo.id, null);
        editor.putString(Util.UserInfo.token, null);
        editor.putString(Util.UserInfo.username, null);
        editor.putString(Util.UserInfo.age, null);
        editor.putString(Util.UserInfo.phone, null);
        editor.putString(Util.UserInfo.email, null);
        editor.putString(Util.UserInfo.date, null);
        editor.putString(Util.UserInfo.nickname, null);
        editor.apply();
    }
}

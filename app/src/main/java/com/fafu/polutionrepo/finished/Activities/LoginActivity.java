package com.fafu.polutionrepo.finished.Activities;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.transition.Explode;
import android.transition.Fade;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.fafu.polutionrepo.finished.Application.MyApplication;
import com.fafu.polutionrepo.finished.Beans.User;
import com.fafu.polutionrepo.finished.R;
import com.fafu.polutionrepo.finished.Util.Util;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    @InjectView(R.id.username)
    EditText mUsername;
    @InjectView(R.id.password)
    EditText mPassword;
    private int errorCount = 0;
    @InjectView(R.id.username_error_msg)
    TextView mUsernameError;
    @InjectView(R.id.password_error_msg)
    TextView mPasswordError;
    private String[] loginInfo = new String[2];
    @InjectView(R.id.image_location)
    ImageView imageLocation;
    private AlertDialog alertDialog;
    private static final String loginUrl = Util.URL_PREFIX + "/green/login";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Explode());
        getWindow().setExitTransition(new Fade());
        setContentView(R.layout.login_layout);
        Injector.injectInto(this);
        findViewById(R.id.login).setOnClickListener(this);
        findViewById(R.id.register_skip).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                errorCount = 0;
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                if(username.isEmpty()){
                    errorCount++;
                    mUsernameError.setText("用户名不能为空");
                }else if(username.length() < 4 || username.length() > 16){
                    errorCount++;
                    mUsernameError.setText("用户名必须在4-16位之间");
                }else{
                    loginInfo[0] = username;
                    mUsernameError.setText("");
                }

                if(password.isEmpty()){
                    errorCount++;
                    mPasswordError.setText("密码不能为空");
                }else if(password.length() < 4 || password.length() > 16){
                    errorCount++;
                    mPasswordError.setText("密码必须在4-16位之间");
                }else{
                    loginInfo[1] = password;
                    mPasswordError.setText("");
                }

                //如果有错误的返回
                if(errorCount != 0){
                    return;
                }else{
                    openDialog();
                    Util.sendLoginRequest(loginUrl, new String[]{username,password}, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "服务器出现问题，无法登陆", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {
                            closeDialog();
                            if(response != null) {
                                String responseText = response.body().string();
                                Log.d(TAG, "onResponse: "+responseText);
                                int status_code = Util.status_code(responseText);
                                if (status_code == 3) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(LoginActivity.this, "用户名或密码错误!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    User user = parseUserInfo(responseText);
                                    if (user != null) {
                                        MyApplication.setUser(user);
                                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }
                        }
                    });
                    Timer timer = new Timer(true);
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            closeDialog();
                        }
                    };
                    timer.schedule(task,20000);
                }
                break;
            case R.id.register_skip:
                Intent intent = new Intent(this,RegisterActivity.class);
                startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(this,imageLocation,"share").toBundle());
                break;
        }
    }

    public void openDialog(){
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminateDrawable(getDrawable(R.drawable.progress_bar));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(120 * Util.getDensity(this),120 * Util.getDensity(this));
        params.gravity = Gravity.START;
        progressBar.setLayoutParams(params);
        linearLayout.addView(progressBar);
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params2.gravity = Gravity.CENTER_VERTICAL;
        textView.setLayoutParams(params2);
        textView.setTextSize(32);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setText("正在登录中...");
        linearLayout.addView(textView);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(linearLayout);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void closeDialog(){
        alertDialog.cancel();
        alertDialog = null;
    }



    public User parseUserInfo(String responseText){
        try {
            User user = new User();
            JSONObject jsonObject = new JSONObject(responseText);
            user.setId(jsonObject.getString("accountId"));
            user.setToken(jsonObject.getString("token"));
            user.setUsername(jsonObject.getString("userName"));
            user.setAge(String.valueOf(jsonObject.getInt("age")));
            user.setPhone(jsonObject.getString("phone"));
            user.setEmail(jsonObject.getString("email"));
            user.setImageName(jsonObject.getString("icon"));
            Date date = new Date(jsonObject.getLong("birthday"));
            String dateText = DateFormat.format("yyyy-MM-dd", date).toString();
            user.setDate(dateText);
            user.setNickname(jsonObject.getString("nickname"));
            saveUserInfo(jsonObject);
            return user;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveUserInfo(JSONObject jsonObject){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        try {
            editor.putString(Util.UserInfo.id, jsonObject.getString("accountId"));
            editor.putString(Util.UserInfo.token, jsonObject.getString("token"));
            editor.putString(Util.UserInfo.username, jsonObject.getString("userName"));
            editor.putString(Util.UserInfo.age, jsonObject.getString("age"));
            editor.putString(Util.UserInfo.phone, jsonObject.getString("phone"));
            editor.putString(Util.UserInfo.email, jsonObject.getString("email"));
            Date date = new Date(jsonObject.getLong("birthday"));
            String dateText = DateFormat.format("yyyy-MM-dd", date).toString();
            editor.putString(Util.UserInfo.date, dateText);
            editor.putString(Util.UserInfo.nickname, jsonObject.getString("nickname"));
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeDialog();
    }
}

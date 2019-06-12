package com.fafu.polutionrepo.finished.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.Explode;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.fafu.polutionrepo.finished.R;
import com.fafu.polutionrepo.finished.Util.Util;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnFocusChangeListener,View.OnClickListener {

    private static final String TAG = "RegisterActivity";
    private String username;
    private String password;
    private String phone;
    private String mail;
    private String birthday;
    @InjectView(R.id.register_username_error)
    TextView usernameError;
    @InjectView(R.id.register_password_error)
    TextView passwordError;
    @InjectView(R.id.register_phone_error)
    TextView phoneError;
    @InjectView(R.id.register_mail_error)
    TextView mailError;
    @InjectView(R.id.register_birthday_error)
    TextView birthdayError;
    @InjectView(R.id.birthday)
    TextView BIRTHDAY;
    @InjectView(R.id.register_username)
    EditText USERNAME;
    @InjectView(R.id.register_password)
    EditText PASSWORD;
    @InjectView(R.id.register_phone)
    EditText PHONE;
    @InjectView(R.id.register_mail)
    EditText MAIL;
    private static final String FIND_USER_URL = Util.URL_PREFIX + "/green/ifExistUser?name=";
    private static final String REGISTER_URL = Util.URL_PREFIX + "/green/register";
    private boolean isCorrect;  //用来判断是否全部没有错误

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Explode());
        getWindow().setExitTransition(new Fade());
        setContentView(R.layout.register_layout);
        Injector.injectInto(this);
        initView();
    }

    public void initView(){

        USERNAME.setOnFocusChangeListener(this);
        PASSWORD.setOnFocusChangeListener(this);
        PHONE.setOnFocusChangeListener(this);
        MAIL.setOnFocusChangeListener(this);
        findViewById(R.id.back_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });  //后退键
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/char.ttf");
        BIRTHDAY.setTypeface(typeface);
        BIRTHDAY.setText("点击选择出生年月:");
        birthday = BIRTHDAY.getText().toString();
        BIRTHDAY.setOnClickListener(this);
    }

    //检测格式
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.register_username:
                if(!hasFocus) {
                    username = USERNAME.getText().toString();
                    if (TextUtils.isEmpty(username)) {
                        usernameError.setText("用户名不能为空");
                        isCorrect = false;
                    } else if (username.length() < 4 || username.length() > 16) {
                        usernameError.setText("用户名长度必须在4-16位之间");
                        isCorrect = false;
                    } else {
                        Util.sendUserNameRequest(FIND_USER_URL, username, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(RegisterActivity.this, "服务器出现问题", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                            if (response.body().string().equals("true")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        usernameError.setText("用户名已存在");
                                        isCorrect = false;
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        usernameError.setText("");
                                        isCorrect = true;
                                    }
                                });
                            }
                            }
                        });
                    }
                }
                break;
            case R.id.register_password:
                if(!hasFocus) {
                    password = PASSWORD.getText().toString();
                    if (TextUtils.isEmpty(password)) {
                        passwordError.setText("密码不能为空");
                        isCorrect = false;
                    }else if (password.length() < 4 || password.length() > 16) {
                        passwordError.setText("密码长度必须在4-16位之间");
                        isCorrect = false;
                    } else {
                        passwordError.setText("");
                        isCorrect = true;
                    }
                }
                break;
            case R.id.register_phone:
                if(!hasFocus) {
                    phone = PHONE.getText().toString();
                    if (TextUtils.isEmpty(phone)) {
                        phoneError.setText("手机号不能为空");
                        isCorrect = false;
                    }else if (phone.length() != 11) {
                        phoneError.setText("手机号码不是11位有效数字");
                        isCorrect = false;
                    } else {
                        phoneError.setText("");
                        isCorrect = true;
                    }
                }
                break;
            case R.id.register_mail:
                if(!hasFocus) {
                    mail = MAIL.getText().toString();
                    if (TextUtils.isEmpty(mail)) {
                        mailError.setText("邮箱不能为空");
                        isCorrect = false;
                    }else if (!mail.matches("^[A-Za-z\\d]+([-_.][A-Za-z\\d]+)*@([A-Za-z\\d]+[-.])+[A-Za-z\\d]{2,4}$")) {
                        mailError.setText("邮箱格式不正确");
                        isCorrect = false;
                    } else {
                        mailError.setText("");
                        isCorrect = true;
                    }
                }
                break;
            default:
                break;
        }
    }

    public void finishedRegister(View view) {
        if(isCorrect && !TextUtils.isEmpty(username) &&
                !TextUtils.isEmpty(password) && !TextUtils.isEmpty(phone) &&
                !TextUtils.isEmpty(mail) && birthday.contains("-")){
            Map<String,String> map = new HashMap<>();
            map.put("userName", username);
            map.put("password", password);
            map.put("phone", phone);
            map.put("email", mail);
            map.put("birthday", birthday);
            map.put("age", String.valueOf(Util.getYear() - Integer.parseInt(birthday.split("-")[0])));
            Util.sendRegisterRequest(REGISTER_URL, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RegisterActivity.this, "注册失败!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(TextUtils.isEmpty(response.body().string())){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this, "注册成功!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        finish();
                    }
                }
            }, map);
        }else{
            if(TextUtils.isEmpty(username)){
                usernameError.setText("用户名不能为空");
                isCorrect = false;
            }
            if(TextUtils.isEmpty(password)){
                passwordError.setText("密码不能为空");
                isCorrect = false;
            }
            if(TextUtils.isEmpty(phone)){
                phoneError.setText("手机号不能为空");
                isCorrect = false;
            }
            if(TextUtils.isEmpty(mail)){
                mailError.setText("邮箱格式不正确");
                isCorrect = false;
            }
            if(!birthday.contains("-")){
                birthdayError.setText("请选择出生年月");
                isCorrect = false;
            }
        }
    }

    @Override
    public void onClick(View v) {
        final DatePicker datePicker = new DatePicker(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择出生日期")
                .setView(datePicker)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = datePicker.getYear();
                        int month = datePicker.getMonth() + 1;
                        int day = datePicker.getDayOfMonth();
                        BIRTHDAY.setText(year+"-"+month+"-"+day);
                        birthday = BIRTHDAY.getText().toString();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

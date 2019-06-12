package com.fafu.polutionrepo.finished.Application;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.fafu.polutionrepo.finished.Activities.MainActivity;
import com.fafu.polutionrepo.finished.Areas.City;
import com.fafu.polutionrepo.finished.Beans.User;
import com.fafu.polutionrepo.finished.Service.AutoUpdateService;
import com.fafu.polutionrepo.finished.Util.test;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MyApplication extends Application {

    public static final String ADD_DATABASE = "add";
    private static final String TAG = "MyApplication";
    private static User sUser;
    private static Context mContext;

    public static Context getContext(){
        return mContext;
    }

    public static void setUser(User user){
        sUser = user;
        Log.d(TAG, "setUser: "+sUser.getId()+","+sUser.getToken());
    }

    public static User getUser(){
        return sUser;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        LitePal.initialize(this);
        addData();
        Intent intent = new Intent(this, AutoUpdateService.class);           //开启后台更新服务
        startService(intent);
    }

    public void addData(){
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(ADD_DATABASE, true)) {
            File file = new File(LitePal.getDatabase().getPath());
            try {
                InputStream inputStream = getAssets().open("weather_db.db");
                FileOutputStream out = new FileOutputStream(file);
                byte[] bytes = new byte[1024];
                int len;
                while ((len = inputStream.read(bytes)) != -1) {
                    out.write(bytes, 0, len);
                }
                out.flush();
                out.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putBoolean(ADD_DATABASE, false);
        editor.apply();
    }
}

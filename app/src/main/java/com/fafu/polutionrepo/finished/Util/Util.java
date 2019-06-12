package com.fafu.polutionrepo.finished.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.icu.util.Calendar;
import android.media.ExifInterface;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DialogTitle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fafu.polutionrepo.finished.Application.MyApplication;
import com.fafu.polutionrepo.finished.Areas.Capital;
import com.fafu.polutionrepo.finished.Areas.City;
import com.fafu.polutionrepo.finished.Areas.County;
import com.fafu.polutionrepo.finished.Areas.Province;
import com.fafu.polutionrepo.finished.Beans.Pollution;
import com.fafu.polutionrepo.finished.Beans.Topic;
import com.fafu.polutionrepo.finished.Beans.User;
import com.fafu.polutionrepo.finished.Beans.Weather;
import com.fafu.polutionrepo.finished.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Util {

    public static final String URL_PREFIX = "http://2u45589i03.wicp.vip";
    private static final String TAG = "LogUtil";
    private static List<String> list = new ArrayList<>();
    private static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");
    public static final MediaType IMAGE = MediaType.parse("image/jpeg");

    public static Headers addHeaders(User user){
        Headers.Builder builder = new Headers.Builder();
        builder.add("id", user.getId());
        builder.add("token", user.getToken());
        return builder.build();
    }

    public static void sendHttpOkRequest(String address, Callback callback){

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient client = builder.connectionPool(new ConnectionPool(10,1, TimeUnit.SECONDS)).build();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

    //发送获取图片的请求
    public static void sendImageRequest(String address,Callback callback){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient client = builder.connectionPool(new ConnectionPool(10,1, TimeUnit.SECONDS)).build();
        Headers.Builder headerBuilder = new Headers.Builder();
        headerBuilder.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.131 Safari/537.36");
        headerBuilder.add("Cookie", "PHPSESSID=vj7knm1d9rta92qd5o2lbcs9of; _ga=GA1.2.2092097863.1557384772; _gid=GA1.2.1434659293.1557384772; weilisessionid3=fb56f25cb3fa741bbfb75ad100c4b30a; wluuid=WLGEUST-69BE8F1F-DF5E-ED05-282D-EDF510DDF4D2; wlsource=tc_pc_home_search; ssoflag=0; _ga=GA1.3.2092097863.1557384772; _gid=GA1.3.1434659293.1557384772; qimo_seosource_e7dfc0b0-b3b6-11e7-b58e-df773034efe4=%E7%AB%99%E5%86%85; qimo_seokeywords_e7dfc0b0-b3b6-11e7-b58e-df773034efe4=; href=http%3A%2F%2Fstock.tuchong.com%2Fsearch%3Fsource%3Dtc_pc_home_search%26term%3D%25E7%25A6%258F%25E5%25B7%259E; accessId=e7dfc0b0-b3b6-11e7-b58e-df773034efe4; bad_ide7dfc0b0-b3b6-11e7-b58e-df773034efe4=11b5f822-7227-11e9-a360-5134b5a63ea5; nice_ide7dfc0b0-b3b6-11e7-b58e-df773034efe4=11b5f823-7227-11e9-a360-5134b5a63ea5; webp_enabled=0; _gat=1; pageViewNum=20");
        Headers headers = headerBuilder.build();
        Request request = new Request.Builder().headers(headers).url(address).build();
        client.newCall(request).enqueue(callback);
    }


    public static void sendLoginRequest(String address,String[] params,Callback callback){
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(20,TimeUnit.SECONDS).build();
        String content = toJson(params);
        Request request = new Request.Builder().url(address).post(RequestBody.create(JSON,content)).build();
        client.newCall(request).enqueue(callback);

    }

    public static void sendRegisterRequest(String address,Callback callback,Map<String,String> map){
        OkHttpClient client = new OkHttpClient();
        JSONObject jsonObject = new JSONObject(map);
        Request request = new Request.Builder().url(address).post(RequestBody.create(JSON, jsonObject.toString())).build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendUserNameRequest(String address,String username,Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address+username).build();
        client.newCall(request).enqueue(callback);
    }

    public static void getUserInfoRequest(String address,Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendPublishRequest(String address,User user,Object[] objects,Callback callback){
        OkHttpClient client = new OkHttpClient();
        Map<String,Object> map = new HashMap<>();
        map.put("latitude",  objects[0]);
        map.put("longtitude", objects[1]);
        map.put("pollutionType",objects[2]);
        map.put("severe",objects[3]);
        Headers headers = addHeaders(user);
        Request request =
                new Request.Builder().url(address).
                        post(RequestBody.create(JSON, new JSONObject(map).toString()))
                        .headers(headers).build();
        client.newCall(request).enqueue(callback);
    }

    public static void getPollutionRequest(String address,Callback callback){
        OkHttpClient client = new OkHttpClient();
        Map<String,Object> map = new HashMap<>();
        map.put("latitude", new double[]{0,90});
        map.put("longtitude", new double[]{-180,180});
        Request request =
                new Request.Builder().url(address).
                        post(RequestBody.create(JSON, new JSONObject(map).toString())).build();
        client.newCall(request).enqueue(callback);
    }

    public static void PublishTopicRequest(String address, String[] infos,Pollution pollution,Callback callback){
        User user = MyApplication.getUser();
        OkHttpClient client = new OkHttpClient();
        Headers headers = addHeaders(user);
        Map<String,String> map = new HashMap<>();
        map.put("accountId",user.getId());
        map.put("tag",pollution.getPollutionType());
        map.put("name",infos[0]);
        map.put("detail",infos[1]);
        map.put("picture",infos[2]);
        RequestBody body = RequestBody.create(JSON,new JSONObject(map).toString());
        Request request = new Request.Builder().headers(headers).url(address).post(body).build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendImageRequest(String address, byte[] imageByte, Callback callback){
        User user = MyApplication.getUser();
        if(user == null){
            Toast.makeText(MyApplication.getContext(), "您还未登录!", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "sendImageRequest: "+user.getId()+","+user.getToken());
        OkHttpClient client = new OkHttpClient();
        Headers headers = addHeaders(user);
        RequestBody body = RequestBody.create(IMAGE,imageByte);
        Request request = new Request.Builder().url(address).headers(headers).post(body).build();
        client.newCall(request).enqueue(callback);
    }

    public static void getTopicsRequest(String address,Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

    public static void addCommentsRequest(String address,int id,String content,Callback callback){
        User user = MyApplication.getUser();
        OkHttpClient client = new OkHttpClient();
        Map<String,Object> map = new HashMap<>();
        map.put("accountId",user.getId());
        map.put("topicId",id);
        map.put("content",content);
        RequestBody body = RequestBody.create(JSON,new JSONObject(map).toString());
        Headers headers = addHeaders(user);
        Request request = new Request.Builder().headers(headers).post(body).url(address).build();
        client.newCall(request).enqueue(callback);
    }

    public static void getTopicDetailRequest(String address,Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

    public static void updateUserRequest(String address,Callback callback){
        User user = MyApplication.getUser();
        OkHttpClient client = new OkHttpClient();
        Map<String,String> map = new HashMap<>();
        map.put("accountId", user.getId());
        map.put("nickname", user.getNickname());
        map.put("icon",user.getImageName());
        Headers headers = addHeaders(user);
        Request request =
                new Request.Builder().url(address).
                        post(RequestBody.create(JSON, new JSONObject(map).toString()))
                        .headers(headers).build();
        client.newCall(request).enqueue(callback);
    }

    public static String toJson(String[] params){
        Map<String,String> map = new HashMap<>();
        map.put("userName",params[0]);
        map.put("password", params[1]);
        JSONObject jsonObject = new JSONObject(map);
        return jsonObject.toString();
    }

    public static int getDensity(Context context){
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return (int) metrics.density;
    }

    public static float getRealDensity(Context context){
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics.density;
    }

    public static int getSPDensity(Context context){
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return (int) metrics.scaledDensity;
    }

    public static int getScreenWidth(Context context){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static String[] readAssetsTxtFile(String filename,Context context){
        try {
            InputStream inputStream = context.getAssets().open(filename+".txt");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String text = new String(buffer,"utf-8");
            String[] strs = text.split("\n");
            return strs;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getHour(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return hour;
    }

    public static int getYear(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        return year;
    }

    public static List<String> checkCityName(String s){
        list.clear();
        List<City> cities = LitePal.where("cityname like ?","%"+s+"%").find(City.class);
        List<County> counties = LitePal.where("countyname like ?","%"+s+"%").find(County.class);
        if(cities.size() == 0 && counties.size() == 0){
            list.add("无");
            return list;
        }else{
            if(cities.size() != 0){
                for(City city : cities){
                    String string = city.getCityName()+","+city.getProvinceName();
                    list.add(string);
                }
            }
            if(counties.size() != 0){
                for(County county : counties){
                    String string = county.getCountyName()+","+county.getCityName()+","+county.getProvinceName();
                    list.add(string);
                }
            }
        }
        HashSet set = new HashSet(list);
        list.clear();
        list.addAll(set);
        return list;
    }

    public static int status_code(String response){
        if(response != null){
            try {
                JSONObject jsonObject = new JSONObject(response);
                String code = jsonObject.getString("code");
                return Integer.parseInt(code);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public static int readPictureDegree(String path){
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation){
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap rotatingImageView(int angle,Bitmap bitmap){
        if(bitmap != null){
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        }
        return null;
    }


    public static class UserInfo {
        public static String id = "id";
        public static String username = "username";
        public static String token = "token";
        public static String phone = "phone";
        public static String email = "email";
        public static String age = "age";
        public static String date = "date";
        public static String nickname = "nickname";
    }

    public static byte[] writeBitmapIntoByte(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
        return outputStream.toByteArray();
    }

    public static Object[] judgePollution(String type){
        switch (type){
            case "WATER":
                return new Object[]{"水污染",R.drawable.water_pollution};
            case "AIR":
                return new Object[]{"空气污染",R.drawable.air_pollution};
            case "SOIL":
                return new Object[]{"土壤污染",R.drawable.soil_pollution};
            case "OTHER":
                return new Object[]{"其他污染",R.drawable.other_pollution};
            case "NORMAL":
                return new Object[]{"正常",R.drawable.normal};
            case "MIDDLE":
                return new Object[]{"中等",R.drawable.middle};
            case "BAD":
                return new Object[]{"差",R.drawable.bad};
            case "SERIOUS":
                return new Object[]{"严重",R.drawable.serious};
            case "SEVERE":
                return new Object[]{"非常严重",R.drawable.severe};
            default:
                return new Object[]{"未知",R.drawable.unknown_level};
        }
    }

    public static int judgePollutionIcon(String type){
        switch (type){
            case "WATER:NORMAL":
                return R.drawable.water_level1;
            case "WATER:MIDDLE":
                return R.drawable.water_level2;
            case "WATER:BAD":
                return R.drawable.water_level3;
            case "WATER:SERIOUS":
                return R.drawable.water_level4;
            case "WATER:SEVERE":
                return R.drawable.water_level5;
            case "AIR:NORMAL":
                return R.drawable.air_level1;
            case "AIR:MIDDLE":
                return R.drawable.air_level2;
            case "AIR:BAD":
                return R.drawable.air_level3;
            case "AIR:SERIOUS":
                return R.drawable.air_level4;
            case "AIR:SEVERE":
                return R.drawable.air_level5;
            case "SOIL:NORMAL":
                return R.drawable.soil_level1;
            case "SOIL:MIDDLE":
                return R.drawable.soil_level2;
            case "SOIL:BAD":
                return R.drawable.soil_level3;
            case "SOIL:SERIOUS":
                return R.drawable.soil_level4;
            case "SOIL:SEVERE":
                return R.drawable.soil_level5;
            case "OTHER:NORMAL":
                return R.drawable.other_pollution;
            case "OTHER:MIDDLE":
                return R.drawable.other_pollution;
            case "OTHER:BAD":
                return R.drawable.other_pollution;
            case "OTHER:SERIOUS":
                return R.drawable.other_pollution;
            case "OTHER:SEVERE":
                return R.drawable.other_pollution;
            default:
                return R.drawable.unknown_level;
        }
    }

    public static String searchCity(Context mContext,double latitude,double longitude,TextView CityView){
        String goalCityName = "";
        List<County> mCountyList = new ArrayList<>();
        int coarseLatitude = (int) latitude;
        int coarseLongitude = (int) longitude;
        List<County> counties = LitePal.where("latitude like ? and longitude like ?",coarseLatitude + "%",coarseLongitude + "%").find(County.class);
        List<County> counties2 = LitePal.where("latitude like ? and longitude like ?",coarseLatitude + "%",(coarseLongitude+1) + "%").find(County.class);
        List<County> counties3 = LitePal.where("latitude like ? and longitude like ?",coarseLatitude + "%",(coarseLongitude-1) + "%").find(County.class);
        List<County> counties4 = LitePal.where("latitude like ? and longitude like ?",(coarseLatitude+1) + "%",coarseLongitude + "%").find(County.class);
        List<County> counties5 = LitePal.where("latitude like ? and longitude like ?",(coarseLatitude-1) + "%",coarseLongitude + "%").find(County.class);
        mCountyList.addAll(counties);
        mCountyList.addAll(counties2);
        mCountyList.addAll(counties3);
        mCountyList.addAll(counties4);
        mCountyList.addAll(counties5);
        Log.d(TAG, "searchCity: "+mCountyList.size());
        if(mCountyList.size() == 0){
            Toast.makeText(mContext, "所属城市未知!", Toast.LENGTH_SHORT).show();
        }else{
            if(mCountyList.size() == 1){
                County county = mCountyList.get(0);
                if(CityView != null) {
                    CityView.setText(CityView.getText().toString() + county.getCityName());
                }
            }else{
                double minLatitudeOffset = Math.abs(latitude - mCountyList.get(0).getLatitude());
                double minLongitudeOffset = Math.abs(longitude - mCountyList.get(0).getLongitude());
                goalCityName = mCountyList.get(0).getCityName();
                for (int i = 1; i < mCountyList.size(); i++) {
                    County county = mCountyList.get(i);
                    double currentLatitude = Math.abs(latitude - county.getLatitude());
                    double currentLongitude = Math.abs(longitude - county.getLongitude());
                    if(currentLatitude < minLatitudeOffset && currentLongitude < minLongitudeOffset){
                        minLatitudeOffset = currentLatitude;
                        minLongitudeOffset = currentLongitude;
                        goalCityName = county.getCityName();
                        Log.d(TAG, "searchCity: "+ goalCityName);
                    }
                }
                if(TextUtils.isEmpty(goalCityName)){
                    Toast.makeText(mContext, "所属城市未知!", Toast.LENGTH_SHORT).show();
                }else{
                    if(CityView != null) {
                        CityView.setText(CityView.getText().toString() + goalCityName);
                    }
                }
            }
        }
        return goalCityName;
    }
}

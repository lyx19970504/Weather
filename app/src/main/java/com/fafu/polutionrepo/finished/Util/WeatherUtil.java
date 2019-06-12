package com.fafu.polutionrepo.finished.Util;

import android.text.TextUtils;
import android.util.Log;

import com.fafu.polutionrepo.finished.Application.MyApplication;
import com.fafu.polutionrepo.finished.Beans.Air;
import com.fafu.polutionrepo.finished.Beans.Comment;
import com.fafu.polutionrepo.finished.Beans.Image;
import com.fafu.polutionrepo.finished.Beans.LifeStyleIndex;
import com.fafu.polutionrepo.finished.Beans.Pollution;
import com.fafu.polutionrepo.finished.Beans.Topic;
import com.fafu.polutionrepo.finished.Beans.Weather;
import com.fafu.polutionrepo.finished.Beans.Weather10;
import com.fafu.polutionrepo.finished.Beans.Weather_industrial;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeatherUtil {

    private static final String TAG = "WeatherUtil";

    public static Weather handleWeatherResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
                String weatherContent = jsonArray.getJSONObject(0).toString();
                return new Gson().fromJson(weatherContent, Weather.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Weather_industrial handleWeatherResponse2(String response){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
                String weatherContent = jsonArray.getJSONObject(0).toString();
                return new Gson().fromJson(weatherContent, Weather_industrial.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static Air handleAirResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
                String airContent = jsonArray.getJSONObject(0).toString();
                return new Gson().fromJson(airContent, Air.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Weather10 handle10DaysResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
                String weatherContent = jsonArray.getJSONObject(0).toString();
                return new Gson().fromJson(weatherContent, Weather10.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static LifeStyleIndex handleLifeStyleResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
                String lifestyleContent = jsonArray.getJSONObject(0).toString();
                return new Gson().fromJson(lifestyleContent, LifeStyleIndex.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static List<Image> handleImageResponse(String response){
        List<Image> images = new ArrayList<>();
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray jsonArray = new JSONArray(response);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Image image = new Gson().fromJson(jsonObject.toString(),Image.class);
                    images.add(image);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return images;
    }

    public static List<Pollution> handlePublishResponse(String response){
        List<Pollution> pollutions = new ArrayList<>();
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray jsonArray = new JSONArray(response);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Pollution pollution = new Gson().fromJson(jsonObject.toString(), Pollution.class);
                    pollutions.add(pollution);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return pollutions;
    }

    public static List<Topic> handleTopics(String response){
        List<Topic> topics = new ArrayList<>();
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Topic topic = new Gson().fromJson(jsonObject.toString(),Topic.class);
                    topics.add(topic);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return topics;
    }


    public static Topic handleDetailTopics(String response){
        Topic topic = new Topic();
        Log.d(TAG, "handleDetailTopics: "+response);
        if(!TextUtils.isEmpty(response)){
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray comments = jsonObject.getJSONArray("comments");
                //解析评论内容
                List<Comment> commentList = new ArrayList<>();
                if(comments != null) {
                    for (int i = 0; i < comments.length(); i++) {
                        JSONObject object = comments.getJSONObject(i);
                        commentList.add(new Gson().fromJson(object.toString(),Comment.class));
                    }
                }
                topic.setComments(commentList);

                JSONObject topicObject = jsonObject.getJSONObject("topic");
                topic.setTopicId(topicObject.getInt("topicId"));
                topic.setImagePath(topicObject.getString("picture"));
                topic.setAccountId(topicObject.getString("accountId"));
                topic.setTitle(topicObject.getString("name"));
                topic.setShortInfo(topicObject.getString("detail"));
                topic.setTag(topicObject.getString("tag"));


                JSONObject pollutionObject = jsonObject.getJSONObject("pollution");
                if(pollutionObject.getInt("pollutionId") != 0) {
                    Pollution pollution = new Gson().fromJson(pollutionObject.toString(), Pollution.class);
                    Log.d(TAG, "handleDetailTopics: "+pollution.getLatitude()+","+pollution.getLongitude());
                    topic.setPollution(pollution);
                    String cityName = Util.searchCity(MyApplication.getContext(),pollution.getLatitude(),pollution.getLongitude(),null);
                    Log.d(TAG, "handleDetailTopics: "+cityName);
                    if(TextUtils.isEmpty(cityName)){
                        topic.setCityName("");
                    }else{
                        topic.setCityName(cityName);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return topic;
    }
}

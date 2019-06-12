package com.fafu.polutionrepo.finished.Util;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.fafu.polutionrepo.finished.Beans.Weather;
import com.fafu.polutionrepo.finished.R;
import com.github.matteobattilana.weather.PrecipType;
import com.github.matteobattilana.weather.WeatherView;
import com.github.matteobattilana.weather.WeatherViewSensorEventListener;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class AnimatorUtil {

    private static FrameLayout frameLayout;
    private static ObjectAnimator animator;
    private static ObjectAnimator animator2;
    private static AnimatorSet set;
    private static ImageView imageView;
    private static ImageView imageView2;
    private static int offset;
    private static FrameLayout.LayoutParams params1;
    private static FrameLayout.LayoutParams params2;
    private static int randomNum1;
    private static int randomNum2;
    private static Timer timer = new Timer(true);

    public static void clearLastAnimation(View view){
        timer.cancel();
        frameLayout = (FrameLayout) view.findViewById(R.id.animation_layout);
        frameLayout.removeAllViews();
    }

    public static void startSunAnimation(View view, Context context,Boolean isNight){
        frameLayout = (FrameLayout) view.findViewById(R.id.animation_layout);
        frameLayout.setBackgroundResource(isNight ? R.drawable.bg_fine_night : R.drawable.bg_fine_day);
        imageView = new ImageView(context);
        offset = context.getResources().getDimensionPixelSize(R.dimen.icon_size);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(offset, offset);
        params.setMargins(0, 250 * Util.getDensity(context), 0, 0);
        imageView.setImageResource(R.drawable.fire_balloon);
        imageView.setLayoutParams(params);
        frameLayout.addView(imageView,frameLayout.getChildCount()-1);
        animator = ObjectAnimator.ofFloat(imageView, "translationX",Util.getScreenWidth(context),-offset);
        animator.setDuration(60000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }

    public static void startCloudyAnimation(View view,Context context,Boolean isNight){
        frameLayout = (FrameLayout) view.findViewById(R.id.animation_layout);
        frameLayout.setBackgroundResource(isNight ? R.drawable.bg_cloudy_night : R.drawable.bg_cloudy_day);
        set = new AnimatorSet();
        set.setDuration(60000);
        set.setInterpolator(new LinearInterpolator());
        //第一朵白云
        imageView = new ImageView(context);
        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);  //白云图像属性
        imageView.setLayoutParams(params1);
        offset = context.getResources().getDimensionPixelSize(R.dimen.cloud1);
        params1.setMargins(0, offset, 0, 0);
        imageView.setImageResource(R.drawable.cloudy_day_1);
        frameLayout.addView(imageView);
        animator = ObjectAnimator.ofFloat(imageView, "translationX",-Util.getScreenWidth(context),Util.getScreenWidth(context));
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(60000);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();

        //第二朵白云
        imageView = new ImageView(context);
        imageView2 = new ImageView(context);
        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);  //白云图像属性
        imageView.setLayoutParams(params2);
        imageView2.setLayoutParams(params2);
        offset = context.getResources().getDimensionPixelSize(R.dimen.cloud2);
        params2.setMargins(0, offset, 0, 0);
        imageView.setImageResource(R.drawable.cloudy_day_2);
        imageView2.setImageResource(R.drawable.cloudy_day_2);
        frameLayout.addView(imageView);
        frameLayout.addView(imageView2);
        animator = ObjectAnimator.ofFloat(imageView, "translationX",Util.getScreenWidth(context));
        animator2 = ObjectAnimator.ofFloat(imageView2, "translationX",-Util.getScreenWidth(context),0);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator2.setRepeatMode(ValueAnimator.RESTART);
        animator2.setRepeatCount(ValueAnimator.INFINITE);
        set.playTogether(animator,animator2);
        set.start();

        //第三、四朵白云
        imageView = new ImageView(context);
        imageView2 = new ImageView(context);
        ImageView imageView1_1 = new ImageView(context);
        ImageView imageView2_2 = new ImageView(context);
        offset = context.getResources().getDimensionPixelSize(R.dimen.cloud3);
        FrameLayout.LayoutParams params3 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);  //白云图像属性
        params3.setMargins(0, offset, 0, 0);
        imageView.setImageResource(R.drawable.cloudy_day_3);
        imageView.setLayoutParams(params3);
        imageView1_1.setImageResource(R.drawable.cloudy_day_3);
        imageView1_1.setLayoutParams(params3);
        imageView2.setImageResource(R.drawable.cloudy_day_4);
        imageView2.setLayoutParams(params3);
        imageView2_2.setImageResource(R.drawable.cloudy_day_4);
        imageView2_2.setLayoutParams(params3);
        frameLayout.addView(imageView);
        frameLayout.addView(imageView2);
        frameLayout.addView(imageView1_1);
        frameLayout.addView(imageView2_2);
        animator = ObjectAnimator.ofFloat(imageView, "translationX",Util.getScreenWidth(context));
        animator2 = ObjectAnimator.ofFloat(imageView2, "translationX",Util.getScreenWidth(context));
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator2.setRepeatCount(ValueAnimator.INFINITE);
        animator2.setRepeatMode(ValueAnimator.RESTART);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(imageView1_1, "translationX",-Util.getScreenWidth(context),0);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(imageView2_2, "translationX",-Util.getScreenWidth(context),0);
        animator3.setRepeatCount(ValueAnimator.INFINITE);
        animator3.setRepeatMode(ValueAnimator.RESTART);
        animator4.setRepeatCount(ValueAnimator.INFINITE);
        animator4.setRepeatMode(ValueAnimator.RESTART);
        set.playTogether(animator,animator2,animator3,animator4);
        set.start();
    }

    public static void startSnowAnimation(View view,Context context){
        frameLayout = (FrameLayout) view.findViewById(R.id.animation_layout);
        frameLayout.setBackgroundResource(R.drawable.bg_snow);
        View snow_view = LayoutInflater.from(context).inflate(R.layout.snowfall_view,null );
        frameLayout.addView(snow_view);
    }

    public static void startSnowAndRainAnimation(View view,Context context){
        frameLayout = (FrameLayout) view.findViewById(R.id.animation_layout);
        frameLayout.setBackgroundResource(R.drawable.bg_snow);
        View snow_view = LayoutInflater.from(context).inflate(R.layout.snowfall_view,null );
        frameLayout.addView(snow_view);

        WeatherView weatherView = new WeatherView(context, null);
        weatherView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        frameLayout.addView(weatherView);
        //Optional
        weatherView.setPrecipType(PrecipType.RAIN);
        weatherView.setEmissionRate(100);
        weatherView.setSpeed(1000);
        weatherView.setAngle(-1);
        weatherView.setFadeOutPercent(1000);
        WeatherViewSensorEventListener sensorEventListener = new WeatherViewSensorEventListener(context, weatherView);
        sensorEventListener.start();
    }


    public static void startsRainAnimation(View view,Context context){
        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.animation_layout);
        frameLayout.setBackgroundResource(R.drawable.bg_rain1);
        WeatherView weatherView = new WeatherView(context, null);
        weatherView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        frameLayout.addView(weatherView);
        //Optional
        weatherView.setPrecipType(PrecipType.RAIN);
        weatherView.setEmissionRate(100);
        weatherView.setSpeed(1000);
        weatherView.setAngle(-1);
        weatherView.setFadeOutPercent(1000);
        WeatherViewSensorEventListener sensorEventListener = new WeatherViewSensorEventListener(context, weatherView);
        sensorEventListener.start();
    }

    public static void startmRainAnimation(View view,Context context){
        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.animation_layout);
        frameLayout.setBackgroundResource(R.drawable.bg_rain);
        WeatherView weatherView = new WeatherView(context, null);
        weatherView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        frameLayout.addView(weatherView);
        //Optional
        weatherView.setPrecipType(PrecipType.RAIN);
        weatherView.setEmissionRate(300);
        weatherView.setSpeed(3000);
        weatherView.setAngle(-3);
        weatherView.setFadeOutPercent(1000);
        WeatherViewSensorEventListener sensorEventListener = new WeatherViewSensorEventListener(context, weatherView);
        sensorEventListener.start();
    }

    public static void startlRainAnimation(View view,final Context context){
        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.animation_layout);
        frameLayout.setBackgroundResource(R.drawable.bg_thunder_storm);
        WeatherView weatherView = new WeatherView(context, null);
        weatherView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        frameLayout.addView(weatherView);
        //Optional
        weatherView.setPrecipType(PrecipType.RAIN);
        weatherView.setEmissionRate(500);
        weatherView.setSpeed(5000);
        weatherView.setAngle(-5);
        weatherView.setFadeOutPercent(1000);
        WeatherViewSensorEventListener sensorEventListener = new WeatherViewSensorEventListener(context, weatherView);
        sensorEventListener.start();

        //add lightning animation
        imageView = new ImageView(context);
        imageView2 = new ImageView(context);
        imageView.setImageResource(R.drawable.lightning_1);
        imageView2.setImageResource(R.drawable.lightning_2);
        int width = BitmapFactory.decodeResource(context.getResources(), R.drawable.lightning_1).getWidth();
        int height = BitmapFactory.decodeResource(context.getResources(), R.drawable.lightning_1).getHeight();
        params1 = new FrameLayout.LayoutParams(width,height);
        params2 = new FrameLayout.LayoutParams(width,height);
        randomNum1 = (new Random().nextInt(350) - 100) * Util.getDensity(context);
        params1.setMargins(randomNum1, 0, 0, 0);
        randomNum2 = (new Random().nextInt(350) - 100) * Util.getDensity(context);
        params2.setMargins(randomNum2, 0, 0, 0);
        frameLayout.addView(imageView);
        frameLayout.addView(imageView2);
        animator = ObjectAnimator.ofFloat(imageView, "alpha", 0f,1f);
        animator2 = ObjectAnimator.ofFloat(imageView, "alpha", 1f,0f);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(imageView2, "alpha", 0f,1f);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(imageView2, "alpha", 1f,0f);
        final AnimatorSet set = new AnimatorSet();
        set.setInterpolator(new AnticipateInterpolator());
        set.play(animator);
        set.play(animator2);
        set.play(animator3);
        set.play(animator4).after(500);
        set.setDuration(500);
        timer = new Timer(true);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        randomNum1 = (new Random().nextInt(350) - 100) * Util.getDensity(context);
                        randomNum2 = (new Random().nextInt(350) - 100) * Util.getDensity(context);
                        params1.setMargins(randomNum1, 0, 0, 0);
                        imageView.setLayoutParams(params1);
                        params2.setMargins(randomNum2, 0, 0, 0);
                        imageView2.setLayoutParams(params2);
                        set.start();
                    }
                });
            }
        };
        timer.schedule(task, 0, 3000);
    }

    public static void startRainLightAnimation(View view,final Context context){
        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.animation_layout);
        frameLayout.setBackgroundResource(R.drawable.bg_thunder_storm);
        WeatherView weatherView = new WeatherView(context, null);
        weatherView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        frameLayout.addView(weatherView);
        //Optional
        weatherView.setPrecipType(PrecipType.RAIN);
        weatherView.setEmissionRate(100);
        weatherView.setSpeed(1000);
        weatherView.setAngle(-1);
        weatherView.setFadeOutPercent(1000);
        WeatherViewSensorEventListener sensorEventListener = new WeatherViewSensorEventListener(context, weatherView);
        sensorEventListener.start();

        //add lightning animation
        imageView = new ImageView(context);
        imageView2 = new ImageView(context);
        imageView.setImageResource(R.drawable.lightning_1);
        imageView2.setImageResource(R.drawable.lightning_2);
        int width = BitmapFactory.decodeResource(context.getResources(), R.drawable.lightning_1).getWidth();
        int height = BitmapFactory.decodeResource(context.getResources(), R.drawable.lightning_1).getHeight();
        params1 = new FrameLayout.LayoutParams(width,height);
        params2 = new FrameLayout.LayoutParams(width,height);
        randomNum1 = (new Random().nextInt(350) - 100) * Util.getDensity(context);
        params1.setMargins(randomNum1, 0, 0, 0);
        randomNum2 = (new Random().nextInt(350) - 100) * Util.getDensity(context);
        params2.setMargins(randomNum2, 0, 0, 0);
        frameLayout.addView(imageView);
        frameLayout.addView(imageView2);
        animator = ObjectAnimator.ofFloat(imageView, "alpha", 0f,1f);
        animator2 = ObjectAnimator.ofFloat(imageView, "alpha", 1f,0f);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(imageView2, "alpha", 0f,1f);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(imageView2, "alpha", 1f,0f);
        final AnimatorSet set = new AnimatorSet();
        set.setInterpolator(new AnticipateInterpolator());
        set.play(animator);
        set.play(animator2);
        set.play(animator3);
        set.play(animator4).after(500);
        set.setDuration(500);
        timer = new Timer(true);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        randomNum1 = (new Random().nextInt(350) - 100) * Util.getDensity(context);
                        randomNum2 = (new Random().nextInt(350) - 100) * Util.getDensity(context);
                        params1.setMargins(randomNum1, 0, 0, 0);
                        imageView.setLayoutParams(params1);
                        params2.setMargins(randomNum2, 0, 0, 0);
                        imageView2.setLayoutParams(params2);
                        set.start();
                    }
                });
            }
        };
        timer.schedule(task, 0, 3000);
    }
}

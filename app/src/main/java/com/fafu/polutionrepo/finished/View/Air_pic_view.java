package com.fafu.polutionrepo.finished.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.fafu.polutionrepo.finished.Util.Util;

public class Air_pic_view extends View {

    private static final String TAG = "Air_pic_view";
    private Paint mAQIPaint;
    private Paint mArcPaint;      //圆弧画笔
    private String AQIvalue;
    private float mCircleXY;
    private int mMeasureWidth;
    private int mMeasureHeight;
    private int startAngle;
    private int sweepAngle;
    private String airCondition;
    private int aqiMaxValue = 500;    //aqi最大值
    private int aqiCount = 38;   //一共有38条
    private int count;

    public void setAQIvalue(String value){
        AQIvalue = value;
    }

    public void setAirCondition(String airCondition){
        this.airCondition = airCondition;
    }

    public Air_pic_view(Context context) {
        super(context);
    }

    public Air_pic_view(Context context,  AttributeSet attrs) {
        super(context, attrs);
    }

    public Air_pic_view(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMeasureWidth = getMeasureWidth(widthMeasureSpec);
        mMeasureHeight = getMeasureHeigtht(heightMeasureSpec);
        setMeasuredDimension(mMeasureWidth, mMeasureHeight);
        initView();
    }

    public int getMeasureHeigtht(int heightMeasureSpec){
        int result;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if(mode == MeasureSpec.EXACTLY){
            result = size;
        }else{
            result = 500 * Util.getDensity(getContext());
            if(size == MeasureSpec.AT_MOST){
                result = Math.min(result, size);
            }
        }
        return result;
    }



    public int getMeasureWidth(int widthMeasureSpec){
        int result;
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        if(mode == MeasureSpec.EXACTLY){
            result = size;
        }else{
            result = 500 * Util.getDensity(getContext());
            if(mode == MeasureSpec.AT_MOST){
                result = Math.min(result, size);
            }
        }
        return result;
    }

    public void initView(){
        float length;
        if(mMeasureWidth >= mMeasureHeight){
            length = mMeasureHeight;
        }else{
            length = mMeasureWidth;
        }
        mAQIPaint = new Paint();
        mCircleXY = length / 2;
        mAQIPaint.setColor(Color.WHITE);
        mAQIPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mAQIPaint.setTextAlign(Paint.Align.CENTER);

        //圆弧画笔初始化
        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStrokeWidth(30 * Util.getDensity(getContext()));
        mArcPaint.setAlpha(90);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        drawValue(canvas);
        drawCircle(canvas);
    }

    public void drawValue(Canvas canvas){
        mAQIPaint.setTextSize(100);
        canvas.drawText("AQI", mCircleXY, mCircleXY - 80 *Util.getDensity(getContext()), mAQIPaint);
        mAQIPaint.setTextSize(150);
        canvas.drawText(AQIvalue, mCircleXY, mCircleXY, mAQIPaint);
        mAQIPaint.setTextSize(100);
        canvas.drawText(airCondition, mCircleXY, mCircleXY + 80 * Util.getDensity(getContext()), mAQIPaint);
    }

    public void drawCircle(Canvas canvas){
        float length1= (float) (0.1 * mMeasureWidth);
        float length2= (float) (0.9 * mMeasureWidth);
        RectF rectF = new RectF(length1,length1,length2,length2);
        mArcPaint.setColor(Color.WHITE);
        startAngle = 120;
        sweepAngle = 300;
        canvas.drawArc(rectF, startAngle, sweepAngle, false, mArcPaint);
        count = Integer.parseInt(AQIvalue) * aqiCount / aqiMaxValue;     //记录aqi值
        for(int i=startAngle-3;i<=startAngle+sweepAngle-3;i++) {
            if(i % 8 == 0) {
                mArcPaint.setColor(0xFFD9D6D6);
                canvas.drawArc(rectF, i, 3, false, mArcPaint);
                if(count > 0) {
                    if(count <= 8){
                        mArcPaint.setColor(0xFF00FF00);
                    }else if(count <= 16){
                        mArcPaint.setColor(0xFF995715);
                    }else if(count <= 24){
                        mArcPaint.setColor(0xFF5B0C0C);
                    }else if(count <= 32){
                        mArcPaint.setColor(0xFF0A0D69);
                    }else{
                        mArcPaint.setColor(0xFF261845);
                    }
                    canvas.drawArc(rectF, i, 3, false, mArcPaint);
                }
                count-- ;
            }
        }
    }
}

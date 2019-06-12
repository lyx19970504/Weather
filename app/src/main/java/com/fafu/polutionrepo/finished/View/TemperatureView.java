package com.fafu.polutionrepo.finished.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.fafu.polutionrepo.finished.Util.Util;

public class TemperatureView extends View {
    private static final String TAG = "TemperatureView";

    private int minValue;
    private int maxValue;
    private int currentValue;
    private int lastValue;
    private int nextValue;
    private Paint mPaint;
    private int viewHeight;
    private int viewWidth;
    private int pointX;
    private int pointY;
    private boolean isDrawLeftLine;
    private boolean isDrawRightLine;
    private int pointTopY = (int) (40 * Util.getRealDensity(getContext()));
    private int pointBottomY = (int) (200 * Util.getRealDensity(getContext()));
    private int mMiddleValue;

    public TemperatureView(Context context) {
        super(context);
    }

    public TemperatureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TemperatureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setMinValue(int minValue){
        this.minValue = minValue;
    }

    public void setMaxValue(int maxValue){
        this.maxValue = maxValue;
    }

    public void setCurrentValue(int currentValue){
        this.currentValue = currentValue;
    }

    public void setDrawLeftLine(boolean isDrawLeftLine){
        this.isDrawLeftLine = isDrawLeftLine;
    }

    public void setDrawRightLine(boolean isDrawRightLine){
        this.isDrawRightLine = isDrawRightLine;
    }

    public void setLastValue(int lastValue){
        this.lastValue = lastValue;
    }

    public void setNextValue(int nextValue){
        this.nextValue = nextValue;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mDefaultWidth = (int) (1080 * Util.getRealDensity(getContext()));
        int mDefaultHeight = (int) (200 * Util.getRealDensity(getContext()));
        int widthSpaceSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpaceSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if(widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(mDefaultWidth,mDefaultHeight);
        }else if(widthMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(mDefaultWidth, heightSpaceSize);
        }else if(heightMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(widthSpaceSize, mDefaultHeight);
        }
        viewHeight = getMeasuredHeight();
        viewWidth = getMeasuredWidth();
        pointX = viewWidth / 2;
        Log.d(TAG, "onMeasure: " + minValue + "," + maxValue);
        Log.d(TAG, "onMeasure: " + viewHeight);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mMiddleValue = (pointTopY + pointBottomY) / 2;
        pointY = mMiddleValue + (int) ((pointBottomY-pointTopY) * 1f / (maxValue - minValue) * ((maxValue + minValue) / 2 - currentValue));

        Log.d(TAG, "onDraw: " + pointY);
        mPaint = new Paint();
        drawGraph(canvas);
        drawValue(canvas);
        drawPoint(canvas);
    }

    private void drawValue(Canvas canvas){
        mPaint.setTextSize(40);
        setTextColor();
        mPaint.setStrokeWidth(0);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(currentValue+"°",pointX , pointY - 20, mPaint);
    }

    public void setTextColor(){
        if(currentValue <= 10 && currentValue >= 0){
            mPaint.setColor(Color.BLUE);
        }else if(currentValue <= 20 && currentValue > 10){
            mPaint.setColor(Color.GREEN);
        }else if(currentValue <= 30 && currentValue > 20){
            mPaint.setColor(0xFFFF8000);
        }else if(currentValue <= 40 && currentValue > 30){
            mPaint.setColor(Color.RED);
        }
    }

    public void drawPoint(Canvas canvas){
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(pointX, pointY, 10, mPaint);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(pointX, pointY, 5, mPaint);
    }

    public void drawGraph(Canvas canvas){
        mPaint.setColor(0xFF24C3F1);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(3);
        mPaint.setAntiAlias(true);    //设置抗锯齿
        if(isDrawLeftLine){
            float middleValue = currentValue - (currentValue - lastValue) / 2f;

            float middleY = mMiddleValue + (int) ((pointBottomY-pointTopY) * 1f / (maxValue - minValue) * ((maxValue + minValue) / 2 - middleValue));
            canvas.drawLine(0, middleY, pointX, pointY, mPaint);
        }
        if(isDrawRightLine){
            float middleValue = currentValue - (currentValue - nextValue) / 2f;
            float middleY = mMiddleValue + (int) ((pointBottomY-pointTopY) * 1f / (maxValue - minValue) * ((maxValue + minValue) / 2 - middleValue));
            canvas.drawLine(pointX, pointY, viewWidth, middleY, mPaint);
        }
    }
}

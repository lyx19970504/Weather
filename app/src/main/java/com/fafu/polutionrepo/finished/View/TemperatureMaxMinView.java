package com.fafu.polutionrepo.finished.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.fafu.polutionrepo.finished.Util.Util;

public class TemperatureMaxMinView extends View {

    private static final String TAG = "TemperatureMaxMinView";
    private int MaxminValue;
    private int MaxmaxValue;
    private int MinminValue;
    private int MinmaxValue;
    private int currentMaxValue;
    private int currentMinValue;
    private int lastMaxValue;
    private int nextMaxValue;
    private int lastMinValue;
    private int nextMinValue;
    private Paint mPaint;
    private int viewHeight;
    private int viewWidth;
    private int pointX;
    private int pointMaxY;
    private int pointMinY;
    private boolean isDrawLeftLine;
    private boolean isDrawRightLine;
    private int pointTopY = 0;
    private int pointBottomY = 125;
    private int offset;
    private int MinTempOffset;          //最小温度曲线的向下偏移量

    public TemperatureMaxMinView(Context context) {
        super(context);
    }

    public TemperatureMaxMinView(Context context,  AttributeSet attrs) {
        super(context, attrs);
    }

    public TemperatureMaxMinView(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMaxMinValue(int minValue){
        this.MaxminValue = minValue;
    }

    public void setMaxMaxValue(int maxValue){
        this.MaxmaxValue = maxValue;
    }

    public void setMinMinValue(int minValue){
        MinminValue = minValue;
    }

    public void setMinMaxValue(int maxValue){
        MinmaxValue = maxValue;
    }

    public void setCurrentMaxValue(int currentValue){
        this.currentMaxValue = currentValue;
    }

    public void setCurrentMinValue(int currentValue){
        currentMinValue = currentValue;
    }

    public void setDrawLeftLine(boolean isDrawLeftLine){
        this.isDrawLeftLine = isDrawLeftLine;
    }

    public void setDrawRightLine(boolean isDrawRightLine){
        this.isDrawRightLine = isDrawRightLine;
    }

    public void setLastMaxValue(int lastValue){
        this.lastMaxValue = lastValue;
    }

    public void setNextMaxValue(int nextValue){
        this.nextMaxValue = nextValue;
    }

    public void setLastMinValue(int lastValue){
        this.lastMinValue = lastValue;
    }

    public void setNextMinValue(int nextValue){
        this.nextMinValue = nextValue;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
        viewHeight = getMeasuredHeight();
        viewWidth = getMeasuredWidth();
        offset = (60 - (7 * (20-MaxmaxValue))) * Util.getDensity(getContext());
        MinTempOffset = 120 * Util.getDensity(getContext());
        pointX = viewWidth / 2;
    }

    private int measureWidth(int widthMeasureSpec){
        int result;
        int specMode = MeasureSpec.getMode(widthMeasureSpec);     //获取测量模式
        int specSize = MeasureSpec.getSize(widthMeasureSpec);     //获取测量大小

        if(specMode == MeasureSpec.EXACTLY){          //具体数值或者MATCH_PARENT属性时使用这个模式
            result = specSize;
        }else{
            result = 100 *Util.getDensity(getContext());     //不指定测量模式，想多大就多大
            if(specMode == MeasureSpec.AT_MOST){     //当wrap_parent使用这个模式
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private int measureHeight(int heightMeasureSpec){
        int result;
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        if(specMode == MeasureSpec.EXACTLY){
            result = specSize;
        }else{
            result = 300 * Util.getDensity(getContext());
            if(specMode == MeasureSpec.AT_MOST){
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        pointMaxY = (int) ((pointBottomY-pointTopY) * 1f / (MaxmaxValue - MaxminValue) * (MaxmaxValue - currentMaxValue + MaxminValue) + pointTopY) - offset;
        pointMinY = (int) ((pointBottomY-pointTopY) * 1f / (MinmaxValue - MinminValue) * (MinmaxValue - currentMinValue + MinminValue) + pointTopY) - offset;
        mPaint = new Paint();
        drawGraph(canvas);
        drawValue(canvas);
        drawPoint(canvas);
    }

    private void drawValue(Canvas canvas){
        mPaint.setTextSize(40);
        setTextColor(currentMaxValue);

        mPaint.setStrokeWidth(0);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(currentMaxValue+"°",pointX , pointMaxY - 10*Util.getDensity(getContext()), mPaint);
        setTextColor(currentMinValue);
        canvas.drawText(currentMinValue+"°", pointX, pointMinY+150*Util.getDensity(getContext()), mPaint);
    }

    public void setTextColor(int currentValue){
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
        canvas.drawCircle(pointX, pointMaxY, 10, mPaint);    //高温点
        canvas.drawCircle(pointX, pointMinY+MinTempOffset, 10, mPaint);    //低温点
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(pointX, pointMaxY, 5, mPaint);
        canvas.drawCircle(pointX, pointMinY+MinTempOffset, 5, mPaint);
    }

    public void drawGraph(Canvas canvas){
        mPaint.setColor(0xFF24C3F1);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(3);
        mPaint.setAntiAlias(true);    //设置抗锯齿
        float middleMaxValue;
        float middleMinValue;
        float middleY;
        if(isDrawLeftLine){
            middleMaxValue = currentMaxValue - (currentMaxValue - lastMaxValue) / 2f;
            middleY = (pointBottomY-pointTopY) * 1f / (MaxmaxValue - MaxminValue) * (MaxmaxValue - middleMaxValue + MaxminValue) + pointTopY - offset;
            canvas.drawLine(0, middleY, pointX, pointMaxY, mPaint);
            middleMinValue = currentMinValue - (currentMinValue - lastMinValue) / 2f;
            middleY = (pointBottomY-pointTopY) * 1f / (MinmaxValue - MinminValue) * (MinmaxValue - middleMinValue + MinminValue) + pointTopY - offset;
            canvas.drawLine(0, middleY+MinTempOffset, pointX, pointMinY+MinTempOffset, mPaint);
        }
        if(isDrawRightLine){
            middleMaxValue = currentMaxValue - (currentMaxValue - nextMaxValue) / 2f;
            middleY = (pointBottomY - pointTopY) * 1f / (MaxmaxValue-MaxminValue) * (MaxmaxValue + MaxminValue - middleMaxValue) + pointTopY - offset;
            canvas.drawLine(pointX, pointMaxY, viewWidth, middleY, mPaint);
            middleMinValue = currentMinValue - (currentMinValue - nextMinValue) / 2f;
            middleY = (pointBottomY - pointTopY) * 1f / (MinmaxValue-MinminValue) * (MinmaxValue + MinminValue - middleMinValue) + pointTopY - offset;
            canvas.drawLine(pointX, pointMinY+MinTempOffset, viewWidth, middleY+MinTempOffset, mPaint);
        }
    }
}

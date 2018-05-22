package com.lwang.customview.stepqqview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.lwang.customview.R;

/**
 * @Author lwang
 * @Date 2018/5/22 21:58
 * @Description 仿QQ运动步数
 */

public class StepQQView extends View {

    private int mOuterColor;
    private int mInnerColor;
    private float mBorderWidth;
    private int mStepTextSize;
    private int mStepTextColor;
    private Paint mOuterPaint, mInnerPaint, mTextPaint;
    private int mStepMax;
    private int mCurrentStep;

    //通过代码new出来时调用
    public StepQQView(Context context) {
        super(context);
    }

    //通过布局使用时调用
    public StepQQView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //获取自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StepQQView);
        mOuterColor = typedArray.getColor(R.styleable.StepQQView_outerColor, mOuterColor);
        mInnerColor = typedArray.getColor(R.styleable.StepQQView_innerColor, mInnerColor);
        mBorderWidth = typedArray.getDimension(R.styleable.StepQQView_borderWidth, mBorderWidth);
        mStepTextSize = typedArray.getDimensionPixelOffset(R.styleable.StepQQView_stepTextSize, mStepTextSize);
        mStepTextColor = typedArray.getColor(R.styleable.StepQQView_stepTextColor, mStepTextColor);

        //释放该实例，从而使其可被其他模块复用
        //从源码可看出：程序在运行时维护了一个 TypedArray的池，程序调用时，会向该池中请求一个实例，所以用完之后，即使释放
        typedArray.recycle();

        mOuterPaint = getPaintByColor(mOuterColor);
        mInnerPaint = getPaintByColor(mInnerColor);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mStepTextColor);
        mTextPaint.setTextSize(mStepTextSize);
    }

    /**
     * 根据不同的颜色获取画笔
     */
    private Paint getPaintByColor(int color) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStrokeWidth(mBorderWidth);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);//画出空心
        return paint;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        //设置view的大小，宽高不一致时取最小值，确保为正方形
        setMeasuredDimension(width > height ? height : width, width > height ? height : width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //画外圆弧
        RectF rectF = new RectF(mBorderWidth / 2, mBorderWidth / 2,
                getWidth() - mBorderWidth / 2, getHeight() - mBorderWidth / 2);

        /**
         * oval :指定圆弧的外轮廓矩形区域(形状和大小的范围)。
         startAngle: 圆弧起始角度，单位为度。
         sweepAngle: 圆弧扫过的角度，顺时针方向，单位为度,从右中间开始为零度。
         useCenter: 如果为True时，在绘制圆弧时将圆心包括在内，通常用来绘制扇形。关键是这个变量，下面将会详细介绍。
         paint: 绘制圆弧的画板属性，如颜色，是否填充等。
         */
        canvas.drawArc(rectF, 135, 270, false, mOuterPaint);

        //画内圆弧  百分比，不能写死
        if (mStepMax == 0) return;

        float sweepAngle = (float) mCurrentStep / mStepMax;
        canvas.drawArc(rectF, 135, sweepAngle * 270, false, mInnerPaint);


        //画文字
        String stepText = mCurrentStep + "步";

        Rect textBounds = new Rect();
        mTextPaint.getTextBounds(stepText, 0, stepText.length(), textBounds);
        int dx = getWidth() / 2 - textBounds.width() / 2;

        //基线 baseLine
        Paint.FontMetricsInt anInt = mTextPaint.getFontMetricsInt();
        int dy = (anInt.bottom - anInt.top) / 2 - anInt.bottom;
        int baseLine = getHeight() / 2 + dy;

        /**
         * text:要绘制的文字
         * x：绘制原点x坐标
         * y：绘制原点y坐标
         * paint:用来做画的画笔
         */
        canvas.drawText(stepText, dx, baseLine, mTextPaint);
    }

    /**
     * 设置最大步数
     * @param stepMax
     */
    public synchronized void setStepMax(int stepMax){
        this.mStepMax = stepMax;
    }

    /**
     * 设置当前步数
     * @param cuttentMax
     */
    public synchronized void setCuttentStep(int cuttentMax){
        this.mCurrentStep = cuttentMax;
        invalidate(); //不断去绘制-->调用onDraw()方法
    }

}

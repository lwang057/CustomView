package com.lwang.customview.colortrackview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.lwang.customview.R;

import static com.lwang.customview.colortrackview.ColorTrackView.Direction.LEFT_TO_RIGHT;

/**
 * @Author lwang
 * @Date 2018/5/22 22:29
 * @Description 自定义字体变色View 继承TextView
 */

public class ColorTrackView extends TextView {

    // 默认的字体颜色的画笔
    private Paint mOriginPaint;
    // 改变的字体颜色的画笔
    private Paint mChangePaint;
    private float mCurrentProgress = 0.0f;
    private Direction mDirection = LEFT_TO_RIGHT;

    public enum Direction {
        LEFT_TO_RIGHT, RIGHT_TO_LEFT
    }

    public ColorTrackView(Context context) {
        super(context, null);
    }

    public ColorTrackView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);

        //获取自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorTrackTextView);
        int originColor = typedArray.getColor(R.styleable.ColorTrackTextView_originColor, getTextColors().getDefaultColor());
        int changeColor = typedArray.getColor(R.styleable.ColorTrackTextView_changeColor, getTextColors().getDefaultColor());

        //释放该实例，从而使其可被其他模块复用
        //从源码可看出：程序在运行时维护了一个 TypedArray的池，程序调用时，会向该池中请求一个实例，所以用完之后，即使释放
        typedArray.recycle();

        mOriginPaint = getPaintByColor(originColor);
        mChangePaint = getPaintByColor(changeColor);
    }

    /**
     * 获取画笔根据不同的颜色
     */
    private Paint getPaintByColor(int color) {
        Paint paint = new Paint();
        // 抗锯齿
        paint.setAntiAlias(true);
        // 仿抖动
        paint.setDither(true);
        paint.setColor(color);
        // 字体的大小设置为TextView的文字大小
        paint.setTextSize(getTextSize());
        return paint;
    }


    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas); 不能使用super，自己画

        //根据进度把中间值算出来
        int middle = (int) (mCurrentProgress * getWidth());

        if (mDirection == LEFT_TO_RIGHT) { //从左画到右
            //画变色的
            drawText(canvas, 0, middle, mChangePaint);

            //画不变色的
            drawText(canvas, middle, getWidth(), mOriginPaint);
        } else { //从右画到左
            //画变色的
            drawText(canvas, getWidth() - middle, getWidth(), mChangePaint);

            //画不变色的
            drawText(canvas, 0, getWidth() - middle, mOriginPaint);
        }
    }


    /**
     * 绘制Text
     *
     * @param canvas
     * @param start
     * @param end
     * @param paint
     */
    private void drawText(Canvas canvas, int start, int end, Paint paint) {

        canvas.save();

        // 通过Rect可以进行裁剪，  左边用一个画笔去画，右边用一个画笔去画
        Rect rect = new Rect(start, 0, end, getHeight());
        canvas.clipRect(rect);

        String text = getText().toString();
        //获取 X = 控件宽度-字体宽度
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int x = getWidth() / 2 - bounds.width() / 2;

        //基线baseLine
        Paint.FontMetricsInt metricsInt = paint.getFontMetricsInt();
        int dy = (metricsInt.bottom - metricsInt.top) / 2 - metricsInt.bottom;
        int baseLine = getHeight() / 2 + dy;

        canvas.drawText(text, x, baseLine, paint);
        canvas.restore();
    }

    /**
     * 设置朝向
     *
     * @param direction
     */
    public void setDirection(Direction direction) {
        this.mDirection = direction;
    }

    /**
     * 设置当前进度
     *
     * @param currentProgress
     */
    public void setCurrentProgress(float currentProgress) {
        this.mCurrentProgress = currentProgress;
        invalidate();
    }
}

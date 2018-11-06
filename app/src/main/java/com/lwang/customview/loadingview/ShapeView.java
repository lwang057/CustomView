package com.lwang.customview.loadingview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.shapes.Shape;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.lwang.customview.R;

/**
 * @author lwang
 * @date 2018/11/05
 * @description 自定义View（画圆、正方形、三角形）
 */
public class ShapeView extends View {

    // 默认开始画圆
    private Shape mCurrentShape = Shape.Circle;
    private Paint mPaint;
    private Path mPath;

    public ShapeView(Context context) {
        super(context);
    }

    public ShapeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        // 设置抗锯齿
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 只保证是正方形
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(Math.min(width, height), Math.min(width, height)); //Math.min() 比较得出参数中的最小值
    }

    @Override
    protected void onDraw(Canvas canvas) {
        switch (mCurrentShape) {
            case Circle:
                // 画圆形
                int center = getWidth() / 2;
                mPaint.setColor(ContextCompat.getColor(getContext(), R.color.circle));
                canvas.drawCircle(center, center, center, mPaint);
                break;
            case Square:
                // 画正方形
                mPaint.setColor(ContextCompat.getColor(getContext(), R.color.rect));
                canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
                break;
            case Triangle:
                // 画三角  Path 画路线
                mPaint.setColor(ContextCompat.getColor(getContext(), R.color.triangle));
                if (mPath == null) {
                    mPath = new Path();
                    mPath.moveTo(getWidth() / 2, 0);// 此点为多边形的起点
                    mPath.lineTo(0, (float) ((getWidth() / 2) * Math.sqrt(3)));
                    mPath.lineTo(getWidth(), (float) ((getWidth() / 2) * Math.sqrt(3)));

                    mPath.close();// 使这些路线闭合，构成封闭的多边形
                }
                canvas.drawPath(mPath, mPaint);
                break;
            default:
                break;
        }
    }

    /**
     * 不断重新绘制形状
     */
    public void changeCurrentShape() {
        switch (mCurrentShape) {
            case Circle:
                mCurrentShape = Shape.Square;
                break;
            case Square:
                mCurrentShape = Shape.Triangle;
                break;
            case Triangle:
                mCurrentShape = Shape.Circle;
                break;
            default:
                break;
        }
        invalidate();
    }

    /**
     * 获取当前Shape
     *
     * @return
     */
    public Shape getCurrentShape() {
        return mCurrentShape;
    }

    public enum Shape {
        /**
         * 圆，正方形，三角形
         */
        Circle,
        Square,
        Triangle
    }

}

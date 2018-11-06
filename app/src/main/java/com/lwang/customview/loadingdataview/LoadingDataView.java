package com.lwang.customview.loadingdataview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.LinearInterpolator;

import com.lwang.customview.R;

/**
 * @author lwang
 * @date 2018/11/06
 * @description 仿雅虎新闻数据加载动画
 */
public class LoadingDataView extends View {

    private boolean mInitParams = false;
    // 旋转动画执行的时间
    private final long ROTATION_ANIMATION_TIME = 1400;
    // 当前大圆旋转的角度（弧度）
    private float mCurrentRotationAngle = 0F;
    // 小圆的颜色列表
    private int[] mCircleColor;
    // 大圆里面包含很多小圆的半径 = 整宽度的 1/4
    private float mRotationRadius;
    // 每个小圆的半径 = 大圆半径的 1/8
    private float mCircleRadius;
    // 绘制所有效果的画笔
    private Paint mPaint;
    // 中心点
    private int mCenterX, mCenterY;
    // 屏幕对角线的一半
    private float mDiagonalDist;
    // 空心圆初始半径
    private float mHoleRadius = 0F;
    // 代表当前状态所画动画
    private LoadingState mLoadingState;
    // 当前大圆的半径
    private float mCurrentRotationRadius = mRotationRadius;
    // 整体的颜色背景
    private int mSplashColor = Color.WHITE;

    public LoadingDataView(Context context) {
        super(context);
    }

    public LoadingDataView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        // 小圆的颜色列表
        mCircleColor = context.getResources().getIntArray(R.array.splash_circle_colors);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!mInitParams) {
            initParams();
        }
        if (mLoadingState == null) {
            mLoadingState = new RotationState();
        }
        mLoadingState.draw(canvas);
    }

    private void initParams() {
        // getMeasuredWidth()获取的是view原始的大小，也就是这个view在XML文件中配置或者是代码中设置的大小
        // getWidth（）获取的是这个view最终显示的大小，这个大小有可能等于原始的大小也有可能不等于原始大小
        // 获取旋转大圆圈的半径
        mRotationRadius = getMeasuredWidth() / 4;
        // 获取每个小圆的半径
        mCircleRadius = mRotationRadius / 8;

        // 设置画笔，防抖动、抗锯齿
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);

        mCenterX = getMeasuredWidth() / 2;
        mCenterY = getMeasuredHeight() / 2;
        mDiagonalDist = (float) Math.sqrt(mCenterX * mCenterX + mCenterY * mCenterY); //Math.sqrt() 得出参数中的开方值

        mInitParams = true;
    }

    /**
     * 消失
     */
    public void disAppear() {
        // 关闭旋转动画
        if (mLoadingState instanceof RotationState) {
            RotationState rotationState = (RotationState) mLoadingState;
            rotationState.cancel();
        }
        // 开始聚合动画
        mLoadingState = new MergeState();
    }

    /**
     * 1.旋转动画
     */
    private class RotationState extends LoadingState {

        private ValueAnimator mAnimator;

        public RotationState() {
            // 搞一个变量不断的去改变 打算采用属性动画 旋转的是 0 - 360
            mAnimator = ObjectAnimator.ofFloat(0f, 2 * (float) Math.PI);
            mAnimator.setDuration(ROTATION_ANIMATION_TIME);

            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    mCurrentRotationAngle = (float) valueAnimator.getAnimatedValue();
                    // 重新绘制
                    invalidate();
                }
            });

            // 线性差值器
            mAnimator.setInterpolator(new LinearInterpolator());
            // 不断反复执行
            mAnimator.setRepeatCount(-1);
            mAnimator.start();
        }

        @Override
        public void draw(Canvas canvas) {
            // 画一个背景 白色
            canvas.drawColor(mSplashColor);
            // 画六个圆  每份角度
            double percentAngle = Math.PI * 2 / mCircleColor.length;
            for (int i = 0; i < mCircleColor.length; i++) {
                mPaint.setColor(mCircleColor[i]);
                // 当前的角度 = 初始角度 + 旋转的角度
                double currentAngle = mCurrentRotationAngle + percentAngle * i;
                int cx = (int) (mCenterX + mRotationRadius * Math.cos(currentAngle)); //返回指定double类型参数的余弦值
                int cy = (int) (mCenterY + mRotationRadius * Math.sin(currentAngle)); //返回指定double类型参数的正弦值
                canvas.drawCircle(cx, cy, mCircleRadius, mPaint);
            }
        }

        public void cancel() {
            mAnimator.cancel();
        }
    }

    /**
     * 2.聚合动画
     */
    private class MergeState extends LoadingState {

        private ValueAnimator mAnimator;

        public MergeState() {
            mAnimator = ObjectAnimator.ofFloat(mRotationRadius, 0);
            mAnimator.setDuration(ROTATION_ANIMATION_TIME / 2);
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCurrentRotationRadius = (float) animation.getAnimatedValue();// 最大半径到 0
                    // 重新绘制
                    invalidate();
                }
            });
            // 开始的时候向后然后向前甩
            mAnimator.setInterpolator(new AnticipateInterpolator(5f));
            // 等聚合完毕画展开
            mAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoadingState = new ExpendState();
                }
            });
            mAnimator.start();
        }

        @Override
        public void draw(Canvas canvas) {
            // 画一个背景 白色
            canvas.drawColor(mSplashColor);
            // 画六个圆  每份角度
            double percentAngle = Math.PI * 2 / mCircleColor.length;
            for (int i = 0; i < mCircleColor.length; i++) {
                mPaint.setColor(mCircleColor[i]);
                // 当前的角度 = 初始角度 + 旋转的角度
                double currentAngle = mCurrentRotationAngle + percentAngle * i;
                int cx = (int) (mCenterX + mCurrentRotationRadius * Math.cos(currentAngle));
                int cy = (int) (mCenterY + mCurrentRotationRadius * Math.sin(currentAngle));
                canvas.drawCircle(cx, cy, mCircleRadius, mPaint);
            }
        }
    }

    /**
     * 3.展开动画
     */
    private class ExpendState extends LoadingState {

        private ValueAnimator mAnimator;

        public ExpendState() {
            mAnimator = ObjectAnimator.ofFloat(0, mDiagonalDist);
            mAnimator.setDuration(ROTATION_ANIMATION_TIME / 2);
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    mHoleRadius = (float) valueAnimator.getAnimatedValue(); // 0 - 对角线的一半
                    invalidate();
                }
            });
            mAnimator.start();
        }

        @Override
        public void draw(Canvas canvas) {

            // 画笔的宽度
            float strokeWidth = mDiagonalDist - mHoleRadius;
            mPaint.setStrokeWidth(strokeWidth);
            mPaint.setColor(mSplashColor);
            mPaint.setStyle(Paint.Style.STROKE);
            float radius = strokeWidth / 2 + mHoleRadius;

            // 绘制一个圆
            canvas.drawCircle(mCenterX, mCenterY, radius, mPaint);
        }
    }

    public abstract class LoadingState {
        public abstract void draw(Canvas canvas);
    }

}

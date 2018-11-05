package com.lwang.customview.loadingview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.lwang.customview.R;

/**
 * @author lwang
 * @date 2018/11/05
 * @description 仿58同城数据加载动画
 */
public class LoadingView extends LinearLayout {

    private ShapeView mShapeView;
    private View mShadowView;
    private int mTranslationDistance = 0;
    // 动画执行的时间
    private final long ANIMATOR_DURATION = 350;
    // 是否停止动画
    private boolean mIsStopAnimator = false;


    public LoadingView(Context context) {
        super(context);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mTranslationDistance = dip2px(80);
        initLayout();
    }

    private void initLayout() {
        // 直接加载布局
        inflate(getContext(), R.layout.loading_view, this);

        mShapeView = (ShapeView) findViewById(R.id.shape_view);
        mShadowView = findViewById(R.id.shadow_view);

    }

    /**
     * 开始下落动画
     */
    private void startFallAnimator() {

    }

    /**
     * 开始执行上抛动画
     */
    private void startUpAnimator() {

    }

    /**
     * 上抛的时候需要旋转
     */
    private void startRotationAnimator() {
        ObjectAnimator rotationAnimator = null;
        switch (mShapeView.getCurrentShape()) {
            case Circle:
            case Square:
                // 旋转180
                rotationAnimator = ObjectAnimator.ofFloat(mShapeView, "rotation", 0, 180);
                break;
            case Triangle:
                // 旋转120
                rotationAnimator = ObjectAnimator.ofFloat(mShapeView, "rotation", 0, -120);
                break;
            default:
                break;
        }
        rotationAnimator.setDuration(ANIMATOR_DURATION);
        rotationAnimator.setInterpolator(new DecelerateInterpolator());
        rotationAnimator.start();
    }

    @Override
    public void setVisibility(int visibility) {
        // 不要再去排放和计算，少走一些系统的源码（View的绘制流程）
        super.setVisibility(View.INVISIBLE);

        // 清理动画
        mShapeView.clearAnimation();
        mShadowView.clearAnimation();

        // 把LoadingView从父布局移除
        ViewGroup parent = (ViewGroup) getParent();
        if (parent != null) {
            // 从父布局移除
            parent.removeView(this);
            // 移除自己所有的View
            removeAllViews();
        }
        mIsStopAnimator = true;
    }

    private int dip2px(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }
}

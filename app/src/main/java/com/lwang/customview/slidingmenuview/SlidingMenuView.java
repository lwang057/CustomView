package com.lwang.customview.slidingmenuview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lwang.customview.R;
import com.lwang.customview.utils.Utils;

/**
 * @author lwang
 * @date 2018/5/27
 * @description 侧滑菜单
 */

public class SlidingMenuView extends HorizontalScrollView {

    /**
     * 菜单宽度
     */
    private int mMenuWidth;
    private View mMenuView, mContentView, mShadowView;

    public SlidingMenuView(Context context) {
        super(context);
    }

    public SlidingMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlidingMenuView);
        float rightMargin = typedArray.getDimension(R.styleable.SlidingMenuView_menuRightMargin, dip2px(context, 50));

        // 菜单页的宽度是 = 屏幕的宽度 - 右边的一小部分距离（自定义属性）
        mMenuWidth = (int) (getScreenWidth(context) - rightMargin);

        typedArray.recycle();
    }

    /**
     * 1.指定宽高 （宽度不对乱套了）
     * 这个方法是布局解析完毕也就是 XML 布局文件解析完毕
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // 0.获取最外层布局
        ViewGroup container = (ViewGroup) getChildAt(0);

        int childCount = container.getChildCount();
        if (2 != childCount) {
            throw new RuntimeException("最多只能放两个View哦");
        }

        // 1.侧滑菜单页的宽度是 屏幕的宽度 - 右边的一小部分距离（mMenuWidth）
        mMenuView = container.getChildAt(0);
        ViewGroup.LayoutParams menuViewParams = mMenuView.getLayoutParams();
        menuViewParams.width = mMenuWidth;
        // 7.0 以下的手机必须采用下面的方式
        mMenuView.setLayoutParams(menuViewParams);

        // 2.内容页的宽度是屏幕的宽度
        mContentView = container.getChildAt(1);
        ViewGroup.LayoutParams contentViewParams = mContentView.getLayoutParams();
        contentViewParams.width = getScreenWidth(getContext());
        mContentView.setLayoutParams(contentViewParams);

        //--仿QQ侧滑效果
        //--1.先将布局从容器中移除掉
//        container.removeView(mContentView);

        //--2.在外面套一层阴影
//        RelativeLayout con = new RelativeLayout(getContext());
//        con.addView(mContentView);
//        mShadowView = new View(getContext());
//        mShadowView.setBackgroundColor(Color.parseColor("#000000"));
//        con.addView(mShadowView);

        //--3.最后把容器放回原来的位置
//        ViewGroup.LayoutParams contentViewParams = mContentView.getLayoutParams();
//        contentViewParams.width = getScreenWidth(getContext());
//        mContentView.setLayoutParams(contentViewParams);
//        container.addView(con);
//        mShadowView.setAlpha(0.0f);
    }

    /**
     * 2. 初始化进来是关闭
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        scrollTo(mMenuWidth, 0);
    }

    /**
     * 3.手指抬起是二选一，要么关闭要么打开
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        // 1. 获取手指滑动的速率，当期大于一定值就认为是快速滑动 ， GestureDetector（系统提供好的类）
        // 2. 处理事件拦截 + ViewGroup 事件分发的源码实践
        //    当菜单打开的时候，手指触摸右边内容部分需要关闭菜单，还需要拦截事件（打开情况下点击内容页不会响应点击事件）
        if (ev.getAction() == MotionEvent.ACTION_UP) {

            // 只需要管手指抬起 ，根据我们当前滚动的距离来判断
            // 往左滑动时是正值，往右是负值
            float currentScrollX = getScrollX();
            if (currentScrollX > mMenuWidth / 2) {
                //关闭
                closeMenu();
            } else {
                //打开
                openMenu();
            }
            // 确保 super.onTouchEvent() 不会执行
            return true;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 4.处理右边的缩放，左边的缩放和透明度，需要不断的获取当前滚动的位置
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        Utils.log("left:::" + l);
        // 计算一个梯度值 scale 变化是: 左1 <---> 右0
        float scale = 1f * l / mMenuWidth;
        Utils.log("scale:::" + scale);

        //--仿QQ侧滑效果
        //--4.控制阴影 0--1
//        float alphaScale = 1 - scale;
//        mShadowView.setAlpha(alphaScale);

        // 设置右边的缩放,默认是以中心点缩放: 最小是 0.7f, 最大是 1f
        float rightScale = 0.7f + 0.3f * scale;
        ViewCompat.setPivotX(mContentView, 0);
        ViewCompat.setPivotY(mContentView, mContentView.getMeasuredHeight() / 2);
        ViewCompat.setScaleX(mContentView, rightScale);
        ViewCompat.setScaleY(mContentView, rightScale);

        // 菜单的透明度是 半透明到完全透明  0.5f - 1.0f
        float leftAlpha = 0.5f + (1 - scale) * 0.5f;
        ViewCompat.setAlpha(mMenuView, leftAlpha);

        // 菜单的缩放 0.7f - 1.0f
        float leftScale = 0.7f + (1 - scale) * 0.3f;
        ViewCompat.setScaleX(mMenuView, leftScale);
        ViewCompat.setScaleY(mMenuView, leftScale);

        // 最后一个效果 退出这个按钮刚开始是在右边，安装我们目前的方式永远都是在左边
        // 设置平移，先看一个抽屉效果
//        ViewCompat.setTranslationX(mMenuView,l);

        // 平移 l*0.7f
        ViewCompat.setTranslationX(mMenuView, 0.25f * l);
    }

    /**
     * 打开菜单 滚动到 0 的位置
     */
    private void openMenu() {
        //相对于scrollTo(0, 0); smoothScrollTo带动画
        smoothScrollTo(0, 0);
    }

    /**
     * 关闭菜单 滚动到 mMenuWidth 的位置
     */
    private void closeMenu() {
        smoothScrollTo(mMenuWidth, 0);
    }

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    private int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }


    /**
     * Dip into pixels
     */
    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}

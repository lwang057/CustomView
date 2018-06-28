package com.lwang.customview.slidingmenuview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;

import com.lwang.customview.R;

/**
 * @Author lwang
 * @Date 2018/5/27 23:35
 * @Description 侧滑菜单
 */

public class SlidingMenuView extends HorizontalScrollView {

    private int mMenuWidth; //菜单宽度
    private View mMenuView, mContentView;

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

    // 1.指定宽高 （宽度不对乱套了）
    // 这个方法是布局解析完毕也就是 XML 布局文件解析完毕
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
    }

    // 2. 初始化进来是关闭
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        scrollTo(mMenuWidth, 0);
    }

    // 3.手指抬起是二选一，要么关闭要么打开
    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        // 1. 获取手指滑动的速率，当期大于一定值就认为是快速滑动 ， GestureDetector（系统提供好的类）
        // 2. 处理事件拦截 + ViewGroup 事件分发的源码实践
        //    当菜单打开的时候，手指触摸右边内容部分需要关闭菜单，还需要拦截事件（打开情况下点击内容页不会响应点击事件）
        if (ev.getAction() == MotionEvent.ACTION_UP) {

            // 只需要管手指抬起 ，根据我们当前滚动的距离来判断
            float currentScrollX = getScrollX(); //往左滑动时是正值，往右是负值
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
     * 打开菜单 滚动到 0 的位置
     */
    private void openMenu() {
        smoothScrollTo(0, 0); //相对于scrollTo(0, 0); smoothScrollTo带动画
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

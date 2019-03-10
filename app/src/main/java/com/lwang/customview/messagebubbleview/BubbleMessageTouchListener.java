package com.lwang.customview.messagebubbleview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.drawable.AnimationDrawable;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.lwang.customview.R;

/**
 * @author lwang
 * @date 2019/3/10
 * @description 监听当前View的触摸时间
 */
public class BubbleMessageTouchListener implements View.OnTouchListener {

    private Context mContext;
    // 需要拖动爆炸的View
    private View mStaticView;
    private WindowManager mWindowManager;
    private MessageBubbleView mMessageBubbleView;
    private WindowManager.LayoutParams mParams;
    // 爆炸动画
    private FrameLayout mBombFrame;
    private ImageView mBombImage;


    public BubbleMessageTouchListener(Context context, View view, BubbleDisappearListener disappearListener) {
        mStaticView = view;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mMessageBubbleView = new MessageBubbleView(context);
        mMessageBubbleView.setMessageBubbleListener(messageBubbleListener);

        mParams = new WindowManager.LayoutParams();
        mParams.format = PixelFormat.TRANSLUCENT; //背景要透明

        this.mContext = context;
        mBombFrame = new FrameLayout(mContext);
        mBombImage = new ImageView(mContext);
        mBombImage.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mBombFrame.addView(mBombImage);

        this.mDisappearListener = disappearListener;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 要在WindowManager上面搞一个View ,上一节写好的贝塞尔的View
                mWindowManager.addView(mMessageBubbleView, mParams);
                // 初始化贝塞尔View的点
                // 保证固定圆的中心在View的中心
                int[] location = new int[2];
                mStaticView.getLocationOnScreen(location);
                Bitmap bitmap = getBitmapByView(mStaticView);

                mMessageBubbleView.initPoint(location[0] + mStaticView.getWidth() / 2, location[1] + mStaticView.getHeight() / 2 - BubbleUtils.getStatusBarHeight(mContext));

                // 给消息拖拽设置一个Bitmap
                mMessageBubbleView.setDragBitmap(bitmap);
                // 将view隐藏
                mStaticView.setVisibility(View.INVISIBLE);
                break;
            case MotionEvent.ACTION_MOVE:
                mMessageBubbleView.updateDragPoint(event.getRawX(), event.getRawY() - BubbleUtils.getStatusBarHeight(mContext));
                break;
            case MotionEvent.ACTION_UP:
                mMessageBubbleView.handleActionUp();
                break;
        }
        return true;
    }

    /**
     * 从一个View中获取Bitmap
     *
     * @param view
     * @return
     */
    private Bitmap getBitmapByView(View view) {
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    private MessageBubbleView.MessageBubbleListener messageBubbleListener = new MessageBubbleView.MessageBubbleListener() {
        @Override
        public void restore() {
            // 把消息的View移除
            mWindowManager.removeView(mMessageBubbleView);
            // 把原来的View显示
            mStaticView.setVisibility(View.VISIBLE);
        }

        @Override
        public void dismiss(PointF pointF) {
            // 要去执行爆炸动画 （帧动画）
            // 原来的View的View肯定要移除
            mWindowManager.removeView(mMessageBubbleView);
            // 要在 mWindowManager 添加一个爆炸动画
            mWindowManager.addView(mBombFrame, mParams);
            mBombImage.setBackgroundResource(R.drawable.anim_bubble_pop);

            AnimationDrawable drawable = (AnimationDrawable) mBombImage.getBackground();
            mBombImage.setX(pointF.x - drawable.getIntrinsicWidth() / 2);
            mBombImage.setY(pointF.y - drawable.getIntrinsicHeight() / 2);

            drawable.start();
            // 等它执行完之后我要移除掉这个 爆炸动画也就是 mBombFrame
            mBombImage.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mWindowManager.removeView(mBombFrame);

                    // 通知一下外面该处理消失的操作了
                    if (mDisappearListener != null) {
                        mDisappearListener.dismiss(mStaticView);
                    }
                }
            }, getAnimationDrawableTime(drawable));
        }
    };

    private long getAnimationDrawableTime(AnimationDrawable drawable) {
        int numberOfFrames = drawable.getNumberOfFrames();
        long time = 0;
        for (int i = 0; i < numberOfFrames; i++) {
            time += drawable.getDuration(i);
        }
        return time;
    }



    private BubbleDisappearListener mDisappearListener;

    /**
     * view消失的接口回调
     */
    public interface BubbleDisappearListener {
        void dismiss(View view);
    }

}

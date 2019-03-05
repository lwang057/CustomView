package com.lwang.customview.messagebubbleview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author lwang
 * @date 2018/11/29
 * @description 消息拖拽View
 */
public class MessageBubbleView extends View {

    //两个圆的圆形
    private PointF mFixactionPoint, mDragPoint;
    //拖拽圆的半径
    private int mDragRadius = 10;
    //画笔
    private Paint mPaint;
    //固定圆的半径
    private int mFixactionRadiusMax = 7;
    private int mFixactionRadius;


    public MessageBubbleView(Context context) {
        super(context);
    }

    public MessageBubbleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mDragRadius = dip2px(mDragRadius);
        mFixactionRadiusMax = dip2px(mFixactionRadiusMax);
        mPaint=new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (mDragPoint == null || mFixactionPoint == null) {
            return;
        }

        //画拖拽圆
        canvas.drawCircle(mDragPoint.x, mDragPoint.y, mDragRadius, mPaint);
        //画固定圆  有一个初始化大小，而且他的半径是随着距离的增大而减小  小到一定程度就不见了（不画了）
        canvas.drawCircle(mDragPoint.x, mDragPoint.y, mDragRadius, mPaint);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //手指按下要去指定当前的位置
                float downX = event.getX();
                float downY = event.getY();
                initPoint(downX, downY);
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();
                updateDragPoint(moveX, moveY);
                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        invalidate();
        return true;
    }

    /**
     * 更新当前拖拽点的位置
     *
     * @param moveX
     * @param moveY
     */
    private void updateDragPoint(float moveX, float moveY) {
        mDragPoint.x = moveX;
        mDragPoint.y = moveY;
    }

    /**
     * 初始化位置
     *
     * @param downX
     * @param downY
     */
    private void initPoint(float downX, float downY) {
        mFixactionPoint = new PointF(downX, downY);
        mDragPoint = new PointF(downX, downY);
    }


    private int dip2px(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }


}

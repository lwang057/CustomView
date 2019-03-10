package com.lwang.customview.messagebubbleview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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
    private PointF mDragPoint, mFixActionPoint;
    //拖拽圆的半径
    private int mDragRadius = 10;
    //画笔
    private Paint mPaint;
    //固定圆的最大半径（初始半径）
    private int mFixActionRadiusMax = 7;
    private int mFixActionRadiusMin = 3;
    private int mFixActionRadius;


    public MessageBubbleView(Context context) {
        super(context);
    }

    public MessageBubbleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mDragRadius = dip2px(mDragRadius);
        mFixActionRadiusMax = dip2px(mFixActionRadiusMax);
        mFixActionRadiusMin = dip2px(mFixActionRadiusMin);

        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (mDragPoint == null || mFixActionPoint == null) {
            return;
        }

        //画拖拽圆
        canvas.drawCircle(mDragPoint.x, mDragPoint.y, mDragRadius, mPaint);

        Path bezierPath = getBezierPath();
        if (bezierPath != null) {
            //画固定圆  有一个初始化大小，而且他的半径是随着距离的增大而减小  小到一定程度就不见了（不画了）
            canvas.drawCircle(mFixActionPoint.x, mFixActionPoint.y, mFixActionRadius, mPaint);
            //画贝塞尔曲线
            canvas.drawPath(bezierPath, mPaint);
        }
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
     * 初始化位置
     *
     * @param downX
     * @param downY
     */
    private void initPoint(float downX, float downY) {
        mFixActionPoint = new PointF(downX, downY);
        mDragPoint = new PointF(downX, downY);
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
     * 获取贝塞尔的路径
     *
     * @return
     */
    private Path getBezierPath() {
        //获取两个点的距离
        double distance = getDistance(mDragPoint, mFixActionPoint);

        //两点距离越大，固定圆的半径就越小
        mFixActionRadius = (int) (mFixActionRadiusMax - distance / 14);
        if (mFixActionRadius < mFixActionRadiusMin) {
            //超过一定距离 贝塞尔和固定圆都不要画了
            return null;
        }

        Path bezierPath = new Path();

        // 求斜率
        float dy = (mDragPoint.y - mFixActionPoint.y);
        float dx = (mDragPoint.x - mFixActionPoint.x);
        float tanA = dy / dx;

        // 求角 a
        double arcTanA = Math.atan(tanA);

        // p0
        float p0x = (float) (mFixActionPoint.x + mFixActionRadius * Math.sin(arcTanA));
        float p0y = (float) (mFixActionPoint.y - mFixActionRadius * Math.cos(arcTanA));

        // p1
        float p1x = (float) (mDragPoint.x + mDragRadius * Math.sin(arcTanA));
        float p1y = (float) (mDragPoint.y - mDragRadius * Math.cos(arcTanA));

        // p2
        float p2x = (float) (mDragPoint.x - mDragRadius * Math.sin(arcTanA));
        float p2y = (float) (mDragPoint.y + mDragRadius * Math.cos(arcTanA));

        // p3
        float p3x = (float) (mFixActionPoint.x - mFixActionRadius * Math.sin(arcTanA));
        float p3y = (float) (mFixActionPoint.y + mFixActionRadius * Math.cos(arcTanA));

        // 控制点
        PointF controlPoint = getControlPoint();

        // 拼装贝塞尔的曲线路径（起始点与控制点）
        // 从p0点开始画
        bezierPath.moveTo(p0x, p0y);
        // 画第一条  p0x, p0y为起点、  controlPoint.x, controlPoint.y为控制点、  p1x, p1y为终点
        bezierPath.quadTo(controlPoint.x, controlPoint.y, p1x, p1y);

        // 从p1连接到p2
        bezierPath.lineTo(p2x, p2y);
        // 画第二条  p2x, p2y为起点、  controlPoint.x, controlPoint.y为控制点、  p3x, p3y为终点
        bezierPath.quadTo(controlPoint.x, controlPoint.y, p3x, p3y);

        // 闭合
        bezierPath.close();
        return bezierPath;
    }

    /**
     * 获取两个圆之间的距离
     *
     * @param point1
     * @param point2
     * @return
     */
    private double getDistance(PointF point1, PointF point2) {
        return Math.sqrt((point1.x - point2.x) * (point1.x - point2.x) + (point1.y - point2.y) * (point1.y - point2.y));
    }

    /**
     * 获取控制点
     *
     * @return
     */
    private PointF getControlPoint() {
        return new PointF((mDragPoint.x + mFixActionPoint.x) / 2, (mDragPoint.y + mFixActionPoint.y) / 2);
    }

    private int dip2px(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }


}

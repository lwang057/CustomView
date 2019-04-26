package com.lwang.customview.messagebubbleview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
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
import android.view.animation.OvershootInterpolator;

/**
 * @author lwang
 * @date 2018/11/29
 * @description 消息拖拽View
 */
public class MessageBubbleView extends View {

    private Context mContext;

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
    private Bitmap mDragBitmap;


    public MessageBubbleView(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public MessageBubbleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    private void initView() {
        mDragRadius = BubbleUtils.dip2px(mDragRadius, mContext);
        mFixActionRadiusMax = BubbleUtils.dip2px(mFixActionRadiusMax, mContext);
        mFixActionRadiusMin = BubbleUtils.dip2px(mFixActionRadiusMin, mContext);

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

        // 画图片 位置也是手指移动的位置 , 中心位置才是手指拖动的位置
        if (mDragBitmap != null) {
            // 搞一个渐变动画
            canvas.drawBitmap(mDragBitmap, mDragPoint.x - mDragBitmap.getWidth() / 2, mDragPoint.y - mDragBitmap.getHeight() / 2, null);
        }
    }

    /**
     * 获取贝塞尔的路径
     *
     * @return
     */
    private Path getBezierPath() {
        //获取两个点的距离
        float distance = BubbleUtils.getDistanceBetween2Points(mDragPoint, mFixActionPoint);

        //两点距离越大，固定圆的半径就越小
        mFixActionRadius = (int) (mFixActionRadiusMax - distance / 30);
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
        PointF controlPoint = new PointF((mDragPoint.x + mFixActionPoint.x) / 2, (mDragPoint.y + mFixActionPoint.y) / 2);

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
     * 绑定可以拖拽的控件
     *
     * @param view
     * @param disappearListener
     */
    public static void attach(View view, BubbleMessageTouchListener.BubbleDisappearListener disappearListener) {
        view.setOnTouchListener(new BubbleMessageTouchListener(view.getContext(), view, disappearListener));
    }

    /**
     * 设置要显示的view
     *
     * @param dragBitmap
     */
    public void setDragBitmap(Bitmap dragBitmap) {
        this.mDragBitmap = dragBitmap;
    }

    /**
     * 初始化位置
     *
     * @param downX
     * @param downY
     */
    public void initPoint(float downX, float downY) {
        mFixActionPoint = new PointF(downX, downY);
        mDragPoint = new PointF(downX, downY);
        invalidate();
    }

    /**
     * 更新当前拖拽点的位置
     *
     * @param moveX
     * @param moveY
     */
    public void updateDragPoint(float moveX, float moveY) {
        mDragPoint.x = moveX;
        mDragPoint.y = moveY;
        invalidate();
    }

    /**
     * 处理手指松开
     */
    public void handleActionUp() {
        if (mFixActionRadius > mFixActionRadiusMin) {
            // 回弹
            // ValueAnimator 值变化的动画  0 变化到 1
            ValueAnimator animator = ObjectAnimator.ofFloat(1);
            animator.setDuration(250);
            final PointF start = new PointF(mDragPoint.x, mDragPoint.y);
            final PointF end = new PointF(mFixActionPoint.x, mFixActionPoint.y);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float percent = (float) animation.getAnimatedValue();
                    PointF pointF = BubbleUtils.getPointByPercent(start, end, percent);

                    // 用代码更新拖拽点
                    updateDragPoint(pointF.x, pointF.y);
                }
            });

            // 设置一个差值器 在结束的时候回弹
            animator.setInterpolator(new OvershootInterpolator(3f));
            animator.start();

            // 还要通知 TouchListener 移除当前View 然后显示静态的 View
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mListener != null) {
                        mListener.restore();
                    }
                }
            });
        } else {
            // 爆炸
            if (mListener != null) {
                mListener.dismiss(mDragPoint);
            }
        }
    }


    private MessageBubbleListener mListener;

    public void setMessageBubbleListener(MessageBubbleListener listener) {
        this.mListener = listener;
    }

    public interface MessageBubbleListener {

        // 还原
        void restore();

        // 消失爆炸
        void dismiss(PointF pointF);
    }


}

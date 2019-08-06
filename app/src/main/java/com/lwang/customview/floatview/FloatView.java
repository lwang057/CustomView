package com.lwang.customview.floatview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PointFEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.lwang.customview.R;

public class FloatView extends View {

    private Bitmap bitmap;
    private Bitmap bitmapNoBorder;
    private int width;
    private int height;

    private Paint paint;
    //没有触摸到小球
    private int NO_STACH = 1;
    //是移动状态
    private int START_MOVE = 2;
    private int START_DOWN = 3;
    private int OPEN = 1;
    private int CLOSE = 2;
    //左右位置信息
    private int NEAR_LEFT = 1;
    private int NEAR_RIGHT = 2;
    //四个位置信息
    public static int LEFT_TOP = 1;
    public static int RIGHT_TOP = 2;
    public static int LEFT_BOTTOM = 3;
    public static int RIGHT_BOTTOM = 4;

    private Rect mSrcRect;
    private int STATE = NO_STACH;
    //小球圆心左边
    private PointF mMovableCenter;
    private Context context;
    //滑动最小距离
    private int scaledTouchSlop;
    private float mDownx;
    private float mDownY;
    private float hypot;
    private Paint mPaint;
    private float mRectLength = 0;
    //打开关闭状态
    private int OPEN_STATE = CLOSE;
    private String text = "操作指南";
    private Paint mTextPaint;
    private Rect mTotalRect;
    private int nearState = 0;
    private Boolean isCanClick = true;
    private boolean clickEnble = true;


    public FloatView(Context context) {
        this(context, null);
    }

    public FloatView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.zhinan_miaobian);
        bitmapNoBorder = BitmapFactory.decodeResource(getResources(), R.mipmap.zhinan_wu);
        width = this.bitmap.getWidth();
        height = this.bitmap.getHeight();

        //移动的圆心位置
        paint = new Paint();
        paint.setFilterBitmap(true);
        paint.setDither(true);

        // 它获得的是触发移动事件的最短距离，如果小于这个距离就不触发移动控件
        scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        //绘制矩形的画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);

        //绘制文字的画笔
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(px2sp(120));
        mTotalRect = new Rect();
        mTextPaint.getTextBounds(text, 0, text.length(), mTotalRect);//获得文字的宽高
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //小球的宽度
        width = bitmap.getWidth();
        //小球的高度
        height = bitmap.getHeight();
        //初始化小球值
        mMovableCenter = new PointF(getRight() - width / 2 - dip2px(context, 20), 1400);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //图片的位置信息
        mSrcRect = new Rect(
                (int) (mMovableCenter.x - (float) (width / 2)),
                (int) (mMovableCenter.y - (float) (height / 2)),
                (int) (mMovableCenter.x + (float) (width / 2)),
                (int) (mMovableCenter.y + (float) (height / 2)));
        if (STATE == NO_STACH) {
            //绘制滑出的矩形和圆弧
            if (nearState == NEAR_RIGHT) {
                canvas.drawArc((int) (mSrcRect.centerX() - mRectLength - width / 3), (int) (mSrcRect.centerY() - height / 3),
                        mSrcRect.centerX() - mRectLength + width / 3 + 10, (int) (mSrcRect.centerY() + height / 3),
                        90, 180, true, mPaint); //画出上部分的半圆
                canvas.drawRect(mSrcRect.centerX() - mRectLength, mSrcRect.centerY() - height / 3,
                        mSrcRect.centerX(), mSrcRect.centerY() + height / 3, mPaint);//画出两个半圆中间的矩形
            } else if (nearState == NEAR_LEFT) {
                canvas.drawArc((int) (mSrcRect.centerX() + mRectLength - width / 3 - 10), (int) (mSrcRect.centerY() - height / 3),
                        mSrcRect.centerX() + mRectLength + width / 3, (int) (mSrcRect.centerY() + height / 3),
                        270, 180, true, mPaint); //画出上部分的半圆
                canvas.drawRect(mSrcRect.centerX(), mSrcRect.centerY() - height / 3, mSrcRect.centerX() + mRectLength,
                        mSrcRect.centerY() + height / 3, mPaint);//画出两个半圆中间的矩形
            }
        }


        if (OPEN_STATE == OPEN) {
            if (nearState == NEAR_RIGHT) {
                //绘制右边的文字和三角形
                if (Math.abs(mSrcRect.centerX() - mRectLength + 10 - (mSrcRect.centerX() + width / 2)) < mTotalRect.width()) {

                } else {
                    canvas.drawText(text, mSrcRect.centerX() - mRectLength + 22, mSrcRect.centerY() + mTotalRect.height() / 2, mTextPaint);
                    Paint p = new Paint();
                    p.setColor(Color.BLACK);
                    //实例化路径
                    Path path = new Path();
                    path.moveTo(mSrcRect.centerX() - mRectLength - 20, mSrcRect.centerY() - 5);// 此点为多边形的起点
                    path.lineTo(mSrcRect.centerX() - mRectLength - 20 + 20, mSrcRect.centerY() - 5);
                    path.lineTo(mSrcRect.centerX() - mRectLength - 20 + 10, mSrcRect.centerY() + 15);
                    path.close(); // 使这些点构成封闭的多边形
                    canvas.drawPath(path, p);
                }

            } else if (nearState == NEAR_LEFT) {
                //绘制左边的文字和三角形
                if (mRectLength + mSrcRect.centerX() - (mSrcRect.centerX() - mSrcRect.width() / 2) < mTotalRect.width()) {

                } else {
                    canvas.drawText(text, mRectLength + mSrcRect.centerX() - mTotalRect.width() - 22, mSrcRect.centerY() + mTotalRect.height() / 2, mTextPaint);
                    Paint p = new Paint();
                    p.setColor(Color.BLUE);
                    //实例化路径
                    Path path = new Path();
                    path.moveTo(mRectLength + mSrcRect.centerX() + 20, mSrcRect.centerY() - 5);// 此点为多边形的起点
                    path.lineTo(mRectLength + mSrcRect.centerX() + 20 - 20, mSrcRect.centerY() - 5);
                    path.lineTo(mRectLength + mSrcRect.centerX() + 20 - 10, mSrcRect.centerY() + 15);
                    path.close(); // 使这些点构成封闭的多边形
                    canvas.drawPath(path, p);
                }

            }
        }

        if (OPEN_STATE == OPEN) {
            canvas.drawBitmap(bitmapNoBorder, null, mSrcRect, paint);
        } else if (OPEN_STATE == CLOSE) {
            canvas.drawBitmap(bitmap, null, mSrcRect, paint);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //getParent().requestDisallowInterceptTouchEvent(true);
                float mDist = (float) Math.hypot(event.getX() - mSrcRect.centerX(), event.getY() - mSrcRect.centerY());
                //判断点击的最小辨别
                if (mDist < bitmap.getWidth() / 2) {
                    STATE = START_DOWN;
                } else {
                    STATE = NO_STACH;
                    return false;
                }
                mDownx = event.getX();
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (OPEN_STATE == OPEN) {
                    return true;
                }
                if (STATE == START_DOWN || STATE == START_MOVE) {
                    hypot = (float) Math.hypot(event.getX() - mDownx, event.getY() - mDownY);
                    //计算滑动的最小距离
                    if (hypot > scaledTouchSlop) {
                        STATE = START_MOVE;
                        mMovableCenter.x = event.getX();
                        mMovableCenter.y = event.getY();
                        //控制滑动边界
                        if (mMovableCenter.y > getBottom() - height / 2) {
                            mMovableCenter.y = getBottom() - height / 2;
                        }
                        if (mMovableCenter.y < height / 2) {
                            mMovableCenter.y = height / 2;
                        }
                        if (mMovableCenter.x <= getRight() / 2) {
                            if (mMovableCenter.y <= getBottom() / 2) {
                                if (onMoveListener != null) {
                                    onMoveListener.onMoveListener(LEFT_TOP);
                                }
                            } else {
                                if (onMoveListener != null) {
                                    onMoveListener.onMoveListener(LEFT_BOTTOM);
                                }
                            }
                        } else {
                            if (mMovableCenter.y <= getBottom() / 2) {
                                if (onMoveListener != null) {
                                    onMoveListener.onMoveListener(RIGHT_TOP);
                                }
                            } else {
                                if (onMoveListener != null) {
                                    onMoveListener.onMoveListener(RIGHT_BOTTOM);
                                }
                            }
                        }
                        if (!isCanClick || !clickEnble) {
                            return true;
                        }
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                float x = event.getX();
                float y = event.getY();
                if (mMovableCenter.y > getBottom() - height / 2) {
                    mMovableCenter.y = getBottom() - height / 2;
                }
                if (mMovableCenter.y < height / 2) {
                    mMovableCenter.y = height / 2;
                }
                if (STATE == START_MOVE) {
                    if (x <= getRight() / 2) {
                        if (y <= getBottom() / 2) {
                            Log.e("left", "左下边");
                        } else {
                            Log.e("left", "左下边");
                        }
                        startMoveAnimal(width / 2 + dip2px(context, 20));
                    } else {
                        if (y <= getBottom() / 2) {
                            Log.e("left", "右上边");
                        } else {
                            Log.e("left", "右下边");
                        }
                        startMoveAnimal(getRight() - width / 2 - dip2px(context, 20));
                    }
                }
                if (STATE == START_DOWN) {
                    hypot = (float) Math.hypot(event.getX() - mDownx, event.getY() - mDownY);
                    if (hypot < scaledTouchSlop) {
                        STATE = NO_STACH;

                        //view滚动或者在执行动画的时候不让其点击
                        if (!isCanClick || !clickEnble) {
                            return true;
                        }
                        clickEnble = false;
                        Log.e("action1", "发生点击事件");
                        //clickEnble = false;
                        if (x <= getRight() / 2) {
                            nearState = NEAR_LEFT;
                            if (y <= getBottom() / 2) {
                                if (onClickButtonListener != null) {
                                    onClickButtonListener.onClickButton(FloatView.LEFT_TOP, OPEN_STATE);
                                }
                            } else {
                                if (onClickButtonListener != null) {
                                    onClickButtonListener.onClickButton(FloatView.LEFT_BOTTOM, OPEN_STATE);
                                }
                            }
                        } else {
                            if (y <= getBottom() / 2) {
                                if (onClickButtonListener != null) {
                                    onClickButtonListener.onClickButton(FloatView.RIGHT_TOP, OPEN_STATE);
                                }
                            } else {
                                if (onClickButtonListener != null) {
                                    onClickButtonListener.onClickButton(FloatView.RIGHT_BOTTOM, OPEN_STATE);
                                }
                            }
                            //根据此值区分动画
                            nearState = NEAR_RIGHT;
                        }
                        if (OPEN_STATE == OPEN) {
                            //startOpenAndCloseAnimal(mTotalRect.width()+width/2+30,0);
                        } else {
                            OPEN_STATE = OPEN;
                            startOpenAndCloseAnimal(0, mTotalRect.width() + width / 2 + 30);
                        }

                        break;
                    }
                }

                break;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startMoveAnimal(float xValue) {
        ValueAnimator valueAnimator = ValueAnimator.ofObject(new PointFEvaluator(),
                new PointF(mMovableCenter.x, mMovableCenter.y),
                new PointF(xValue, mMovableCenter.y));
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mMovableCenter = (PointF) animation.getAnimatedValue();
                STATE = NO_STACH;
                if (mMovableCenter.x <= getRight() / 2) {
                    nearState = NEAR_LEFT;
                    if (mMovableCenter.y <= getBottom() / 2) {
                        if (onMoveListener != null) {
                            onMoveListener.onMoveListener(FloatView.LEFT_TOP);
                        }
                    } else {
                        if (onMoveListener != null) {
                            onMoveListener.onMoveListener(FloatView.LEFT_BOTTOM);
                        }
                    }
                } else {
                    if (mMovableCenter.y <= getBottom() / 2) {
                        if (onMoveListener != null) {
                            onMoveListener.onMoveListener(FloatView.RIGHT_TOP);
                        }
                    } else {
                        if (onMoveListener != null) {
                            onMoveListener.onMoveListener(FloatView.RIGHT_BOTTOM);
                        }
                    }
                }
                invalidate();
            }
        });
        valueAnimator.start();
    }

    public void startOpenAndCloseAnimal(float start, float end) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(start, end);
        valueAnimator.setDuration(100);
        valueAnimator.setInterpolator(new AccelerateInterpolator(3f));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                mRectLength = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.start();
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mRectLength == 0) {
                    OPEN_STATE = CLOSE;
                } else {
                    OPEN_STATE = OPEN;
                }

            }
        });
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public int px2sp(float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    //设置如果recycleView在滚动的时候或者用手指拖动的时候不执行点击事件
    public void setIsCanClick(Boolean isCanClick) {
        this.isCanClick = isCanClick;
    }

    public void setCanClickEnble(boolean clickEnble) {
        this.clickEnble = clickEnble;
    }

    public float getCenterX() {
        return mMovableCenter.x;
    }

    public float getCenterY() {
        return mMovableCenter.y;
    }

    public int getFloatWidth() {
        return width;
    }

    public int getFloatHeight() {
        return height;
    }

    public int getTextWidth() {
        return mTotalRect.width();
    }


    private OnMoveListener onMoveListener;

    public interface OnMoveListener {
        void onMoveListener(int state);
    }

    public void setOnMoveListener(OnMoveListener onMoveListener) {
        this.onMoveListener = onMoveListener;
    }

    private OnClickButtonListener onClickButtonListener;

    public interface OnClickButtonListener {
        void onClickButton(int state, int OpenState);
    }

    public void setOnClickButtonListener(OnClickButtonListener onClickButtonListener) {
        this.onClickButtonListener = onClickButtonListener;
    }
}

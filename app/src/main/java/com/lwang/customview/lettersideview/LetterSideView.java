package com.lwang.customview.lettersideview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.lwang.customview.R;
import com.lwang.customview.utils.Utils;

/**
 * @Author lwang
 * @Date 2018/5/23 22:14
 * @Description 自定义字母索引列表的view
 */

public class LetterSideView extends View {

    private int mNormalTextColor;
    private int mPressedTextColor;
    private int mTextSize;
    private Paint mNormalPaint;
    private Paint mPressedPaint;
    private static String[] mLetter = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"};
    private int mCurrentPosition;


    public LetterSideView(Context context) {
        super(context);
    }

    public LetterSideView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // 获取自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LetterSideView);
        mNormalTextColor = typedArray.getColor(R.styleable.LetterSideView_normalTextColor, mNormalTextColor);
        mPressedTextColor = typedArray.getColor(R.styleable.LetterSideView_pressedTextColor, mPressedTextColor);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.LetterSideView_textSize, mTextSize);
        typedArray.recycle();

        mNormalPaint = getPaint(mNormalTextColor, mTextSize);
        mPressedPaint = getPaint(mPressedTextColor, mTextSize);
    }

    /**
     * 获取不同颜色画笔
     *
     * @param textColor
     * @param textSize
     * @return
     */
    private Paint getPaint(int textColor, int textSize) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        return paint;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 宽度=左内边距+字体宽度+右内边距
        int textWidth = (int) mNormalPaint.measureText("A");
        int width = getPaddingLeft() + getPaddingRight() + textWidth;

        int height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(width, height);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        //画26个字母

        // 获取每个字母的高度
        int itemHeight = (getHeight() - getPaddingTop() - getPaddingBottom()) / mLetter.length;

        for (int i = 0; i < mLetter.length; i++) {

            // 画x   字母居中 = 宽度/2 - 字体宽度/2
            int textWidth = (int) mNormalPaint.measureText(mLetter[i]);
            int x = getWidth() / 2 - textWidth / 2;

            int centerHeight = i * itemHeight + itemHeight / 2 + getPaddingTop();

            Paint.FontMetrics metricsInt = mNormalPaint.getFontMetrics();
            int dy = (int) ((metricsInt.bottom - metricsInt.top) / 2 - metricsInt.bottom);

            //画基线要基于中心位置，要知道每个字母的中心位置
            int baseLine = centerHeight + dy;

            if (mLetter[i].equals(mLetter[mCurrentPosition])) {
                canvas.drawText(mLetter[i], x, baseLine, mPressedPaint);
            } else {
                canvas.drawText(mLetter[i], x, baseLine, mNormalPaint);
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:

                // 获取每个字母的高度
                int itemHeight = (getHeight() - getPaddingTop() - getPaddingBottom()) / mLetter.length;

                // 当前触摸字母的位置 = Y坐标 / 字母的高度
                float y = event.getY();
                mCurrentPosition = (int) (y / itemHeight);

                if (mCurrentPosition < 0)
                    mCurrentPosition = 0;

                if (mCurrentPosition > mLetter.length - 1)
                    mCurrentPosition = mLetter.length - 1;

                if (onLetterTouchListener != null) {
                    onLetterTouchListener.touch(mLetter[mCurrentPosition], true);
                }

                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (onLetterTouchListener != null) {
                    onLetterTouchListener.touch(mLetter[mCurrentPosition], false);
                }
                break;
        }
        return true;
    }


    private OnLetterTouchListener onLetterTouchListener;

    /**
     * 将触摸到的字母实时返回显示
     *
     * @param listener
     */
    public void setOnLetterTouchListener(OnLetterTouchListener listener) {
        this.onLetterTouchListener = listener;
    }

    public interface OnLetterTouchListener {
        void touch(CharSequence letter, boolean isTouch);
    }

}

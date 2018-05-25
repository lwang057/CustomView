package com.lwang.customview.lettersideview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.lwang.customview.R;

/**
 * @Author lwang
 * @Date 2018/5/23 22:14
 * @Description 自定义字母索引列表的view
 */

public class LetterSideView extends View {

    private int mTextColor;
    private int mTextSize;
    private Paint mPaint;

    public LetterSideView(Context context) {
        super(context);
    }

    public LetterSideView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // 获取自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LetterSideView);
        mTextColor = typedArray.getColor(R.styleable.LetterSideView_textColor, mTextColor);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.LetterSideView_textSize, mTextSize);
        typedArray.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mTextColor);
        mPaint.setColor(mTextSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int textWidth = (int) mPaint.measureText("A");
        // 宽度=左内边距+字体宽度+右内边距
        int width = getPaddingLeft() + textWidth + getPaddingRight();

        int height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(width, height);
    }


    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas); 画26个字母

    }

}

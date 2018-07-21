package com.lwang.customview.indicatorseekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import com.lwang.customview.R;
import com.lwang.customview.utils.Utils;
import com.orhanobut.logger.Logger;

/**
 * @author lwang
 * @date 2018/6/4 21:46
 * @description 自定义带图标的SeekBar
 */

public class IndicatorSeekBar extends SeekBar {

    private Bitmap mBitmap;
    private Paint mPaint;
    private int max = 327314;
    private double progress = 1000 * 179;

    public IndicatorSeekBar(Context context) {
        super(context);
    }

    public IndicatorSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.IndicatorSeekBar);

        // 我们要在布局中设置的就是一张图片，所以此处强转为 BitmapDrawable
        BitmapDrawable drawableBg = (BitmapDrawable) ta.getDrawable(R.styleable.IndicatorSeekBar_bitmap);
        mBitmap = drawableBg.getBitmap();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }


    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        double i = progress / max;
        int width = getWidth() - 50;
        double v = width * i;
        Utils.log("i:::" + i + ",width:::" + width + ",v:::" + v);

        // 绘制图片
        // 参数2 是图片的左边距
        // 参数3 是图片的上边距
        canvas.drawBitmap(mBitmap, (float) v, 6, mPaint);
    }

    public void setMaxNum(int maxNum) {
        this.max = maxNum;
        Utils.log("max:::" + max);
    }

}

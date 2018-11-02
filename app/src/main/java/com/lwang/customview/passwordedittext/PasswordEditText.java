package com.lwang.customview.passwordedittext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.lwang.customview.R;

/**
 * @author lwang
 * @date 2018/10/31
 * @description 自定义密码输入框
 */
@SuppressLint("AppCompatCustomView")
public class PasswordEditText extends EditText {

    // 密码的个数（默认为6位数）
    private int mPasswordNumber = 6;
    // 密码圆点的半径
    private int mPasswordRadius = 4;
    // 密码圆点的颜色
    private int mPasswordColor = Color.parseColor("#d1d2d6");
    // 分割线的颜色
    private int mDivisionLineColor = Color.parseColor("#d1d2d6");
    // 分割线的大小
    private int mDivisionLineSize = 1;
    // 背景边框的颜色
    private int mBgColor = Color.parseColor("#d1d2d6");
    // 背景边框的大小
    private int mBgSize = 1;
    // 背景边框的圆角大小
    private int mBgCorner = 0;

    // 画笔
    private Paint mPaint;

    // 一个密码所占的宽度
    private int mPasswordItemWidth;

    /**
     * 通过代码new出来时调用
     *
     * @param context
     */
    public PasswordEditText(Context context) {
        super(context);
    }

    /**
     * 通过布局使用时调用
     *
     * @param context
     * @param attrs
     */
    public PasswordEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributeSet(context, attrs);
        initPaint();
        // 设置输入模式是密码
        setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
        // 不显示光标
        setCursorVisible(false);
        // 不让点击
        setEnabled(false);
    }

    /**
     * 初始化属性
     *
     * @param context
     * @param attrs
     */
    private void initAttributeSet(Context context, AttributeSet attrs) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PasswordEditText);
        mPasswordRadius = (int) typedArray.getDimension(R.styleable.PasswordEditText_passwordRadius, dip2px(mPasswordRadius));
        mPasswordColor = typedArray.getColor(R.styleable.PasswordEditText_passwordColor, mDivisionLineColor);

        mDivisionLineColor = typedArray.getColor(R.styleable.PasswordEditText_divisionLineColor, mDivisionLineColor);
        mDivisionLineSize = (int) typedArray.getDimension(R.styleable.PasswordEditText_divisionLineSize, dip2px(mDivisionLineSize));

        mBgSize = (int) typedArray.getDimension(R.styleable.PasswordEditText_bgSize, dip2px(mBgSize));
        mBgColor = typedArray.getColor(R.styleable.PasswordEditText_bgColor, mBgColor);
        mBgCorner = (int) typedArray.getDimension(R.styleable.PasswordEditText_bgCorner, 0);

        //释放该实例，从而使其可被其他模块复用
        //从源码可看出：程序在运行时维护了一个 TypedArray的池，程序调用时，会向该池中请求一个实例，所以用完之后，即使释放
        typedArray.recycle();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mPaint = new Paint();
        // 抗锯齿
        mPaint.setAntiAlias(true);
        // 防抖动
        mPaint.setDither(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 一个密码所占的宽度
        mPasswordItemWidth = (getWidth() - 2 * mBgSize - (mPasswordNumber - 1) * mDivisionLineSize) / mPasswordNumber;
        // 画背景
        drawBg(canvas);
        // 画分割线
        drawDivisionLine(canvas);
        // 画密码
        drawPassword(canvas);

        // 判断当前密码是不是满了
        if (mListener != null) {
            String password = getText().toString().trim();
            if (password.length() >= mPasswordNumber) {
                mListener.passwordFull(password);
            }
        }
    }

    /**
     * 画背景
     *
     * @param canvas
     */
    private void drawBg(Canvas canvas) {

        // 给画笔设置大小
        mPaint.setStrokeWidth(mBgSize);
        // 设置背景的颜色
        mPaint.setColor(mBgColor);
        // 画空心
        mPaint.setStyle(Paint.Style.STROKE);

        RectF rectF = new RectF(mBgSize, mBgSize, getWidth() - mBgSize, getHeight() - mBgSize);

        // 绘制背景  drawRect 画矩形, drawRoundRect  画圆角矩形,
        // 如果有圆角那么就绘制drawRoundRect，否则绘制drawRect
        if (mBgCorner == 0) {
            canvas.drawRect(rectF, mPaint);
        } else {
            canvas.drawRoundRect(rectF, mBgCorner, mBgCorner, mPaint);
        }
    }

    /**
     * 画分割线
     *
     * @param canvas
     */
    private void drawDivisionLine(Canvas canvas) {

        // 给画笔设置大小
        mPaint.setStrokeWidth(mDivisionLineSize);
        // 设置分割线的颜色
        mPaint.setColor(mDivisionLineColor);

        for (int i = 0; i < mPasswordNumber - 1; i++) {
            int startX = mBgSize + (i + 1) * mPasswordItemWidth + i * mDivisionLineSize;
            int startY = mBgSize;
            int endX = startX;
            int endY = getHeight() - mBgSize;
            canvas.drawLine(startX, startY, endX, endY, mPaint);
        }
    }

    /**
     * 画密码
     *
     * @param canvas
     */
    private void drawPassword(Canvas canvas) {

        // 密码绘制是实心
        mPaint.setStyle(Paint.Style.FILL);
        // 设置密码的颜色
        mPaint.setColor(mPasswordColor);
        // 获取密码的长度
        int passwordLength = getText().toString().trim().length();
        // 不断的绘制密码
        for (int i = 0; i < passwordLength; i++) {
            int cx = mBgSize + i * mPasswordItemWidth + i * mDivisionLineSize + mPasswordItemWidth / 2;
            int cy = getHeight() / 2;
            canvas.drawCircle(cx, cy, mPasswordRadius, mPaint);
        }
    }

    /**
     * 添加一个秘密
     */
    public void addPassword(String number) {

        // 把之前的密码取出来
        String password = getText().toString().trim();
        if (password.length() >= mPasswordNumber) {
            // 密码不能超过当前密码个输
            return;
        }
        // 密码叠加
        password += number;
        setText(password);
    }

    /**
     * 删除最后一位密码
     */
    public void deleteLastPassword() {
        String password = getText().toString().trim();
        // 判断当前密码是不是空
        if (TextUtils.isEmpty(password)) {
            return;
        }
        password = password.substring(0, password.length() - 1);
        setText(password);
    }

    private PasswordFullListener mListener;

    interface PasswordFullListener {
        void passwordFull(String password);
    }

    /**
     * 设置当前密码已满的接口回掉
     */
    public void setOnPasswordFullListener(PasswordFullListener listener) {
        this.mListener = listener;
    }

    /**
     * dip 转 px
     */
    private float dip2px(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }

}

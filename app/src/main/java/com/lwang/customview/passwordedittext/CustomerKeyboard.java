package com.lwang.customview.passwordedittext;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lwang.customview.R;

/**
 * @author lwang
 * @date 2018/11/02
 * @description 自定义键盘
 */
public class CustomerKeyboard extends LinearLayout {

    public CustomerKeyboard(Context context) {
        super(context);
    }

    public CustomerKeyboard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        // 直接加载布局
        inflate(context, R.layout.customer_keyboard, this);
        setItemClickListener(this);
    }

    /**
     * 设置子View的ClickListener
     */
    private void setItemClickListener(View view) {
        if (view instanceof ViewGroup) {
            setViewGroupClick(view);
        } else {
            setViewClick(view);
        }
    }

    /**
     * 通过遍历ViewGroup给所有的子view设置点击事件
     *
     * @param view
     */
    private void setViewGroupClick(View view) {

        ViewGroup viewGroup = (ViewGroup) view;
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            // 不断的递归给里面所有的View设置OnClickListener
            View childView = viewGroup.getChildAt(i);
            setItemClickListener(childView);
        }
    }

    /**
     * 如果是子view直接设置点击事件
     *
     * @param view
     */
    private void setViewClick(View view) {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof TextView) {
                    // 点击的是数字
                    String number = ((TextView) v).getText().toString().trim();
                    if (!TextUtils.isEmpty(number)) {
                        if (mListener != null) {
                            mListener.click(number);
                        }
                    }
                }
                if (v instanceof ImageView) {
                    // 点击的是删除
                    if (mListener != null) {
                        mListener.delete();
                    }
                }
            }
        });
    }

    private CustomerKeyboardClickListener mListener;

    /**
     * 设置点击回掉监听
     */
    public void setOnCustomerKeyboardClickListener(CustomerKeyboardClickListener listener) {
        this.mListener = listener;
    }

    /**
     * 点击键盘的回调监听
     */
    interface CustomerKeyboardClickListener {
        void click(String number);

        void delete();
    }
}

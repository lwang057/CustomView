package com.lwang.customview.guideview.pager;

import android.content.Context;
import android.view.View;

/**
 * @author lwang
 * @date 2019/02/28
 * @description
 */
public abstract class BasePager {

    public Context context;

    public BasePager(Context context) {
        this.context = context;
    }

    /**
     * 初始化界面方法 子类必须重写 初始化item方法中调用，instantiateItem
     *
     * @return
     */
    public abstract View initView(Context context);
}

package com.lwang.customview.guideview.pager;

import android.content.Context;
import android.view.View;

import com.lwang.customview.R;

/**
 * @author lwang
 * @date 2019/02/28
 * @description
 */
public class GuidePagerTwo extends BasePager {
    private View view;

    public GuidePagerTwo(Context context) {
        super(context);
    }

    @Override
    public View initView(Context context) {
        view = View.inflate(context, R.layout.guide_viewpager_two, null);
        return view;
    }
}

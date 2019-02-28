package com.lwang.customview.guideview.pager;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lwang.customview.R;

/**
 * @author lwang
 * @date 2019/02/28
 * @description
 */
public class GuidePageThree extends BasePager {

    private View view;

    public GuidePageThree(Context context) {
        super(context);
    }

    @Override
    public View initView(final Context context) {
        view = View.inflate(context, R.layout.guide_viewpager_three, null);
        TextView btnOpen = (TextView) view.findViewById(R.id.btn_open);
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return view;
    }
}

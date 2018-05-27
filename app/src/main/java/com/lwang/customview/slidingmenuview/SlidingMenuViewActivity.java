package com.lwang.customview.slidingmenuview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.lwang.customview.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lwang
 * @Date 2018/5/27 23:36
 * @Description 侧滑菜单页面
 */

public class SlidingMenuViewActivity extends AppCompatActivity {

    @BindView(R.id.sliding_menu_view)
    SlidingMenuView mSlidingMenuView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding_menu_view);
        ButterKnife.bind(this);
    }


}

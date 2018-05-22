package com.lwang.customview.colortrackview;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.lwang.customview.R;

import butterknife.ButterKnife;

/**
 * @Author lwang
 * @Date 2018/5/22 22:24
 * @Description 字体变色页面
 */

public class ColorTrackViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_track_view);
        ButterKnife.bind(this);
    }

}

package com.lwang.customview.indicatorseekbar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.lwang.customview.R;

import butterknife.ButterKnife;

/**
 * @Author lwang
 * @Date 2018/6/4 21:47
 * @Description 自定义带图标的SeekBar页面
 */

public class IndicatorSeekBarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indicator_seekbar);
        ButterKnife.bind(this);
    }


}

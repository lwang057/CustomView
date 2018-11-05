package com.lwang.customview.loadingview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.lwang.customview.R;

/**
 * @author lwang
 * @date 2018/11/05
 * @description 数据加载动画界面
 */
public class LoadingViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_view);
    }


}

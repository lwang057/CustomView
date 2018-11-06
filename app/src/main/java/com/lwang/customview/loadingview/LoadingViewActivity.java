package com.lwang.customview.loadingview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.lwang.customview.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author lwang
 * @date 2018/11/05
 * @description 数据加载动画界面
 */
public class LoadingViewActivity extends AppCompatActivity {

    @BindView(R.id.loading_view)
    LoadingView loadingView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_view);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.stop_animation)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stop_animation:
                loadingView.setVisibility(View.GONE);
                break;
        }
    }

}

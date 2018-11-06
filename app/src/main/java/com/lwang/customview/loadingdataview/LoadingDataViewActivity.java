package com.lwang.customview.loadingdataview;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.lwang.customview.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author lwang
 * @date 2018/11/06
 * @description 数据加载动画界面(仿雅虎新闻)
 */
public class LoadingDataViewActivity extends AppCompatActivity {

    @BindView(R.id.loading_data_view)
    LoadingDataView mLoadingDataView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_data_view);
        ButterKnife.bind(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mLoadingDataView.disAppear();
            }
        }, 2000);
    }

}

package com.lwang.customview.lettersideview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.lwang.customview.R;

import butterknife.ButterKnife;

/**
 * @Author lwang
 * @Date 2018/5/23 22:15
 * @Description 字母索引列表页面
 */

public class LetterSideViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter_side_view);
        ButterKnife.bind(this);
    }

}

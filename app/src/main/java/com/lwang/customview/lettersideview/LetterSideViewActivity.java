package com.lwang.customview.lettersideview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.lwang.customview.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author lwang
 * @date 2018/5/23
 * @description 字母索引列表页面
 */

public class LetterSideViewActivity extends AppCompatActivity {

    @BindView(R.id.tv)
    TextView mTextView;
    @BindView(R.id.letter_side_view)
    LetterSideView mLetterSideView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter_side_view);
        ButterKnife.bind(this);

        mLetterSideView.setOnLetterTouchListener(new LetterSideView.OnLetterTouchListener() {
            @Override
            public void touch(CharSequence letter, boolean isTouch) {
                if (isTouch){
                    mTextView.setVisibility(View.VISIBLE);
                    mTextView.setText(letter);
                }else {
                    mTextView.setVisibility(View.GONE);
                }
            }
        });

    }


}

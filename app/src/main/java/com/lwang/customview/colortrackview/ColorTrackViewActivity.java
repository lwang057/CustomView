package com.lwang.customview.colortrackview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
 * @Author lwang
 * @Date 2018/5/22 22:24
 * @Description 字体变色页面
 */

public class ColorTrackViewActivity extends AppCompatActivity {

    @BindView(R.id.color_track_view)
    ColorTrackView mColorTrackView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_track_view);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.button_left, R.id.button_right})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_left:
                startAnimator(ColorTrackView.Direction.LEFT_TO_RIGHT);
                break;
            case R.id.button_right:
                startAnimator(ColorTrackView.Direction.RIGHT_TO_LEFT);
                break;
        }
    }

    private void startAnimator(ColorTrackView.Direction direction) {

        mColorTrackView.setDirection(direction);
        ValueAnimator animator = ObjectAnimator.ofFloat(0, 1);
        animator.setDuration(2000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentProgress = (float) animation.getAnimatedValue();
                mColorTrackView.setCurrentProgress(currentProgress);
            }
        });
        animator.start();
    }

}

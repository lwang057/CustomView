package com.lwang.customview.stepqqview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;

import com.lwang.customview.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lwang
 * @Date 2018/5/22 21:56
 * @Description 仿QQ运动步数
 */

public class StepQQViewActivity extends AppCompatActivity {

    @BindView(R.id.step_view)
    StepQQView mStepQQView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_qqview);
        ButterKnife.bind(this);

        mStepQQView.setStepMax(5000);

        // 属性动画来实时改变位置
        ValueAnimator valueAnimator = ObjectAnimator.ofFloat(0, 3000);
        valueAnimator.setDuration(1000);
        valueAnimator.setInterpolator(new DecelerateInterpolator()); //设置插值器，在动画开始的地方快然后慢
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                float currentStep = (float) animation.getAnimatedValue();
                mStepQQView.setCuttentStep((int)currentStep);
            }
        });

        valueAnimator.start();
    }


}

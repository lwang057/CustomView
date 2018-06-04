package com.lwang.customview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.lwang.customview.colortrackview.ColorTrackViewActivity;
import com.lwang.customview.indicatorseekbar.IndicatorSeekBarActivity;
import com.lwang.customview.lettersideview.LetterSideViewActivity;
import com.lwang.customview.slidingmenuview.SlidingMenuViewActivity;
import com.lwang.customview.stepqqview.StepQQViewActivity;
import com.lwang.customview.waveview.WaveViewActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.button_wave_view, R.id.button_step_qq_view, R.id.button_color_track_view,
            R.id.button_letter_side_view, R.id.button_sliding_menu_view, R.id.button_indicator_seekbar,
            R.id.button2, R.id.button3, R.id.button4, R.id.button5})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_wave_view:
                startActivity(new Intent(this, WaveViewActivity.class));
                break;
            case R.id.button_step_qq_view:
                startActivity(new Intent(this, StepQQViewActivity.class));
                break;
            case R.id.button_color_track_view:
                startActivity(new Intent(this, ColorTrackViewActivity.class));
                break;
            case R.id.button_letter_side_view:
                startActivity(new Intent(this, LetterSideViewActivity.class));
                break;
            case R.id.button_sliding_menu_view:
                startActivity(new Intent(this, SlidingMenuViewActivity.class));
                break;
            case R.id.button_indicator_seekbar:
                startActivity(new Intent(this, IndicatorSeekBarActivity.class));
                break;
            case R.id.button2:
                startActivity(new Intent(this, SlidingMenuViewActivity.class));
                break;
            case R.id.button3:
                startActivity(new Intent(this, SlidingMenuViewActivity.class));
                break;
            case R.id.button4:
                startActivity(new Intent(this, SlidingMenuViewActivity.class));
                break;
            case R.id.button5:
                startActivity(new Intent(this, SlidingMenuViewActivity.class));
                break;
        }
    }

}

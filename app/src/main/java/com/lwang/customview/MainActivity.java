package com.lwang.customview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.lwang.customview.colortrackview.ColorTrackViewActivity;
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

    @OnClick({R.id.button_wave_view, R.id.button_step_qq_view, R.id.button_color_track_view})
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
        }
    }

}

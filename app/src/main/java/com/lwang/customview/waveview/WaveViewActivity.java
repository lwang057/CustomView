package com.lwang.customview.waveview;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.lwang.customview.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author lwang
 * @date 2018/5/22
 * @description 波浪船页面
 */
public class WaveViewActivity extends AppCompatActivity {

    @BindView(R.id.wave_view)
    WaveView mWaveView;
    @BindView(R.id.img_select)
    ImageView mImgSelect;
    private WaveHelper mWaveHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wave_view);
        ButterKnife.bind(this);

        mWaveHelper = new WaveHelper(mWaveView, ContextCompat.getColor(this, R.color.colorAccent),
                ContextCompat.getColor(this, R.color.colorAccent), mImgSelect);
        mWaveHelper.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mWaveHelper.cancel();
    }

}

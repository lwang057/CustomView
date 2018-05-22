package com.lwang.customview.waveview;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.lwang.customview.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WaveViewActivity extends AppCompatActivity {

    @BindView(R.id.wave_view)
    WaveView waveView;
    @BindView(R.id.img_select)
    ImageView imgSelect;
    private WaveHelper waveHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wave_view);
        ButterKnife.bind(this);

        waveHelper = new WaveHelper(waveView, ContextCompat.getColor(this, R.color.colorAccent),
                ContextCompat.getColor(this, R.color.colorAccent), imgSelect);
        waveHelper.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        waveHelper.cancel();
    }

}

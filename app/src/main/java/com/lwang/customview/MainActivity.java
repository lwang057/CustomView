package com.lwang.customview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

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

    @OnClick({R.id.button_wave_view})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.button_wave_view:
                startActivity(new Intent(this, WaveViewActivity.class));
                break;
        }
    }

}

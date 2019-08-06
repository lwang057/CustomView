package com.lwang.customview.floatview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.lwang.customview.R;

public class FloatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_float);

        final View view = findViewById(R.id.zhezhao);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        PopupView myPopupView = (PopupView) findViewById(R.id.myPopupView);
        myPopupView.setOnClickImageListener(new PopupView.OnClickImageListener() {
            @Override
            public void onClickImage(int state) {
                if (state == 2) {
                    view.setVisibility(View.VISIBLE);
                } else {
                    view.setVisibility(View.GONE);
                }
            }
        });

        findViewById(R.id.tv0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FloatActivity.this, "我是小孩子", Toast.LENGTH_SHORT).show();
            }
        });
    }


}

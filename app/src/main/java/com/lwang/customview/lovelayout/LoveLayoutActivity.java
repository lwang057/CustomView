package com.lwang.customview.lovelayout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.lwang.customview.R;

/**
 * @author lwang
 * @date 2018/11/29
 * @description 直播点赞效果View页面
 */
public class LoveLayoutActivity extends AppCompatActivity {

    private LoveLayout loveLayout;
    private Button onClick;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_love_layout);
        loveLayout = (LoveLayout) findViewById(R.id.love_layout);
        onClick = (Button) findViewById(R.id.on_click);

        onClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < 10; i++) {
                    loveLayout.addLove();
                }
            }
        });
    }

}

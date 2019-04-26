package com.lwang.customview.messagebubbleview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.lwang.customview.R;

/**
 * @author lwang
 * @date 2018/11/29
 * @description 消息拖拽View页面
 */
public class MessageBubbleViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_bubble_view);

        MessageBubbleView.attach(findViewById(R.id.tv), new BubbleMessageTouchListener.BubbleDisappearListener() {
            @Override
            public void dismiss(View view) {
                Toast.makeText(MessageBubbleViewActivity.this, "气泡消失", Toast.LENGTH_SHORT).show();
            }
        });

        MessageBubbleView.attach(findViewById(R.id.btn), new BubbleMessageTouchListener.BubbleDisappearListener() {
            @Override
            public void dismiss(View view) {
                Toast.makeText(MessageBubbleViewActivity.this, "气泡消失", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

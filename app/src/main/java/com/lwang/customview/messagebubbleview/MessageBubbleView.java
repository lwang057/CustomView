package com.lwang.customview.messagebubbleview;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author lwang
 * @date 2018/11/29
 * @description 消息拖拽View
 */
public class MessageBubbleView extends View {

    public MessageBubbleView(Context context) {
        super(context);
    }

    public MessageBubbleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }


}

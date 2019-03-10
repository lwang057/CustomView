package com.lwang.customview.messagebubbleview;

import android.content.Context;
import android.graphics.PointF;
import android.util.TypedValue;

/**
 * @author lwang
 * @date 2019/3/10
 * @description 工具类
 */
public class BubbleUtils {

    /**
     * dip 转换成 px
     *
     * @param dip
     * @return
     */
    public static int dip2px(int dip, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }


    /**
     * 获得两点之间的距离 (x1-x2)*(x1-x2)+(y1-y2)*(y1-y2) 开平方
     * Math.sqrt:开平方 Math.pow(p0.y - p1.y, 2):求一个数的平方
     *
     * @param p0
     * @param p1
     * @return
     */
    public static float getDistanceBetween2Points(PointF p0, PointF p1) {
        return (float) Math.sqrt(Math.pow(p0.x - p1.x, 2) + Math.pow(p0.y - p1.y, 2));
    }


    /**
     * G根据百分比获取两点之间的某个点坐标
     *
     * @param p1
     * @param p2
     * @param percent
     * @return
     */
    public static PointF getPointByPercent(PointF p1, PointF p2, float percent) {
        return new PointF(evaluateValue(percent, p1.x, p2.x), evaluateValue(percent, p1.y, p2.y));
    }

    /**
     * 根据分度值，计算从start到end中，fraction位置的值。fraction范围为0 -> 1
     *
     * @param fraction = 1
     * @param start    = 10
     * @param end      = 3
     * @return
     */
    public static float evaluateValue(float fraction, Number start, Number end) {
        return start.floatValue() + (end.floatValue() - start.floatValue()) * fraction;
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        //获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return dip2px(25, context);
    }

}

package com.lwang.customview.lovelayout;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

/**
 * @author lwang
 * @date 2019/3/10
 * @description 自定义路径属性动画
 * TypeEvaluator可以直接改变动画的属性值的，比如要改变颜色属性、位置属性
 */
public class LoveTypeEvaluator implements TypeEvaluator<PointF> {

    private PointF point1, point2;

    public LoveTypeEvaluator(PointF point1, PointF point2) {
        this.point1 = point1;
        this.point2 = point2;
    }

    @Override
    public PointF evaluate(float t, PointF point0, PointF point3) {

        // 改变属性动画的位置 开始套公式 （把贝塞尔曲线的公式拿过来直接套）
        // t 是 [0,1]   公式中有四个点 还有两个点从哪里来（可以从构造函数中来）
        PointF pointF = new PointF();

        pointF.x = point0.x * (1 - t) * (1 - t) * (1 - t)
                + 3 * point1.x * t * (1 - t) * (1 - t)
                + 3 * point2.x * t * t * (1 - t)
                + point3.x * t * t * t;

        pointF.y = point0.y * (1 - t) * (1 - t) * (1 - t)
                + 3 * point1.y * t * (1 - t) * (1 - t)
                + 3 * point2.y * t * t * (1 - t)
                + point3.y * t * t * t;


        return pointF;
    }

}

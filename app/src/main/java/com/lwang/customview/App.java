package com.lwang.customview;

import android.app.Application;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

/**
 * @author lwang
 * @date 2018/7/5
 * @description  全局配置
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 1.显示线程信息  2.显示的方法行数（每一行代表一个方法） 3.隐藏内部方法调用到偏移量  4.LOG TAG
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(true)
                .methodCount(2)
                .methodOffset(5)
                .tag("wang")
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
    }


}

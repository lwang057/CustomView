package com.lwang.customview.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * @Author lwang
 * @Date 2018/5/27 12:48
 * @Description 工具类
 */

public class Utils {

    public static String TAG = "wang";

    public static void log(String s) {
        Log.i(TAG, s);
    }

    public static void showToast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

}

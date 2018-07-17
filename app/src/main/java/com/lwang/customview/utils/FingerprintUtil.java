package com.lwang.customview.utils;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;

/**
 * @author lwang
 * @date 2018/6/13
 * @description 指纹识别工具类
 */

public class FingerprintUtil {


    private static CancellationSignal cancellationSignal;

    public static void callFingerPrint(Context context, final OnCallBackListener listener) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        FingerprintManagerCompat managerCompat = FingerprintManagerCompat.from(context);
        //判断设备是否支持
        if (!managerCompat.isHardwareDetected()) {
            if (listener != null) {
                listener.onSupportFailed();
            }
            return;
        }

        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        //判断设备是否处于安全保护中
        if (!keyguardManager.isKeyguardSecure()) {
            if (listener != null) {
                listener.onInsecurity();
            }
            return;
        }

        //判断设备是否已经注册过指纹
        if (!managerCompat.hasEnrolledFingerprints()) {
            if (listener != null) {
                listener.onEnrollFailed(); //未注册
            }
            return;
        }

        //开始指纹识别
        if (listener != null) {
            listener.onAuthenticationStart();
        }

        //必须重新实例化，否则cancel 过一次就不能再使用了
        cancellationSignal = new CancellationSignal();

        managerCompat.authenticate(null, 0, cancellationSignal, new FingerprintManagerCompat.AuthenticationCallback() {
            // 当出现错误的时候回调此函数，比如多次尝试都失败了的时候，errString是错误信息，比如华为的提示就是：尝试次数过多，请稍后再试。
            @Override
            public void onAuthenticationError(int errMsgId, CharSequence errString) {
                if (listener != null) {
                    listener.onAuthenticationError(errMsgId, errString);
                }
            }

            // 当指纹验证失败的时候会回调此函数，失败之后允许多次尝试，失败次数过多会停止响应一段时间然后再停止sensor的工作
            @Override
            public void onAuthenticationFailed() {
                if (listener != null) {
                    listener.onAuthenticationFailed();
                }
            }

            @Override
            public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                if (listener != null) {
                    listener.onAuthenticationHelp(helpMsgId, helpString);
                }
            }

            // 当验证的指纹成功时会回调此函数，然后不再监听指纹sensor
            @Override
            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                if (listener != null) {
                    listener.onAuthenticationSucceeded(result);
                }
            }
        }, null);

    }

    public interface OnCallBackListener {
        /**
         * xxx
         */
        void onSupportFailed();

        /**
         * xxx
         */
        void onInsecurity();

        /**
         * xxx
         */
        void onEnrollFailed();

        /**
         * xxx
         */
        void onAuthenticationStart();

        /**
         * xxx
         *
         * @param errMsgId
         * @param errString
         */
        void onAuthenticationError(int errMsgId, CharSequence errString);

        /**
         * xxx
         */
        void onAuthenticationFailed();

        /**
         * xxx
         *
         * @param helpMsgId
         * @param helpString
         */
        void onAuthenticationHelp(int helpMsgId, CharSequence helpString);

        /**
         * xxx
         *
         * @param result
         */
        void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result);
    }

    public static void cancel() {
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
        }
    }

}

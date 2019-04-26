package com.lwang.customview.fingerprintview;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;

import com.yonyou.zgcbank.utils.LogUtils;

import java.lang.ref.WeakReference;

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintCore {

    private FingerprintManagerCompat mFingerprintManager;
    private WeakReference<IFingerprintResultListener> listener;
    private CancellationSignal mCancellationSignal;
    private FingerprintManagerCompat.AuthenticationCallback mAuthCallback;
    private CryptoObjectHelper objectHelpers;
    private boolean isCrypto;

    public FingerprintCore(Context context, IFingerprintResultListener listener) {
        mFingerprintManager = getFingerprintManager(context);
        this.listener = new WeakReference<>(listener);
    }

    private FingerprintManagerCompat getFingerprintManager(Context context) {
        FingerprintManagerCompat fingerprintManager = null;
        try {
            fingerprintManager = FingerprintManagerCompat.from(context);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return fingerprintManager;
    }

    /**
     * 检测设备指纹开关
     *
     * @param context
     * @return
     */
    public static boolean isSupport(Context context) {
        boolean isSupport = false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return isSupport;
        }
        FingerprintManagerCompat finger = FingerprintManagerCompat.from(context);
        try {
            isSupport = finger != null && finger.isHardwareDetected() && //硬件不支持
                    ((KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE)).isKeyguardSecure() && //没有屏幕锁
                    finger.hasEnrolledFingerprints();//系统不存在指纹列表
        } catch (Exception e) {
            //防止有些机型没有以上api会抛空指针异常
            return false;
        }
        return isSupport;
    }

    /**
     * 判断是否有指纹识别硬件支持
     * 判断是否有设置密码锁屏
     * 判断是否有录入指纹，有些设备上即使录入了指纹，但是没有开启锁屏密码的话此方法还是返回false
     *
     * @return
     */
    public boolean isSupportFingerprint(Context context) {
        boolean isSupport;
        try {
            isSupport = mFingerprintManager != null && mFingerprintManager.isHardwareDetected() && //硬件不支持
                    ((KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE)).isKeyguardSecure() && //没有屏幕锁
                    mFingerprintManager.hasEnrolledFingerprints();//系统不存在指纹列表
        } catch (Exception e) {
            //防止有些机型没有以上api会抛空指针异常
            return false;
        }
        return isSupport;
    }

    /**
     * 调用指纹识别
     */
    public void startAuthenticate() {
        prepareData();
        try {
            objectHelpers = new CryptoObjectHelper();
            mFingerprintManager.authenticate(objectHelpers.buildCryptoObject(), 0, mCancellationSignal, mAuthCallback, null);
            isCrypto = true;
        } catch (Exception e) {
            mFingerprintManager.authenticate(null, 0, mCancellationSignal, mAuthCallback, null);
            isCrypto = false;
        }
        if (null != listener && null != listener.get()) {
            listener.get().onAuthenticateStart();
        }
    }

    /**
     * 关闭指纹识别
     */
    public void cancelAuthenticate() {
        if (mCancellationSignal != null) {
            mCancellationSignal.cancel();
        }
    }

    private void prepareData() {

        // 必须重新实例化，否则cancel 过一次就不能再使用了
        mCancellationSignal = new CancellationSignal();

        if (mAuthCallback == null) {
            mAuthCallback = new FingerprintManagerCompat.AuthenticationCallback() {

                @Override
                public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    try {
                        if (isCrypto) {
                            //doFinal方法会检查结果是不是会拦截或者篡改过，如果是的话会抛出一个异常，异常的时候都将认证当做是失败来处理
                            result.getCryptoObject().getCipher().doFinal();
                        }
                        LogUtils.i("成功----->isCrypto:::" + isCrypto);
                        if (null != listener && null != listener.get()) {
                            listener.get().onAuthenticateSuccess(true);
                        }
                    } catch (Exception e) {
                        if (null != listener && null != listener.get()) {
                            listener.get().onAuthenticateSuccess(false);
                        }
                    }
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    LogUtils.i("失败----->");
                    if (null != listener && null != listener.get()) {
                        listener.get().onAuthenticateFailed();
                    }
                }

                @Override
                public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                    super.onAuthenticationHelp(helpMsgId, helpString);
                    // 建议根据参数helpString返回值，并且仅针对特定的机型做处理，并不能保证所有厂商返回的状态一致
                    LogUtils.i("帮助----->helpMsgId:::" + helpMsgId + ", helpString:::" + helpString.toString());
                    if (null != listener && null != listener.get()) {
                        listener.get().onAuthenticateHelp(helpString);
                    }
                }

                @Override
                public void onAuthenticationError(int errMsgId, CharSequence errString) {
                    super.onAuthenticationError(errMsgId, errString);
                    // 多次指纹密码验证错误后，进入此方法；并且，不能短时间内调用指纹验证,一般间隔从几秒到几十秒不等
                    // 这种情况不建议重试，建议提示用户用其他的方式解锁或者认证
                    LogUtils.i("错误----->errMsgId:::" + errMsgId + ", errString:::" + errString);
                    if (null != listener && null != listener.get()) {
                        listener.get().onAuthenticateError(errMsgId, errString);
                    }
                }
            };
        }
    }

    public void onDestroy() {
        cancelAuthenticate();
        mAuthCallback = null;
        listener = null;
        mCancellationSignal = null;
        mFingerprintManager = null;
        if (objectHelpers != null) {
            objectHelpers.onDestroy();
            objectHelpers = null;
        }
    }

    /**
     * 指纹识别回调接口
     */
    public interface IFingerprintResultListener {

        /**
         * 指纹识别开始
         */
        void onAuthenticateStart();

        /**
         * 指纹识别成功
         */
        void onAuthenticateSuccess(boolean isAuthSuccess);

        /**
         * 指纹识别失败
         */
        void onAuthenticateFailed();

        /**
         * 指纹识别帮助
         */
        void onAuthenticateHelp(CharSequence helpString);

        /**
         * 指纹识别发生错误-不可短暂恢复
         */
        void onAuthenticateError(int errMsgId, CharSequence errString);
    }

}

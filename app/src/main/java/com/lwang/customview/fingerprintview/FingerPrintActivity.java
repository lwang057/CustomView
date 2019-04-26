package com.lwang.customview.fingerprintview;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lwang.customview.R;
import com.lwang.customview.utils.Utils;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author lwang
 * @date 2019/04/26
 * @description
 */
public class FingerPrintActivity extends AppCompatActivity {

    private FingerprintCore mFingerprintCore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mFingerprintCore = new FingerprintCore(this, mResultListener);
        }
    }

    @OnClick({R.id.button_finger_view})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_finger_view:
                if (!FingerprintCore.isSupport(this)) {
                    Utils.showToast(this, "您尚未设置指纹或密码锁屏，请前往设置进行开启。");
                    return;
                }
                mFingerprintCore.startAuthenticate();
                break;
            default:
                break;
        }
    }


    /**
     * 指纹识别回调结果
     */
    private FingerprintCore.IFingerprintResultListener mResultListener = new FingerprintCore.IFingerprintResultListener() {

        @Override
        public void onAuthenticateStart() {
            Utils.showToast(FingerPrintActivity.this, "请进行指纹识别");
        }

        @Override
        public void onAuthenticateSuccess(boolean isAuthSuccess) {
            if (isAuthSuccess) {
                Utils.showToast(FingerPrintActivity.this, "指纹识别成功");
            } else {
                Utils.showToast(FingerPrintActivity.this, "指纹识别异常");
            }
        }

        @Override
        public void onAuthenticateFailed() {
            Utils.showToast(FingerPrintActivity.this, "指纹识别失败");
        }

        @Override
        public void onAuthenticateHelp(CharSequence helpString) {
            if (helpString != null) Utils.showToast(FingerPrintActivity.this, helpString.toString());
        }

        @Override
        public void onAuthenticateError(int errMsgId, CharSequence errString) {
            if (5 != errMsgId) Utils.showToast(FingerPrintActivity.this, errString.toString()); // 5是取消指纹操作，不提示Toast
        }
    };

}

package com.lwang.customview.utils;

import android.support.v7.app.AppCompatActivity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.lwang.customview.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * @author lwang
 * @date 2020/7/3
 * @description 短信自动读取类
 */
public class MsgAutoReadActivity extends AppCompatActivity {

    private SMSReceiver smsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        smsReceiver = new SMSReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, filter);
    }

//    <uses-permission android:name="android.permission.RECEIVE_SMS" /> <!-- 接收短信权限 -->
//    <uses-permission android:name="android.permission.READ_SMS" />    <!-- 读取短信权限 -->


    class SMSReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            getMsg(context, intent);
        }
    }


    private void getMsg(Context context, Intent intent) {

        //1.解析短信内容
        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
        assert pdus != null;
        for (Object pdu : pdus) {

            //2.获取短信对象
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);

            //3.获取短信对象的基本信息
            String address = sms.getDisplayOriginatingAddress();
            String body = sms.getMessageBody();

            Log.i("wang", "address:" + address + "---body:" + body);
            getCode(context, body);
        }
    }


    private void getCode(Context context, String body) {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        Pattern pattern1 = Pattern.compile("(\\d{6})");//提取六位数字
        Matcher matcher1 = pattern1.matcher(body);//进行匹配

        Pattern pattern2 = Pattern.compile("(\\d{4})");//提取四位数字
        Matcher matcher2 = pattern2.matcher(body);//进行匹配

        if (matcher1.find()) {//匹配成功
            String code = matcher1.group(0);

            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", code);
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
            Toast.makeText(context, "验证码复制成功", Toast.LENGTH_SHORT).show();
            Log.i("wang", "onReceive: " + code);
        } else if (matcher2.find()) {
            String code = matcher2.group(0);

            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", code);
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
            Toast.makeText(context, "验证码复制成功", Toast.LENGTH_SHORT).show();
            Log.i("wang", "onReceive: " + code);
        } else {
            Toast.makeText(context, "未检测到验证码", Toast.LENGTH_SHORT).show();
            Log.i("wang", "onReceive: " + "未检测到验证码");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销广播
        unregisterReceiver(smsReceiver);
    }
}
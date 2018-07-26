package com.lwang.customview.lockpatternview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.lwang.customview.R
import com.lwang.customview.utils.Utils
import kotlinx.android.synthetic.main.activity_lock_pattern_view.*

/**
 * @author lwang
 * @date 2018/7/4
 * @description 九宫格锁屏页面
 */
class LockPatternViewActivity : AppCompatActivity() {

    var pwd = ""
    var mIsOk = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_pattern_view)

        lock_pattern_view.setLockPatternListener(object : LockPatternView.LockPatternListener {
            override fun lock(password: String) {
                Utils.log("password:::" + password)
                if (!mIsOk) {
                    pwd = password
                    Utils.log("pwd:::" + pwd)
                    mIsOk = true
                }

                if (pwd.equals(password)) {
                    lock_pattern_view.clearSelect()
                }else{
                    lock_pattern_view.showSelectError()
                }
            }
        })
    }


}
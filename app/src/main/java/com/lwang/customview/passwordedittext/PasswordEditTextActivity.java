package com.lwang.customview.passwordedittext;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.lwang.customview.R;
import com.lwang.customview.utils.Utils;
import com.lwang.customview.utils.dialog.CommonDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author lwang
 * @date 2018/10/31
 * @description 自定义密码输入框与键盘界面
 */
public class PasswordEditTextActivity extends AppCompatActivity {

    @BindView(R.id.image)
    ImageView image;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_edittext);
        ButterKnife.bind(this);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    /**
     * 弹出dialog  从底部并且带动画
     */
    private void showDialog() {

        final CommonDialog.Builder builder = new CommonDialog.Builder(this);
        builder.setView(R.layout.dialog_customer_keyboard)
                .fromBottom()
                .fullWidth()
                .create()
                .show();

        final PasswordEditText mPasswordEt = builder.getView(R.id.password_edit_text);
        CustomerKeyboard mCustomerKeyboard = builder.getView(R.id.custom_key_board);
        ImageView mDeleteDialog = builder.getView(R.id.delete_dialog);

        // 设置键盘点击回掉
        mCustomerKeyboard.setOnCustomerKeyboardClickListener(new CustomerKeyboard.CustomerKeyboardClickListener() {
            @Override
            public void click(String number) {
                mPasswordEt.addPassword(number);
            }

            @Override
            public void delete() {
                mPasswordEt.deleteLastPassword();
            }
        });

        // 设置密码输入完成的回调
        mPasswordEt.setOnPasswordFullListener(new PasswordEditText.PasswordFullListener() {
            @Override
            public void passwordFull(String password) {
                builder.dismiss();
                Utils.showToast(PasswordEditTextActivity.this, password);
            }
        });

        // 关闭支付框
        mDeleteDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });
    }
}

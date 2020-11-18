package com.lwang.customview.qrcodeview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.lwang.customview.R;
import com.lwang.customview.utils.zxing.QRCodeUtil;


public class QRCodeViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_view);

        //1.准备好背景图和生成二维码
        Bitmap bitmap1 = ((BitmapDrawable) getResources().getDrawable(R.mipmap.bg_share)).getBitmap();
        Bitmap bitmap2 = QRCodeUtil.createQRCodeBitmap("https://www.baidu.com", dp2Px(this, 60));

        //2.将二维码按比例放大
        Matrix matrix = new Matrix();
        matrix.preScale((float) 1.183, (float) 1.183);
        Bitmap bitmap3 = Bitmap.createBitmap(bitmap2, 0, 0, bitmap2.getWidth(), bitmap2.getHeight(), matrix, false);

        //3.合成背景图和放大后的二维码
        Bitmap bitmap = Bitmap.createBitmap(bitmap1.getWidth(), bitmap1.getHeight(), bitmap1.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bitmap1, new Matrix(), null);
        canvas.drawBitmap(bitmap3, dp2Px(this, 69), bitmap1.getHeight() - dp2Px(this, 146), null);


        ImageView iv = (ImageView) findViewById(R.id.iv);
        iv.setImageBitmap(bitmap);
    }

    public static int dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}

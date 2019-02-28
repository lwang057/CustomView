package com.lwang.customview.guideview;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lwang.customview.R;
import com.lwang.customview.guideview.pager.BasePager;
import com.lwang.customview.guideview.pager.GuidePageThree;
import com.lwang.customview.guideview.pager.GuidePagerOne;
import com.lwang.customview.guideview.pager.GuidePagerTwo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author lwang
 * @date 2019/02/28
 * @description 引导页界面
 */
public class GuideActivity extends AppCompatActivity {

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    List<BasePager> pagers = new ArrayList<>();
    @BindView(R.id.ll)
    LinearLayout ll;//添加小圆点的布局
    private ImageView[] images;//小圆点的数组

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);

        pagers.add(new GuidePagerOne(this));
        pagers.add(new GuidePagerTwo(this));
        pagers.add(new GuidePageThree(this));
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                for (int i = 0; i < images.length; i++) {
                    if (i == arg0) {
                        images[i].setImageResource(R.mipmap.point_sky);
                    } else {
                        images[i].setImageResource(R.mipmap.icon_point);
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        initDot();
    }


    public class ViewPagerAdapter extends PagerAdapter {
        private Context context;

        public ViewPagerAdapter(Context context) {
            this.context = context;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            BasePager currentPager = pagers.get(position);
            View currentPagerView = currentPager.initView(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            currentPagerView.setLayoutParams(lp);
            container.addView(currentPagerView);
            return currentPagerView;
        }

        @Override
        public int getCount() {
            return pagers.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }


    /**
     * 初始化小圆点的方法 8665 7256 5000 5500
     */
    private void initDot() {
        images = new ImageView[pagers.size()];// 小圆点具体有多少个，就看有多少张图片
        for (int i = 0; i < pagers.size(); i++) {
            ImageView iv = new ImageView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.weight = 1;// 让他们权重都为1，注意这里是LinearLayout.LayoutParams.
            lp.setMargins(15, 0, 0, 0);
            iv.setLayoutParams(lp);
            if (i == 0) {// 这里默认让第一个圆点是蓝色，因为是第一页嘛
                iv.setImageResource(R.mipmap.point_sky);
            } else {// 其他为灰色
                iv.setImageResource(R.mipmap.icon_point);
            }
            images[i] = iv;// 把创建出来的ImageView放到这个数组里面
            ll.addView(images[i]);// 添加到刚写的那个空的线性布局中去
        }
    }

}

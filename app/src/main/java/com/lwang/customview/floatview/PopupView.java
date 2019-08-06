package com.lwang.customview.floatview;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lwang.customview.utils.Utils;

import java.util.ArrayList;

public class PopupView extends RelativeLayout {

    private Context context;
    private FloatView floatView;
    private RecyclerView recyclerView;
    private ArrayList<String> list = new ArrayList<>();
    private LinearLayout linearLayout;
    private PopupViewAdapter adapter;
    private int viewHeight;
    private int viewWidth;
    private LinearLayoutManager layoutManager;
    private boolean isMove = false;


    public PopupView(Context context) {
        this(context, null);
    }

    public PopupView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PopupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // 初始化悬浮图片和弹框列表
        floatView = new FloatView(context);
        recyclerView = new RecyclerView(context);

        // 设置recyclerView与myFloatView
        setRecyclerView();
        setFloatView();

        // 初始化MyLinerLayout，并将recyclerView添加进去
        linearLayout = new MyLinerLayout(context);
        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.addView(recyclerView);

        // 添加到布局中
        addView(linearLayout);
        addView(floatView);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //为了重新定位位置，防止更新的时候onLayout的触发导致数据位置发生变化
        if (l == 0 && t == 0 && isMove) {
            return;
        }
        super.onLayout(changed, l, t, r, b);
    }

    /**
     * 对recyclerView进行设置
     */
    public void setRecyclerView() {
        list.clear();
        for (int i = 0; i < 20; i++) {
            list.add("HelloWorld" + i);
        }

        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setOverScrollMode(OVER_SCROLL_NEVER); //去掉recyclerView上下边界阴影
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        adapter = new PopupViewAdapter(list, context);
        recyclerView.setAdapter(adapter);

        // 设置recyclerView滑动的监听
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //自动滚动或者用手指拖动的时候 防止点击图片隐藏
                if (newState == RecyclerView.SCROLL_STATE_SETTLING || newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    floatView.setIsCanClick(false);
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //停止滚动状态可以点击
                    floatView.setIsCanClick(true);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //只要滚动就清除掉动画
                adapter.setCancelAnimate(true);
                adapter.setCancelEndAnimate(true);
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        //处理显示的个数
        ViewTreeObserver viewTreeObserver = recyclerView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                //移除监听，只用于布局初始化
                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                //传入 RecyclerView 高度，并做一些 Adapter 的初始化工作
                int num = list.size();
                if (list.size() > 5) {
                    num = 5;
                }
                viewHeight = adapter.getViewHeight() * num;
                viewWidth = linearLayout.getWidth();

                // 重新设置linearLayout的高度，并将其隐藏
                LayoutParams layoutParams = (LayoutParams) linearLayout.getLayoutParams();
                layoutParams.height = viewHeight;
                linearLayout.setLayoutParams(layoutParams);
                linearLayout.setVisibility(INVISIBLE);

                //设置初始化位置（左上右下）
                isMove = true;
                linearLayout.layout(
                        (int) (floatView.getCenterX() + floatView.getFloatWidth() / 2 - viewWidth - dip2px(context, 20)),
                        (int) (floatView.getCenterY() - floatView.getFloatHeight() / 2 - viewHeight - dip2px(context, 8)),
                        (int) (floatView.getCenterX() + floatView.getFloatWidth() / 2 - dip2px(context, 20)),
                        (int) (floatView.getCenterY() - floatView.getFloatHeight() / 2 - dip2px(context, 8)));

                // 重绘view ( invalidate()用于主线程、postInvalidate()用于子线程 )
                linearLayout.postInvalidate();

                adapter.setLayoutGravity(floatView.RIGHT_TOP);
            }
        });

        //点击RecycleView的条目
        adapter.setOnClickContent(new PopupViewAdapter.OnClickContentListener() {
            @Override
            public void onClickContent(String text, int position) {
                Utils.showToast(context, "position:::" + position);
            }
        });

        //处理动画隐藏之后  按钮可以点击 以及隐藏遮罩 和 执行关闭动画
        adapter.setOnEndAnimal(new PopupViewAdapter.OnEndAnimalListener() {
            @Override
            public void onEndAnimal() {
                floatView.setCanClickEnble(true);
                linearLayout.setVisibility(INVISIBLE);
                if (onFinishAnimalListener != null) {
                    onFinishAnimalListener.onFinishAnimal();
                }
                floatView.startOpenAndCloseAnimal(floatView.getTextWidth() + floatView.getFloatWidth() / 2 + 30, 0);
            }
        });

        //开始动画结束 让按钮可以点击
        adapter.setOnStartAnimal(new PopupViewAdapter.OnStartAnimalListener() {
            @Override
            public void onStartAnimal() {
                floatView.setCanClickEnble(true);
            }
        });
    }

    /**
     * 对floatView进行设置
     */
    private void setFloatView() {

        // 点击悬浮图片回掉 根据位置动态显示数据
        floatView.setOnClickButtonListener(new FloatView.OnClickButtonListener() {
            @Override
            public void onClickButton(int state, int OpenState) {
                //显示遮罩的回调用
                if (onClickImageListener != null) {
                    onClickImageListener.onClickImage(OpenState);
                }
                switch (state) {
                    case 1://左上方
                        if (OpenState == 2) {
                            linearLayout.setVisibility(VISIBLE);
                            adapter.setCancelAnimate(false);
                            adapter.startOtherViewAnimate(layoutManager.findFirstVisibleItemPosition());
                        } else {
                            adapter.setCancelEndAnimate(false);
                            adapter.startOtherEndAnimate(layoutManager.findFirstVisibleItemPosition());
                        }
                        break;
                    case 2://右上方
                        if (OpenState == 2) {
                            linearLayout.setVisibility(VISIBLE);
                            adapter.setCancelAnimate(false);
                            adapter.startOtherViewAnimate(layoutManager.findFirstVisibleItemPosition());
                        } else {
                            adapter.setCancelEndAnimate(false);
                            adapter.startOtherEndAnimate(layoutManager.findFirstVisibleItemPosition());
                        }
                        break;
                    case 3://左下方
                        if (OpenState == 2) {
                            linearLayout.setVisibility(VISIBLE);
                            adapter.setCancelAnimate(false);
                            adapter.startViewAnimate(layoutManager.findFirstVisibleItemPosition());
                        } else {
                            adapter.setCancelEndAnimate(false);
                            adapter.startEndAnimate(layoutManager.findFirstVisibleItemPosition());
                        }
                        break;
                    case 4://右下方
                        if (OpenState == 2) {
                            linearLayout.setVisibility(VISIBLE);
                            adapter.setCancelAnimate(false);
                            adapter.startViewAnimate(layoutManager.findFirstVisibleItemPosition());
                        } else {
                            adapter.setCancelEndAnimate(false);
                            adapter.startEndAnimate(layoutManager.findFirstVisibleItemPosition());
                        }
                        break;
                }
            }
        });
        //悬浮图片移动时刻调用此方法 更新数据的位置 以及数据的背景 数据的靠左靠右显示
        floatView.setOnMoveListener(new FloatView.OnMoveListener() {
            @Override
            public void onMoveListener(int state) {

                switch (state) {
                    case 1://左上方
                        isMove = true;
                        adapter.setLeftOrRight(2);
                        adapter.notifyDataSetChanged();
                        linearLayout.layout((int) (floatView.getCenterX() - floatView.getFloatWidth() / 2),
                                (int) (floatView.getCenterY() + floatView.getFloatHeight() / 2 - dip2px(context, 10) + dip2px(context, 8))
                                , (int) (floatView.getCenterX() + floatView.getFloatWidth() / 2 + viewWidth)
                                , (int) (floatView.getCenterY() + floatView.getFloatHeight() / 2 + viewHeight + dip2px(context, 8)));
                        linearLayout.postInvalidate();
                        adapter.setLayoutGravity(FloatView.LEFT_TOP);
                        break;
                    case 2://右上方
                        isMove = true;
                        adapter.setLeftOrRight(1);
                        adapter.notifyDataSetChanged();
                        linearLayout.layout((int) (floatView.getCenterX() + floatView.getFloatWidth() / 2 - viewWidth),
                                (int) (floatView.getCenterY() + floatView.getFloatHeight() / 2 - dip2px(context, 10) + dip2px(context, 8))
                                , (int) (floatView.getCenterX() + floatView.getFloatWidth() / 2)
                                , (int) (floatView.getCenterY() + floatView.getFloatHeight() / 2 + viewHeight + dip2px(context, 8)));
                        linearLayout.postInvalidate();
                        adapter.setLayoutGravity(FloatView.RIGHT_TOP);
                        break;
                    case 3://左下方
                        isMove = true;
                        adapter.setLeftOrRight(2);
                        adapter.notifyDataSetChanged();
                        linearLayout.layout((int) (floatView.getCenterX() - floatView.getFloatWidth() / 2),
                                (int) (floatView.getCenterY() - floatView.getFloatHeight() / 2 - viewHeight - dip2px(context, 8))
                                , (int) (floatView.getCenterX() + floatView.getFloatWidth() / 2 + viewWidth)
                                , (int) (floatView.getCenterY() - floatView.getFloatHeight() / 2 - dip2px(context, 8)));
                        linearLayout.postInvalidate();
                        adapter.setLayoutGravity(FloatView.LEFT_TOP);
                        break;
                    case 4://右下方
                        isMove = true;
                        adapter.setLeftOrRight(1);
                        adapter.notifyDataSetChanged();
                        linearLayout.layout((int) (floatView.getCenterX() + floatView.getFloatWidth() / 2 - viewWidth),
                                (int) (floatView.getCenterY() - floatView.getFloatHeight() / 2 - viewHeight - dip2px(context, 8))
                                , (int) (floatView.getCenterX() + floatView.getFloatWidth() / 2)
                                , (int) (floatView.getCenterY() - floatView.getFloatHeight() / 2 - dip2px(context, 8)));
                        linearLayout.postInvalidate();
                        adapter.setLayoutGravity(FloatView.RIGHT_TOP);
                        break;
                }
            }
        });
    }


    /**
     * 自定义MyLinerLayout布局
     * 内部类
     */
    class MyLinerLayout extends LinearLayout {

        public MyLinerLayout(Context context) {
            this(context, null);
        }

        public MyLinerLayout(Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public MyLinerLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            //Log.e("chaged++",changed+"----"+l+"----"+t+"---"+r+"---"+b);
            if (l == 0 && t == 0) {
                return;
            }
            super.onLayout(changed, l, t, r, b);
        }
    }


    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    private OnClickImageListener onClickImageListener;

    public interface OnClickImageListener {
        void onClickImage(int state);
    }

    public void setOnClickImageListener(OnClickImageListener onClickImageListener) {
        this.onClickImageListener = onClickImageListener;
    }

    private OnFinishAnimalListener onFinishAnimalListener;

    public interface OnFinishAnimalListener {
        void onFinishAnimal();
    }

    public void setOnFinishAnimal(OnFinishAnimalListener onFinishAnimalListener) {
        this.onFinishAnimalListener = onFinishAnimalListener;
    }


}

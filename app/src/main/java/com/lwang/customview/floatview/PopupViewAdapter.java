package com.lwang.customview.floatview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lwang.customview.R;

import java.util.ArrayList;
import java.util.List;

public class PopupViewAdapter extends RecyclerView.Adapter<PopupViewAdapter.ViewHolder> {

    private List<String> data;
    private ViewHolder viewHolder;
    boolean animationsLocked = false;
    boolean endAnimationLocked = false;
    private ArrayList<View> ar = new ArrayList<>();
    private int lastAnimatedPosition = -1;
    private int lastEndAnimatedPosition = -1;
    private int length;
    private int num;
    private int realLength;
    private int showState = 1;//1代表右边 2代表左边
    private int layoutGravity = 1;


    public PopupViewAdapter(List<String> data, Context context) {
        this.data = data;
        for (int i = 0; i < data.size(); i++) {
            ar.add(new View(context));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_body, parent, false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // 取消adapter复用
        holder.setIsRecyclable(false);

        // 设置item背景 1代表右边 2代表左边
        if (showState == 1) {
            holder.textView.setBackgroundResource(R.drawable.float_right_shape);
        } else if (showState == 2) {
            holder.textView.setBackgroundResource(R.drawable.float_left_shape);
        }

        // 设置item对齐方式
        if (layoutGravity == 1) {
            ((LinearLayout) holder.itemView).setGravity(Gravity.LEFT);
        } else if (layoutGravity == 2) {
            ((LinearLayout) holder.itemView).setGravity(Gravity.RIGHT);
        }

        holder.textView.setText(data.get(position));
        ar.set(position, holder.itemView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickContentListener != null) {
                    onClickContentListener.onClickContent(data.get(position), position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            textView = itemView.findViewById(R.id.tv);
        }
    }

    /**
     * 设置view显示在左边还是右边
     *
     * @param showState 1代表右边 2代表左边
     */
    public void setLeftOrRight(int showState) {
        this.showState = showState;
    }

    /**
     * 设置recyclerView的对齐方式
     *
     * @param layoutGravity
     */
    public void setLayoutGravity(int layoutGravity) {
        this.layoutGravity = layoutGravity;
        for (int i = 0; i < data.size(); i++) {
            if (ar.get(i) instanceof LinearLayout) {
                LinearLayout view = (LinearLayout) ar.get(i);
                if (layoutGravity == 1) {
                    view.setGravity(Gravity.LEFT);
                } else if (layoutGravity == 2) {
                    view.setGravity(Gravity.RIGHT);
                }
            }
        }
    }

    /**
     * 获取item的高度
     *
     * @return
     */
    public int getViewHeight() {
        int height = viewHolder.itemView.getHeight();
        return height;
    }

    public void setCancelAnimate(boolean animationsLocked) {
        this.animationsLocked = animationsLocked;
        this.lastAnimatedPosition = -1;
    }


    public void startViewAnimate(int firstPosition) {
        int num = 0;
        length = firstPosition + 6;
        if (firstPosition + 6 > ar.size()) {
            length = firstPosition + 5;
        }
        if (data.size() <= 5) {
            length = data.size();
        }
        for (int i = firstPosition; i < length; i++) {
            startBubbleRestAnim(ar.get(i), i, num);
            num++;
        }
    }

    public void setCancelEndAnimate(boolean endAnimationLocked) {
        this.endAnimationLocked = endAnimationLocked;
        this.lastEndAnimatedPosition = -1;
    }


    public void startEndAnimate(int firstPosition) {
        int num = 0;
        length = firstPosition + 6;
        if (firstPosition + 6 > ar.size()) {
            length = firstPosition + 5;
        }
        if (data.size() <= 5) {
            length = data.size();
        }

        for (int i = firstPosition; i < length; i++) {
            startEndAnim(ar.get(i), i, num);
            num++;
        }
    }

    public void startOtherViewAnimate(int firstPosition) {
        realLength = 0;
        length = firstPosition + 6;
        num = 5;

        if (firstPosition + 6 > ar.size()) {
            length = firstPosition + 5;
            num = 4;
        }
        if (data.size() <= 5) {
            length = data.size();
            num = data.size() - 1;
        }
        realLength = num;
        for (int i = firstPosition; i < length; i++) {
            startOtherRestAnim(ar.get(i), i, num);
            num--;
        }
    }

    public void startOtherEndAnimate(int firstPosition) {
        num = 5;
        length = firstPosition + 6;
        if (firstPosition + 6 > ar.size()) {
            length = firstPosition + 5;
            num = 4;
        }
        if (data.size() <= 5) {
            length = data.size();
            num = data.size() - 1;
        }
        realLength = num;
        for (int i = firstPosition; i < length; i++) {
            startOtherEndAnim(ar.get(i), i, num);
            num--;
        }
    }

    private void startBubbleRestAnim(final View view, final int postion, int num) {
        if (animationsLocked) return;
        if (postion > lastAnimatedPosition) {
            lastAnimatedPosition = postion;
            view.setTranslationY(300);
            view.setAlpha(0f);
            view.animate().
                    translationY(0)
                    .setDuration(300)
                    .alpha(1.0f)
                    .setInterpolator(new OvershootInterpolator(1.2f))
                    .setStartDelay(20 * (num + 1))
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            /* animationsLocked = true;*/
                            view.clearAnimation();
                            if (postion == length - 1) {
                                //说明动画执行完成
                                if (onStartAnimalListener != null) {
                                    onStartAnimalListener.onStartAnimal();
                                }
                            }
                        }
                    })
                    .start();
        }
    }

    private void startEndAnim(final View view, final int postion, final int num) {
        if (endAnimationLocked) return;
        if (postion > lastEndAnimatedPosition) {
            lastEndAnimatedPosition = postion;
            view.animate().
                    translationY(view.getHeight() + view.getHeight() * 3 / 4)
                    .setDuration(100 + num * 50)
                    .alpha(0f)
                    .setInterpolator(new AccelerateInterpolator(1f))
                    .setStartDelay(60 * (num + 1))
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            /* endAnimationLocked = true;*/
                            view.clearAnimation();
                            if (postion == length - 1) {
                                //说明动画执行完成
                                if (onEndAnimalListener != null) {
                                    onEndAnimalListener.onEndAnimal();
                                }
                            }
                        }
                    })
                    .start();
        }
    }

    private void startOtherRestAnim(final View view, final int postion, final int num) {
        if (animationsLocked) return;
        if (postion > lastAnimatedPosition) {
            lastAnimatedPosition = postion;
            view.setTranslationY(-300f);
            Log.e("lala+++", "ssss" + postion + view.getY());
            view.setAlpha(0f);
            view.animate().
                    translationY(0)
                    .setDuration(300)
                    .alpha(1.0f)
                    .setInterpolator(new OvershootInterpolator(3f))
                    .setStartDelay(20 * (num + 1))
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            /* animationsLocked = true;*/
                            view.clearAnimation();
                            Log.e("num", num + "----" + realLength);
                            if (num == realLength) {
                                //说明动画执行完成
                                if (onStartAnimalListener != null) {
                                    onStartAnimalListener.onStartAnimal();
                                }
                            }
                        }
                    })
                    .start();
        }
    }

    private void startOtherEndAnim(final View view, final int postion, final int num) {
        if (endAnimationLocked) return;
        if (postion > lastEndAnimatedPosition) {
            lastEndAnimatedPosition = postion;
            view.animate().
                    translationY(-(view.getHeight() + view.getHeight() * 2 / 3))
                    .setDuration(300 + num * 50)
                    .alpha(0f)
                    .setInterpolator(new AccelerateInterpolator(2f))
                    .setStartDelay(20 * (num + 1))
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            /* endAnimationLocked = true;*/
                            view.clearAnimation();
                            if (num == realLength) {
                                //说明动画执行完成
                                if (onEndAnimalListener != null) {
                                    onEndAnimalListener.onEndAnimal();
                                }
                            }

                        }
                    })
                    .start();
        }
    }


    private OnClickContentListener onClickContentListener;

    public interface OnClickContentListener {
        void onClickContent(String text, int positon);
    }

    public void setOnClickContent(OnClickContentListener onClickContentListener) {
        this.onClickContentListener = onClickContentListener;
    }


    private OnEndAnimalListener onEndAnimalListener;

    public interface OnEndAnimalListener {
        void onEndAnimal();
    }

    public void setOnEndAnimal(OnEndAnimalListener onEndAnimalListener) {
        this.onEndAnimalListener = onEndAnimalListener;
    }


    private OnStartAnimalListener onStartAnimalListener;

    public interface OnStartAnimalListener {
        void onStartAnimal();
    }

    public void setOnStartAnimal(OnStartAnimalListener onStartAnimalListener) {
        this.onStartAnimalListener = onStartAnimalListener;
    }


}

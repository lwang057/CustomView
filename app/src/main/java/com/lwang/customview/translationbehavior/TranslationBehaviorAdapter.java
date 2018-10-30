package com.lwang.customview.translationbehavior;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lwang.customview.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author lwang
 * @date 2018/10/30
 * @description 控件联动适配器
 */
public class TranslationBehaviorAdapter extends RecyclerView.Adapter<TranslationBehaviorAdapter.MyViewHolder> {

    private Activity activity;
    private List<String> list;

    public TranslationBehaviorAdapter(Activity activity, List<String> list) {
        this.activity = activity;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.setData(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size() > 0 ? list.size() : 0;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(activity);
            tv = (TextView) itemView.findViewById(R.id.item_tv);
        }

        private void setData(String text) {
            tv.setText(text);
        }
    }

}

package com.lwang.customview.dragswipeview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;


import com.lwang.customview.R;
import com.lwang.customview.recyclerview.OnItemClickListener;
import com.lwang.customview.recyclerview.OnItemMenuClickListener;
import com.lwang.customview.recyclerview.SwipeMenu;
import com.lwang.customview.recyclerview.SwipeMenuBridge;
import com.lwang.customview.recyclerview.SwipeMenuCreator;
import com.lwang.customview.recyclerview.SwipeMenuItem;
import com.lwang.customview.recyclerview.SwipeRecyclerView;
import com.lwang.customview.recyclerview.touch.OnItemMoveListener;
import com.lwang.customview.recyclerview.touch.OnItemStateChangedListener;
import com.lwang.customview.recyclerview.widget.DefaultItemDecoration;
import com.lwang.customview.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author lwang
 * @date 2018/11/29
 * @description recyclerView拖拽Item + 侧滑删除页面
 */
public class DragSwipeListActivity extends AppCompatActivity {

    protected SwipeRecyclerView mRecyclerView;
    protected DragSwipeAdapter mAdapter;
    protected List<String> mDataList = new ArrayList<>();
    protected SwitchCompat mSwitchCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_swipe);

        for (int i = 0; i < 55; i++) {
            mDataList.add("第" + i + "个Item");
        }

        mSwitchCompat = findViewById(R.id.switch_compat);
        mSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 控制是否可以侧滑删除。
                mRecyclerView.setItemViewSwipeEnabled(isChecked);
            }
        });

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DefaultItemDecoration(ContextCompat.getColor(this, R.color.divider_color)));
        mRecyclerView.setOnItemClickListener(mOnItemClickListener); // Item的点击。
        mRecyclerView.setOnItemMenuClickListener(mItemMenuClickListener); // Item的Menu点击。
        mRecyclerView.setSwipeMenuCreator(mSwipeMenuCreator); // 菜单创建器。

        mAdapter = new DragSwipeAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged(mDataList);

        mRecyclerView.setOnItemMoveListener(mOnItemMoveListener);// 监听拖拽和侧滑删除，更新UI和数据源。
        mRecyclerView.setOnItemStateChangedListener(mOnItemStateChangedListener); // 监听Item的手指状态，拖拽、侧滑、松开。

        mRecyclerView.setLongPressDragEnabled(true); // 长按拖拽，默认关闭。
        mRecyclerView.setItemViewSwipeEnabled(false); // 滑动删除，默认关闭。
    }


    /**
     * Item的点击
     */
    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Utils.showToast(DragSwipeListActivity.this, "第" + position + "个");
        }
    };


    /**
     * Item的Menu点击
     */
    private OnItemMenuClickListener mItemMenuClickListener = new OnItemMenuClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge, int position) {
            menuBridge.closeMenu();

            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。

            if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {
                Utils.showToast(DragSwipeListActivity.this, "list第" + position + "; 右侧菜单第" + menuPosition);
            } else if (direction == SwipeRecyclerView.LEFT_DIRECTION) {
                Utils.showToast(DragSwipeListActivity.this, "list第" + position + "; 右侧菜单第" + menuPosition);
            }
        }
    };


    /**
     * 菜单创建器
     */
    private SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int position) {
            int width = getResources().getDimensionPixelSize(R.dimen.dp_70);

            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            // 添加左侧的，如果不添加，则左侧不会出现菜单。
            {
                SwipeMenuItem addItem = new SwipeMenuItem(DragSwipeListActivity.this).setBackground(R.drawable.selector_green).setImage(R.drawable.ic_action_add).setWidth(width).setHeight(height);
                swipeLeftMenu.addMenuItem(addItem); // 添加一个按钮到左侧菜单。

                SwipeMenuItem closeItem = new SwipeMenuItem(DragSwipeListActivity.this).setBackground(R.drawable.selector_red).setImage(R.drawable.ic_action_close).setWidth(width).setHeight(height);
                swipeLeftMenu.addMenuItem(closeItem); // 添加一个按钮到左侧菜单。
            }

            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(DragSwipeListActivity.this).setBackground(R.drawable.selector_red).setImage(R.drawable.ic_action_close).setText("删除").setTextColor(Color.WHITE).setWidth(width).setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。

                SwipeMenuItem closeItem = new SwipeMenuItem(DragSwipeListActivity.this).setBackground(R.drawable.selector_green).setImage(R.drawable.ic_action_add).setWidth(width).setHeight(height);
                swipeRightMenu.addMenuItem(closeItem); // 添加一个按钮到右侧菜单。
            }
        }
    };


    /**
     * 监听拖拽和侧滑删除，更新UI和数据源
     */
    private OnItemMoveListener mOnItemMoveListener = new OnItemMoveListener() {
        @Override
        public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
            // 不同的ViewType不能拖拽换位置。
            if (srcHolder.getItemViewType() != targetHolder.getItemViewType()) return false;

            int fromPosition = srcHolder.getAdapterPosition();
            int toPosition = targetHolder.getAdapterPosition();

            Collections.swap(mDataList, fromPosition, toPosition);
            mAdapter.notifyItemMoved(fromPosition, toPosition);
            return true;// 返回true表示处理了并可以换位置，返回false表示你没有处理并不能换位置。
        }

        @Override
        public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
            int position = srcHolder.getAdapterPosition();

            mDataList.remove(position);
            mAdapter.notifyItemRemoved(position);
            Utils.showToast(DragSwipeListActivity.this, "现在的第" + position + "条被删除");
        }
    };


    /**
     * Item的拖拽/侧滑删除时，手指状态发生变化监听
     */
    private OnItemStateChangedListener mOnItemStateChangedListener = new OnItemStateChangedListener() {
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState == OnItemStateChangedListener.ACTION_STATE_DRAG) {
                // 拖拽的时候背景就透明了，这里我们可以添加一个特殊背景。
                viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(DragSwipeListActivity.this, R.color.white_pressed));
                mSwitchCompat.setText("状态：拖拽-----侧滑删除开关");
            } else if (actionState == OnItemStateChangedListener.ACTION_STATE_SWIPE) {
                mSwitchCompat.setText("状态：滑动删除-----侧滑删除开关");
            } else if (actionState == OnItemStateChangedListener.ACTION_STATE_IDLE) {
                // 在手松开的时候还原背景。
                ViewCompat.setBackground(viewHolder.itemView, ContextCompat.getDrawable(DragSwipeListActivity.this, R.drawable.select_white));
                mSwitchCompat.setText("状态：手指松开-----侧滑删除开关");
            }
        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

}
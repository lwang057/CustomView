package com.lwang.customview.dragswipeview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import com.lwang.customview.R;
import com.lwang.customview.utils.recyclerview.OnItemClickListener;
import com.lwang.customview.utils.recyclerview.OnItemMenuClickListener;
import com.lwang.customview.utils.recyclerview.SwipeMenu;
import com.lwang.customview.utils.recyclerview.SwipeMenuBridge;
import com.lwang.customview.utils.recyclerview.SwipeMenuCreator;
import com.lwang.customview.utils.recyclerview.SwipeMenuItem;
import com.lwang.customview.utils.recyclerview.SwipeRecyclerView;
import com.lwang.customview.utils.Utils;

import java.util.ArrayList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_swipe);

        for (int i = 0; i < 55; i++) {
            mDataList.add("第" + i + "个Item");
        }

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setOnItemClickListener(mOnItemClickListener); // Item的点击。
        mRecyclerView.setOnItemMenuClickListener(mItemMenuClickListener); // Item的Menu点击。
        mRecyclerView.setSwipeMenuCreator(mSwipeMenuCreator); // 菜单创建器。

        mAdapter = new DragSwipeAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged(mDataList);
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
            if (SwipeRecyclerView.RIGHT_DIRECTION == menuBridge.getDirection()) {
                mDataList.remove(position);
                mAdapter.notifyItemRemoved(position);
                Utils.showToast(DragSwipeListActivity.this, "现在的第" + position + "条被删除");
            }
        }
    };


    /**
     * 菜单创建器
     */
    private SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int position) {

            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            int width = getResources().getDimensionPixelSize(R.dimen.dp_70);

            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(DragSwipeListActivity.this).setBackground(R.drawable.selector_red).setImage(R.drawable.ic_action_close).setText("删除").setTextColor(Color.WHITE).setWidth(width).setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单
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
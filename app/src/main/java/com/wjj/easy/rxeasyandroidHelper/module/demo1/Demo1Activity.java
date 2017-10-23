package com.wjj.easy.rxeasyandroidHelper.module.demo1;

import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.wjj.easy.rxeasyandroidHelper.R;
import com.wjj.easy.rxeasyandroidHelper.common.ui.BaseActivity;
import com.wjj.easy.rxeasyandroidHelper.model.ProductItemInfo;
import com.wjj.easy.rxeasyandroidHelper.module.ProductAdapter;
import com.wjj.easy.rxeasyandroidHelper.module.ProductContract;
import com.wjj.easy.rxeasyandroidHelper.module.ProductPresenter;

import java.util.List;

import butterknife.BindView;

/**
 * Demo1
 * Created by wujiajun on 2017/10/18.
 */
public class Demo1Activity extends BaseActivity<ProductPresenter> implements ProductContract.View, BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    ProductAdapter adapter;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout swipeLayout;

    int currentPage;
    int pageNumber = 20;

    @Override
    protected int getContentView() {
        return R.layout.activity_demo1;
    }

    @Override
    protected void initInject() {
        getActivityComponent().inject(this);
    }

    @Override
    protected void initEventAndData() {
        commonTheme();
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
        adapter = new ProductAdapter(R.layout.item_product);
        adapter.setOnLoadMoreListener(this, recyclerView);
        adapter.setEnableLoadMore(true);
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        refresh();
    }

    private void refresh() {
        swipeLayout.setRefreshing(true);
        swipeLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                onRefresh();
            }
        }, 100);
    }

    @Override
    public void onRefresh() {
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentPage = 0;
                adapter.getData().clear();
                adapter.notifyDataSetChanged();
                getPresenter().getData(currentPage, pageNumber);
            }
        }, 100);
    }

    @Override
    public void onLoadMoreRequested() {
        swipeLayout.setEnabled(false);
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                getPresenter().getData(currentPage, pageNumber);
            }
        }, 100);
    }

    @Override
    public void callback(boolean success, List<ProductItemInfo> data) {
        swipeLayout.setRefreshing(false);
        adapter.setEnableLoadMore(true);
        swipeLayout.setEnabled(true);
        if (data == null || data.size() == 0) {
            adapter.loadMoreEnd();
            return;
        }
        adapter.addData(data);
        if (data.size() < pageNumber) {
            adapter.loadMoreEnd();
            return;
        }
        adapter.loadMoreComplete();
        currentPage++;
    }
}

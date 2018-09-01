package com.wyzk.lottery.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.wyzk.lottery.R;
import com.wyzk.lottery.adapter.ManageAdapter;
import com.wyzk.lottery.adapter.OnItemClickListener;
import com.wyzk.lottery.model.RechargeManageModel;
import com.wyzk.lottery.model.ResultReturn;
import com.wyzk.lottery.network.Network;
import com.wyzk.lottery.utils.BuildManager;
import com.wyzk.lottery.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class RechargeManageActivity extends LotteryBaseActivity {


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.title)
    View title;
    private List<RechargeManageModel.RechargeItem> mDataList = new ArrayList<>();

    private int pageIndex = 1;
    private int pageRows = 10;
    private ManageAdapter manageAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rechage_manage);
        ButterKnife.bind(this);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.mipmap.arrow_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        BuildManager.setStatusTrans(this, 1, title);
        RefreshLayout refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setEnableRefresh(false);

        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore(2000);
                pageIndex++;
                getRechargeRecordList(pageIndex, pageRows, 0);
            }
        });

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if (token != null) {
                    pageIndex = 1;
                    getRechargeRecordList(pageIndex, pageRows, 0);
                }
                refreshlayout.finishRefresh(2000);
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        manageAdapter = new ManageAdapter(this, mDataList, R.layout.item_recharge_record);

        recyclerView.setAdapter(manageAdapter);

        manageAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                AdminRechargeDetailActivity.startAdminRechargeDetailActivity(RechargeManageActivity.this, mDataList.get(position));
            }
        });

        getRechargeRecordList(pageIndex, pageRows, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void getRechargeRecordList(int pageIndex, int rowIndex, int chargeStatus) {
        Network.getNetworkInstance().getIntegralApi()
                .getRechargeRecord(token, pageIndex, rowIndex, chargeStatus)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultReturn<RechargeManageModel>>() {
                    @Override
                    public void accept(ResultReturn<RechargeManageModel> result) throws Exception {
                        if (result != null && result.getCode() == ResultReturn.ResultCode.RESULT_OK.getValue()) {
                            if (result != null && result.getData() != null) {
                                mDataList.addAll(result.getData().getRows());
                                manageAdapter.notifyDataSetChanged();
                            }
                        } else {
                            ToastUtil.showToast(RechargeManageActivity.this, "失败");
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        ToastUtil.showToast(RechargeManageActivity.this, "失败");
                    }
                });
    }


}

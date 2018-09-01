package com.wyzk.lottery.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.wyzk.lottery.R;
import com.wyzk.lottery.adapter.RecordAdapter;
import com.wyzk.lottery.model.ChargeModel;
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


public class RechargeRecordActivity extends LotteryBaseActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.title)
    View title;

    private List<ChargeModel.ChargeHistoryModel> mDataList = new ArrayList<>();
    private RecordAdapter recordAdapter;

    private int currentPage = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_record);
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
        refreshLayout.setEnableRefresh(true);
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore(2000);
                currentPage++;
                getChargeRecord();
            }
        });
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recordAdapter = new RecordAdapter(this, mDataList, R.layout.item_recharge_record);
        recyclerView.setAdapter(recordAdapter);
        recordAdapter.setOnItemClickListener(new RecordAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                RechargeDetailActivity.startRechargeDetailActivity(RechargeRecordActivity.this, mDataList.get(position));
            }
        });

        getChargeRecord();
    }


    private void getChargeRecord() {
        Network.getNetworkInstance().getIntegralApi()
                .getChargeHistory(token, currentPage, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultReturn<ChargeModel>>() {
                    @Override
                    public void accept(ResultReturn<ChargeModel> result) {
                        if (result != null && result.getCode() == ResultReturn.ResultCode.RESULT_OK.getValue()) {
                            if (result.getData() != null) {
                                mDataList.clear();
                                mDataList.addAll(result.getData().getRows());
                                recordAdapter.notifyDataSetChanged();
                            }
                        } else {
                            ToastUtil.showToast(RechargeRecordActivity.this, "失败");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        ToastUtil.showToast(RechargeRecordActivity.this, "失败");
                    }
                });
    }

}

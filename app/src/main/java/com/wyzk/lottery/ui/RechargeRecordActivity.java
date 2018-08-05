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
import com.wyzk.lottery.network.Network;
import com.wyzk.lottery.utils.BuildManager;

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
    RecordAdapter recordAdapter;

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
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore(2000);
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
                .getChargeHistory(token, 1, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ChargeModel>() {
                    @Override
                    public void accept(ChargeModel resultReturn) {
                        mDataList.clear();
                        mDataList.addAll(resultReturn.getRows());
                        recordAdapter.notifyDataSetChanged();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                    }
                });
    }

}

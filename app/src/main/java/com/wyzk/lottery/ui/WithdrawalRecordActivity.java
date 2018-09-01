package com.wyzk.lottery.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.wyzk.lottery.R;
import com.wyzk.lottery.adapter.WithdrawalRecordAdapter;
import com.wyzk.lottery.model.ExchangeModel;
import com.wyzk.lottery.model.ResultReturn;
import com.wyzk.lottery.model.UserInfoModel;
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


public class WithdrawalRecordActivity extends LotteryBaseActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.title)
    View title;

    @Bind(R.id.tv_available_integral)
    TextView tv_available_integral;

    private List<ExchangeModel.ExchangeItem> mDataList = new ArrayList<>();
    private WithdrawalRecordAdapter withdrawalRecordAdapter;
    private int currentPage = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal_record);
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
                currentPage++;
                getWithdrawalRecordList();
                refreshlayout.finishLoadmore(2000);
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                currentPage = 1;
                getWithdrawalRecordList();
                refreshlayout.finishRefresh(2000);
            }
        });
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        withdrawalRecordAdapter = new WithdrawalRecordAdapter(this, mDataList, R.layout.item_excharge_record);
        withdrawalRecordAdapter.setOnItemClickListener(new WithdrawalRecordAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                ExchargeDetailActivity.startExchargeDetailActivity(WithdrawalRecordActivity.this, mDataList.get(position));
            }
        });

        recyclerView.setAdapter(withdrawalRecordAdapter);

        getWithdrawalRecordList();

        getUserInfo();
    }

    private void getUserInfo() {
        Network.getNetworkInstance().getUserApi()
                .getUserInfo(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultReturn<UserInfoModel>>() {
                    @Override
                    public void accept(ResultReturn<UserInfoModel> userInfoModelResultReturn) {
                        if (userInfoModelResultReturn != null) {
                            UserInfoModel userInfoModel = userInfoModelResultReturn.getData();
                            if (userInfoModel != null) {
                                tv_available_integral.setText(userInfoModel.getIntegralValue() + "");
                            }
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    private void getWithdrawalRecordList() {
        Network.getNetworkInstance().getIntegralApi()
                .getExchangeHistory(token, currentPage, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultReturn<ExchangeModel>>() {
                    @Override
                    public void accept(ResultReturn<ExchangeModel> result) throws Exception {
                        if (result != null && result.getCode() == ResultReturn.ResultCode.RESULT_OK.getValue()) {
                            if (result.getData() != null) {
                                mDataList.clear();
                                mDataList.addAll(result.getData().getRows());
                                withdrawalRecordAdapter.notifyDataSetChanged();
                            }
                        } else {
                            ToastUtil.showToast(WithdrawalRecordActivity.this, "失败");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        ToastUtil.showToast(WithdrawalRecordActivity.this, "失败");
                    }
                });

    }

}

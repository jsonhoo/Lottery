package com.wyzk.lottery.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.wyzk.lottery.R;
import com.wyzk.lottery.adapter.ManageAdapter;
import com.wyzk.lottery.adapter.OnItemClickListener;
import com.wyzk.lottery.constant.IConst;
import com.wyzk.lottery.model.RechargeManageModel;
import com.wyzk.lottery.network.Network;
import com.wyzk.lottery.ui.AdminRechargeDetailActivity;
import com.wyzk.lottery.utils.ACache;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ChargeManageFragment extends Fragment {

    private int pageIndex = 1;
    private int pageRows = 10;
    private ManageAdapter manageAdapter;
    private List<RechargeManageModel.RechargeItem> mDataList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_rechage_manage, container, false);

        RefreshLayout refreshLayout = (RefreshLayout) view.findViewById(R.id.refreshLayout);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore(2000);
                pageIndex++;
                getRechargeRecordList(pageIndex, pageRows, 0);
            }
        });
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        manageAdapter = new ManageAdapter(getContext(), mDataList, R.layout.item_recharge_record);
        recyclerView.setAdapter(manageAdapter);
        manageAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                AdminRechargeDetailActivity.startAdminRechargeDetailActivity(getContext(), mDataList.get(position));
            }
        });
        getRechargeRecordList(pageIndex, pageRows, 0);
        return view;
    }

    private void getRechargeRecordList(int pageIndex, int rowIndex, int chargeStatus) {
        Network.getNetworkInstance().getIntegralApi()
                .getRechargeRecord(ACache.get(getContext()).getAsString(IConst.TOKEN), pageIndex, rowIndex, chargeStatus)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RechargeManageModel>() {
                    @Override
                    public void accept(RechargeManageModel rechargeManageModel) throws Exception {
                        if (rechargeManageModel != null && rechargeManageModel.getRows() != null) {
                            mDataList.addAll(rechargeManageModel.getRows());
                            manageAdapter.notifyDataSetChanged();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                    }
                });
    }

}

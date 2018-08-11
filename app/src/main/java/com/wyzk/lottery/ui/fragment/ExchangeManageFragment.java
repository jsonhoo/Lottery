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
import com.wyzk.lottery.adapter.ManageExchangeAdapter;
import com.wyzk.lottery.adapter.OnItemClickListener;
import com.wyzk.lottery.constant.IConst;
import com.wyzk.lottery.model.ExchangeModel;
import com.wyzk.lottery.network.Network;
import com.wyzk.lottery.ui.ManageExchangeDetailActivity;
import com.wyzk.lottery.utils.ACache;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ExchangeManageFragment extends Fragment {

    private int pageIndex = 1;
    private int pageRows = 10;
    private ManageExchangeAdapter manageExchangeAdapter;
    private List<ExchangeModel.ExchangeItem> mDataList = new ArrayList<>();

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
                getExchangeList(pageIndex, pageRows, "");
            }
        });
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        manageExchangeAdapter = new ManageExchangeAdapter(getContext(), mDataList, R.layout.item_recharge_record);
        recyclerView.setAdapter(manageExchangeAdapter);
        manageExchangeAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ManageExchangeDetailActivity.startManageExchangeDetailActivity(getContext(),mDataList.get(position));
            }
        });
        getExchangeList(pageIndex, pageRows, "");
        return view;
    }

    private void getExchangeList(int pageIndex, int rowIndex,String username) {
        Network.getNetworkInstance().getIntegralApi()
                .getExchangeList(ACache.get(getContext()).getAsString(IConst.TOKEN), pageIndex, rowIndex, username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ExchangeModel>() {
                    @Override
                    public void accept(ExchangeModel exchangeModel) throws Exception {
                        if (exchangeModel != null && exchangeModel.getRows() != null) {
                            mDataList.addAll(exchangeModel.getRows());
                            manageExchangeAdapter.notifyDataSetChanged();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                    }
                });
    }

}

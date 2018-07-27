package com.wyzk.lottery.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.wyzk.lottery.R;
import com.wyzk.lottery.model.IntegralRecordModel;
import com.wyzk.lottery.model.ResultReturn;
import com.wyzk.lottery.model.UserInfoModel;
import com.wyzk.lottery.network.Network;
import com.wyzk.lottery.utils.BuildManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class MyIntegralActivity extends LotteryBaseActivity {
    @Bind(R.id.person_integral)
    TextView person_integral;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.title)
    View title;
    private List<IntegralRecordModel.RecordModel> mDataList = new ArrayList<>();
    private int pageIndex = 1;
    private int pageRows = 10;
    private HomeAdapter homeAdapter;
    IntegralRecordModel integralRecordModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_integral);
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
                if (integralRecordModel != null && integralRecordModel.getTotal() > pageIndex) {
                    getUserConsumePageList(++pageIndex, pageRows);
                }
                refreshlayout.finishLoadmore(2000);
            }
        });
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(homeAdapter = new HomeAdapter());
        getUserConsumePageList(pageIndex, pageRows);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserInfo();
    }

    private void getUserInfo() {
        Network.getNetworkInstance().getUserApi()
                .getUserInfo(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResultReturn<UserInfoModel>>() {
                    @Override
                    public void call(ResultReturn<UserInfoModel> userInfoModelResultReturn) {
                        if (userInfoModelResultReturn != null) {
                            UserInfoModel user = userInfoModelResultReturn.getData();
                            if (user != null) {
                                person_integral.setText(user.getIntegralValue() + "");
                            }
                        }

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    private void getUserConsumePageList(int pageIndex, int rowIndex) {
        Network.getNetworkInstance().getUserApi()
                .getConsumePageList(token, pageIndex, rowIndex)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<IntegralRecordModel>() {
                    @Override
                    public void call(IntegralRecordModel data) {
                        if (data != null && data.getRows() != null) {
                            integralRecordModel = data;
                            mDataList.addAll(data.getRows());
                            homeAdapter.notifyDataSetChanged();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(MyIntegralActivity.this).inflate(R.layout.item_integral_record, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.roomeId.setText("房间名：" + mDataList.get(position).getRoomName());
            holder.roomeDate.setText("下注时间：" + mDataList.get(position).getBetTime());
            if (mDataList.get(position).getWinLoseValue() > 0) {
                holder.score.setTextColor(Color.parseColor("#FFFF0000"));
            } else if (mDataList.get(position).getWinLoseValue() < 0) {
                holder.score.setTextColor(Color.parseColor("#FF00FF00"));
            } else {
                holder.score.setTextColor(Color.parseColor("#FF323232"));
            }
            holder.score.setText("盈亏：" + mDataList.get(position).getWinLoseValue());
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView roomeId;
            TextView roomeDate;
            TextView score;

            public MyViewHolder(View view) {
                super(view);
                roomeId = (TextView) view.findViewById(R.id.roomeId);
                roomeDate = (TextView) view.findViewById(R.id.roomeDate);
                score = (TextView) view.findViewById(R.id.score);
            }
        }
    }
}

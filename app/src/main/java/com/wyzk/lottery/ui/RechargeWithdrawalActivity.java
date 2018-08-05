package com.wyzk.lottery.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wyzk.lottery.R;
import com.wyzk.lottery.model.ResultReturn;
import com.wyzk.lottery.model.UserInfoModel;
import com.wyzk.lottery.network.Network;
import com.wyzk.lottery.utils.BuildManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class RechargeWithdrawalActivity extends LotteryBaseActivity implements View.OnClickListener {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.title)
    View title;

    @Bind(R.id.tv_money)
    TextView tv_money;

    @Bind(R.id.rl_view_one)
    RelativeLayout rl_view_one;
    @Bind(R.id.rl_view_two)
    RelativeLayout rl_view_two;
    @Bind(R.id.rl_view_three)
    RelativeLayout rl_view_three;
    @Bind(R.id.rl_view_four)
    RelativeLayout rl_view_four;

    private UserInfoModel userInfoModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_withdrawal);
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

        rl_view_one.setOnClickListener(this);
        rl_view_two.setOnClickListener(this);
        rl_view_three.setOnClickListener(this);
        rl_view_four.setOnClickListener(this);
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
                .subscribe(new Consumer<ResultReturn<UserInfoModel>>() {
                    @Override
                    public void accept(ResultReturn<UserInfoModel> userInfoModelResultReturn) {
                        if (userInfoModelResultReturn != null) {
                            userInfoModel = userInfoModelResultReturn.getData();
                            if (userInfoModel != null) {
                                tv_money.setText(userInfoModel.getIntegralValue() + "");
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

    public void rechargeRecord(View view) {
        toActivity(RechargeRecordActivity.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_view_one:
                if (userInfoModel != null) {
                    SelectChargeWayActivity.startSelectChargeWayActivity(this, userInfoModel);
                }
                break;
            case R.id.rl_view_two:
                toActivity(UserWithdrawalActivity.class);
                break;
            case R.id.rl_view_three:
                toActivity(RechargeRecordActivity.class);
                break;
            case R.id.rl_view_four:
                toActivity(WithdrawalRecordActivity.class);
                break;
        }
    }
}

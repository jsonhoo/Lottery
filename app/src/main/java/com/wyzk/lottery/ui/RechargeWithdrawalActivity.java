package com.wyzk.lottery.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.wyzk.lottery.R;
import com.wyzk.lottery.utils.BuildManager;

import butterknife.Bind;
import butterknife.ButterKnife;


public class RechargeWithdrawalActivity extends LotteryBaseActivity implements View.OnClickListener {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.title)
    View title;

    @Bind(R.id.rl_view_one)
    RelativeLayout rl_view_one;
    @Bind(R.id.rl_view_two)
    RelativeLayout rl_view_two;
    @Bind(R.id.rl_view_three)
    RelativeLayout rl_view_three;
    @Bind(R.id.rl_view_four)
    RelativeLayout rl_view_four;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
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

    public void rechargeRecord(View view) {
        toActivity(RechargeRecordActivity.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_view_one:
                toActivity(SelectChargeWayActivity.class);
                break;
            case R.id.rl_view_two:
                toActivity(UserWithdrawalActivity.class);
                break;
            case R.id.rl_view_three:
                toActivity(RechargeRecordActivity.class);
                break;
            case R.id.rl_view_four:
                toActivity(UserWithdrawalActivity.class);
                break;
        }
    }
}

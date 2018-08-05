package com.wyzk.lottery.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wyzk.lottery.R;
import com.wyzk.lottery.model.UserInfoModel;
import com.wyzk.lottery.utils.BuildManager;

import butterknife.Bind;
import butterknife.ButterKnife;


public class SelectChargeWayActivity extends LotteryBaseActivity implements View.OnClickListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.title)
    View title;

    @Bind(R.id.tv_contact_service)
    TextView tv_contact_service;

    @Bind(R.id.rl_view_one)
    RelativeLayout rl_view_one;
    @Bind(R.id.rl_view_two)
    RelativeLayout rl_view_two;
    @Bind(R.id.rl_view_three)
    RelativeLayout rl_view_three;

    public static void startSelectChargeWayActivity(Context context, UserInfoModel userInfoModel) {
        Intent intent = new Intent(context, SelectChargeWayActivity.class);
        intent.putExtra("userInfoModel", userInfoModel);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_charge_way);
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

        tv_contact_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toCall();
            }
        });
        rl_view_one.setOnClickListener(this);
        rl_view_two.setOnClickListener(this);
        rl_view_three.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_view_one:
                toAccountChargeActivity(1);
                break;
            case R.id.rl_view_two:
                toAccountChargeActivity(2);
                break;
            case R.id.rl_view_three:
                toAccountChargeActivity(3);
                break;
        }
    }

    private void toAccountChargeActivity(int type) {
        AccountChargeActivity.startAccountChargeActivity(this,
                (UserInfoModel) getIntent().getSerializableExtra("userInfoModel"), type);
    }
}

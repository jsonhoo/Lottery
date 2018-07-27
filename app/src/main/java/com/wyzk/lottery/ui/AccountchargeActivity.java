package com.wyzk.lottery.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wyzk.lottery.R;
import com.wyzk.lottery.utils.BuildManager;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AccountchargeActivity extends LotteryBaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.title)
    View title;

    @Bind(R.id.tv_conact_service)
    TextView tv_conact_service;
    @Bind(R.id.tv_current_account)
    TextView tv_current_account;

    @Bind(R.id.ev_input)
    EditText ev_input;

    @Bind(R.id.gv_value)
    GridView gv_value;

    @Bind(R.id.rl_view_one)
    RelativeLayout rl_view_one;
    @Bind(R.id.rl_view_two)
    RelativeLayout rl_view_two;
    @Bind(R.id.rl_view_three)
    RelativeLayout rl_view_three;

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
    }


}

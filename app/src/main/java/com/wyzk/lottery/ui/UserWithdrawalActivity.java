package com.wyzk.lottery.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.wyzk.lottery.R;
import com.wyzk.lottery.utils.BuildManager;

import butterknife.Bind;
import butterknife.ButterKnife;


public class UserWithdrawalActivity extends LotteryBaseActivity {
    
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.title)
    View title;

    @Bind(R.id.tv_withdrawal_value)
    TextView tv_withdrawal_value;

    @Bind(R.id.tv_user_card_name)
    TextView tv_user_card_name;

    @Bind(R.id.tv_bank_name)
    TextView tv_bank_name;

    @Bind(R.id.tv_card_num)
    TextView tv_card_num;

    @Bind(R.id.ev_input)
    EditText ev_input;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_withdrawal);
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

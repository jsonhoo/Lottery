package com.wyzk.lottery.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.wyzk.lottery.R;
import com.wyzk.lottery.model.ChargeModel;
import com.wyzk.lottery.utils.BuildManager;

import butterknife.Bind;
import butterknife.ButterKnife;


public class RechargeDetailActivity extends LotteryBaseActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.title)
    View title;

    @Bind(R.id.tv_order_serial_number)
    TextView tv_order_serial_number;
    @Bind(R.id.tv_settlement_type)
    TextView tv_settlement_type;
    @Bind(R.id.tv_order_status)
    TextView tv_order_status;
    @Bind(R.id.tv_order_value)
    TextView tv_order_value;
    @Bind(R.id.tv_order_time)
    TextView tv_order_time;
    @Bind(R.id.tv_platform_card_no)
    TextView tv_platform_card_no;
    @Bind(R.id.tv_platform_card_user_name)
    TextView tv_platform_card_user_name;
    @Bind(R.id.tv_platform_card_bank_name)
    TextView tv_platform_card_bank_name;
    @Bind(R.id.tv_order_note_code)
    TextView tv_order_note_code;

    ChargeModel.ChargeHistoryModel chargeHistoryModel;

    public static void startRechargeDetailActivity(Context context, ChargeModel.ChargeHistoryModel chargeHistoryModel) {
        Intent intent = new Intent(context, RechargeDetailActivity.class);
        intent.putExtra("chargeHistoryModel", chargeHistoryModel);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_detail);
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

        chargeHistoryModel = (ChargeModel.ChargeHistoryModel) getIntent().getSerializableExtra("chargeHistoryModel");
    }


}

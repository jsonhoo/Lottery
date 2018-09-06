package com.wyzk.lottery.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.wyzk.lottery.R;
import com.wyzk.lottery.model.ExchangeModel;
import com.wyzk.lottery.utils.BuildManager;
import com.wyzk.lottery.utils.UTCDateUtil;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ExchargeDetailActivity extends LotteryBaseActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.title)
    View title;

    @Bind(R.id.tv_exchange_no)
    TextView tv_exchange_no;

    @Bind(R.id.tv_exchange_value)
    TextView tv_exchange_value;

    @Bind(R.id.tv_exchange_status)
    TextView tv_exchange_status;

    @Bind(R.id.tv_exchange_time)
    TextView tv_exchange_time;

    @Bind(R.id.tv_platform_card_no)
    TextView tv_platform_card_no;

    @Bind(R.id.tv_platform_card_user_name)
    TextView tv_platform_card_user_name;

    @Bind(R.id.tv_platform_card_bank_name)
    TextView tv_platform_card_bank_name;


    private ExchangeModel.ExchangeItem exchangeItem;

    public static void startExchargeDetailActivity(Context context, ExchangeModel.ExchangeItem exchangeItem) {
        Intent intent = new Intent(context, ExchargeDetailActivity.class);
        intent.putExtra("ExchangeItem", exchangeItem);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal_detail);
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

        exchangeItem = (ExchangeModel.ExchangeItem) getIntent().getSerializableExtra("ExchangeItem");

        tv_exchange_no.setText(exchangeItem.getExchangeNo());
        tv_exchange_value.setText(""+exchangeItem.getExchangeValue());

        switch (exchangeItem.getExchangeStatus()) {
            case "0":
                tv_exchange_status.setText(R.string.status_1);
                break;
            case "1":
                tv_exchange_status.setText(R.string.status_2);
                break;
            case "2":
                tv_exchange_status.setText(R.string.status_3);
                break;
            case "3":
                tv_exchange_status.setText(R.string.status_4);
                break;
        }

        tv_exchange_time.setText(UTCDateUtil.format(exchangeItem.getCreateTime(), UTCDateUtil.FMT_DATETIME));

        tv_platform_card_no.setText(exchangeItem.getUserPayAccount());
        tv_platform_card_user_name.setText(exchangeItem.getUserPayAccountName());
        tv_platform_card_bank_name.setText(exchangeItem.getUserPayAccountBank());

    }


}

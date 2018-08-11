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
import com.wyzk.lottery.model.ResultReturn;
import com.wyzk.lottery.network.Network;
import com.wyzk.lottery.utils.BuildManager;
import com.wyzk.lottery.utils.ToastUtil;
import com.wyzk.lottery.utils.UTCDateUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class ManageExchangeDetailActivity extends LotteryBaseActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.title)
    View title;

    @Bind(R.id.tv_exchange_user)
    TextView tv_exchange_user;

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

    @Bind(R.id.submit_ok)
    TextView submit_ok;

    @Bind(R.id.submit_reject)
    TextView submit_reject;


    private ExchangeModel.ExchangeItem exchangeItem;

    public static void startManageExchangeDetailActivity(Context context, ExchangeModel.ExchangeItem exchangeItem) {
        Intent intent = new Intent(context, ManageExchangeDetailActivity.class);
        intent.putExtra("ExchangeItem", exchangeItem);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_withdrawal_detail);
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

        tv_exchange_user.setText(exchangeItem.getUsername());

        tv_exchange_no.setText(exchangeItem.getExchangeNo());
        tv_exchange_value.setText("" + exchangeItem.getExchangeValue());

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

    @OnClick({R.id.submit_ok, R.id.submit_reject})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit_ok:
                setExchangeFinish();
                break;
            case R.id.submit_reject:
                setExchangeReject();
                break;
        }
    }

    private void setExchangeFinish() {
        Network.getNetworkInstance().getIntegralApi()
                .setExchangeFinish(token, exchangeItem.getExchangeId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultReturn<String>>() {
                    @Override
                    public void accept(ResultReturn<String> string) throws Exception {
                        ToastUtil.showToast(ManageExchangeDetailActivity.this,"成功");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        ToastUtil.showToast(ManageExchangeDetailActivity.this,"失败");
                    }
                });
    }

    private void  setExchangeReject() {
        Network.getNetworkInstance().getIntegralApi()
                .setExchangeReject(token, exchangeItem.getExchangeId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultReturn<String>>() {
                    @Override
                    public void accept(ResultReturn<String> string) throws Exception {
                        ToastUtil.showToast(ManageExchangeDetailActivity.this,"成功");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        ToastUtil.showToast(ManageExchangeDetailActivity.this,"失败");
                    }
                });
    }

}

package com.wyzk.lottery.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wyzk.lottery.R;
import com.wyzk.lottery.model.BankBean;
import com.wyzk.lottery.model.ResultReturn;
import com.wyzk.lottery.model.UserInfoModel;
import com.wyzk.lottery.network.Network;
import com.wyzk.lottery.utils.BuildManager;
import com.wyzk.lottery.utils.ToastUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AccountChargeActivity extends LotteryBaseActivity implements View.OnClickListener {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.title)
    View title;

    @Bind(R.id.tv_select_charge_way)
    TextView tv_select_charge_way;
    @Bind(R.id.tv_platform_card_no)
    TextView tv_platform_card_no;
    @Bind(R.id.tv_platform_card_user_name)
    TextView tv_platform_card_user_name;
    @Bind(R.id.tv_platform_card_bank_name)
    TextView tv_platform_card_bank_name;
    @Bind(R.id.tv_order_note_code)
    TextView tv_order_note_code;
    @Bind(R.id.tv_current_account)
    TextView tv_current_account;
    @Bind(R.id.ev_input)
    EditText ev_input;
    @Bind(R.id.tv_100)
    TextView tv_100;
    @Bind(R.id.tv_500)
    TextView tv_500;
    @Bind(R.id.tv_1000)
    TextView tv_1000;
    @Bind(R.id.tv_5000)
    TextView tv_5000;
    @Bind(R.id.tv_10000)
    TextView tv_10000;
    @Bind(R.id.tv_20000)
    TextView tv_20000;
    @Bind(R.id.tv_submit)
    TextView tv_submit;

    private UserInfoModel userInfoModel;
    private int type;
    private BankBean bankBean;

    public static void startAccountChargeActivity(Context context, UserInfoModel userInfoModel, int type) {
        Intent intent = new Intent(context, AccountChargeActivity.class);
        intent.putExtra("userInfoModel", userInfoModel);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_charge);
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


        tv_100.setOnClickListener(this);
        tv_500.setOnClickListener(this);
        tv_1000.setOnClickListener(this);
        tv_5000.setOnClickListener(this);
        tv_10000.setOnClickListener(this);
        tv_20000.setOnClickListener(this);
        tv_submit.setOnClickListener(this);

        userInfoModel = (UserInfoModel) getIntent().getSerializableExtra("userInfoModel");
        tv_current_account.setText(userInfoModel.getUsername());

        type = getIntent().getIntExtra("type", 1);
        setType(type);

        getBankInfo();
    }

    private void setType(int type) {
        switch (type) {
            case 1:
                tv_select_charge_way.setText(getString(R.string.select_bank));
                break;
            case 2:
                tv_select_charge_way.setText(getString(R.string.select_alipay));
                break;
            case 3:
                tv_select_charge_way.setText(getString(R.string.select_wechat));
                break;
            default:
                tv_select_charge_way.setText(getString(R.string.select_bank));
                break;
        }
    }

    private void resetData() {
        setType(bankBean.getManagerPayAccountType());
        tv_platform_card_no.setText(bankBean.getManagerPayAccount());
        tv_platform_card_user_name.setText(bankBean.getManagerPayAccountName());
        tv_platform_card_bank_name.setText(bankBean.getManagerPayAccountBank());
        tv_order_note_code.setText(bankBean.getRemarkCode());
    }

    private void getBankInfo() {
        Network.getNetworkInstance().getIntegralApi()
                .getBank(token, String.valueOf(type))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultReturn<BankBean>>() {
                    @Override
                    public void accept(ResultReturn<BankBean> resultReturn) {
                        if (resultReturn != null && resultReturn.getCode() == ResultReturn.ResultCode.RESULT_OK.getValue()) {
                            bankBean = resultReturn.getData();
                            resetData();
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_100:
                setEditText("100");
                break;
            case R.id.tv_500:
                setEditText("500");
                break;
            case R.id.tv_1000:
                setEditText("1000");
                break;
            case R.id.tv_5000:
                setEditText("5000");
                break;
            case R.id.tv_10000:
                setEditText("10000");
                break;
            case R.id.tv_20000:
                setEditText("20000");
                break;
            case R.id.tv_submit:
                submit();
                break;
        }
    }

    private void setEditText(String text) {
        ev_input.setText(text.toCharArray(), 0, text.length());
        ev_input.setSelection(text.length());
    }

    private void submit() {
        if (bankBean == null) {
            return;
        }

        String money = ev_input.getText().toString().trim();

        if (TextUtils.isEmpty(money)) {
            ToastUtil.showToast(this, getString(R.string.money_is_empty));
            return;
        }
        if(Integer.valueOf(money)<100){
            ToastUtil.showToast(this, "充值金额必须大于等于100");
            return;
        }
        Network.getNetworkInstance().getIntegralApi()
                .addCharge(token, money, bankBean.getUserManagerPayAccountId(), String.valueOf(type), bankBean.getRemarkCode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultReturn<String>>() {
                    @Override
                    public void accept(ResultReturn<String> resultReturn) {
                        if (resultReturn != null && resultReturn.getCode() == ResultReturn.ResultCode.RESULT_OK.getValue()) {
                            showToast(getString(R.string.submit_success));
                            finish();
                        } else {
                            if (!TextUtils.isEmpty(resultReturn.getMsg())) {
                                showToast(resultReturn.getMsg());
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        showToast(throwable.getMessage());
                    }
                });
    }
}

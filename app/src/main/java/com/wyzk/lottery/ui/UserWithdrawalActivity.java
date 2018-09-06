package com.wyzk.lottery.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.wyzk.lottery.R;
import com.wyzk.lottery.adapter.AccountAdapter;
import com.wyzk.lottery.model.AccountBean;
import com.wyzk.lottery.model.ResultReturn;
import com.wyzk.lottery.model.UserInfoModel;
import com.wyzk.lottery.network.Network;
import com.wyzk.lottery.utils.BuildManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class UserWithdrawalActivity extends LotteryBaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.title)
    View title;
    @Bind(R.id.tv_add_account)
    TextView tv_add_account;
    @Bind(R.id.tv_add)
    TextView tv_add;

    @Bind(R.id.tv_withdrawal_value)
    TextView tv_withdrawal_value;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.ev_input)
    EditText ev_input;
    @Bind(R.id.submit)
    TextView submit;

    UserInfoModel userInfoModel;
    int integralValue;

    List<AccountBean> list = new ArrayList<>();
    AccountAdapter accountAdapter;
    AccountBean chooseAccountBean;

    public static void startUserWithdrawalActivity(Context context, UserInfoModel userInfoModel) {
        Intent intent = new Intent(context, UserWithdrawalActivity.class);
        intent.putExtra("userInfoModel", userInfoModel);
        context.startActivity(intent);
    }

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
        BuildManager.setStatusTrans(this, 2, title);

        userInfoModel = (UserInfoModel) getIntent().getSerializableExtra("userInfoModel");
        integralValue = userInfoModel.getIntegralValue();
        tv_withdrawal_value.setText(String.valueOf(integralValue));

        tv_add_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
            }
        });
        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
            }
        });

        accountAdapter = new AccountAdapter(this, list, R.layout.lv_account_list);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.HORIZONTAL));
        recyclerview.setAdapter(accountAdapter);
        accountAdapter.setOnItemClickListener(new AccountAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                accountAdapter.setOnClickItemPosition(position);
                chooseAccountBean = list.get(position);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitWithdrawal();
            }
        });

        getAccount();
    }

    private void getAccount() {
        Network.getNetworkInstance().getIntegralApi()
                .getAccountList(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultReturn<List<AccountBean>>>() {
                    @Override
                    public void accept(ResultReturn<List<AccountBean>> resultReturn) {
                        if (resultReturn != null && resultReturn.getCode() == ResultReturn.ResultCode.RESULT_OK.getValue()) {
                            list.clear();
                            if (resultReturn.getData() != null) {
                                list.addAll(resultReturn.getData());
                            }
                            if (list.size() > 0) {
                                chooseAccountBean = list.get(0);
                                tv_add.setVisibility(View.GONE);
                                recyclerview.setVisibility(View.VISIBLE);
                            } else {
                                chooseAccountBean = null;
                                tv_add.setVisibility(View.VISIBLE);
                                recyclerview.setVisibility(View.GONE);
                            }
                            accountAdapter.notifyDataSetChanged();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        showToast(throwable.getMessage());
                    }
                });
    }


    //添加账号
    private Dialog addDialog;
    String type = "1";

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_add_account, null);
        TextView tvSure = (TextView) view.findViewById(R.id.tv_sure);
        TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);

        final RadioGroup rgType = (RadioGroup) view.findViewById(R.id.rg_type);
        final EditText etCardName = (EditText) view.findViewById(R.id.et_user_card_name);
        final EditText etBankName = (EditText) view.findViewById(R.id.et_bank_name);
        final EditText etCardNum = (EditText) view.findViewById(R.id.et_card_num);

        rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_bank:
                        type = "1";
                        break;
                    case R.id.rb_alipay:
                        type = "2";
                        break;
                    case R.id.rb_wechat:
                        type = "3";
                        break;
                }
            }
        });
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cardName = etCardName.getText().toString().trim();
                String bankName = etBankName.getText().toString().trim();
                String cardNum = etCardNum.getText().toString().trim();
                if (TextUtils.isEmpty(cardName) || TextUtils.isEmpty(bankName) || TextUtils.isEmpty(cardNum)) {
                    showToast(getString(R.string.empty_data));
                    return;
                }
                addAccount(bankName, cardNum, cardName, type);
                addDialog.dismiss();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDialog.dismiss();
            }
        });
        builder.setView(view);
        addDialog = builder.create();
        addDialog.setCanceledOnTouchOutside(true);
        addDialog.show();
    }

    private void addAccount(String bankName, String cardNum, String cardName, String type) {
        Network.getNetworkInstance().getIntegralApi()
                .addAccount(token, bankName, cardNum, cardName, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultReturn<String>>() {
                    @Override
                    public void accept(ResultReturn<String> resultReturn) {
                        if (resultReturn != null && resultReturn.getCode() == ResultReturn.ResultCode.RESULT_OK.getValue()) {
                            getAccount();
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

    private void submitWithdrawal() {
        if (chooseAccountBean == null) {
            showToast(getString(R.string.account_empty));
            return;
        }

        String etValue = ev_input.getText().toString().trim();

        if (Integer.valueOf(etValue) > integralValue) {
            showToast(getString(R.string.money_shortage));
            return;
        }
        if (Integer.valueOf(etValue) < 100) {
            showToast("提现金额必须大于等于100");
            return;
        }
        if (TextUtils.isEmpty(etValue)) {
            showToast(getString(R.string.empty_data));
            return;
        }
        Network.getNetworkInstance().getIntegralApi()
                .withdrawal(token, etValue, chooseAccountBean.getUserPayAccountId())
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

package com.wyzk.lottery.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.wyzk.lottery.R;
import com.wyzk.lottery.model.RechargeManageModel;
import com.wyzk.lottery.model.ResultReturn;
import com.wyzk.lottery.network.Network;
import com.wyzk.lottery.utils.BuildManager;
import com.wyzk.lottery.utils.UTCDateUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class AdminRechargeDetailActivity extends LotteryBaseActivity {
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

    @Bind(R.id.tv_user_name)
    TextView tv_user_name;

    @Bind(R.id.submit_ok)
    TextView submit_ok;

    @Bind(R.id.submit_reject)
    TextView submit_reject;

    private RechargeManageModel.RechargeItem rechargeItem;

    public static void startAdminRechargeDetailActivity(Context context, RechargeManageModel.RechargeItem rechargeItem) {
        Intent intent = new Intent(context, AdminRechargeDetailActivity.class);
        intent.putExtra("RechargeItem", rechargeItem);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_charge_detail);
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
        rechargeItem = (RechargeManageModel.RechargeItem) getIntent().getSerializableExtra("RechargeItem");

        tv_user_name.setText(rechargeItem.getUsername());
        tv_order_serial_number.setText(rechargeItem.getChargeNo());

        switch (rechargeItem.getChargeType()) {
            case 1:
                tv_settlement_type.setText(R.string.bank);
                break;
            case 2:
                tv_settlement_type.setText(R.string.alipay);
                break;
            case 3:
                tv_settlement_type.setText(R.string.wechat);
                break;
        }

        switch (rechargeItem.getChargeStatus()) {
            case 0:
                tv_order_status.setText(R.string.status_5);
                break;
            case 1:
                tv_order_status.setText(R.string.status_6);
                break;
            case 2:
                tv_order_status.setText(R.string.status_3);
                break;
            case 3:
                tv_order_status.setText(R.string.status_4);
                break;
        }
        tv_order_value.setText("" + rechargeItem.getChargeValue());
        tv_order_time.setText(UTCDateUtil.format(rechargeItem.getCreateTime(), UTCDateUtil.FMT_DATETIME));
        tv_platform_card_no.setText(rechargeItem.getManagerPayAccount());
        tv_platform_card_user_name.setText(rechargeItem.getManagerPayAccountName());
        tv_platform_card_bank_name.setText(rechargeItem.getManagerPayAccountBank());
        tv_order_note_code.setText(rechargeItem.getRemarkCode());
    }

    @OnClick({R.id.submit_ok, R.id.submit_reject})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit_ok:
                setRechargeFinish();
                break;
            case R.id.submit_reject:
                setRechargeReject();
                break;
        }
    }

    private void setRechargeFinish() {
        showMyDialog(QMUITipDialog.Builder.ICON_TYPE_LOADING, "努力进行中...");

        Network.getNetworkInstance().getIntegralApi()
                .setRechargeFinish(token, rechargeItem.getChargeId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultReturn<String>>() {
                    @Override
                    public void accept(ResultReturn<String> result) throws Exception {
                        //ToastUtil.showToast(AdminRechargeDetailActivity.this,"成功");
                        hideMyDialog(QMUITipDialog.Builder.ICON_TYPE_LOADING);

                        if (result != null && result.getCode() == ResultReturn.ResultCode.RESULT_OK.getValue()) {
                            final QMUITipDialog successDialog = new QMUITipDialog.Builder(AdminRechargeDetailActivity.this)
                                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                                    .setTipWord("充值成功")
                                    .create();
                            successDialog.show();

                            submit_ok.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    successDialog.dismiss();
                                    AdminRechargeDetailActivity.this.finish();
                                }
                            }, 500);
                        } else {
                            showMyFailDialog("充值失败", tv_order_status);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        //ToastUtil.showToast(AdminRechargeDetailActivity.this,"失败");
                        showMyFailDialog("充值失败", tv_order_status);
                    }
                });
    }

    private void setRechargeReject() {
        showMyDialog(QMUITipDialog.Builder.ICON_TYPE_LOADING, "努力进行中...");

        Network.getNetworkInstance().getIntegralApi()
                .setRechargeReject(token, rechargeItem.getChargeId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultReturn<String>>() {
                    @Override
                    public void accept(ResultReturn<String> resultReturn) throws Exception {
                        hideMyDialog(QMUITipDialog.Builder.ICON_TYPE_LOADING);
                        if (resultReturn != null && resultReturn.getCode() == ResultReturn.ResultCode.RESULT_OK.getValue()) {
                            final QMUITipDialog successDialog = new QMUITipDialog.Builder(AdminRechargeDetailActivity.this)
                                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                                    .setTipWord("提交成功")
                                    .create();
                            successDialog.show();
                            submit_ok.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    successDialog.dismiss();
                                    AdminRechargeDetailActivity.this.finish();
                                }
                            }, 500);
                        } else {
                            showMyFailDialog("提交失败", tv_order_status);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        //ToastUtil.showToast(AdminRechargeDetailActivity.this, "失败");
                        hideMyDialog(QMUITipDialog.Builder.ICON_TYPE_LOADING);
                        showMyFailDialog("提交失败", tv_order_status);
                    }
                });
    }
}

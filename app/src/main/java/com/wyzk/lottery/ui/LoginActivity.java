package com.wyzk.lottery.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.wyzk.lottery.R;
import com.wyzk.lottery.constant.IConst;
import com.wyzk.lottery.model.ResultReturn;
import com.wyzk.lottery.model.TokenModel;
import com.wyzk.lottery.model.UserInfoModel;
import com.wyzk.lottery.network.Network;
import com.wyzk.lottery.utils.ACache;
import com.wyzk.lottery.utils.BuildManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * 登录页面
 */
public class LoginActivity extends LotteryBaseActivity {

    @Bind(R.id.edt_username)
    EditText edt_username;
    @Bind(R.id.edt_password)
    EditText edtPassword;
    @Bind(R.id.btn_login)
    Button btnLogin;
    @Bind(R.id.tv_register)
    TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        BuildManager.setStatusTransOther(this);
        UserInfoModel cache = getSp(IConst.USER_INFO_KEY);
        if (cache != null) {
            edt_username.setText(cache.getUsername());
            edtPassword.setText(cache.getPassword());
        }
    }

    @OnClick({R.id.btn_login, R.id.tv_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                executeLogin();
                break;
            case R.id.tv_register:
                toActivity(RegisterActivity.class);
                break;
        }
    }

    private void executeLogin() {
        String username = edt_username.getText().toString().trim();
        String pwd = edtPassword.getText().toString().trim();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(pwd)) {
            showToast(getString(R.string.username_pwd_empty));
            return;
        }
        login(username, pwd);
    }





    private void login(final String username, final String password) {

        showMyDialog(QMUITipDialog.Builder.ICON_TYPE_LOADING, "登录中...");

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();

        subscription = Network.getNetworkInstance().getUserApi()
                .login(username, password, imei)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultReturn<TokenModel>>() {
                    @Override
                    public void accept(ResultReturn<TokenModel> result) throws Exception {
                        hideMyDialog(QMUITipDialog.Builder.ICON_TYPE_LOADING);
                        if (result.getCode() == ResultReturn.ResultCode.RESULT_OK.getValue()) {
                            //Toast.makeText(LoginActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                            setSp(IConst.USER_INFO_KEY, new UserInfoModel(username, password));
                            TokenModel tokenModel = result.getData();
                            ACache.get(LoginActivity.this).put(IConst.TOKEN, tokenModel.getToken());
                            ACache.get(LoginActivity.this).put(IConst.USER_ID, tokenModel.getUserId());
                            ACache.get(LoginActivity.this).put(IConst.IS_ADMIN, tokenModel.getIsAdmin());

                            final QMUITipDialog successDialog = new QMUITipDialog.Builder(LoginActivity.this)
                                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                                    .setTipWord("登录成功")
                                    .create();
                            successDialog.show();

                            tvRegister.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    successDialog.dismiss();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                }
                            }, 500);
                        } else {
                            //Toast.makeText(LoginActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                            showMyFailDialog("登录失败",tvRegister);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //dismissLoadingView();
                        hideMyDialog(QMUITipDialog.Builder.ICON_TYPE_LOADING);
                        showMyFailDialog("登录失败",tvRegister);
                    }
                });
    }

}
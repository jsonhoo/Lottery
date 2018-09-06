package com.wyzk.lottery.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.wyzk.lottery.R;
import com.wyzk.lottery.model.ResultReturn;
import com.wyzk.lottery.network.Network;
import com.wyzk.lottery.utils.BuildManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RegisterActivity extends LotteryBaseActivity {

    @Bind(R.id.edt_reg_username)
    EditText edtRegUsername;
    @Bind(R.id.edt_reg_password)
    EditText edtRegPassword;
    @Bind(R.id.edt_reg_nickname)
    EditText edt_reg_nickname;
    @Bind(R.id.btn_reg_submit)
    Button btnRegSubmit;
    @Bind(R.id.rg_sex)
    RadioGroup rg_sex;
    @Bind(R.id.title)
    View title;
    @Bind(R.id.rl_back)
    View rl_back;

    @Bind(R.id.edt_reg_invite_code)
    EditText edt_reg_invite_code;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        BuildManager.setStatusTrans(this, 2, title);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick(R.id.btn_reg_submit)
    public void onClick() {
        String username = edtRegUsername.getText().toString().trim();
        if (username != null && username.length() < 6) {
            Toast.makeText(getApplicationContext(), getString(R.string.account_register_too_short), Toast.LENGTH_LONG).show();
            return;
        }

        String password = edtRegPassword.getText().toString().trim();
        if (password != null && password.length() < 6) {
            Toast.makeText(getApplicationContext(), getString(R.string.account_password_too_short), Toast.LENGTH_LONG).show();
            return;
        }

        String nickname = edt_reg_nickname.getText().toString().trim();
        if (nickname != null && nickname.length() < 6) {
            Toast.makeText(getApplicationContext(), getString(R.string.account_nick_too_short), Toast.LENGTH_LONG).show();
            return;
        }

        int sex = rg_sex.getCheckedRadioButtonId() == R.id.male ? 0 : 1;
        String inviteCode = edt_reg_invite_code.getText().toString().trim();

        if (!username.isEmpty() && !password.isEmpty()) {
            registerUser(username, password, nickname, sex,inviteCode);
        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.enter_your_details), Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void registerUser(final String username, final String password, final String realname, int sex,String inviteCode) {
        showLoadingView();
        Network.getNetworkInstance().getUserApi()
                .register(username, password, realname, sex,inviteCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResultReturn<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        subscription = d;
                    }

                    @Override
                    public void onNext(ResultReturn<String> result) {
                        if (result.getCode() == ResultReturn.ResultCode.RESULT_OK.getValue()) {
                            dismissLoadingView();
                            toActivity(RegisterSuccessActivity.class);
                            finish();
                        } else {
                            dismissLoadingView();
                            Toast.makeText(RegisterActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissLoadingView();
                        showToast(getString(R.string.register_fail));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}

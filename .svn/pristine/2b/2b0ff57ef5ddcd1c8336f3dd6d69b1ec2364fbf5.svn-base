package com.wyzk.lottery.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.wyzk.lottery.R;
import com.wyzk.lottery.model.ResultReturn;
import com.wyzk.lottery.model.UserInfoModel;
import com.wyzk.lottery.network.Network;
import com.wyzk.lottery.utils.BuildManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class PersonalInformationActivity extends LotteryBaseActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.title)
    View title;
    @Bind(R.id.tv_name)
    TextView tv_name;
    @Bind(R.id.tv_integral)
    TextView tv_integral;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);
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

        setInitUserInfo();
        getUserInfo();
    }

    private void setInitUserInfo() {
        if (extraBean != null) {
            UserInfoModel userInfoModel = (UserInfoModel) extraBean.getData();
            if (userInfoModel != null) {
                tv_name.setText("用户昵称：" + userInfoModel.getRealname());
                tv_integral.setText("当前积分：" + String.valueOf(userInfoModel.getIntegralValue()));
            }
        }
    }

    private void getUserInfo() {
        Network.getNetworkInstance().getUserApi()
                .getUserInfo(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResultReturn<UserInfoModel>>() {
                    @Override
                    public void call(ResultReturn<UserInfoModel> userInfoModelResultReturn) {
                        if (userInfoModelResultReturn != null) {
                            UserInfoModel user = userInfoModelResultReturn.getData();
                            if (user != null) {
                                tv_name.setText("用户昵称：" + user.getRealname());
                                tv_integral.setText("当前积分：" + user.getIntegralValue());
                            }
                        }

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }
}

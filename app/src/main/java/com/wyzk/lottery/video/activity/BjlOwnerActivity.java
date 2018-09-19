package com.wyzk.lottery.video.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.wyzk.lottery.R;
import com.wyzk.lottery.constant.IConst;
import com.wyzk.lottery.model.ResultReturn;
import com.wyzk.lottery.model.RoomModel;
import com.wyzk.lottery.model.RoomRoundModel;
import com.wyzk.lottery.network.Network;
import com.wyzk.lottery.utils.BuildManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * 玩家的页面
 */
public class BjlOwnerActivity extends VideoBaseActivity implements OnClickListener {


    @Bind(R.id.sex)
    RadioGroup sex;

    @Bind(R.id.tv_start)
    TextView tv_start;

    @Bind(R.id.tv_cancel)
    TextView tv_cancel;

    @Bind(R.id.tv_serial_num)
    TextView tv_serial_num;

    @Bind(R.id.iv_back)
    ImageView iv_back;

    @Bind(R.id.tv_title)
    TextView tv_title;

    private int positionValue = 0;

    private int roomId;
    private int roundId;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bjl_owner);
        BuildManager.setStatusTransOther(this);
        ButterKnife.bind(this);

        sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = group.getCheckedRadioButtonId();
                if (id == R.id.rp_1) {
                    positionValue = 1;
                } else if (id == R.id.rp_2) {
                    positionValue = 2;
                } else if (id == R.id.rp_3) {
                    positionValue = 3;
                } else if (id == R.id.rp_4) {
                    positionValue = 4;
                } else if (id == R.id.rp_5) {
                    positionValue = 5;
                } else if (id == R.id.rp_6) {
                    positionValue = 6;
                }
            }
        });
        tv_start.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);

        iv_back.setOnClickListener(this);

        RoomModel.RowModel rowModel = (RoomModel.RowModel) getIntent().getSerializableExtra(IConst.ROW_INFO);
        roomId = rowModel.getRoomId();

        //订阅房间消息
        subscribeMqTopic(IConst.TOPIC + roomId);
    }


    public void setStatus(int value, boolean flag) {
        tv_serial_num.setText(getString(R.string.serial_number) + ": " + roundId);
        switch (value) {
            case 1://下注中
                break;
            case 2://封盘中
                break;
            case 3://结算中
                break;
            case 4://结算完成
                break;
            case 5://场次作废
                break;
        }
    }

    @Override
    protected void networkAvailable() {
        super.networkAvailable();

        getLastRoomRound();
    }

    @Override
    protected void onDestroy() {
        canSubscribeMqTopic(IConst.TOPIC + roomId);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_start:
                generateGame();
                break;
            case R.id.tv_cancel:
                cancelRound();
                break;
            case R.id.iv_back:
                this.finish();
                break;
        }
    }

    /**
     * 获取最新的场次信息
     */
    private void getLastRoomRound() {
        Network.getNetworkInstance().getLiveApi()
                .getLastRoomRound(token, roomId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultReturn<RoomRoundModel>>() {
                    @Override
                    public void accept(ResultReturn<RoomRoundModel> ret) throws Exception {
                        if (ret != null && ret.getCode() == ResultReturn.ResultCode.RESULT_OK.getValue() && ret.getData() != null) {
                            roundId = ret.getData().getRoomRoundId();
                            setStatus(ret.getData().getRoundState(), false);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        //ToastUtil.showToast(AdminRechargeDetailActivity.this,"失败");

                    }
                });
    }

    /**
     * 生成场次接口
     */
    private void generateGame() {
        Network.getNetworkInstance().getLiveApi()
                .generate(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultReturn<String>>() {
                    @Override
                    public void accept(ResultReturn<String> resultReturn) throws Exception {
                        if (resultReturn != null && resultReturn.getCode() == ResultReturn.ResultCode.RESULT_OK.getValue()) {
                            tips(getString(R.string.generate_success));
                            getLastRoomRound();
                        } else {
                            tips(resultReturn.getMsg() + "");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        //ToastUtil.showToast(AdminRechargeDetailActivity.this,"失败");

                    }
                });
    }

    /**
     * 封盘接口
     */
    private void fengpanGame() {
        Network.getNetworkInstance().getLiveApi()
                .licensing(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultReturn<String>>() {
                    @Override
                    public void accept(ResultReturn<String> resultReturn) throws Exception {
                        if (resultReturn != null && resultReturn.getCode() == ResultReturn.ResultCode.RESULT_OK.getValue()) {
                            tips(getString(R.string.fengpan_success));
                        } else {
                            tips(resultReturn.getMsg() + "");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        //ToastUtil.showToast(AdminRechargeDetailActivity.this,"失败");

                    }
                });
    }

    /**
     * 场次作废接口
     */
    private void cancelRound() {
        Network.getNetworkInstance().getLiveApi()
                .cancelRoomRound(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultReturn<String>>() {
                    @Override
                    public void accept(ResultReturn<String> resultReturn) throws Exception {
                        if (resultReturn != null && resultReturn.getCode() == ResultReturn.ResultCode.RESULT_OK.getValue()) {
                            tips(getString(R.string.cancel_success));
                        } else {
                            tips(resultReturn.getMsg() + "");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        //ToastUtil.showToast(AdminRechargeDetailActivity.this,"失败");

                    }
                });
    }

}

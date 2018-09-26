package com.wyzk.lottery.video.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.csr.csrmesh2.MeshConstants;
import com.fsix.mqtt.bean.MQBean;
import com.fsix.mqtt.util.GsonUtil;
import com.squareup.otto.Subscribe;
import com.uuxia.email.Logc;
import com.wyzk.lottery.LotteryApplication;
import com.wyzk.lottery.R;
import com.wyzk.lottery.constant.Constants;
import com.wyzk.lottery.constant.IConst;
import com.wyzk.lottery.event.MeshResponseEvent;
import com.wyzk.lottery.model.Device;
import com.wyzk.lottery.model.MQPlayerBean;
import com.wyzk.lottery.model.ResultReturn;
import com.wyzk.lottery.model.RoomModel;
import com.wyzk.lottery.model.RoomRoundModel;
import com.wyzk.lottery.network.Network;
import com.wyzk.lottery.utils.BuildManager;
import com.wyzk.lottery.utils.ToastUtil;
import com.wyzk.lottery.utils.Utils;
import com.wyzk.lottery.view.CircleTextProgressbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.wyzk.lottery.event.MeshResponseEvent.ResponseEvent.DATA_RECEIVE_BLOCK;
import static com.wyzk.lottery.event.MeshResponseEvent.ResponseEvent.DATA_RECEIVE_STREAM;
import static com.wyzk.lottery.event.MeshResponseEvent.ResponseEvent.DATA_RECEIVE_STREAM_END;


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

    public static final String EXTRA_DEVICE = "EXTRA_DEVICE";
    public static final String TAG = BjlOwnerActivity.class.getSimpleName();
    Device device = null;
    int id = 0;

    @Bind(R.id.iv_discern_player_1)
    ImageView iv_discern_player_1;
    @Bind(R.id.iv_discern_player_2)
    ImageView iv_discern_player_2;
    @Bind(R.id.iv_discern_player_3)
    ImageView iv_discern_player_3;

    @Bind(R.id.iv_discern_banker_1)
    ImageView iv_discern_banker_1;
    @Bind(R.id.iv_discern_banker_2)
    ImageView iv_discern_banker_2;
    @Bind(R.id.iv_discern_banker_3)
    ImageView iv_discern_banker_3;

    @Bind(R.id.tv_time_circle)
    CircleTextProgressbar tv_time_circle;


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

        initData();

        getLastRoomRound();
    }


    private void initData() {
        LotteryApplication.bus.register(this);
        if (getIntent().getExtras().containsKey(EXTRA_DEVICE)) {
            device = mDeviceManager.getDeviceById(getIntent().getIntExtra(EXTRA_DEVICE, Constants.INVALID_VALUE));
            if (device == null) {
                deviceNotFound();
                return;
            }
            id = device.getDeviceId();
        }
    }

    private void deviceNotFound() {
        Toast.makeText(this, "没有发现设备", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void showCoutDown(int coutDown) {
        if (coutDown <= 0) {
            return;
        }
        tv_time_circle.setProgressLineWidth(30);//写入宽度。
        tv_time_circle.setTimeMillis(coutDown * 1000);// 把倒计时时间改长一点。
        tv_time_circle.setCountdownProgressListener(1, progressListener);
        tv_time_circle.reStart();
    }

    private CircleTextProgressbar.OnCountdownProgressListener progressListener = new CircleTextProgressbar.OnCountdownProgressListener() {
        @Override
        public void onProgress(int what, int progress) {
            if (what == 1) {
                tv_time_circle.setText(progress + "%");
                if (progress == 0) {
                    fengpanGame();
                }
            } else if (what == 2) {
                tv_time_circle.setText(progress + "%");
            }
            // 比如在首页，这里可以判断进度，进度到了100或者0的时候，你可以做跳过操作。
        }
    };


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


    @Subscribe
    public void onEventMainThread(MeshResponseEvent event) {
        if (event == null || event.data == null) {
            return;
        }
        if (event.what == DATA_RECEIVE_BLOCK || event.what == DATA_RECEIVE_STREAM || event.what == DATA_RECEIVE_STREAM_END) {
            int deviceId = event.data.getInt(MeshConstants.EXTRA_DEVICE_ID);
            if (deviceId == Constants.INVALID_VALUE) {
                return;
            }

            switch (event.what) {
                case DATA_RECEIVE_BLOCK: {//unack rerevice
                    byte[] reData = event.data.getByteArray(MeshConstants.EXTRA_DATA);
                    if (reData != null) {
                        //showInfo("接收Id:" + deviceId + ":" + StringUtil.byteArrayToHexString(reData));
                        runOnUiThread(() -> {
                            //dataValue.setText("设备编号：" + deviceId);
                            if (reData.length == 3) {
                                showPokeInfo(reData);
                            }

                        });
                    }
                    break;
                }
                case DATA_RECEIVE_STREAM: {//ack rerevice
                    byte[] reData = event.data.getByteArray(MeshConstants.EXTRA_DATA);
                    int dataSqn = event.data.getInt(MeshConstants.EXTRA_DATA_SQN);
                    if (reData != null) {
                        //showInfo("#####接收Id:" + deviceId + "## Sqn:" + dataSqn + "数据:" + StringUtil.byteArrayToHexString(reData) + "##");
                        //Logc.e(TAG, "#####接收Id:" + deviceId + "## Sqn:" + dataSqn + "数据:" + StringUtil.byteArrayToHexString(reData) + "##");
                    }

                    break;
                }
                case DATA_RECEIVE_STREAM_END: {//ack rerevice end
                    //showInfo("#####接收Id:" + deviceId + ":数据接收完成 end###");
                    //Logc.e(TAG, "#####接收Id:" + deviceId + ":数据接收完成 end###");
                    break;
                }
            }
        } else {
            Logc.e(TAG, "#####无关数据:");
        }
    }

    private void showPokeInfo(byte[] result) {

        if (Utils.getPokeByIndex(result[1]) != -1) {
            if (positionValue == 1) {
                iv_discern_player_1.setBackgroundResource(Utils.getPokeByIndex(result[1]));
            } else if (positionValue == 2) {
                iv_discern_player_2.setBackgroundResource(Utils.getPokeByIndex(result[1]));
            } else if (positionValue == 3) {
                iv_discern_player_3.setBackgroundResource(Utils.getPokeByIndex(result[1]));
            } else if (positionValue == 4) {
                iv_discern_banker_1.setBackgroundResource(Utils.getPokeByIndex(result[1]));
            } else if (positionValue == 5) {
                iv_discern_banker_2.setBackgroundResource(Utils.getPokeByIndex(result[1]));
            } else if (positionValue == 6) {
                iv_discern_banker_3.setBackgroundResource(Utils.getPokeByIndex(result[1]));
            }
            upPoke(result[1], positionValue);
        } else {
            ToastUtil.showToast(this, "失败错误");
        }
    }

    private void showPokeInfo2(int cardId, int positionId) {

        if (positionId == 1) {
            iv_discern_player_1.setBackgroundResource(Utils.getPokeByIndex(cardId));
        } else if (positionId == 2) {
            iv_discern_player_2.setBackgroundResource(Utils.getPokeByIndex(cardId));
        } else if (positionId == 3) {
            iv_discern_player_3.setBackgroundResource(Utils.getPokeByIndex(cardId));
        } else if (positionId == 4) {
            iv_discern_banker_1.setBackgroundResource(Utils.getPokeByIndex(cardId));
        } else if (positionId == 5) {
            iv_discern_banker_2.setBackgroundResource(Utils.getPokeByIndex(cardId));
        } else if (positionId == 6) {
            iv_discern_banker_3.setBackgroundResource(Utils.getPokeByIndex(cardId));
        }
    }

    @Override
    protected void onDestroy() {
        canSubscribeMqTopic(IConst.TOPIC + roomId);
        LotteryApplication.bus.unregister(this);
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_start:
                generateGame();
                showCoutDown(30);
                break;
            case R.id.tv_cancel:
                cancelRound();
                break;
            case R.id.iv_back:
                this.finish();
                break;
        }
    }

    private void showCutDown() {

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
                            showCoutDown(ret.getData().getCountdown());
                            setStatus(ret.getData().getRoundState(), false);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        ToastUtil.showToast(BjlOwnerActivity.this, "失败");

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
                        ToastUtil.showToast(BjlOwnerActivity.this, "失败");

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
                        ToastUtil.showToast(BjlOwnerActivity.this, "失败");

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
                        ToastUtil.showToast(BjlOwnerActivity.this, "失败");
                    }
                });
    }


    private void upPoke(int cardId, int positionId) {
        Network.getNetworkInstance().getLiveApi()
                .upPoke(token, cardId, positionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultReturn<Integer>>() {
                    @Override
                    public void accept(ResultReturn<Integer> resultReturn) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        ToastUtil.showToast(BjlOwnerActivity.this, "失败");
                    }
                });
    }

    @Override
    public void onNotify(MQBean eventData) {
        super.onNotify(eventData);
        if (eventData != null && eventData.getMessage() != null) {
            String data = eventData.getMessage().toString();

            MQPlayerBean mqPlayerBean = GsonUtil.getInstance().toObject(data, MQPlayerBean.class);
            roundId = mqPlayerBean.getRoomRoundId();

            if (mqPlayerBean.getCardId() != 0) {
                showPokeInfo2(mqPlayerBean.getCardId(), mqPlayerBean.getPosition());
            }
        }
    }

}

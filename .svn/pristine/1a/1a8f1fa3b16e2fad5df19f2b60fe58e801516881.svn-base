package com.wyzk.lottery.video.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fsix.mqtt.bean.MQBean;
import com.fsix.mqtt.util.GsonUtil;
import com.wangsu.wsrtc.sdk.WSChatConfig;
import com.wangsu.wsrtc.sdk.WSSurfaceView;
import com.wyzk.lottery.R;
import com.wyzk.lottery.constant.IConst;
import com.wyzk.lottery.model.MQPlayerBean;
import com.wyzk.lottery.model.ResultReturn;
import com.wyzk.lottery.model.RoomModel;
import com.wyzk.lottery.model.RoomRoundModel;
import com.wyzk.lottery.model.RoundInfoModel;
import com.wyzk.lottery.model.UserInfoModel;
import com.wyzk.lottery.model.WinLoseModel;
import com.wyzk.lottery.network.Network;
import com.wyzk.lottery.utils.BuildManager;
import com.wyzk.lottery.utils.TimeUtils;
import com.wyzk.lottery.utils.ToastUtil2;
import com.wyzk.lottery.video.linkmic.LinkMicManager;
import com.wyzk.lottery.video.utils.MyWSChatConfig;
import com.wyzk.lottery.view.MarqueeView;
import com.wyzk.lottery.view.PlaceView;
import com.wyzk.lottery.view.TrendPopupWindow;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 玩家的页面
 */
public class PlayerActivity extends VideoBaseActivity implements OnClickListener {

    private int roomId;
    private int gameId;

    private int roundId;

    private boolean isGameStart = false;//游戏是否开始
    private boolean isBet = false;//定义本人是否下注

    private LinkMicManager mChatManager = null;

    private TextView tv_username;
    private TextView tv_user_integral;
    private TextView tv_bet_integral;
    private int userIntegral;//用户积分
    private int betIntegral; //用户已下注积分
    private TextView serial_num;//流水号

    private TextView status;//当前状态 ，下注 封盘。。。
    private TextView time;//剩余时间

    private PlaceView p1;//方位1
    private PlaceView p2;//方位2
    private PlaceView p3;//方位3
    private PlaceView p4;//方位4
    private PlaceView p5;//方位5
    private PlaceView p6;//方位6
    private int positionId;

    private TextView show;

    private Button yishi;
    private Button wushi;
    private Button yibai;
    private Button wubai;
    private Button yik;
    private Button wuk;
    private Button shik;
    private Button wushik;
    private Button yibaik;

    private Handler handler = new Handler(Looper.getMainLooper());
    private int countdown;

    private Runnable countDownRunnable = new Runnable() {
        @Override
        public void run() {
            if (countdown <= 0) {
                fengPan();
            } else {
                handler.postDelayed(countDownRunnable, 1000);
            }
            time.setText(countdown + "s");
            countdown--;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player2);

        BuildManager.setStatusTransOther(this);

        init();

        getUserInfo();//用户信息

        getLastRoomRound();//场次信息

        getTrend();//趋势
    }

    @Override
    protected void networkAvailable() {
        super.networkAvailable();
        getUserInfo();//用户信息
        getLastRoomRound();//场次信息
        getTrend();//趋势
    }

    private void init() {
        WSSurfaceView wSSurfaceView = (WSSurfaceView) findViewById(R.id.surface_main);

        tv_user_integral = (TextView) findViewById(R.id.tv_user_integral);
        tv_username = (TextView) findViewById(R.id.tv_username);
        tv_bet_integral = (TextView) findViewById(R.id.bet_integral);
        serial_num = (TextView) findViewById(R.id.serial_num);
        status = (TextView) findViewById(R.id.status);
        time = (TextView) findViewById(R.id.time);

        p1 = (PlaceView) findViewById(R.id.p1);
        p2 = (PlaceView) findViewById(R.id.p2);
        p3 = (PlaceView) findViewById(R.id.p3);
        p4 = (PlaceView) findViewById(R.id.p4);
        p5 = (PlaceView) findViewById(R.id.p5);
        p6 = (PlaceView) findViewById(R.id.p6);

        p1.setOnClickListener(this);
        p2.setOnClickListener(this);
        p3.setOnClickListener(this);
        p4.setOnClickListener(this);
        p5.setOnClickListener(this);
        p6.setOnClickListener(this);

        show = (TextView) findViewById(R.id.show);
        show.setOnClickListener(this);

        yishi = (Button) findViewById(R.id.yishi);
        wushi = (Button) findViewById(R.id.wushi);
        yibai = (Button) findViewById(R.id.yibai);
        wubai = (Button) findViewById(R.id.wubai);
        yik = (Button) findViewById(R.id.yik);
        wuk = (Button) findViewById(R.id.wuk);
        shik = (Button) findViewById(R.id.shik);
        wushik = (Button) findViewById(R.id.wushik);
        yibaik = (Button) findViewById(R.id.yibaik);

        yishi.setOnClickListener(this);
        wushi.setOnClickListener(this);
        yibai.setOnClickListener(this);
        wubai.setOnClickListener(this);
        yik.setOnClickListener(this);
        wuk.setOnClickListener(this);
        shik.setOnClickListener(this);
        wushik.setOnClickListener(this);
        yibaik.setOnClickListener(this);

        final TextView current_time = (TextView) findViewById(R.id.current_time);
        TimeUtils.sub(new TimeUtils.CallBack() {
            @Override
            public void onNext(String str) {
                if (!TextUtils.isEmpty(str)) {
                    current_time.setText(str);
                }
            }
        });

        RoomModel.RowModel rowModel = (RoomModel.RowModel) getIntent().getSerializableExtra(IConst.ROW_INFO);
        roomId = rowModel.getRoomId();
        gameId = rowModel.getGameId();

        subscribeMqTopic(IConst.TOPIC + roomId);

        WSChatConfig wsChatConfig = MyWSChatConfig.getWSChatConfig(wSSurfaceView, rowModel.getRoomAddress(), rowModel.getRoomAddress(),
                rowModel.getUserId(), rowModel.getRoomId() + "", false);

        mChatManager = LinkMicManager.getInstance();
        mChatManager.init(this, true, wsChatConfig);
    }


    private void getUserInfo() {
        Network.getNetworkInstance().getUserApi()
                .getUserInfo(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResultReturn<UserInfoModel>>() {
                    @Override
                    public void call(ResultReturn<UserInfoModel> userInfoModelResultReturn) {
                        if (userInfoModelResultReturn != null && userInfoModelResultReturn.getData() != null) {
                            UserInfoModel user = userInfoModelResultReturn.getData();
                            userIntegral = user.getIntegralValue();
                            tv_user_integral.setText("" + user.getIntegralValue());
                            tv_username.setText("" + user.getUsername());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    /**
     * 获取趋势
     */
    private List<RoundInfoModel> roundInfoModels = new ArrayList<>();

    private synchronized void getTrend() {
        roundInfoModels.clear();
        //添加的方位信息
        RoundInfoModel roundInfoModel = new RoundInfoModel();
        roundInfoModel.setRoomRoundId(getString(R.string.serial_number));
        roundInfoModel.setEastNorth(getString(R.string.place1));
        roundInfoModel.setEast(getString(R.string.place2));
        roundInfoModel.setEastSouth(getString(R.string.place3));
        roundInfoModel.setWestNorth(getString(R.string.place4));
        roundInfoModel.setWest(getString(R.string.place5));
        roundInfoModel.setWestSouth(getString(R.string.place6));
        roundInfoModel.setWinLostValue(getString(R.string.win_lose));
        roundInfoModels.add(roundInfoModel);

        Network.getNetworkInstance().getLiveApi()
                .getTrend(token, roomId, gameId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResultReturn<List<RoundInfoModel>>>() {
                    @Override
                    public void call(ResultReturn<List<RoundInfoModel>> ret) {
                        if (ret.getCode() == ResultReturn.ResultCode.RESULT_OK.getValue()) {
                            if (ret.getData() != null) {
                                roundInfoModels.addAll(ret.getData());
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                    }
                });
    }

    /**
     * 显示趋势
     */
    private void showTrend() {
        TrendPopupWindow pw = new TrendPopupWindow(this, roundInfoModels);
        pw.show(this);
    }

    private void getLastRoomRound() {
        Network.getNetworkInstance().getLiveApi()
                .getLastRoomRound(token, roomId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResultReturn<RoomRoundModel>>() {
                    @Override
                    public void call(ResultReturn<RoomRoundModel> ret) {
                        if (ret != null && ret.getCode() == ResultReturn.ResultCode.RESULT_OK.getValue() && ret.getData() != null) {
                            updateRoomStatus(ret.getData());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                    }
                });
    }

    private void updateRoomStatus(RoomRoundModel r) {
        if (r != null) {

            //场次id
            roundId = r.getRoomRoundId();

            //下注位置
            setChecked(r.getBetPosition());

            countdown = r.getCountdown();

            //标记本人是否下注
            if (r.getBetValue() > 0) {
                isBet = true;
            }

            //已经下注的积分
            betIntegral = r.getBetValue();
            tv_bet_integral.setText(String.valueOf(betIntegral));

            //设置当前的状态
            setStatus(r.getRoundState(), false);

            List<RoomRoundModel.PlaceBetModel> placeBetModels = r.getList();
            if (placeBetModels != null && placeBetModels.size() > 0) {
                int length = placeBetModels.size();
                for (int i = 0; i < length; i++) {
                    RoomRoundModel.PlaceBetModel placeBetModel = placeBetModels.get(i);
                    setPlaceValue(placeBetModel.getPosition(), placeBetModel.getBetValue(), placeBetModel.getCount());
                }
            } else {
                clearData();
            }
        }
    }

    @Override
    public void onNotify(MQBean eventData) {
        super.onNotify(eventData);
        if (eventData != null && eventData.getMessage() != null) {
            String data = eventData.getMessage().toString();
            MQPlayerBean mqPlayerBean = GsonUtil.getInstance().toObject(data, MQPlayerBean.class);
            roundId = mqPlayerBean.getRoomRoundId();
            setPlaceValue(mqPlayerBean.getPosition(), mqPlayerBean.getBetValue());
            getUserInfo();
            setStatus(mqPlayerBean.getStatus(), true);
        }
    }

    private void killTimer() {
        handler.removeCallbacks(countDownRunnable);
        time.setText(getString(R.string.zero_second));
    }

    /**
     * @param value
     * @param flag  是否需要清除数据
     */
    public void setStatus(int value, boolean flag) {
        //设置序列号
        serial_num.setText(getString(R.string.serial_number) + ": " + roundId);
        switch (value) {
            case 1://下注中
                betting(flag);
                break;
            case 2://封盘
                fengPan();
                break;
            case 3://结算中
                ending();
                break;
            case 4://结算完成
                ended();
                break;
            case 5://场次作废
                cancel();
                break;
        }
    }

    /**
     * 下注中
     * 为什么此处需要用flag标记 因为可能用户下注完 退出当前游戏页面了 然后再进房间 考虑到这种情况下
     */
    private void betting(boolean flag) {
        status.setText(getString(R.string.status1));
        time.setVisibility(View.VISIBLE);

        isGameStart = false;

        if (flag) {
            //flag 为true时是对应的从MQTT接收到的开始下注的情况，此时正式开始游戏
            countdown = IConst.GAME_TIME;//设置初始化游戏时间60秒
            handler.post(countDownRunnable);
            isBet = false;
            clearData();
        } else {
            //flag 为false时是对应的从最新场次接口获取到的最新场次信息的情况
            if (countdown > 0) {
                handler.post(countDownRunnable);
            } else {
                killTimer();
            }
        }
    }

    /**
     * 封盘中
     */
    private void fengPan() {
        killTimer();
        status.setText(getString(R.string.status2));
        time.setVisibility(View.GONE);
        isGameStart = true;
    }

    /**
     * 结算中
     */
    private void ending() {
        killTimer();
        status.setText(getString(R.string.status3));
        time.setVisibility(View.GONE);
        isGameStart = true;
    }

    /**
     * 结算完成
     */
    private void ended() {
        killTimer();
        status.setText(getString(R.string.status4));
        time.setVisibility(View.GONE);
        isGameStart = true;
        tv_bet_integral.setText(String.valueOf(0));
        clearData();
        getTrend();
        if (roundId != 0 && isBet) {
            //本人下注了才需要查询盈亏情况
            getWinLoseByRoundId(roundId);
        }
    }

    /**
     * 场次作废
     */
    private void cancel() {
        killTimer();
        status.setText(getString(R.string.status5));
        time.setVisibility(View.GONE);
        isGameStart = true;
        tv_bet_integral.setText(String.valueOf(0));
        clearData();
    }

    /**
     * 场次作废 清除数据
     */
    private void clearData() {
        p1.setItemNumText(0);
        p1.setItemPlacePeo(0);
        p2.setItemNumText(0);
        p2.setItemPlacePeo(0);
        p3.setItemNumText(0);
        p3.setItemPlacePeo(0);
        p4.setItemNumText(0);
        p4.setItemPlacePeo(0);
        p5.setItemNumText(0);
        p5.setItemPlacePeo(0);
        p6.setItemNumText(0);
        p6.setItemPlacePeo(0);
        clearSelect();
        positionId = 0;//清空选择的位置
    }

    /**
     * 清除方位选择
     */
    private void clearSelect() {
        p1.setItemChecked(false);
        p2.setItemChecked(false);
        p3.setItemChecked(false);
        p4.setItemChecked(false);
        p5.setItemChecked(false);
        p6.setItemChecked(false);
    }


    private void setPlaceValue(int position, int betValue) {
        switch (position) {
            case 1:
                p1.setItemNumText(p1.getItemNum() + betValue);
                p1.setItemPlacePeo(p1.getItemPlacePeo() + 1);
                break;
            case 2:
                p2.setItemNumText(p2.getItemNum() + betValue);
                p2.setItemPlacePeo(p2.getItemPlacePeo() + 1);
                break;
            case 3:
                p3.setItemNumText(p3.getItemNum() + betValue);
                p3.setItemPlacePeo(p3.getItemPlacePeo() + 1);
                break;
            case 4:
                p4.setItemNumText(p4.getItemNum() + betValue);
                p4.setItemPlacePeo(p4.getItemPlacePeo() + 1);
                break;
            case 5:
                p5.setItemNumText(p5.getItemNum() + betValue);
                p5.setItemPlacePeo(p5.getItemPlacePeo() + 1);
                break;
            case 6:
                p6.setItemNumText(p6.getItemNum() + betValue);
                p6.setItemPlacePeo(p6.getItemPlacePeo() + 1);
                break;
        }
    }

    private void setPlaceValue(int position, int betValue, int count) {
        switch (position) {
            case 1:
                p1.setItemNumText(betValue);
                p1.setItemPlacePeo(count);
                break;
            case 2:
                p2.setItemNumText(betValue);
                p2.setItemPlacePeo(count);
                break;
            case 3:
                p3.setItemNumText(betValue);
                p3.setItemPlacePeo(count);
                break;
            case 4:
                p4.setItemNumText(betValue);
                p4.setItemPlacePeo(count);
                break;
            case 5:
                p5.setItemNumText(betValue);
                p5.setItemPlacePeo(count);
                break;
            case 6:
                p6.setItemNumText(betValue);
                p6.setItemPlacePeo(count);
                break;
        }
    }

    @Override
    protected void onResume() {
        mChatManager.resume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mChatManager.pause();
        handler.removeCallbacks(countDownRunnable);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mChatManager.release();
        TimeUtils.disSub();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.p1:
                setChecked(1);
                break;
            case R.id.p2:
                setChecked(2);
                break;
            case R.id.p3:
                setChecked(3);
                break;
            case R.id.p4:
                setChecked(4);
                break;
            case R.id.p5:
                setChecked(5);
                break;
            case R.id.p6:
                setChecked(6);
                break;
            case R.id.show:
                showTrend();
                break;
            case R.id.yishi:
                setCoin(10);
                break;
            case R.id.wushi:
                setCoin(50);
                break;
            case R.id.yibai:
                setCoin(100);
                break;
            case R.id.wubai:
                setCoin(500);
                break;
            case R.id.yik:
                setCoin(1000);
                break;
            case R.id.wuk:
                setCoin(5000);
                break;
            case R.id.shik:
                setCoin(10000);
                break;
            case R.id.wushik:
                setCoin(50000);
                break;
            case R.id.yibaik:
                setCoin(100000);
                break;
            default:
                break;
        }
    }

    private void setChecked(int position) {
        if (isGameStart) {
            tips(getString(R.string.game_already_start));
            return;
        }
        if (isBet) {
            tips(getString(R.string.already_bet));
            return;
        }
        p1.setItemChecked(false);
        p2.setItemChecked(false);
        p3.setItemChecked(false);
        p4.setItemChecked(false);
        p5.setItemChecked(false);
        p6.setItemChecked(false);
        switch (position) {
            case 1:
                positionId = 1;
                p1.setItemChecked(true);
                break;
            case 2:
                positionId = 2;
                p2.setItemChecked(true);
                break;
            case 3:
                positionId = 3;
                p3.setItemChecked(true);
                break;
            case 4:
                positionId = 4;
                p4.setItemChecked(true);
                break;
            case 5:
                positionId = 5;
                p5.setItemChecked(true);
                break;
            case 6:
                positionId = 6;
                p6.setItemChecked(true);
                break;
        }
        betIntegral = 0;
        tv_bet_integral.setText(String.valueOf(betIntegral));
    }

    /**
     * 设置筹码
     *
     * @param data 按钮上面的筹码大小
     */
    private void setCoin(int data) {
        if (isGameStart) {
            tips(getString(R.string.game_already_start));
            return;
        }
        if (positionId == 0) {
            tips(getString(R.string.select_place));
            return;
        }
        if (isBet) {
            tips(getString(R.string.already_bet));
            return;
        }
        betIntegral = betIntegral + data;
        tv_bet_integral.setText(String.valueOf(betIntegral));
    }

    /**
     * 确定下注信息
     *
     * @param view
     */
    public void onSubmitScore(View view) {
        if (roundId == 0) {
            tips(getString(R.string.game_not_start));
            return;
        }
        if (isGameStart) {
            tips(getString(R.string.game_already_start));
            return;
        }
        if (isBet) {
            tips(getString(R.string.already_bet));
            return;
        }
        if (positionId == 0 || betIntegral == 0) {
            tips(getString(R.string.please_select_place_or_integral));
            return;
        }
        if (betIntegral > userIntegral) {
            tips(getString(R.string.integral_shortage));
            return;
        }
        bet();
    }

    private void bet() {
        Network.getNetworkInstance().getLiveApi()
                .bet(token, roomId, positionId, roundId, betIntegral)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResultReturn<String>>() {
                    @Override
                    public void call(ResultReturn<String> resultReturn) {
                        if (resultReturn != null && resultReturn.getCode() == ResultReturn.ResultCode.RESULT_OK.getValue()) {
                            isBet = true;
                            tips(getString(R.string.bet_success));
                        } else {
                            tips(resultReturn.getMsg() + "");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                    }
                });
    }

    /**
     * @param view
     */
    public void onClearScore(View view) {
        if (isGameStart) {
            tips(getString(R.string.game_already_start_cannot_clear));
            return;
        }
        if (isBet) {
            tips(getString(R.string.already_bet_cannot_clear));
            return;
        }
        betIntegral = 0;
        tv_bet_integral.setText(String.valueOf(0));
        clearSelect();
    }

    public void toServiceCall(View view) {
        toCall();
    }

    private void getWinLoseByRoundId(int roundId) {
        Network.getNetworkInstance().getLiveApi()
                .getWinLoseByRoundId(token, roundId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResultReturn<WinLoseModel>>() {
                    @Override
                    public void call(ResultReturn<WinLoseModel> resultReturn) {
                        if (resultReturn != null && resultReturn.getCode() == ResultReturn.ResultCode.RESULT_OK.getValue()) {
                            showResult(resultReturn.getData().getWinLoseValue());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                    }
                });
    }

    ToastUtil2 toastUtil;

    private void showResult(int value) {
        View view = View.inflate(this, R.layout.dialog_result, null);
        SimpleDraweeView sdv = (SimpleDraweeView) view.findViewById(R.id.sdv);
        TextView tvPoint = (TextView) view.findViewById(R.id.tv_my_point);
        if (value > 0) {
            sdv.setBackgroundResource(R.drawable.win);
            tvPoint.setText("恭喜你!本局赢了" + value + "分!");
            tvPoint.setTextColor(Color.parseColor("#FFFF0000"));
        } else if (value < 0) {
            sdv.setBackgroundResource(R.drawable.lose);
            tvPoint.setText("加油!本局输了" + value + "分!");
            tvPoint.setTextColor(Color.parseColor("#FF00FF00"));
        } else {
            sdv.setBackgroundResource(R.drawable.win);
            tvPoint.setText("加油!本局是平局!");
            tvPoint.setTextColor(Color.parseColor("#FF323232"));
        }

        toastUtil = new ToastUtil2(this, view);
        toastUtil.show(3000);
    }
}

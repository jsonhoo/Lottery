package com.wyzk.lottery.video.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fsix.mqtt.bean.MQBean;
import com.fsix.mqtt.util.GsonUtil;
import com.wangsu.wsrtc.sdk.WSChatConfig;
import com.wangsu.wsrtc.sdk.WSSurfaceView;
import com.wyzk.lottery.R;
import com.wyzk.lottery.constant.IConst;
import com.wyzk.lottery.model.ExtraBean;
import com.wyzk.lottery.model.MQPlayerBean;
import com.wyzk.lottery.model.ResultReturn;
import com.wyzk.lottery.model.RoomModel;
import com.wyzk.lottery.model.RoomRoundModel;
import com.wyzk.lottery.model.UserInfoModel;
import com.wyzk.lottery.network.Network;
import com.wyzk.lottery.ui.SetRateActivity;
import com.wyzk.lottery.utils.BuildManager;
import com.wyzk.lottery.utils.FastClickUtils;
import com.wyzk.lottery.utils.TimeUtils;
import com.wyzk.lottery.video.linkmic.LinkMicManager;
import com.wyzk.lottery.video.utils.MyWSChatConfig;
import com.wyzk.lottery.view.MarqueeView;
import com.wyzk.lottery.view.PlaceView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;


/**
 * 房主的页面
 */
public class HouseOwnerActivity extends VideoBaseActivity implements OnClickListener {
    private TextView serial_num;//流水号

    private View createView, cancelView, fengpan, reportResult;

    private TextView status;//当前状态 ，下注 封盘。。。
    private TextView time;//剩余时间

    private LinearLayout llRank;//排名
    private Button yi;
    private Button er;
    private Button san;
    private Button si;
    private Button wu;
    private Button liu;

    private TextView tv_username;
    private TextView tv_user_integral;

    private PlaceView p1;//方位1
    private PlaceView p2;//方位2
    private PlaceView p3;//方位3
    private PlaceView p4;//方位4
    private PlaceView p5;//方位5
    private PlaceView p6;//方位6

    private MarqueeView mMarqueeView;

    private int roomId;

    private int roundId;

    private LinkMicManager mChatManager = null;

    private Handler handler = new Handler(Looper.getMainLooper());

    private int countdown = IConst.GAME_TIME;

    private Runnable countDownRunnable = new Runnable() {
        @Override
        public void run() {
            if (countdown <= 0) {
                fengpanGame();
            } else {
                handler.postDelayed(countDownRunnable, 1000);
            }
            time.setText(countdown + "s");
            countdown--;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_owner);

        init();

        BuildManager.setStatusTransOther(this);

        getUserInfo();//用户信息

        initStatus();
        getLastRoomRound();//获取场次信息
    }

    @Override
    protected void networkAvailable() {
        super.networkAvailable();
        getUserInfo();//用户信息
        getLastRoomRound();//获取场次信息
    }

    private void init() {
        serial_num = (TextView) findViewById(R.id.serial_num);

        status = (TextView) findViewById(R.id.status);
        time = (TextView) findViewById(R.id.time);

        WSSurfaceView wSSurfaceView = (WSSurfaceView) findViewById(R.id.preview);
        final RelativeLayout surface_group = (RelativeLayout) findViewById(R.id.surface_group);
        final ImageView iv_arrow = (ImageView) findViewById(R.id.iv_arrow);
        iv_arrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) surface_group.getLayoutParams();
                if (lp.width == WindowManager.LayoutParams.MATCH_PARENT) {
                    lp.width = (int) (getResources().getDimension(R.dimen.dimens220) + 0.5f);
                    lp.height = (int) (getResources().getDimension(R.dimen.dimens160) + 0.5f);
                    iv_arrow.setImageResource(R.mipmap.uvv_player_scale_btn);
                } else {
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                    iv_arrow.setImageResource(R.mipmap.uvv_star_zoom_in);
                }
                surface_group.setLayoutParams(lp);
            }
        });

        createView = findViewById(R.id.generate);
        cancelView = findViewById(R.id.cancle);
        fengpan = findViewById(R.id.fengpan);
        reportResult = findViewById(R.id.reportResult);

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

        mMarqueeView = (MarqueeView) findViewById(R.id.adv);
        mMarqueeView.setText(getString(R.string.tips));

        tv_user_integral = (TextView) findViewById(R.id.tv_user_integral);
        tv_username = (TextView) findViewById(R.id.tv_username);

        llRank = (LinearLayout) findViewById(R.id.ll_rank);

        yi = (Button) findViewById(R.id.yi);
        er = (Button) findViewById(R.id.er);
        san = (Button) findViewById(R.id.san);
        si = (Button) findViewById(R.id.si);
        wu = (Button) findViewById(R.id.wu);
        liu = (Button) findViewById(R.id.liu);
        yi.setOnClickListener(this);
        er.setOnClickListener(this);
        san.setOnClickListener(this);
        si.setOnClickListener(this);
        wu.setOnClickListener(this);
        liu.setOnClickListener(this);

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

        subscribeMqTopic(IConst.TOPIC + roomId);

        WSChatConfig wsChatConfig = MyWSChatConfig.getWSChatConfig(wSSurfaceView, rowModel.getRoomAddress(), rowModel.getRoomAddress(),
                rowModel.getUserId(), rowModel.getUserId() + "", true);

        mChatManager = LinkMicManager.getInstance();
        mChatManager.init(this, true, wsChatConfig);
    }


    private void getUserInfo() {
        Network.getNetworkInstance().getUserApi()
                .getUserInfo(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultReturn<UserInfoModel>>() {
                    @Override
                    public void accept(ResultReturn<UserInfoModel> userInfoModelResultReturn) throws Exception {
                        if (userInfoModelResultReturn != null && userInfoModelResultReturn.getData() != null) {
                            UserInfoModel user = userInfoModelResultReturn.getData();
                            tv_user_integral.setText("" + user.getIntegralValue());
                            tv_username.setText("" + user.getUsername());
                        }
                    }
                });
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
                            updateRoomStatus(ret.getData());
                        }
                    }
                });
    }

    private void updateRoomStatus(RoomRoundModel r) {
        if (r != null) {
            //场次id
            roundId = r.getRoomRoundId();

            countdown = r.getCountdown();

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
            final MQPlayerBean mqPlayerBean = GsonUtil.getInstance().toObject(data, MQPlayerBean.class);
            roundId = mqPlayerBean.getRoomRoundId();
            setPlaceValue(mqPlayerBean.getPosition(), mqPlayerBean.getBetValue());
            getUserInfo();
            setStatus(mqPlayerBean.getStatus(), true);
        }
    }

    public void setStatus(int value, boolean flag) {
        serial_num.setText(getString(R.string.serial_number) + ": " + roundId);
        switch (value) {
            case 1://下注中
                betting(flag);
                break;
            case 2://封盘中
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
     * 初始化状态
     *
     * @return
     */
    private void initStatus() {
        createView.setEnabled(true);
        cancelView.setEnabled(false);
        fengpan.setEnabled(false);
        reportResult.setEnabled(false);
    }

    /**
     * 下注中状态
     */
    private void betting(boolean flag) {
        status.setText(getString(R.string.status1));
        time.setVisibility(View.VISIBLE);
        createView.setEnabled(false);
        cancelView.setEnabled(true);
        fengpan.setEnabled(true);
        reportResult.setEnabled(false);
        llRank.setVisibility(View.GONE);
        if (flag) {
            countdown = IConst.GAME_TIME;//设置初始化游戏时间60秒
            handler.post(countDownRunnable);
            clearData();
            resetData();
        } else {
            if (countdown > 0) {
                handler.post(countDownRunnable);
            } else {
                killTimer();
            }
        }
    }


    /**
     * 封盘状态
     */
    private void fengPan() {
        killTimer();
        status.setText(getString(R.string.status2));
        time.setVisibility(View.GONE);
        createView.setEnabled(false);
        cancelView.setEnabled(true);
        fengpan.setEnabled(false);
        reportResult.setEnabled(true);
        llRank.setVisibility(View.GONE);
    }

    /**
     * 结算中  这是个事务  一般不会存在这个状态很久
     */
    private void ending() {
        killTimer();
        status.setText(getString(R.string.status3));
        time.setVisibility(View.GONE);
        llRank.setVisibility(View.GONE);
    }

    /**
     * 结算完成
     */
    private void ended() {
        killTimer();
        status.setText(getString(R.string.status4));
        time.setVisibility(View.GONE);
        createView.setEnabled(true);
        cancelView.setEnabled(false);
        fengpan.setEnabled(false);
        reportResult.setEnabled(false);
        llRank.setVisibility(View.GONE);
        clearData();
        resetData();
    }

    /**
     * 场次作废
     */
    private void cancel() {
        killTimer();
        status.setText(getString(R.string.status5));
        time.setVisibility(View.GONE);
        createView.setEnabled(true);
        cancelView.setEnabled(false);
        fengpan.setEnabled(false);
        reportResult.setEnabled(false);
        llRank.setVisibility(View.GONE);
        clearData();
        resetData();
    }

    private void killTimer() {
        handler.removeCallbacks(countDownRunnable);
        time.setText(getString(R.string.zero_second));
    }

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
    }

    private void resetData() {
        p1.setItemChecked(false);
        p2.setItemChecked(false);
        p3.setItemChecked(false);
        p4.setItemChecked(false);
        p5.setItemChecked(false);
        p6.setItemChecked(false);
        p1.clearRank();
        p2.clearRank();
        p3.clearRank();
        p4.clearRank();
        p5.clearRank();
        p6.clearRank();
        positionId = 0;
    }

    @Override
    public void onClick(final View v) {
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
            case R.id.yi:
                setButtonChecked(1);
                break;
            case R.id.er:
                setButtonChecked(2);
                break;
            case R.id.san:
                setButtonChecked(3);
                break;
            case R.id.si:
                setButtonChecked(4);
                break;
            case R.id.wu:
                setButtonChecked(5);
                break;
            case R.id.liu:
                setButtonChecked(6);
                break;
            default:
                break;
        }
    }

    private int positionId;

    /**
     * 方位选择
     *
     * @param position
     */
    private void setChecked(int position) {
        p1.setItemChecked(false);
        p2.setItemChecked(false);
        p3.setItemChecked(false);
        p4.setItemChecked(false);
        p5.setItemChecked(false);
        p6.setItemChecked(false);
        switch (position) {
            case 1:
                setPosition(1, p1);
                break;
            case 2:
                setPosition(2, p2);
                break;
            case 3:
                setPosition(3, p3);
                break;
            case 4:
                setPosition(4, p4);
                break;
            case 5:
                setPosition(5, p5);
                break;
            case 6:
                setPosition(6, p6);
                break;
        }
    }

    /**
     * 设置方位
     */
    private void setPosition(int position, PlaceView p) {
        positionId = position;
        p.setItemChecked(true);
    }

    /**
     * 排名按钮
     *
     * @param checkedId
     */
    private void setButtonChecked(int checkedId) {
        if (positionId == 0) {
            tips(getString(R.string.please_select_place));
            return;
        }
        switch (checkedId) {
            case 1:
                setScore(1);
                yi.setVisibility(View.INVISIBLE);
                break;
            case 2:
                setScore(2);
                er.setVisibility(View.INVISIBLE);
                break;
            case 3:
                setScore(3);
                san.setVisibility(View.INVISIBLE);
                break;
            case 4:
                setScore(4);
                si.setVisibility(View.INVISIBLE);
                break;
            case 5:
                setScore(5);
                wu.setVisibility(View.INVISIBLE);
                break;
            case 6:
                setScore(6);
                liu.setVisibility(View.INVISIBLE);
                break;
        }
    }

    /**
     * 设置分数
     */
    private void setScore(int value) {
        switch (positionId) {
            case 1:
                setRank(p1, value);
                break;
            case 2:
                setRank(p2, value);
                break;
            case 3:
                setRank(p3, value);
                break;
            case 4:
                setRank(p4, value);
                break;
            case 5:
                setRank(p5, value);
                break;
            case 6:
                setRank(p6, value);
                break;
        }
        positionId = 0;
    }

    /**
     * 设置排名
     */
    private void setRank(PlaceView p, int value) {
        switch (p.getRank()) {
            case 1:
                yi.setVisibility(View.VISIBLE);
                break;
            case 2:
                er.setVisibility(View.VISIBLE);
                break;
            case 3:
                san.setVisibility(View.VISIBLE);
                break;
            case 4:
                si.setVisibility(View.VISIBLE);
                break;
            case 5:
                wu.setVisibility(View.VISIBLE);
                break;
            case 6:
                liu.setVisibility(View.VISIBLE);
                break;
        }
        p.setRank(String.valueOf(value));
    }


    /**
     * 重置结果
     */
    public void resetResult(View view) {
        setViewVisible();
        resetData();
    }

    private void setViewVisible() {
        yi.setVisibility(View.VISIBLE);
        er.setVisibility(View.VISIBLE);
        san.setVisibility(View.VISIBLE);
        si.setVisibility(View.VISIBLE);
        wu.setVisibility(View.VISIBLE);
        liu.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onResume() {
        mChatManager.resume();
        mMarqueeView.startScroll();
        super.onResume();
    }

    @Override
    protected void onStop() {
        mChatManager.pause();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mChatManager.release();
        TimeUtils.disSub();
        mMarqueeView.stopScroll();

        canSubscribeMqTopic(IConst.TOPIC + roomId);

        super.onDestroy();
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
            default:
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

    /**
     * 生成场次
     *
     * @param view
     */
    public void generate(View view) {
        if (FastClickUtils.isClickAvailable()) {
            generateGame();
        }
    }

    /**
     * 场次作废
     *
     * @param view
     */
    public void onCancel(View view) {
        if (FastClickUtils.isClickAvailable()) {
            cancelRound();
        }
    }

    /**
     * 封盘
     *
     * @param view
     */
    public void onFengPan(View view) {
        if (FastClickUtils.isClickAvailable()) {
            fengpanGame();
        }
    }

    /**
     * 封盘
     *
     * @param view
     */
    public void onReport(View view) {
        if (FastClickUtils.isClickAvailable()) {
            reportResult.setEnabled(false);
            llRank.setVisibility(View.VISIBLE);
            setViewVisible();
        }
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
                        } else {
                            tips(resultReturn.getMsg() + "");
                        }
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
                });
    }


    /**
     * 结算
     *
     * @param view
     */
    public void onSubmitResult(View view) {
        int value1 = p1.getRank();
        int value2 = p2.getRank();
        int value3 = p3.getRank();
        int value4 = p4.getRank();
        int value5 = p5.getRank();
        int value6 = p6.getRank();

        if (value1 == 0 || value2 == 0 || value3 == 0 || value4 == 0 || value5 == 0 || value6 == 0) {
            tips("请正确上报数据");
            return;
        }

        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("position", 1);
            jsonObject1.put("rank", value1);

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("position", 2);
            jsonObject2.put("rank", value2);

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("position", 3);
            jsonObject3.put("rank", value3);

            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("position", 4);
            jsonObject4.put("rank", value4);

            JSONObject jsonObject5 = new JSONObject();
            jsonObject5.put("position", 5);
            jsonObject5.put("rank", value5);

            JSONObject jsonObject6 = new JSONObject();
            jsonObject6.put("position", 6);
            jsonObject6.put("rank", value6);

            jsonArray.put(0, jsonObject1);
            jsonArray.put(1, jsonObject2);
            jsonArray.put(2, jsonObject3);
            jsonArray.put(3, jsonObject4);
            jsonArray.put(4, jsonObject5);
            jsonArray.put(5, jsonObject6);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody rsbody = RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), jsonArray.toString());
        Network.getNetworkInstance().getLiveApi()
                .settleRound(token, rsbody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultReturn<String>>() {
                    @Override
                    public void accept(ResultReturn<String> resultReturn) throws Exception {
                        if (resultReturn != null && resultReturn.getCode() == ResultReturn.ResultCode.RESULT_OK.getValue()) {
                            tips("上报成功");
                        } else {
                            tips(resultReturn.getMsg() + "");
                        }
                    }
                });

    }


    /**
     * 设置抽成比例页面
     *
     * @param view
     */
    public void setRate(View view) {
        if (FastClickUtils.isClickAvailable()) {
            ExtraBean extraBean = new ExtraBean();
            extraBean.setData(roomId);
            toActivityWithExtra(extraBean, SetRateActivity.class);
        }
    }

}

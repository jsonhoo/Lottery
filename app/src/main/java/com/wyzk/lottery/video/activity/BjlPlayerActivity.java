package com.wyzk.lottery.video.activity;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fsix.mqtt.bean.MQBean;
import com.fsix.mqtt.util.GsonUtil;
import com.wyzk.lottery.R;
import com.wyzk.lottery.adapter.LayoutAdapter;
import com.wyzk.lottery.constant.IConst;
import com.wyzk.lottery.model.BetVO;
import com.wyzk.lottery.model.MQPlayerBean;
import com.wyzk.lottery.model.ResultReturn;
import com.wyzk.lottery.model.RoomModel;
import com.wyzk.lottery.model.RoomRoundModel;
import com.wyzk.lottery.model.UserBetVO;
import com.wyzk.lottery.network.Network;
import com.wyzk.lottery.utils.BuildManager;
import com.wyzk.lottery.utils.ToastUtil;
import com.wyzk.lottery.utils.Utils;
import com.wyzk.lottery.view.CountDownView;

import org.lucasr.twowayview.widget.DividerItemDecoration;
import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;


/**
 * 玩家的页面
 */
public class BjlPlayerActivity extends VideoBaseActivity implements OnClickListener {

    @Bind(R.id.list0)
    TwoWayView list0;


    List<String> data = new ArrayList<>();
    List<String> dataList = new ArrayList<>();


    @Bind(R.id.list)
    TwoWayView list;

    @Bind(R.id.list2)
    TwoWayView list2;

    @Bind(R.id.list3)
    TwoWayView list3;

    @Bind(R.id.list4)
    TwoWayView list4;

    @Bind(R.id.tv_sure)
    TextView tv_sure;

    @Bind(R.id.tv_cancel)
    TextView tv_cancel;


    List<Integer> mItems0 = new ArrayList<>();

    List<Integer> mItems1 = new ArrayList<>();
    List<Integer> mItems2 = new ArrayList<>();
    List<Integer> mItems3 = new ArrayList<>();
    List<Integer> mItems4 = new ArrayList<>();


    @Bind(R.id.l_bet1)
    LinearLayout l_bet1;

    @Bind(R.id.l_bet2)
    LinearLayout l_bet2;

    @Bind(R.id.l_bet3)
    LinearLayout l_bet3;

    @Bind(R.id.l_bet4)
    LinearLayout l_bet4;

    @Bind(R.id.l_bet5)
    LinearLayout l_bet5;

    @Bind(R.id.l_bet6)
    LinearLayout l_bet6;

    @Bind(R.id.l_bet7)
    LinearLayout l_bet7;

    @Bind(R.id.l_bet8)
    LinearLayout l_bet8;

    @Bind(R.id.l_bet9)
    LinearLayout l_bet9;


    @Bind(R.id.yishi)
    ImageView yishi;

    @Bind(R.id.yibai)
    ImageView yibai;

    @Bind(R.id.yik)
    ImageView yik;

    @Bind(R.id.shik)
    ImageView shik;

    @Bind(R.id.yibaik)
    ImageView yibaik;

    private int roomId;
    private int roundId;


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


    @Bind(R.id.tv_serial_num)
    TextView tv_serial_num;

    private RoomRoundModel roomRoundModel;

    @Bind(R.id.countDownView)
    CountDownView countDownView;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bjl_player);
        BuildManager.setStatusTransOther(this);

        ButterKnife.bind(this);


        final Drawable divider = getResources().getDrawable(R.drawable.divider);

        for (int i = 1; i <= 36; i++) {
            mItems0.add(i);
        }
        list0.setHasFixedSize(true);
        list0.addItemDecoration(new DividerItemDecoration(divider));
        list0.setAdapter(new LayoutAdapter(this, mItems0, R.layout.item_3));


        list.setHasFixedSize(true);
        list.addItemDecoration(new DividerItemDecoration(divider));

        for (int i = 0; i < 120; i++) {
            mItems1.add(i);
        }
        list.setAdapter(new LayoutAdapter(this, mItems1, R.layout.item_2));


        list2.setHasFixedSize(true);
        list2.addItemDecoration(new DividerItemDecoration(divider));
        for (int i = 0; i < 60; i++) {
            mItems2.add(i);
        }
        list2.setAdapter(new LayoutAdapter(this, mItems2, R.layout.item_2));


        list3.setHasFixedSize(true);
        list3.addItemDecoration(new DividerItemDecoration(divider));
        for (int i = 0; i < 36; i++) {
            mItems3.add(i);
        }
        list3.setAdapter(new LayoutAdapter(this, mItems3, R.layout.item_2));


        list4.setHasFixedSize(true);
        list4.addItemDecoration(new DividerItemDecoration(divider));
        for (int i = 0; i < 24; i++) {
            mItems4.add(i);
        }
        list4.setAdapter(new LayoutAdapter(this, mItems4, R.layout.item_2));


        tv_sure.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);

        yishi.setOnClickListener(this);
        yibai.setOnClickListener(this);
        yik.setOnClickListener(this);
        shik.setOnClickListener(this);
        yibaik.setOnClickListener(this);

        l_bet1.setOnClickListener(this);
        l_bet2.setOnClickListener(this);
        l_bet3.setOnClickListener(this);

        l_bet4.setOnClickListener(this);
        l_bet5.setOnClickListener(this);
        l_bet6.setOnClickListener(this);

        l_bet7.setOnClickListener(this);
        l_bet8.setOnClickListener(this);
        l_bet9.setOnClickListener(this);

        RoomModel.RowModel rowModel = (RoomModel.RowModel) getIntent().getSerializableExtra(IConst.ROW_INFO);
        roomId = rowModel.getRoomId();
        subscribeMqTopic(IConst.TOPIC + roomId);

        getLastRoomRound();
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


    public void selectBetValue(ImageView view) {

        yishi.setBackground(null);
        yibai.setBackground(null);
        yik.setBackground(null);
        shik.setBackground(null);
        yibaik.setBackground(null);

        if (view == yishi) {
            yishi.setBackgroundResource(R.drawable.rect);
            currentBetBaseValue = 10;
        } else if (view == yibai) {
            yibai.setBackgroundResource(R.drawable.rect);
            currentBetBaseValue = 100;
        } else if (view == yik) {
            yik.setBackgroundResource(R.drawable.rect);
            currentBetBaseValue = 1000;
        } else if (view == shik) {
            shik.setBackgroundResource(R.drawable.rect);
            currentBetBaseValue = 10000;
        } else if (view == yibaik) {
            yibaik.setBackgroundResource(R.drawable.rect);
            currentBetBaseValue = 100000;
        }
    }

    @Override
    protected void networkAvailable() {
        super.networkAvailable();
    }

    private int currentBetBaseValue = 10;
    private int currentSelectPosition = 0;
    private int positionValue = 0;

    private int betPositionValue_1 = 0;
    private int betPositionValue_2 = 0;
    private int betPositionValue_3 = 0;
    private int betPositionValue_4 = 0;
    private int betPositionValue_5 = 0;
    private int betPositionValue_6 = 0;
    private int betPositionValue_7 = 0;
    private int betPositionValue_8 = 0;
    private int betPositionValue_9 = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sure:
                userBetSubmit();
                break;
            case R.id.tv_cancel:
                cancelUserBet();
                countDownView.setCountdownTime(30);
                countDownView.startCountDown();
                break;
            case R.id.yishi:
                selectBetValue(yishi);
                break;
            case R.id.yibai:
                selectBetValue(yibai);
                break;
            case R.id.yik:
                selectBetValue(yik);
                break;
            case R.id.shik:
                selectBetValue(shik);
                break;
            case R.id.yibaik:
                selectBetValue(yibaik);
                break;
            case R.id.l_bet1:
                selectBetPosition(l_bet1);
                break;
            case R.id.l_bet2:
                selectBetPosition(l_bet2);
                break;
            case R.id.l_bet3:
                selectBetPosition(l_bet3);
                break;
            case R.id.l_bet4:
                selectBetPosition(l_bet4);
                break;
            case R.id.l_bet5:
                selectBetPosition(l_bet5);
                break;
            case R.id.l_bet6:
                selectBetPosition(l_bet6);
                break;
            case R.id.l_bet7:
                selectBetPosition(l_bet7);
                break;
            case R.id.l_bet8:
                selectBetPosition(l_bet8);
                break;
            case R.id.l_bet9:
                selectBetPosition(l_bet9);
                break;
        }
    }

    private void selectBetPosition(View v) {
        l_bet1.setBackgroundResource(R.drawable.rect_position_bg);
        l_bet2.setBackgroundResource(R.drawable.rect_position_bg);
        l_bet3.setBackgroundResource(R.drawable.rect_position_bg);
        l_bet4.setBackgroundResource(R.drawable.rect_position_bg);
        l_bet5.setBackgroundResource(R.drawable.rect_position_bg);

        l_bet6.setBackgroundResource(R.drawable.shape_top_right);
        l_bet7.setBackgroundResource(R.drawable.shape_top_left);

        l_bet8.setBackgroundResource(R.drawable.rect_position_bg);
        l_bet9.setBackgroundResource(R.drawable.rect_position_bg);

        if (v == l_bet1) {
            l_bet1.setBackgroundColor(getResources().getColor(R.color.fuchsia));
            currentSelectPosition = 1;
            betPositionValue_1 += currentBetBaseValue;
        } else if (v == l_bet2) {
            l_bet2.setBackgroundColor(getResources().getColor(R.color.fuchsia));
            currentSelectPosition = 2;
            betPositionValue_2 += currentBetBaseValue;
        } else if (v == l_bet3) {
            l_bet3.setBackgroundColor(getResources().getColor(R.color.fuchsia));
            currentSelectPosition = 3;
            betPositionValue_2 += currentBetBaseValue;
        } else if (v == l_bet4) {
            l_bet4.setBackgroundColor(getResources().getColor(R.color.fuchsia));
            currentSelectPosition = 4;
            betPositionValue_4 += currentBetBaseValue;
        } else if (v == l_bet5) {
            l_bet5.setBackgroundColor(getResources().getColor(R.color.fuchsia));
            currentSelectPosition = 5;
            betPositionValue_5 += currentBetBaseValue;
        } else if (v == l_bet6) {
            l_bet6.setBackgroundColor(getResources().getColor(R.color.fuchsia));
            currentSelectPosition = 6;
            betPositionValue_6 += currentBetBaseValue;
        } else if (v == l_bet7) {
            l_bet7.setBackgroundColor(getResources().getColor(R.color.fuchsia));
            currentSelectPosition = 7;
            betPositionValue_7 += currentBetBaseValue;
        } else if (v == l_bet8) {
            l_bet8.setBackgroundColor(getResources().getColor(R.color.fuchsia));
            currentSelectPosition = 8;
            betPositionValue_8 += currentBetBaseValue;
        } else if (v == l_bet9) {
            l_bet9.setBackgroundColor(getResources().getColor(R.color.fuchsia));
            currentSelectPosition = 9;
            betPositionValue_9 += currentBetBaseValue;
        }

    }

    private void startFlick(View view) {
        if (null == view) {
            return;
        }
        view.setBackgroundColor(getResources().getColor(R.color.fuchsia));
        Animation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setInterpolator(new LinearInterpolator());
        alphaAnimation.setRepeatCount(Animation.INFINITE);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        view.startAnimation(alphaAnimation);

    }

    private void stopFlick(View view) {
        if (null == view) {
            return;
        }
        view.clearAnimation();
        view.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void cancelUserBet() {

        betPositionValue_1 = 0;
        betPositionValue_2 = 0;
        betPositionValue_3 = 0;
        betPositionValue_4 = 0;
        betPositionValue_5 = 0;
        betPositionValue_6 = 0;
        betPositionValue_7 = 0;
        betPositionValue_8 = 0;
        betPositionValue_9 = 0;

        //重置
        l_bet1.setBackgroundResource(R.drawable.rect_position_bg);
        l_bet2.setBackgroundResource(R.drawable.rect_position_bg);
        l_bet3.setBackgroundResource(R.drawable.rect_position_bg);
        l_bet4.setBackgroundResource(R.drawable.rect_position_bg);
        l_bet5.setBackgroundResource(R.drawable.rect_position_bg);
        l_bet6.setBackgroundResource(R.drawable.shape_top_right);
        l_bet7.setBackgroundResource(R.drawable.shape_top_left);
        l_bet8.setBackgroundResource(R.drawable.rect_position_bg);
        l_bet9.setBackgroundResource(R.drawable.rect_position_bg);
    }

    private void userBetSubmit() {
        List<BetVO> list = new ArrayList<>();

        if (betPositionValue_1 != 0) {
            BetVO betVO = new BetVO();
            betVO.setPositionId(1);
            betVO.setBetValue(betPositionValue_1);
            list.add(betVO);
        }
        if (betPositionValue_2 != 0) {
            BetVO betVO = new BetVO();
            betVO.setPositionId(2);
            betVO.setBetValue(betPositionValue_2);
            list.add(betVO);
        }
        if (betPositionValue_3 != 0) {
            BetVO betVO = new BetVO();
            betVO.setPositionId(3);
            betVO.setBetValue(betPositionValue_3);
            list.add(betVO);
        }
        if (betPositionValue_4 != 0) {
            BetVO betVO = new BetVO();
            betVO.setPositionId(4);
            betVO.setBetValue(betPositionValue_4);
            list.add(betVO);
        }
        if (betPositionValue_5 != 0) {
            BetVO betVO = new BetVO();
            betVO.setPositionId(5);
            betVO.setBetValue(betPositionValue_5);
            list.add(betVO);
        }
        if (betPositionValue_6 != 0) {
            BetVO betVO = new BetVO();
            betVO.setPositionId(6);
            betVO.setBetValue(betPositionValue_6);
            list.add(betVO);
        }
        if (betPositionValue_7 != 0) {
            BetVO betVO = new BetVO();
            betVO.setPositionId(7);
            betVO.setBetValue(betPositionValue_7);
            list.add(betVO);
        }
        if (betPositionValue_8 != 0) {
            BetVO betVO = new BetVO();
            betVO.setPositionId(8);
            betVO.setBetValue(betPositionValue_8);
            list.add(betVO);
        }
        if (betPositionValue_9 != 0) {
            BetVO betVO = new BetVO();
            betVO.setPositionId(9);
            betVO.setBetValue(betPositionValue_9);
            list.add(betVO);
        }
        if (list.size() <= 0) {
            ToastUtil.showToast(this, "请选择下注");
            return;
        }
        UserBetVO userBetVO = new UserBetVO();
        userBetVO.setRoomId(roomId);
        userBetVO.setRoundId(roomRoundModel.getRoomRoundId());
        userBetVO.setBetDetail(list);

        String json = GsonUtil.getInstance().toJson(userBetVO);
        RequestBody rsbody = RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), json);

        Network.getNetworkInstance().getLiveApi()
                .betBatch(token, rsbody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResultReturn<String>>() {
                    @Override
                    public void accept(ResultReturn<String> ret) throws Exception {
                        if (ret != null && ret.getCode() == ResultReturn.ResultCode.RESULT_OK.getValue()) {
                            ToastUtil.showToast(BjlPlayerActivity.this, "下注成功");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        ToastUtil.showToast(BjlPlayerActivity.this, "失败");
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
                            roomRoundModel = ret.getData();
                            setStatus();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        ToastUtil.showToast(BjlPlayerActivity.this, "失败");

                    }
                });
    }

    public void setStatus(int value) {
        tv_serial_num.setText(getString(R.string.serial_number) + ": " + roundId);
        switch (value) {
            case 1://下注中
                countDownView.setCountdownTime(30);
                countDownView.startCountDown();
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
    public void onNotify(MQBean eventData) {
        super.onNotify(eventData);
        if (eventData != null && eventData.getMessage() != null) {
            String data = eventData.getMessage().toString();

            MQPlayerBean mqPlayerBean = GsonUtil.getInstance().toObject(data, MQPlayerBean.class);
            roundId = mqPlayerBean.getRoomRoundId();
            if (mqPlayerBean.getCardId() != 0) {
                showPokeInfo2(mqPlayerBean.getCardId(), mqPlayerBean.getPosition());
            }
            setStatus(mqPlayerBean.getStatus());
        }
    }

    @Override
    protected void onDestroy() {
        canSubscribeMqTopic(IConst.TOPIC + roomId);
        super.onDestroy();
    }
}

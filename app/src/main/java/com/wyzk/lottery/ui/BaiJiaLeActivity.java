package com.wyzk.lottery.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wyzk.lottery.R;
import com.wyzk.lottery.utils.BuildManager;
import com.wyzk.lottery.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class BaiJiaLeActivity extends LotteryBaseActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.title)
    View title;


    @Bind(R.id.tv_game_stuta)
    TextView tv_game_stuta;

    @Bind(R.id.iv_xj_third)
    ImageView iv_xj_third;

    @Bind(R.id.iv_xj_first_position)
    ImageView iv_xj_first_position;

    @Bind(R.id.iv_xj_second_position)
    ImageView iv_xj_second_position;


    @Bind(R.id.iv_zj_third)
    ImageView iv_zj_third;

    @Bind(R.id.iv_zj_first)
    ImageView iv_zj_first;

    @Bind(R.id.iv_zj_second)
    ImageView iv_zj_second;

    @Bind(R.id.tv_xj_dui)
    TextView tv_xj_dui;

    @Bind(R.id.tv_tie)
    TextView tv_tie;

    @Bind(R.id.tv_zj_dui)
    TextView tv_zj_dui;

    @Bind(R.id.tv_xian_bao_long)
    TextView tv_xian_bao_long;

    @Bind(R.id.tv_blank)
    TextView tv_blank;

    @Bind(R.id.tv_zhuang_bao_long)
    TextView tv_zhuang_bao_long;

    @Bind(R.id.tv_big)
    TextView tv_big;

    @Bind(R.id.tv_xian)
    TextView tv_xian;

    @Bind(R.id.tv_small)
    TextView tv_small;

    @Bind(R.id.tv_cancel)
    TextView tv_cancel;

    @Bind(R.id.tv_fapai)
    TextView tv_fapai;

    @Bind(R.id.tv_sure)
    TextView tv_sure;

    @Bind(R.id.yishi)
    Button yishi;

    @Bind(R.id.wushi)
    Button wushi;

    @Bind(R.id.yibai)
    Button yibai;

    @Bind(R.id.yik)
    Button yik;

    @Bind(R.id.yibaik)
    Button yibaik;

    @Bind(R.id.tv_bet_xd)
    TextView tv_bet_xd;

    @Bind(R.id.tv_bet_tie)
    TextView tv_bet_tie;

    @Bind(R.id.tv_bet_zd)
    TextView tv_bet_zd;

    @Bind(R.id.tv_bet_xbl)
    TextView tv_bet_xbl;

    @Bind(R.id.tv_bet_z)
    TextView tv_bet_z;

    @Bind(R.id.tv_bet_zbl)
    TextView tv_bet_zbl;

    @Bind(R.id.tv_bet_big)
    TextView tv_bet_big;

    @Bind(R.id.tv_bet_x)
    TextView tv_bet_x;

    @Bind(R.id.tv_bet_small)
    TextView tv_bet_small;


    private List<String> pokers = new ArrayList<>();


    //创建牌的方法
    public void newPoker() {
        //1.创建数组，用以存储扑克牌
        pokers.clear();

        String[] colors = {"HX", "HT", "MH", "FK"};
        String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13"};
        for (int i = 0; i < numbers.length; i++) {
            for (int j = 0; j < colors.length; j++) {
                pokers.add(colors[j] + numbers[i]);
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bjl_layout);
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

        select_default_Position();

    }


    @OnClick({R.id.tv_xj_dui, R.id.tv_tie, R.id.tv_zj_dui,
            R.id.tv_xian_bao_long, R.id.tv_blank, R.id.tv_zhuang_bao_long,
            R.id.tv_big, R.id.tv_xian, R.id.tv_small,
            R.id.yishi, R.id.wushi, R.id.yibai, R.id.yik, R.id.yibaik, R.id.tv_fapai,
            R.id.tv_cancel,R.id.tv_sure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_xj_dui:
                selectXjPosition();
                break;
            case R.id.tv_tie:
                selectTiePosition();
                break;
            case R.id.tv_zj_dui:
                selectZjPosition();
                break;
            case R.id.tv_xian_bao_long:
                select_xian_bao_long_Position();
                break;
            case R.id.tv_blank:
                select_blank_Position();
                break;
            case R.id.tv_zhuang_bao_long:
                select_zhuang_bao_long_Position();
                break;
            case R.id.tv_big:
                select_big_Position();
                break;
            case R.id.tv_xian:
                select_xian_Position();
                break;
            case R.id.tv_small:
                select_small_Position();
                break;
            case R.id.tv_sure:
                cancelBetValue();
                break;
            case R.id.tv_fapai:
                fapaiGame();
                break;
            case R.id.tv_cancel:
                cancelBetValue();
                break;
            case R.id.yishi:
                addBetValue(10);
                break;
            case R.id.wushi:
                addBetValue(50);
                break;
            case R.id.yibai:
                addBetValue(100);
                break;
            case R.id.yik:
                addBetValue(1000);
                break;
            case R.id.yibaik:
                addBetValue(10000);
                break;
        }
    }

    private void cancelBetValue() {
        tv_bet_xd.setText("");
        tv_bet_tie.setText("");
        tv_bet_zd.setText("");
        tv_bet_xbl.setText("");

        tv_bet_z.setText("");
        tv_bet_zbl.setText("");
        tv_bet_big.setText("");
        tv_bet_x.setText("");
        tv_bet_small.setText("");

        bet_position_1 = 0;
        bet_position_2 = 0;
        bet_position_3 = 0;
        bet_position_4 = 0;
        bet_position_5 = 0;
        bet_position_6 = 0;
        bet_position_7 = 0;
        bet_position_8 = 0;
        bet_position_9 = 0;

    }

    private void addBetValue(int value) {
        if (currentPosition == 1) {
            bet_position_1 += value;
            tv_bet_small.setText("小:" + bet_position_1);
        } else if (currentPosition == 2) {
            bet_position_2 += value;
            tv_bet_x.setText("闲对:" + bet_position_2);
        } else if (currentPosition == 3) {
            bet_position_3 += value;
            tv_bet_big.setText("大:" + bet_position_3);
        } else if (currentPosition == 4) {
            bet_position_4 += value;
            tv_bet_zbl.setText("庄宝龙:" + bet_position_4);
        } else if (currentPosition == 5) {
            bet_position_5 += value;
            tv_bet_z.setText("庄:" + bet_position_5);
        } else if (currentPosition == 6) {
            bet_position_6 += value;
            tv_bet_xbl.setText("闲宝龙:" + bet_position_6);
        } else if (currentPosition == 7) {
            bet_position_7 += value;
            tv_bet_zd.setText("庄对:" + bet_position_7);
        } else if (currentPosition == 8) {
            bet_position_8 += value;
            tv_bet_tie.setText("和:" + bet_position_8);
        } else if (currentPosition == 9) {
            bet_position_9 += value;
            tv_bet_xd.setText("闲对:" + bet_position_9);
        }
    }

    private int position_x_1 = 0;
    private int position_x_2 = 0;
    private int position_x_3 = 0;

    private int position_z_1 = 0;
    private int position_z_2 = 0;
    private int position_z_3 = 0;

    private int bet_position_1 = 0;
    private int bet_position_2 = 0;
    private int bet_position_3 = 0;

    private int bet_position_4 = 0;
    private int bet_position_5 = 0;
    private int bet_position_6 = 0;

    private int bet_position_7 = 0;
    private int bet_position_8 = 0;
    private int bet_position_9 = 0;

    private int currentPosition = 0;

    private void fapaiGame() {
        if (pokers.size() < 4) {
            newPoker();
            ToastUtil.showToast(this, "洗牌中...");
        }

        int i = (int) (Math.random() * (pokers.size()));

        ImageView view = null;

        if (position_x_1 == 0) {
            view = iv_xj_first_position;
            position_x_1 = 1;
        } else if (position_z_1 == 0) {
            view = iv_zj_first;
            position_z_1 = 1;
        } else if (position_x_2 == 0) {
            view = iv_xj_second_position;
            position_x_2 = 1;
        } else if (position_z_2 == 0) {
            view = iv_zj_second;
            position_z_2 = 1;
        } else if (position_x_3 == 0) {
            view = iv_xj_third;
            position_x_3 = 1;
        } else if (position_z_3 == 0) {
            view = iv_zj_third;
            position_z_3 = 1;
        } else {
            position_x_1 = 0;
            position_x_2 = 0;
            position_x_3 = 0;
            position_z_1 = 0;
            position_z_2 = 0;
            position_z_3 = 0;
            return;
        }
        String str = pokers.get(i);
        pokers.remove(i);

        System.out.println("random i == " + i + " str========== " + str + " size== " + pokers.size());

        if (str.equals("HX1")) {
            view.setBackgroundResource(R.mipmap.hx_a);
        } else if (str.equals("HX2")) {
            view.setBackgroundResource(R.mipmap.hx_2);
        } else if (str.equals("HX3")) {
            view.setBackgroundResource(R.mipmap.hx_3);
        } else if (str.equals("HX4")) {
            view.setBackgroundResource(R.mipmap.hx_4);
        } else if (str.equals("HX5")) {
            view.setBackgroundResource(R.mipmap.hx_5);
        } else if (str.equals("HX6")) {
            view.setBackgroundResource(R.mipmap.hx_6);
        } else if (str.equals("HX7")) {
            view.setBackgroundResource(R.mipmap.hx_7);
        } else if (str.equals("HX8")) {
            view.setBackgroundResource(R.mipmap.hx_8);
        } else if (str.equals("HX9")) {
            view.setBackgroundResource(R.mipmap.hx_9);
        } else if (str.equals("HX10")) {
            view.setBackgroundResource(R.mipmap.hx_10);
        } else if (str.equals("HX11")) {
            view.setBackgroundResource(R.mipmap.hx_j);
        } else if (str.equals("HX12")) {
            view.setBackgroundResource(R.mipmap.hx_q);
        } else if (str.equals("HX13")) {
            view.setBackgroundResource(R.mipmap.hx_k);
        } else if (str.equals("HT1")) {
            view.setBackgroundResource(R.mipmap.ht_a);
        } else if (str.equals("HT2")) {
            view.setBackgroundResource(R.mipmap.ht_2);
        } else if (str.equals("HT3")) {
            view.setBackgroundResource(R.mipmap.ht_3);
        } else if (str.equals("HT4")) {
            view.setBackgroundResource(R.mipmap.ht_4);
        } else if (str.equals("HT5")) {
            view.setBackgroundResource(R.mipmap.ht_5);
        } else if (str.equals("HT6")) {
            view.setBackgroundResource(R.mipmap.ht_6);
        } else if (str.equals("HT7")) {
            view.setBackgroundResource(R.mipmap.ht_7);
        } else if (str.equals("HT8")) {
            view.setBackgroundResource(R.mipmap.ht_8);
        } else if (str.equals("HT9")) {
            view.setBackgroundResource(R.mipmap.ht_9);
        } else if (str.equals("HT10")) {
            view.setBackgroundResource(R.mipmap.ht_10);
        } else if (str.equals("HT11")) {
            view.setBackgroundResource(R.mipmap.ht_j);
        } else if (str.equals("HT12")) {
            view.setBackgroundResource(R.mipmap.ht_q);
        } else if (str.equals("HT13")) {
            view.setBackgroundResource(R.mipmap.ht_k);
        } else if (str.equals("MH1")) {
            view.setBackgroundResource(R.mipmap.ht_a);
        } else if (str.equals("MH2")) {
            view.setBackgroundResource(R.mipmap.mh_2);
        } else if (str.equals("MH3")) {
            view.setBackgroundResource(R.mipmap.mh_3);
        } else if (str.equals("MH4")) {
            view.setBackgroundResource(R.mipmap.mh_4);
        } else if (str.equals("MH5")) {
            view.setBackgroundResource(R.mipmap.mh_5);
        } else if (str.equals("MH6")) {
            view.setBackgroundResource(R.mipmap.mh_6);
        } else if (str.equals("MH7")) {
            view.setBackgroundResource(R.mipmap.mh_7);
        } else if (str.equals("MH8")) {
            view.setBackgroundResource(R.mipmap.mh_8);
        } else if (str.equals("MH9")) {
            view.setBackgroundResource(R.mipmap.mh_9);
        } else if (str.equals("MH10")) {
            view.setBackgroundResource(R.mipmap.mh_10);
        } else if (str.equals("MH11")) {
            view.setBackgroundResource(R.mipmap.mh_j);
        } else if (str.equals("MH12")) {
            view.setBackgroundResource(R.mipmap.mh_q);
        } else if (str.equals("MH13")) {
            view.setBackgroundResource(R.mipmap.mh_k);
        } else if (str.equals("FK1")) {
            view.setBackgroundResource(R.mipmap.fk_a);
        } else if (str.equals("FK2")) {
            view.setBackgroundResource(R.mipmap.fk_2);
        } else if (str.equals("FK3")) {
            view.setBackgroundResource(R.mipmap.fk_3);
        } else if (str.equals("FK4")) {
            view.setBackgroundResource(R.mipmap.fk_4);
        } else if (str.equals("FK5")) {
            view.setBackgroundResource(R.mipmap.fk_5);
        } else if (str.equals("FK6")) {
            view.setBackgroundResource(R.mipmap.fk_6);
        } else if (str.equals("FK7")) {
            view.setBackgroundResource(R.mipmap.fk_7);
        } else if (str.equals("FK8")) {
            view.setBackgroundResource(R.mipmap.fk_8);
        } else if (str.equals("FK9")) {
            view.setBackgroundResource(R.mipmap.fk_9);
        } else if (str.equals("FK10")) {
            view.setBackgroundResource(R.mipmap.fk_10);
        } else if (str.equals("FK11")) {
            view.setBackgroundResource(R.mipmap.fk_j);
        } else if (str.equals("FK12")) {
            view.setBackgroundResource(R.mipmap.fk_q);
        } else if (str.equals("FK13")) {
            view.setBackgroundResource(R.mipmap.fk_k);
        }
    }

    private void select_default_Position() {
        tv_xj_dui.setBackgroundResource(R.drawable.rect);
        tv_tie.setBackgroundResource(R.drawable.rect);
        tv_zj_dui.setBackgroundResource(R.drawable.rect);
        tv_xian_bao_long.setBackgroundResource(R.drawable.rect);
        tv_blank.setBackgroundResource(R.drawable.rect);
        tv_zhuang_bao_long.setBackgroundResource(R.drawable.rect);
        tv_big.setBackgroundResource(R.drawable.rect);
        tv_xian.setBackgroundResource(R.drawable.rect);
        tv_small.setBackgroundResource(R.drawable.rect);
    }

    private void select_small_Position() {
        tv_xj_dui.setBackgroundResource(R.drawable.rect);
        tv_tie.setBackgroundResource(R.drawable.rect);
        tv_zj_dui.setBackgroundResource(R.drawable.rect);
        tv_xian_bao_long.setBackgroundResource(R.drawable.rect);
        tv_blank.setBackgroundResource(R.drawable.rect);
        tv_zhuang_bao_long.setBackgroundResource(R.drawable.rect);
        tv_big.setBackgroundResource(R.drawable.rect);
        tv_xian.setBackgroundResource(R.drawable.rect);
        tv_small.setBackgroundResource(R.drawable.select_rect);
        currentPosition = 1;
    }

    private void select_xian_Position() {
        tv_xj_dui.setBackgroundResource(R.drawable.rect);
        tv_tie.setBackgroundResource(R.drawable.rect);
        tv_zj_dui.setBackgroundResource(R.drawable.rect);
        tv_xian_bao_long.setBackgroundResource(R.drawable.rect);
        tv_blank.setBackgroundResource(R.drawable.rect);
        tv_zhuang_bao_long.setBackgroundResource(R.drawable.rect);
        tv_big.setBackgroundResource(R.drawable.rect);
        tv_xian.setBackgroundResource(R.drawable.select_rect);
        tv_small.setBackgroundResource(R.drawable.rect);
        currentPosition = 2;
    }

    private void select_big_Position() {
        tv_xj_dui.setBackgroundResource(R.drawable.rect);
        tv_tie.setBackgroundResource(R.drawable.rect);
        tv_zj_dui.setBackgroundResource(R.drawable.rect);
        tv_xian_bao_long.setBackgroundResource(R.drawable.rect);
        tv_blank.setBackgroundResource(R.drawable.rect);
        tv_zhuang_bao_long.setBackgroundResource(R.drawable.rect);
        tv_big.setBackgroundResource(R.drawable.select_rect);
        tv_xian.setBackgroundResource(R.drawable.rect);
        tv_small.setBackgroundResource(R.drawable.rect);
        currentPosition = 3;
    }

    private void select_zhuang_bao_long_Position() {
        tv_xj_dui.setBackgroundResource(R.drawable.rect);
        tv_tie.setBackgroundResource(R.drawable.rect);
        tv_zj_dui.setBackgroundResource(R.drawable.rect);
        tv_xian_bao_long.setBackgroundResource(R.drawable.rect);
        tv_blank.setBackgroundResource(R.drawable.rect);
        tv_zhuang_bao_long.setBackgroundResource(R.drawable.select_rect);
        tv_big.setBackgroundResource(R.drawable.rect);
        tv_xian.setBackgroundResource(R.drawable.rect);
        tv_small.setBackgroundResource(R.drawable.rect);
        currentPosition = 4;
    }

    private void select_blank_Position() {
        tv_xj_dui.setBackgroundResource(R.drawable.rect);
        tv_tie.setBackgroundResource(R.drawable.rect);
        tv_zj_dui.setBackgroundResource(R.drawable.rect);
        tv_xian_bao_long.setBackgroundResource(R.drawable.rect);
        tv_blank.setBackgroundResource(R.drawable.select_rect);
        tv_zhuang_bao_long.setBackgroundResource(R.drawable.rect);
        tv_big.setBackgroundResource(R.drawable.rect);
        tv_xian.setBackgroundResource(R.drawable.rect);
        tv_small.setBackgroundResource(R.drawable.rect);
        currentPosition = 5;
    }


    private void select_xian_bao_long_Position() {
        tv_xj_dui.setBackgroundResource(R.drawable.rect);
        tv_tie.setBackgroundResource(R.drawable.rect);
        tv_zj_dui.setBackgroundResource(R.drawable.rect);
        tv_xian_bao_long.setBackgroundResource(R.drawable.select_rect);
        tv_blank.setBackgroundResource(R.drawable.rect);
        tv_zhuang_bao_long.setBackgroundResource(R.drawable.rect);
        tv_big.setBackgroundResource(R.drawable.rect);
        tv_xian.setBackgroundResource(R.drawable.rect);
        tv_small.setBackgroundResource(R.drawable.rect);
        currentPosition = 6;
    }

    private void selectZjPosition() {
        tv_xj_dui.setBackgroundResource(R.drawable.rect);
        tv_tie.setBackgroundResource(R.drawable.rect);
        tv_zj_dui.setBackgroundResource(R.drawable.select_rect);
        tv_xian_bao_long.setBackgroundResource(R.drawable.rect);
        tv_blank.setBackgroundResource(R.drawable.rect);
        tv_zhuang_bao_long.setBackgroundResource(R.drawable.rect);
        tv_big.setBackgroundResource(R.drawable.rect);
        tv_xian.setBackgroundResource(R.drawable.rect);
        tv_small.setBackgroundResource(R.drawable.rect);
        currentPosition = 7;
    }

    private void selectTiePosition() {
        tv_xj_dui.setBackgroundResource(R.drawable.rect);
        tv_tie.setBackgroundResource(R.drawable.select_rect);
        tv_zj_dui.setBackgroundResource(R.drawable.rect);
        tv_xian_bao_long.setBackgroundResource(R.drawable.rect);
        tv_blank.setBackgroundResource(R.drawable.rect);
        tv_zhuang_bao_long.setBackgroundResource(R.drawable.rect);
        tv_big.setBackgroundResource(R.drawable.rect);
        tv_xian.setBackgroundResource(R.drawable.rect);
        tv_small.setBackgroundResource(R.drawable.rect);
        currentPosition = 8;
    }

    private void selectXjPosition() {
        tv_xj_dui.setBackgroundResource(R.drawable.select_rect);
        tv_tie.setBackgroundResource(R.drawable.rect);
        tv_zj_dui.setBackgroundResource(R.drawable.rect);
        tv_xian_bao_long.setBackgroundResource(R.drawable.rect);
        tv_blank.setBackgroundResource(R.drawable.rect);
        tv_zhuang_bao_long.setBackgroundResource(R.drawable.rect);
        tv_big.setBackgroundResource(R.drawable.rect);
        tv_xian.setBackgroundResource(R.drawable.rect);
        tv_small.setBackgroundResource(R.drawable.rect);
        currentPosition = 9;
    }


}

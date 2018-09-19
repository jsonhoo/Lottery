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
import android.widget.RadioGroup;
import android.widget.TextView;

import com.wyzk.lottery.R;
import com.wyzk.lottery.adapter.LayoutAdapter;
import com.wyzk.lottery.utils.BuildManager;

import org.lucasr.twowayview.widget.DividerItemDecoration;
import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


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


    @Bind(R.id.sex)
    RadioGroup sex;

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sure:
                startFlick(l_bet3);
                break;
            case R.id.tv_cancel:
                stopFlick(l_bet3);
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
        } else if (v == l_bet2) {
            l_bet2.setBackgroundColor(getResources().getColor(R.color.fuchsia));
            currentSelectPosition = 2;
        } else if (v == l_bet3) {
            l_bet3.setBackgroundColor(getResources().getColor(R.color.fuchsia));
            currentSelectPosition = 3;
        } else if (v == l_bet4) {
            l_bet4.setBackgroundColor(getResources().getColor(R.color.fuchsia));
            currentSelectPosition = 4;
        } else if (v == l_bet5) {
            l_bet5.setBackgroundColor(getResources().getColor(R.color.fuchsia));
            currentSelectPosition = 5;
        } else if (v == l_bet6) {
            l_bet6.setBackgroundColor(getResources().getColor(R.color.fuchsia));
            currentSelectPosition = 6;
        } else if (v == l_bet7) {
            l_bet7.setBackgroundColor(getResources().getColor(R.color.fuchsia));
            currentSelectPosition = 7;
        } else if (v == l_bet8) {
            l_bet8.setBackgroundColor(getResources().getColor(R.color.fuchsia));
            currentSelectPosition = 8;
        } else if (v == l_bet9) {
            l_bet9.setBackgroundColor(getResources().getColor(R.color.fuchsia));
            currentSelectPosition = 9;
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
}

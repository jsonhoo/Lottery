package com.wyzk.lottery.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.wyzk.lottery.R;
import com.wyzk.lottery.adapter.HomeAdapter;
import com.wyzk.lottery.constant.IConst;
import com.wyzk.lottery.model.ExtraBean;
import com.wyzk.lottery.model.ResultReturn;
import com.wyzk.lottery.model.RoomModel;
import com.wyzk.lottery.model.UserInfoModel;
import com.wyzk.lottery.network.Network;
import com.wyzk.lottery.utils.ACache;
import com.wyzk.lottery.utils.BuildManager;
import com.wyzk.lottery.video.activity.HouseOwnerActivity;
import com.wyzk.lottery.video.activity.PlayerActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends LotteryBaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    @Bind(R.id.right)
    CoordinatorLayout right;
    @Bind(R.id.nav_view)
    NavigationView left;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer_layout;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    TextView tvName;
    HomeAdapter adapter;
    private boolean isDrawer = false;
    private UserInfoModel userInfoModel;
    private List<RoomModel.RowModel> mDataList = new ArrayList<>();

    private long mPressedTime = 0;

    private int pageIndex = 1;
    private int pageRows = 10;


    Consumer<ResultReturn<UserInfoModel>> userObserver = new Consumer<ResultReturn<UserInfoModel>>() {

        @Override
        public void accept(ResultReturn<UserInfoModel> result) throws Exception {
            if (result != null && result.getCode() == ResultReturn.ResultCode.RESULT_OK.getValue() && result.getData() != null) {
                userInfoModel = result.getData();
                tvName.setText(userInfoModel.getUsername());
            } else {
                logout();
            }
        }
    };

    Consumer<RoomModel> roomObserver = new Consumer<RoomModel>() {
        @Override
        public void accept(RoomModel result) throws Exception {
            if (result != null) {
                List<RoomModel.RowModel> rowModels = result.getRows();
                mDataList.clear();
                if (rowModels != null) {
                    mDataList.addAll(rowModels);
                }
                adapter.notifyDataSetChanged();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer_layout.setDrawerListener(toggle);
        toggle.syncState();

        BuildManager.setStatusTrans2(this);

        startMqtt();

        left.setNavigationItemSelectedListener(this);
        right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (isDrawer) {
                    return left.dispatchTouchEvent(motionEvent);
                } else {
                    return false;
                }
            }
        });
        drawer_layout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                isDrawer = true;
                //获取屏幕的宽高
                WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                Display display = manager.getDefaultDisplay();
                //设置右面的布局位置  根据左面菜单的right作为右面布局的left   左面的right+屏幕的宽度（或者right的宽度这里是相等的）为右面布局的right
                right.layout(left.getRight(), 0, left.getRight() + display.getWidth(), display.getHeight());
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                isDrawer = false;
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });

        LinearLayout rootView = (LinearLayout) left.getHeaderView(0);
        LinearLayout linearLayout = (LinearLayout) rootView.getChildAt(0);
        tvName = (TextView) linearLayout.getChildAt(1);

        RefreshLayout refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if (token != null) {
                    pageIndex = 1;
                    getRoomList(token, pageIndex, pageRows);
                    getUserInfo(token);
                }
                refreshlayout.finishRefresh(2000);
            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                if (token != null) {
                    getRoomList(token, ++pageIndex, pageRows);
                    getUserInfo(token);
                }
                refreshlayout.finishLoadmore(2000);
            }
        });
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.HORIZONTAL));
        adapter = new HomeAdapter(this, mDataList, R.layout.item_room);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new HomeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                toVideoActivity(position);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        getRoomList(token, pageIndex, pageRows);
        getUserInfo(token);
    }

    private void getRoomList(String token, int pageIndex, int pageRows) {
        subscription = Network.getNetworkInstance().getLiveApi()
                .getRoomList(token, pageIndex, pageRows)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(roomObserver);
    }

    private void getUserInfo(String token) {
        subscription2 = Network.getNetworkInstance().getUserApi()
                .getUserInfo(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userObserver);
    }

    public void toVideoActivity(int position) {
        Intent intent;

        RoomModel.RowModel rowModel = mDataList.get(position);
        String userId = ACache.get(this).getAsString(IConst.USER_ID);
        if (userId != null && userId.equals(rowModel.getUserId())) {
            //房主
            intent = new Intent(this, HouseOwnerActivity.class);
        } else {
            //玩家
            intent = new Intent(this, PlayerActivity.class);
        }

        intent.putExtra(IConst.ROW_INFO, rowModel);
        startActivity(intent);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.my_integral) {
            toActivity(MyIntegralActivity.class);
        } else if (id == R.id.recharge) {
            toActivity(RechargeWithdrawalActivity.class);
        } else if (id == R.id.personal_information) {
            ExtraBean extraBean = new ExtraBean();
            extraBean.setData(userInfoModel);
            toActivityWithExtra(extraBean, PersonalInformationActivity.class);
        } else if (id == R.id.my_message) {
            toActivity(MyMessageActivity.class);
        } else if (id == R.id.service) {
            toCall();
        } else if (id == R.id.about) {
            toActivity(AboutActivity.class);
        }
        drawer_layout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START);
        } else {
            long nowTime = System.currentTimeMillis();
            if (nowTime - mPressedTime > 2000) {
                Toast.makeText(this, getString(R.string.press_again), Toast.LENGTH_SHORT).show();
                mPressedTime = nowTime;
            } else {
                super.onBackPressed();
            }
        }
    }

    public void onExit(View view) {
        ACache.get(this).clear();
        startActivity(new Intent(this, SplashActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMqtt();
    }
}

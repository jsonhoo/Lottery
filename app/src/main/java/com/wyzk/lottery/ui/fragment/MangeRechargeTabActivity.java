package com.wyzk.lottery.ui.fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.wyzk.lottery.R;
import com.wyzk.lottery.ui.LotteryBaseActivity;
import com.wyzk.lottery.utils.BuildManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MangeRechargeTabActivity extends LotteryBaseActivity {

    @Bind(R.id.tabLayout)
    TabLayout tabLayout;

    @Bind(R.id.viewPager)
    ViewPager viewPager;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.title)
    View title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_2_tab);
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

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new ChargeManageFragment());
        fragments.add(new ExchangeManageFragment());

        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        myPagerAdapter.setFragments(fragments);
        // 给ViewPager设置适配器
        viewPager.setAdapter(myPagerAdapter);

        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());

        tabLayout.setupWithViewPager(viewPager);

        TabLayout.Tab tab0 = tabLayout.getTabAt(0);
        tab0.setCustomView(R.layout.tab_item);

        TabLayout.Tab tab1 = tabLayout.getTabAt(1);
        tab1.setCustomView(R.layout.tab_item);

        TextView textView0 = (TextView) tab0.getCustomView().findViewById(R.id.tab_text);
        textView0.setText("充值记录");

        TextView textView1 = (TextView) tab1.getCustomView().findViewById(R.id.tab_text);
        textView1.setText("提现记录");
    }

    class MyPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragmentList;

        public void setFragments(ArrayList<Fragment> fragments) {
            mFragmentList = fragments;
        }

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = mFragmentList.get(position);

            return fragment;
        }

        @Override
        public int getCount() {

            return mFragmentList.size();
        }
    }

}
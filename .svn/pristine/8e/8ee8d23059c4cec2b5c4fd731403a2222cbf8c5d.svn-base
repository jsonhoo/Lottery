package com.wyzk.lottery.view;

import android.content.Context;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.wyzk.lottery.R;
import com.wyzk.lottery.adapter.TrendAdapter;
import com.wyzk.lottery.model.RoundInfoModel;

import java.util.List;

/**
 * BottomPushPopupWindow的简单例子
 *
 * @author y
 */
public class TrendPopupWindow extends BottomPushPopupWindow<List<RoundInfoModel>> {

    public TrendPopupWindow(Context context, List<RoundInfoModel> dataList) {
        super(context, dataList);
    }

    @Override
    protected View generateCustomView(List<RoundInfoModel> dataList) {
        View root = View.inflate(context, R.layout.popup_trend, null);
        RecyclerView recyclerview = (RecyclerView) root.findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(context));
        recyclerview.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL));
        TrendAdapter adapter = new TrendAdapter(context, dataList, R.layout.item_trend);
        recyclerview.setAdapter(adapter);
        return root;
    }
}
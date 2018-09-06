package com.wyzk.lottery.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wyzk.lottery.R;
import com.wyzk.lottery.model.ExchangeModel;
import com.wyzk.lottery.utils.UTCDateUtil;

import java.util.List;

public class WithdrawalRecordAdapter extends RecyclerView.Adapter<WithdrawalRecordAdapter.MyViewHolder> implements View.OnClickListener {

    private Context context;
    private List<ExchangeModel.ExchangeItem> mDataList;
    private int layoutId;

    public WithdrawalRecordAdapter(Context context, List data, int layoutId) {
        this.context = context;
        this.mDataList = data;
        this.layoutId = layoutId;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.itemView.setTag(position);
        ExchangeModel.ExchangeItem model = mDataList.get(position);

        switch (model.getUserPayAccountType()) {
            case "1":
                holder.tv_charge_type.setText(R.string.bank);
                break;
            case "2":
                holder.tv_charge_type.setText(R.string.alipay);
                break;
            case "3":
                holder.tv_charge_type.setText(R.string.wechat);
                break;
        }


        switch (model.getExchangeStatus()) {
            case "0":
                holder.tv_charge_status.setText(R.string.status_1);
                break;
            case "1":
                holder.tv_charge_status.setText(R.string.status_2);
                break;
            case "2":
                holder.tv_charge_status.setText(R.string.status_3);
                break;
            case "3":
                holder.tv_charge_status.setText(R.string.status_4);
                break;
        }
        holder.tv_charge_value.setText("" + model.getExchangeValue());

        holder.tv_time.setText(UTCDateUtil.format(model.getCreateTime(), UTCDateUtil.FMT_DATETIME));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_charge_type;
        TextView tv_time;
        TextView tv_charge_value;
        TextView tv_charge_status;

        public MyViewHolder(View view) {
            super(view);
            tv_charge_type = (TextView) view.findViewById(R.id.tv_charge_type);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            tv_charge_value = (TextView) view.findViewById(R.id.tv_charge_value);
            tv_charge_status = (TextView) view.findViewById(R.id.tv_charge_status);
        }
    }

    private WithdrawalRecordAdapter.OnItemClickListener mOnItemClickListener = null;

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    public void setOnItemClickListener(WithdrawalRecordAdapter.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}

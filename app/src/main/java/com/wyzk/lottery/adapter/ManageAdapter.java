package com.wyzk.lottery.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wyzk.lottery.R;
import com.wyzk.lottery.model.RechargeManageModel;
import com.wyzk.lottery.utils.UTCDateUtil;

import java.util.List;

public class ManageAdapter extends RecyclerView.Adapter<ManageAdapter.MyViewHolder> implements View.OnClickListener{

    private Context context;
    private List<RechargeManageModel.RechargeItem> mDataList;
    private int layoutId;


    public ManageAdapter(Context context, List data, int layoutId) {
        this.context = context;
        this.mDataList = data;
        this.layoutId = layoutId;
    }

    private OnItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    @Override
    public ManageAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        view.setOnClickListener(this);

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.itemView.setTag(position);
        RechargeManageModel.RechargeItem rechargeItem = mDataList.get(position);

        if (rechargeItem.getChargeType() == 1) {
            holder.tv_charge_type.setText(R.string.bank);
        } else if (rechargeItem.getChargeType() == 2) {
            holder.tv_charge_type.setText(R.string.alipay);
        } else if (rechargeItem.getChargeType() == 3) {
            holder.tv_charge_type.setText(R.string.wechat);
        }
        if (rechargeItem.getChargeStatus() == 0) {
            holder.tv_charge_status.setText(R.string.status_5);
        } else if (rechargeItem.getChargeStatus() == 1) {
            holder.tv_charge_status.setText(R.string.status_6);
        } else if (rechargeItem.getChargeStatus() == 2) {
            holder.tv_charge_status.setText(R.string.status_3);
        } else if (rechargeItem.getChargeStatus() == 3) {
            holder.tv_charge_status.setText(R.string.status_4);
        }

        holder.tv_charge_value.setText("" + rechargeItem.getChargeValue());
        holder.tv_time.setText(UTCDateUtil.format(rechargeItem.getCreateTime(), UTCDateUtil.FMT_DATETIME));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public void onClick(View v) {

        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_charge_type;
        TextView tv_charge_status;
        TextView tv_charge_value;
        TextView tv_time;

        public MyViewHolder(View view) {
            super(view);
            tv_charge_type = (TextView) view.findViewById(R.id.tv_charge_type);
            tv_charge_status = (TextView) view.findViewById(R.id.tv_charge_status);
            tv_charge_value = (TextView) view.findViewById(R.id.tv_charge_value);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
        }
    }
}

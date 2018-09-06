package com.wyzk.lottery.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wyzk.lottery.R;
import com.wyzk.lottery.model.AccountBean;

import java.util.List;


public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.MyViewHolder> implements View.OnClickListener{
    private Context context;
    private List<AccountBean> dataList;
    private int layoutId;

    public AccountAdapter(Context context, List<AccountBean> data, int layoutId) {
        this.context = context;
        this.dataList = data;
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
        //将position保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(position);
        AccountBean accountBean = dataList.get(position);
        holder.tv_bank_name.setText(accountBean.getUserPayAccountBank());
        holder.tv_card_user.setText(accountBean.getUserPayAccountName());
        holder.tv_card_num.setText(accountBean.getUserPayAccount());
        if (onClickItemPosition == position) {
            holder.iv_selected.setVisibility(View.VISIBLE);
        } else {
            holder.iv_selected.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_bank_name;
        TextView tv_card_user;
        TextView tv_card_num;
        ImageView iv_selected;

        public MyViewHolder(View view) {
            super(view);
            tv_bank_name = (TextView) view.findViewById(R.id.tv_bank_name);
            tv_card_user = (TextView) view.findViewById(R.id.tv_card_user);
            tv_card_num = (TextView) view.findViewById(R.id.tv_card_num);
            iv_selected = (ImageView) view.findViewById(R.id.iv_selected);
        }
    }

    private int onClickItemPosition = 0;

    public void setOnClickItemPosition(int onClickItemPosition) {
        this.onClickItemPosition = onClickItemPosition;
        notifyDataSetChanged();
    }

    private AccountAdapter.OnItemClickListener mOnItemClickListener = null;

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    public void setOnItemClickListener(AccountAdapter.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}

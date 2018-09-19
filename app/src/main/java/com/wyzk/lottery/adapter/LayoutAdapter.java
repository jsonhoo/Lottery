package com.wyzk.lottery.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wyzk.lottery.R;

import java.util.List;

public class LayoutAdapter extends RecyclerView.Adapter<LayoutAdapter.SimpleViewHolder> {

    private final Context mContext;
    private List<Integer> mItems;
    private int mCurrentItemId = 0;
    private int mLayoutId;

    public LayoutAdapter(Context context, List<Integer> mItems,int mLayoutId) {
        mContext = context;
        this.mItems = mItems;
        this.mLayoutId = mLayoutId;
    }

    public void addItem(int position) {
        final int id = mCurrentItemId++;
        mItems.add(position, id);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final View view = LayoutInflater.from(mContext).inflate(mLayoutId, parent, false);

        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {

        holder.iv_q.setBackgroundResource(R.mipmap.red_b_q);

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {

        public final ImageView iv_q;

        public SimpleViewHolder(View view) {
            super(view);
            iv_q = (ImageView) view.findViewById(R.id.iv_q);
        }
    }
}

package com.wangsu.wsrtc.faceunity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangsu.wsrtc.faceunity.R;


public class FilterAdapter extends RecyclerView.Adapter {

    public static final int[] FILTER_ITEM_RES_ARRAY = {
            R.mipmap.nature, R.mipmap.delta, R.mipmap.electric, R.mipmap.slowlived, R.mipmap.tokyo, R.mipmap.warm
    };
    public final static String[] FILTERS_NAME = {"nature", "delta", "electric", "slowlived", "tokyo", "warm"};


    private View.OnClickListener mOnClickFilterListener;
    private int mSelectedPosition = 0;
    Context mContext;

    public FilterAdapter( Context context) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_item, null);
        return new FilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final FilterViewHolder viewHolder = (FilterViewHolder) holder;
        viewHolder.imageView.setImageResource(FILTER_ITEM_RES_ARRAY[position]);
        viewHolder.textView.setText(FILTERS_NAME[position]);
        holder.itemView.setSelected(mSelectedPosition == position);
        if(mOnClickFilterListener != null) {
            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(mOnClickFilterListener);

            holder.itemView.setSelected(mSelectedPosition == position);
        }
    }

    public void setClickFilterListener(View.OnClickListener listener) {
        mOnClickFilterListener = listener;
    }

    @Override
    public int getItemCount() {
        return FILTER_ITEM_RES_ARRAY.length;
    }

    static class FilterViewHolder extends RecyclerView.ViewHolder {

        View view;
        ImageView imageView;
        TextView textView;

        public FilterViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.filter_image);
            textView = (TextView) itemView.findViewById(R.id.filter_text);
        }
    }

    public void setSelectedPosition(int position){
        mSelectedPosition = position;
    }
}

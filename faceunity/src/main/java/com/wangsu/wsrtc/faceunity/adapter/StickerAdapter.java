package com.wangsu.wsrtc.faceunity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wangsu.wsrtc.faceunity.R;


public class StickerAdapter extends RecyclerView.Adapter {

    public static final int[] EFFECT_ITEM_RES_ARRAY = {
            R.mipmap.ic_delete_all, R.mipmap.yuguan, R.mipmap.lixiaolong, R.mipmap.matianyu, R.mipmap.yazui,
            R.mipmap.mood, R.mipmap.item0204,
    };
    public static final String[] EFFECT_ITEM_FILE_NAME = {"none", "yuguan.mp3", "lixiaolong.bundle",
            "mask_matianyu.bundle", "yazui.mp3", "Mood.mp3", "item0204.mp3"};

    private View.OnClickListener mOnClickStickerListener;
    private int mSelectedPosition = 0;
    Context mContext;

    public StickerAdapter( Context context) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sticker_item, null);
        return new FilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final FilterViewHolder viewHolder = (FilterViewHolder) holder;
        viewHolder.imageView.setImageResource(EFFECT_ITEM_RES_ARRAY[position]);
        holder.itemView.setSelected(mSelectedPosition == position);
        if (mOnClickStickerListener != null) {
            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(mOnClickStickerListener);

            holder.itemView.setSelected(mSelectedPosition == position);
        }
    }

    public void setClickStickerListener(View.OnClickListener listener) {
        mOnClickStickerListener = listener;
    }

    @Override
    public int getItemCount() {
        return EFFECT_ITEM_FILE_NAME.length;
    }

    static class FilterViewHolder extends RecyclerView.ViewHolder {

        View view;
        ImageView imageView;

        public FilterViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.iv_sticker);
        }
    }

    public void setSelectedPosition(int position) {
        mSelectedPosition = position;
    }
}

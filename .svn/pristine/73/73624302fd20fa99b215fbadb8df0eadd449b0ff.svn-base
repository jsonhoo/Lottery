package com.wyzk.lottery.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wyzk.lottery.R;
import com.wyzk.lottery.model.RoomModel;

import java.util.List;


public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> implements View.OnClickListener {
    private Context context;
    private List<RoomModel.RowModel> dataList;
    private int layoutId;
    private OnItemClickListener mOnItemClickListener = null;

    public HomeAdapter(Context context, List data, int layoutId) {
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
        RoomModel.RowModel rowModel = dataList.get(position);
        holder.tvRoomName.setText("房间名称： " + rowModel.getRoomName());
        holder.tvGameName.setText("游戏名称： " + rowModel.getGameName());
        holder.tvRoomPeo.setText("在线人数： " + rowModel.getRoomOnline());
        switch (rowModel.getRoundState()) {
            case 0:
                holder.tvRoomStatus.setText(context.getString(R.string.status0));
                break;
            case 1:
                holder.tvRoomStatus.setText(context.getString(R.string.status1));
                break;
            case 2:
                holder.tvRoomStatus.setText(context.getString(R.string.status2));
                break;
            case 3:
                holder.tvRoomStatus.setText(context.getString(R.string.status3));
                break;
            case 4:
                holder.tvRoomStatus.setText(context.getString(R.string.status4));
                break;
            case 5:
                holder.tvRoomStatus.setText(context.getString(R.string.status5));
                break;
        }

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvRoomName;
        TextView tvRoomPeo;
        TextView tvRoomStatus;
        TextView tvGameName;

        public MyViewHolder(View view) {
            super(view);
            tvRoomName = (TextView) view.findViewById(R.id.room_name);
            tvRoomPeo = (TextView) view.findViewById(R.id.room_peo);
            tvRoomStatus = (TextView) view.findViewById(R.id.room_status);
            tvGameName = (TextView) view.findViewById(R.id.game_name);
        }
    }
}

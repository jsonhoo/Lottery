package com.wyzk.lottery.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wyzk.lottery.R;
import com.wyzk.lottery.model.RoundInfoModel;

import java.util.List;


public class TrendAdapter extends RecyclerView.Adapter<TrendAdapter.MyViewHolder> {
    private Context context;
    private List<RoundInfoModel> dataList;
    private int layoutId;

    public TrendAdapter(Context context, List<RoundInfoModel> data, int layoutId) {
        this.context = context;
        this.dataList = data;
        this.layoutId = layoutId;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //将position保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(position);
        RoundInfoModel roundModel = dataList.get(position);

        holder.yi.setText(roundModel.getRoomRoundId());

        holder.er.setText(roundModel.getEastNorth());
        holder.san.setText(roundModel.getEast());
        holder.si.setText(roundModel.getEastSouth());
        holder.wu.setText(roundModel.getWestNorth());
        holder.liu.setText(roundModel.getWest());
        holder.qi.setText(roundModel.getWestSouth());

        holder.ba.setText(roundModel.getWinLostValue());

        if (position == 0) {
            return;
        }

        if (roundModel.getEastNorth().contains("赢")) {
            holder.er.setTextColor(Color.parseColor("#FF0000"));
        } else if (roundModel.getEastNorth().contains("输")) {
            holder.er.setTextColor(Color.parseColor("#00FF00"));
        }
        if (roundModel.getEast().contains("赢")) {
            holder.san.setTextColor(Color.parseColor("#FF0000"));
        } else if (roundModel.getEast().contains("输")) {
            holder.san.setTextColor(Color.parseColor("#00FF00"));
        }
        if (roundModel.getEastSouth().contains("赢")) {
            holder.si.setTextColor(Color.parseColor("#FF0000"));
        } else if (roundModel.getEastSouth().contains("输")) {
            holder.si.setTextColor(Color.parseColor("#00FF00"));
        }
        if (roundModel.getWestNorth().contains("赢")) {
            holder.wu.setTextColor(Color.parseColor("#FF0000"));
        } else if (roundModel.getWestNorth().contains("输")) {
            holder.wu.setTextColor(Color.parseColor("#00FF00"));
        }
        if (roundModel.getWest().contains("赢")) {
            holder.liu.setTextColor(Color.parseColor("#FF0000"));
        } else if (roundModel.getWest().contains("输")) {
            holder.liu.setTextColor(Color.parseColor("#00FF00"));
        }
        if (roundModel.getWestSouth().contains("赢")) {
            holder.qi.setTextColor(Color.parseColor("#FF0000"));
        } else if (roundModel.getWestSouth().contains("输")) {
            holder.qi.setTextColor(Color.parseColor("#00FF00"));
        }

        if (Integer.valueOf(roundModel.getWinLostValue()) == 0) {
            return;
        }

        if (roundModel.getWinLostValue().contains("-")) {
            holder.ba.setTextColor(Color.parseColor("#00FF00"));
        } else {
            holder.ba.setTextColor(Color.parseColor("#FF0000"));
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView yi;
        TextView er;
        TextView san;
        TextView si;
        TextView wu;
        TextView liu;
        TextView qi;
        TextView ba;

        public MyViewHolder(View view) {
            super(view);
            yi = (TextView) view.findViewById(R.id.yi);
            er = (TextView) view.findViewById(R.id.er);
            san = (TextView) view.findViewById(R.id.san);
            si = (TextView) view.findViewById(R.id.si);
            wu = (TextView) view.findViewById(R.id.wu);
            liu = (TextView) view.findViewById(R.id.liu);
            qi = (TextView) view.findViewById(R.id.qi);
            ba = (TextView) view.findViewById(R.id.ba);
        }
    }
}

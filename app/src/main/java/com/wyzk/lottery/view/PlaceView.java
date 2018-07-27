package com.wyzk.lottery.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wyzk.lottery.R;


public class PlaceView extends RelativeLayout {

    private View view;
    private LinearLayout ll_rank;
    private TextView rank;
    private TextView item_num_text;
    private TextView item_place_text;
    private TextView item_place_peo;

    public PlaceView(Context context) {
        super(context);
        init(context);
    }

    public PlaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PlaceView);

        boolean checked = ta.getBoolean(R.styleable.PlaceView_item_checked, false);
        setItemChecked(checked);

        String item_num_text = ta.getString(R.styleable.PlaceView_item_num_text);
        setItemNumText(item_num_text);

        String item_place_text = ta.getString(R.styleable.PlaceView_item_place_text);
        setItemPlaceText(item_place_text);

        String item_place_peo = ta.getString(R.styleable.PlaceView_item_place_peo);
        setItemPlacePeo(item_place_peo);

        ta.recycle();
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_place, this);
        view = findViewById(R.id.view);
        ll_rank = (LinearLayout) findViewById(R.id.llrank);
        rank = (TextView) findViewById(R.id.rank);
        item_num_text = (TextView) findViewById(R.id.item_num_text);
        item_place_text = (TextView) findViewById(R.id.item_place_text);
        item_place_peo = (TextView) findViewById(R.id.item_place_peo);
    }


    public void setItemChecked(boolean checked) {
        view.setBackgroundResource(checked ? R.drawable.rect_place_checked : R.drawable.rect_place_normal);
    }

    public void setItemNumText(String text) {
        if (!TextUtils.isEmpty(text)) {
            item_num_text.setText(text);
        }
    }

    public void setItemNumText(int value) {
        item_num_text.setText(String.valueOf(value));
    }

    public int getItemNum() {
        return Integer.parseInt(item_num_text.getText().toString());
    }

    public void setItemPlaceText(String text) {
        if (!TextUtils.isEmpty(text)) {
            item_place_text.setText(text);
        }
    }

    public void setItemPlacePeo(String text) {
        if (!TextUtils.isEmpty(text)) {
            item_place_peo.setText(text);
        }
    }

    public void setItemPlacePeo(int value) {
        item_place_peo.setText(String.valueOf(value));
    }

    public int getItemPlacePeo() {
        return Integer.parseInt(item_place_peo.getText().toString());
    }


    public void setRank(String value) {
        ll_rank.setVisibility(VISIBLE);
        if (!TextUtils.isEmpty(value)) {
            rank.setText(value);
        }
    }

    public void setRank(int value) {
        ll_rank.setVisibility(VISIBLE);
        rank.setText(String.valueOf(value));
    }

    public int getRank() {
        String text = rank.getText().toString();
        return TextUtils.isEmpty(text) ? 0 : Integer.parseInt(text);
    }

    public void clearRank() {
        ll_rank.setVisibility(INVISIBLE);
        rank.setText("");
    }

}

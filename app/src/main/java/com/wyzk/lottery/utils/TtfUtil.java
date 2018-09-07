package com.wyzk.lottery.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 * Created by Administrator on 2016-06-30.
 */
public class TtfUtil {

    public void setTtfUti(Context context , TextView textView , String path){
        Typeface typeFace = Typeface.createFromAsset(context.getAssets(),path);
        //使用字体
        textView.setTypeface(typeFace);
    }

    public static void setDetTtf(Context context , TextView textView ){
        Typeface typeFace = Typeface.createFromAsset(context.getAssets(),"fonts/DINCond-Bold.otf");
        //使用字体
        textView.setTypeface(typeFace);
    }
}

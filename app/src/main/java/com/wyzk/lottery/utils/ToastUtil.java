package com.wyzk.lottery.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * 只显示一次信息的toast 避免toast提示重复存在显示
 */
public class ToastUtil {
    private static Toast sToast;

    public static void showToast(Context context, String text) {
        if (!TextUtils.isEmpty(text)) {
            if (sToast == null) {
                sToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            } else {
                sToast.setText(text);
            }
            sToast.show();
        }
    }

    public static void showToast(Context context, int resId) {
        String text = context.getString(resId);
        showToast(context, text);
    }
}

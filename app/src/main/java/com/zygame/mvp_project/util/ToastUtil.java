package com.zygame.mvp_project.util;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.zygame.mvp_project.MyApplication;


/**
 * @author xjl
 */
public class ToastUtil {
    private static Toast myToast;
    private static TextView toastTv;


    private static void init() {
        myToast = new Toast(MyApplication.getContext());
        toastTv = new TextView(MyApplication.getContext());
        toastTv.setBackgroundColor(Color.BLACK);
        toastTv.setTextColor(Color.WHITE);
        toastTv.setTextSize(16);
        toastTv.setPadding(
                DpiUtil.dipTopx(25),
                DpiUtil.dipTopx(15),
                DpiUtil.dipTopx(25),
                DpiUtil.dipTopx(15));

        myToast.setGravity(Gravity.BOTTOM, 0, DpiUtil.dipTopx(100));
        myToast.setView(toastTv);
    }

    public static void showToast(String text) {
        if (myToast == null) {
            init();
        } else {
            myToast.cancel();
        }

        toastTv.setText(text);
        myToast.setDuration(Toast.LENGTH_SHORT);
        myToast.show();
    }

    public static void showLongToast(String text) {
        if (myToast == null) {
            init();
        } else {
            myToast.cancel();
        }

        toastTv.setText(text);
        myToast.setDuration(Toast.LENGTH_LONG);
        myToast.show();
    }
}

package com.zygame.mvp_project.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import androidx.core.app.NotificationManagerCompat;

/**
 * @author admin
 */
public class NotificationUtil {
    public static int getRandomId() {
        return 1000 + (int) (Math.random() * 8999);
    }

    private static WindowManager sWindowManager;
    private static View sFloatView;


    /**
     * 发送悬浮通知栏
     *
     * @param pContext
     * @param pIntent
     * @param pId      悬浮窗id
     * @param pView    悬浮窗布局
     */
    public static void sendFloatNotification(Context pContext, Intent pIntent, int pId, View pView) {
//        if (!XXPermissions.isGranted(pContext, Permission.SYSTEM_ALERT_WINDOW)) {
//            LogUtil.e("未获得悬浮窗权限");
//            return;
//        }

        if (pView == null) {
            LogUtil.e("悬浮窗布局为空");
            return;
        }

        sWindowManager = (WindowManager) pContext.getSystemService(Context.WINDOW_SERVICE);

        WindowManager.LayoutParams lLayoutParams = new WindowManager.LayoutParams();

        //设置浮窗类型
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            lLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            lLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }

        lLayoutParams.format = PixelFormat.RGBA_8888;
        lLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

        //全屏
//                | WindowManager.LayoutParams.FLAG_FULLSCREEN
//                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN

        // 设置悬浮窗的长得宽
        lLayoutParams.width = DpiUtil.getWidth() - DpiUtil.dipTopx(40);
        lLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        lLayoutParams.height = DpiUtil.dipTopx(150);
        lLayoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;

        sFloatView = pView;
        sWindowManager.addView(sFloatView, lLayoutParams);

        sFloatView.setVisibility(View.INVISIBLE);
        sFloatView.post(() -> showFloatAnim());

        sFloatView.setEnabled(true);

        sFloatView.setOnClickListener(v -> {
            hideFloatView();
            pContext.startActivity(pIntent);
            cancelNotification(pContext, pId);
        });
    }

    /**
     * 隐藏悬浮通知栏
     */
    public static void hideFloatView() {
        hideHandler.removeCallbacksAndMessages(null);
        if (sWindowManager != null && sFloatView != null) {
            sFloatView.setEnabled(false);
            hideFloatAnim();
        }
    }

    private static Handler hideHandler = new Handler();

    /**
     * 悬浮通知栏下拉显示
     */
    private static void showFloatAnim() {
        if (sFloatView == null) {
            return;
        }

        ObjectAnimator lObjectAnimator = ObjectAnimator.ofFloat(sFloatView, "translationY", -sFloatView.getHeight(), 0);
        lObjectAnimator.setDuration(600);
        lObjectAnimator.start();
        sFloatView.setVisibility(View.VISIBLE);

        hideHandler.postDelayed(() -> hideFloatView(), 3600);
    }

    /**
     * 悬浮通知栏上滑隐藏
     */
    private static void hideFloatAnim() {
        if (sFloatView == null) {
            return;
        }

        ObjectAnimator lObjectAnimator = ObjectAnimator.ofFloat(sFloatView, "translationY", 0, -sFloatView.getHeight());
        lObjectAnimator.setDuration(600);
        lObjectAnimator.start();
        lObjectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                sFloatView.setVisibility(View.GONE);
                sWindowManager.removeView(sFloatView);
                sFloatView = null;
            }
        });
    }


    /**
     * 取消通知栏
     *
     * @param id 通知栏渠道id
     */
    public static void cancelNotification(Context pContext, int id) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(pContext);
        notificationManager.deleteNotificationChannel(pContext.getPackageName() + "_alarm_" + id);
    }
}

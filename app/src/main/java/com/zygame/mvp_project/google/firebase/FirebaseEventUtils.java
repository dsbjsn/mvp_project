package com.zygame.mvp_project.google.firebase;

import android.app.Application;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * @author admin
 */
public class FirebaseEventUtils {
    //添加新事件
    public static String ADD_NEW_EVENT = "add_new_event";
    //点击添加新事件按钮
    public static String CLICK_ADD_EVENT_BTN = "click_add_event_btn";
    //好评
    public static String GOOD_COMMENT = "good_comment";
    //分享应用
    public static String SHARE_APP = "share_app";
    //分享图片
    public static String SHARE_IMG = "share_img";

    private static FirebaseAnalytics mFirebaseAnalytics = null;


    /**
     * @param pApplication
     */
    public static void init(Application pApplication) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(pApplication);
    }

    /**
     * firebase 发送自定义事件
     *
     * @param name 自定义事件名称
     */
    public static void sendEvent(String name) {
        if (mFirebaseAnalytics == null) {
            Log.e("FirebaseEventUtils", "FirebaseAnalytics had not init");
            return;
        }

        Log.i("FirebaseEventUtils", "sendEvent:" + name);

        mFirebaseAnalytics.logEvent(name, null);
    }
}

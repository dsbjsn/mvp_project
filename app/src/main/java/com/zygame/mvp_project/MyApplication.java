package com.zygame.mvp_project;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.zygame.mvp_project.google.pay.CheckVipCallBack;
import com.zygame.mvp_project.google.pay.PayUtil;
import com.zygame.mvp_project.util.DeviceIdUtil;
import com.zygame.mvp_project.util.SharedPreferencesUtil;

/**
 * Created on 2021/9/7 10
 *
 * @author xjl
 */
public class MyApplication extends Application {
    public MyApplication() {
    }

    private static MyApplication sMyApplication;

    public static Context getContext() {
        return sMyApplication.getBaseContext();
    }

    public static MyApplication getMyApplication() {
        return sMyApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sMyApplication = this;

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        DeviceIdUtil.getInstance().init(this);
        SharedPreferencesUtil.init(this, getPackageName(), Context.MODE_PRIVATE);


        PayUtil.getInstance().init(this, new CheckVipCallBack() {
            @Override
            public void isVip(boolean is) {

            }
        });
    }
}

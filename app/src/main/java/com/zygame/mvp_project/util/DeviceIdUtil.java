package com.zygame.mvp_project.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class DeviceIdUtil  {
    private static DeviceIdUtil sDeviceIdUtil;

    public static DeviceIdUtil getInstance() {
        if (sDeviceIdUtil == null) {
            sDeviceIdUtil = new DeviceIdUtil();
        }
        return sDeviceIdUtil;
    }

    private String IMEI = null;
    private String OAID = null;
    private String VAID = null;
    private String AAID = null;
    private String ANDROID_ID = null;

    public String getPhoneID() {
        if (!TextUtils.isEmpty(IMEI)) {
            Log.i("手机唯一标识","IMEI=" + IMEI);
            return IMEI;
        }

        if (!TextUtils.isEmpty(ANDROID_ID)) {
            Log.i("手机唯一标识","ANDROID_ID=" + ANDROID_ID);
            return ANDROID_ID;
        }

        if (!TextUtils.isEmpty(OAID)) {
            Log.i("手机唯一标识","oaid=" + OAID);
            return OAID;
        }

        return null;
    }

    private boolean hadInit = false;

    public void init(Context pContext) {
        if (hadInit) {
            return;
        }

        hadInit = true;

        IMEI = getIMEI(pContext);
        ANDROID_ID = getAndroidId(pContext);
    }

    /**
     * 获取imei
     *
     * @param context
     * @return imei
     */
    @SuppressLint("MissingPermission")
    public String getIMEI(Context context) {
        try {
            String deviceUniqueIdentifier;
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm == null) return null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    deviceUniqueIdentifier = tm.getImei();

                    if (TextUtils.isEmpty(deviceUniqueIdentifier)) {
                        try {
                            return tm.getDeviceId();
                        } catch (Throwable ignored) {
                            ignored.printStackTrace();
                        }

                        return tm.getMeid();
                    }
                } else {
                    return tm.getDeviceId();
                }
            } else {
                deviceUniqueIdentifier = tm.getDeviceId();
            }
            if (deviceUniqueIdentifier != null) {
                return deviceUniqueIdentifier;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取android id
     *
     * @return
     */
    public String getAndroidId(Context pContext) {
        return Settings.Secure.getString(pContext.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}

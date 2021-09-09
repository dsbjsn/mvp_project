package com.zygame.mvp_project.evnetBus;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by xjl on 2017/11/21
 *
 * @author admin
 */

public class MessageEvent {
    private EventBusCode code;

    private Map<String, Object> mMap;
    private Object mObject;
    private String mString;
    private int mInt;
    private long mLong;
    private float mFloat;
    private double mDouble;
    private boolean mBoolean;
    private Serializable mSerializable;
    private Drawable mDrawable;
    private Bitmap mBitmap;
    private byte[] mBytes;


    /**
     * 发送通知标识字段
     *
     * @param pCode
     */
    public MessageEvent(EventBusCode pCode) {
        this.code = pCode;
    }

    /**
     * 发送map
     *
     * @param pCode
     * @param pMap
     */
    public MessageEvent(EventBusCode pCode, Map<String, Object> pMap) {
        this.code = pCode;
        this.mMap = pMap;
    }

    /**
     * 发送object
     *
     * @param pCode
     * @param pObj
     */
    public MessageEvent(EventBusCode pCode, Object pObj) {
        this.code = pCode;

        if (pObj instanceof String) {
            mString = (String) pObj;
        } else if (pObj instanceof Integer) {
            mInt = (int) pObj;
        } else if (pObj instanceof Long) {
            mLong = (long) pObj;
        } else if (pObj instanceof Float) {
            mFloat = (float) pObj;
        } else if (pObj instanceof Double) {
            mDouble = (double) pObj;
        } else if (pObj instanceof Boolean) {
            mBoolean = (boolean) pObj;
        } else if (pObj instanceof Serializable) {
            mSerializable = (Serializable) pObj;
        } else if (pObj instanceof Drawable) {
            mDrawable = (Drawable) pObj;
        } else if (pObj instanceof Bitmap) {
            mBitmap = (Bitmap) pObj;
        } else if (pObj instanceof byte[]) {
            mBytes = (byte[]) pObj;
        } else {
            this.mObject = pObj;
        }
    }


    public EventBusCode getCode() {
        return code;
    }


    public Map<String, Object> getMap() {
        return mMap;
    }

    public Object getObject() {
        return mObject;
    }

    public String getString() {
        return mString;
    }

    public int getInt() {
        return mInt;
    }

    public long getLong() {
        return mLong;
    }

    public float getFloat() {
        return mFloat;
    }

    public double getDouble() {
        return mDouble;
    }

    public boolean isBoolean() {
        return mBoolean;
    }

    public Serializable getSerializable() {
        return mSerializable;
    }

    public Drawable getDrawable() {
        return mDrawable;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public byte[] getBytes() {
        return mBytes;
    }
}

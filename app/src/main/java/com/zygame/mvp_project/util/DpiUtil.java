package com.zygame.mvp_project.util;

import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import com.zygame.mvp_project.MyApplication;

/**
 * DpiUtils
 */
public class DpiUtil {

    private static final String TAG = "DpiUtils";

    // 当前屏幕的densityDpi
    private static float mDmDensityDpi = 0.0f;
    private static int mWidth = 0;
    private static int mHeight = 0;

    private static DisplayMetrics mDisplayMetrics;
    private static float mScale = 1.0f;

    // 设计分辨率
    private static int mDesignWidth = 0;
    private static int mDesignHeight = 0;
    private static float mScaleX = 1.0f;
    private static float mScaleY = 1.0f;

    static {
        setContext();
    }

    /**
     * 根据构造函数获得当前手机的屏幕系数
     */
    public static void setContext() {
        // 获取当前屏幕
        mDisplayMetrics = MyApplication.getContext().getResources().getDisplayMetrics();
        // 设置DensityDpi,mWidth,mHeight
        mDmDensityDpi = mDisplayMetrics.densityDpi;
        mWidth = mDisplayMetrics.widthPixels;
        mHeight = mDisplayMetrics.heightPixels;
        // 密度因子
        mScale = getDmDensityDpi() / 160;
        Log.i(TAG, " dmDensityDpi:" + mDmDensityDpi + " mWidth:" + mWidth + " mHeight:" + mHeight);
    }

    /**
     * 密度转换像素
     */
    public static int dipTopx(float dipValue) {

        return (int) (dipValue * mScale + 0.5f);

    }

    public static int sp2px(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, MyApplication.getContext().getResources().getDisplayMetrics());
    }

    /**
     * 像素转换密度
     */
    public static int pxTodip(float pxValue) {
        return (int) (pxValue / mScale + 0.5f);
    }


    /**
     * 当前屏幕的density因子
     *
     * @retrun DmDensity Getter
     */
    public static float getDmDensityDpi() {
        return mDmDensityDpi;
    }

    /**
     * 获取设备的屏幕宽度(Pixels)
     *
     * @return mWidth
     */
    public static int getWidth() {
        return mWidth;
    }


    /**
     * 获取设备的屏幕高度(Pixels)
     *
     * @return mHeight
     */
    public static int getHeight() {
        return mHeight;
    }

    /**
     * 设计分辨率
     */
    public static void SetDesignResolution(int width, int height) {
        mDesignWidth = width;
        mDesignHeight = height;
        mScaleX = (float) mWidth / mDesignWidth;
        mScaleY = (float) mHeight / mDesignHeight;
        Log.i(TAG, "mScaleX" + mScaleX + ", mScaleY" + mScaleY);
    }

    /**
     * 返回横向缩放比例
     *
     * @return
     */
    public static float getScaleX() {
        return mScaleX;
    }

    /**
     * 返回纵向缩放比例
     *
     * @return
     */
    public static float getScaleY() {
        return mScaleY;
    }

    /**
     * 将设计坐标点的横坐标x转换成实际像素点的x
     *
     * @param x
     * @return
     */
    public static int toRealX(int x) {
        return (int) (x * mScaleX);
    }

    /**
     * 将设计坐标点的纵坐标y转换成实际像素点的y
     *
     * @param y
     * @return
     */
    public static int toRealY(int y) {
        return (int) (y * mScaleY);
    }

    /**
     * 将实际像素点的x转换成设计坐标点的横坐标x
     *
     * @param x
     * @return
     */
    public static int toDesignX(int x) {
        return (int) (x / mScaleX);
    }

    /**
     * 将实际像素点的y转换成设计坐标点的纵坐标y
     *
     * @param y
     * @return
     */
    public static int toDesignY(int y) {
        return (int) (y / mScaleY);
    }

}

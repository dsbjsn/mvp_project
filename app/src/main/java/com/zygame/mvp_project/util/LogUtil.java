package com.zygame.mvp_project.util;

import android.util.Log;

import java.util.List;

public class LogUtil {
    private static LogUtil mLogUtil;

    public static LogUtil getInstance() {
        if (mLogUtil == null)
            mLogUtil = new LogUtil();

        return mLogUtil;
    }

    private String getLogDetail() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts == null) {
            return null;
        }

        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }
            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }
            if (st.getClassName().equals(this.getClass().getName())) {
                continue;
            }
            return st.getFileName() + ": [ Line:" + st.getLineNumber() + " ---> " + st.getMethodName() + "() ] ";
        }
        return "";
    }

    public static void i(String msg) {
        String string = getInstance().getLogDetail();
        Log.i(string, msg);
    }

    public static void i(Object msg) {
        String string = getInstance().getLogDetail();
        Log.i(string, String.valueOf(msg));
    }


    public static void d(String msg) {
        String string = getInstance().getLogDetail();
        Log.d(string, msg);
    }

    public static <T> void d(String pS, List<T> pList) {
        String string = getInstance().getLogDetail();
        String msg = pS + "\n";
        for (T lT : pList) {
            msg += lT.toString() + "\n";
        }

        Log.d(string, msg);
    }

    public static void d(Object msg) {
        String string = getInstance().getLogDetail();
        Log.d(string, String.valueOf(msg));
    }


    public static void e(String msg) {
        String string = getInstance().getLogDetail();
        Log.e(string, msg);
    }

    public static void e(Object msg) {
        String string = getInstance().getLogDetail();
        Log.e(string, String.valueOf(msg));
    }
}

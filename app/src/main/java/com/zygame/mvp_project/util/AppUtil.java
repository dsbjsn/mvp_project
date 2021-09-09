package com.zygame.mvp_project.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.core.content.FileProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created on 2021/7/27 16
 *
 * @author xjl
 */
public class AppUtil {
    /**
     * 获取应用名称
     *
     * @return
     */
    public static String getAppName(Context pContext) {
        PackageManager packageManager = pContext.getPackageManager();
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = packageManager.getApplicationInfo(pContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
        return applicationName;
    }

    /**
     * 获取包信息
     *
     * @param context
     * @return
     */
    public static PackageInfo getAppInfo(Context context) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo;
    }

    /**
     * 获取应用图标
     *
     * @param pContext
     * @return
     */
    public static Drawable getAppIcon(Context pContext) {
        try {
            PackageManager pm = pContext.getPackageManager();
            ApplicationInfo info = pm.getApplicationInfo(pContext.getPackageName(), 0);
            return info.loadIcon(pm);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static int getVersionCode(Context pContext) {
        try {
            PackageManager manager = pContext.getPackageManager();
            return manager.getPackageInfo(pContext.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 获取版本名称
     *
     * @return 当前应用的版本名称
     */
    public static String getVersionName(Context pContext) {
        try {
            PackageManager manager = pContext.getPackageManager();
            return manager.getPackageInfo(pContext.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 获取手机机型
     *
     * @return
     */
    public static String getPhoneModel() {
        return Build.MODEL;
    }


    /**
     * 设置全屏
     *
     * @param activity
     */
    public static void fullScreen(Activity activity) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
            View decorView = window.getDecorView();
            //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else {
            WindowManager.LayoutParams attributes = window.getAttributes();
            int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            attributes.flags |= flagTranslucentStatus;
            window.setAttributes(attributes);
        }
    }

    /**
     * 隐藏状态栏
     *
     * @param pActivity
     */
    public static void hideStatusBar(Activity pActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = pActivity.getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            pActivity.getWindow().setAttributes(lp);
            // 设置页面全屏显示
            final View decorView = pActivity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        pActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    /**
     * 设置状态栏模式
     *
     * @param pActivity
     * @param pDark     深色模式
     */
    public static void setStatusBarMode(Activity pActivity, boolean pDark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = pActivity.getWindow().getDecorView();
            if (decorView != null) {
                int vis = decorView.getSystemUiVisibility();
                if (pDark) {
                    vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else {
                    vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                decorView.setSystemUiVisibility(vis);
            }
        }
    }

    /**
     * 显示软键盘
     */
    public static void showSoftInput(View pView) {
        InputMethodManager imm = (InputMethodManager) pView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            pView.requestFocus();
            imm.showSoftInput(pView, 0);
        }
    }

    /**
     * 隐藏软键盘
     */
    public static void hideSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    /**
     * 从Assets读取文件
     *
     * @param pContext
     * @param fileName
     * @return
     */
    public static String getFileFromAssets(Context pContext, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = pContext.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileString = stringBuilder.toString();
        return fileString;
    }


    /**
     * 调用系统分享
     *
     * @param pContext
     */
    public static void shareApp(Context pContext) {
        Intent textIntent = new Intent(Intent.ACTION_SEND);
        textIntent.setType("text/plain");
        textIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.stickermaker.whatsapp");
        pContext.startActivity(Intent.createChooser(textIntent, getAppName(pContext)));
    }

    /**
     * 调用系统分享 图片
     *
     * @param pContext
     * @param filePath
     */
    public static void shareApp(Context pContext, String filePath) {
        File lFile = new File(filePath);
        if (lFile.exists()) {
            Intent lIntent = new Intent(Intent.ACTION_SEND);

            Uri lUri = Uri.fromFile(lFile);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri contentUri = FileProvider.getUriForFile(pContext, pContext.getPackageName() + ".fileprovider", lFile);
                lIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                lIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                lIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(lFile));
            }

            lIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            lIntent.setType("image/jpg");
            pContext.startActivity(Intent.createChooser(lIntent, getAppName(pContext)));
        } else {
            Log.i("分享图片失败", "未找到图片");
        }
    }

//
//    /**
//     * 检查是否获取了悬浮窗权限
//     *
//     * @return
//     */
//    public static boolean checkAlertWindow(AppCompatActivity pActivity) {
//        return XXPermissions.isGranted(pActivity, Permission.SYSTEM_ALERT_WINDOW);
//    }
//
//    /**
//     * 获取悬浮窗权限
//     *
//     * @param pActivity
//     */
//    public static void showAlertWindowPermissionDialog(AppCompatActivity pActivity, OnDialogDismissListener pOnDialogDismissListener) {
//        if (SharedPreferencesUtil.getBoolean(ConfigData.ALLOW_SHOW_ALERT_WINDOW, true)) {
//            PermissionDialog lPermissionDialog = new PermissionDialog();
//            lPermissionDialog.show(pActivity.getSupportFragmentManager());
//            lPermissionDialog.addOnDismissListener(isSkip -> pOnDialogDismissListener.dismiss(isSkip));
//        } else {
//            pOnDialogDismissListener.dismiss(true);
//        }
//    }

    public static int REQUEST_DIALOG_PERMISSION = 12333;

    /**
     * 跳转系统获取悬浮窗权限
     */
    public static void gotoAlertWindowSetting(Activity activity) {
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            activity.startActivityForResult(intent, REQUEST_DIALOG_PERMISSION);
        }
    }


    /**
     * 跳转到好评
     *
     * @param pActivity
     */
    public static void goodPf(Activity pActivity) {
        if (pActivity == null) {
            return;
        }

        String playPackage = "com.android.vending";
        String currentPackageName = pActivity.getPackageName();
        try {
            if (currentPackageName != null) {
                Uri currentPackageUri = Uri.parse("market://details?id=" + currentPackageName);
                Intent intent = new Intent(Intent.ACTION_VIEW, currentPackageUri);
                intent.setPackage(playPackage);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                pActivity.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Uri currentPackageUri = Uri.parse("https://play.google.com/store/apps/details?id=" + currentPackageName);
            Intent intent = new Intent(Intent.ACTION_VIEW, currentPackageUri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            pActivity.startActivity(intent);
        }
    }
}

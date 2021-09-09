package com.zygame.mvp_project.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;


/**
 * @author admin
 */
public class SystemUtil {
    /**
     * 检查权限
     *
     * @param pActivity
     * @param pPermissionList 权限列表
     * @param requestCode     返回码
     * @return
     */
    public static boolean checkPermission(Activity pActivity, String[] pPermissionList, int requestCode) {
        if (pPermissionList != null && pPermissionList.length > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(pActivity, pPermissionList, requestCode);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    /**
     * 获取系统语言
     *
     * @return 语言标识
     */
    public static String getSystemLanguage(Context pContext) {
        Locale locale = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = pContext.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = pContext.getResources().getConfiguration().locale;
        }

        String language = locale.getLanguage();
        String tag = locale.toLanguageTag();

        if (language.equals("zh") && tag.contains("Hant")) {
            language = "tw";
        }

        Log.i("系统语言", language + " tag：" + tag);

        return language;
    }


    public static Uri sTakePhotoImageUri = null;
    public static String sTakePhotoImagePath = null;

    public static void takePhoto(Activity pActivity, int requestCode) {
        //打开相机的Intent
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //这句作用是如果没有相机则该应用不会闪退，要是不加这句则当系统没有相机应用的时候该应用会闪退
        if (takePhotoIntent.resolveActivity(pActivity.getPackageManager()) != null) {
            //创建用来保存照片的文件
            File imageFile = FileUtil.getNewJPGFile(FileUtil.TAKE_PHOTO_CACHE_PATH, System.currentTimeMillis() + ".jpg");
            if (imageFile != null) {
                sTakePhotoImagePath = imageFile.getPath();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    /*7.0以上要通过FileProvider将File转化为Uri*/
                    sTakePhotoImageUri = FileProvider.getUriForFile(pActivity, pActivity.getPackageName() + ".fileprovider", imageFile);
                } else {
                    /*7.0以下则直接使用Uri的fromFile方法将File转化为Uri*/
                    sTakePhotoImageUri = Uri.fromFile(imageFile);
                }
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, sTakePhotoImageUri);
                pActivity.startActivityForResult(takePhotoIntent, requestCode);
            }
        }
    }

    /**
     * 跳转到相册
     *
     * @param pActivity
     */
    public static void choicePhotoFromSystem(Activity pActivity, int resultCode) {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pActivity.startActivityForResult(intent, resultCode);
    }

    /**
     * 裁剪图片 正方形
     *
     * @param pActivity
     * @param dataUri    输入图片路径
     * @param outFile    输出图片路径
     * @param width      输出图片宽
     * @param height     输出图片高
     * @param resultCode 返回码
     */
    public static void resizeImage(Activity pActivity, Uri dataUri, File outFile, int width, int height, int resultCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");

        intent.setDataAndType(dataUri, "image/*");
        // 设置Intent中的view是可以裁剪的
        intent.putExtra("crop", true);
        // 设置宽高比
        if (Build.MANUFACTURER.equals("HUAWEI")) {
            intent.putExtra("aspectX", 9998);
            intent.putExtra("aspectY", 9999);
        } else {
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
        }

        // 设置裁剪图片的宽高
//        intent.putExtra("outputX", width);
//        intent.putExtra("outputY", height);

        // return-data为true时,会直接返回bitmap数据,但是大图裁剪时会出现问题,推荐下面为false时的方式
        // return-data为false时,不会返回bitmap,但需要指定一个MediaStore.EXTRA_OUTPUT保存图片uri
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outFile));

        // 这两句是在7.0以上版本大于23时需要
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }

        try {
            pActivity.startActivityForResult(intent, resultCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 裁剪图片 任意矩形
     *
     * @param pActivity
     * @param uri        输入图片路径
     * @param outFile    输出图片路径
     * @param width      输出图片宽
     * @param height     输出图片高
     * @param aspectX    aspectX
     * @param aspectY    aspectY
     * @param resultCode 返回码
     */
    public static void resizeImage(Activity pActivity, Uri uri, File outFile, int width, int height, int aspectX, int aspectY, int resultCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置Intent中的view是可以裁剪的
        intent.putExtra("crop", true);
        // 设置宽高比
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);

        // 设置裁剪图片的宽高
//        intent.putExtra("outputX", width);
//        intent.putExtra("outputY", height);

        // return-data为true时,会直接返回bitmap数据,但是大图裁剪时会出现问题,推荐下面为false时的方式
        // return-data为false时,不会返回bitmap,但需要指定一个MediaStore.EXTRA_OUTPUT保存图片uri
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outFile));

        // 这两句是在7.0以上版本大于23时需要
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        //设置返回码
        try {
            pActivity.startActivityForResult(intent, resultCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取系统默认铃声的Uri
     */
    public static Uri getSystemDefaultRingtoneUri(Context pContext) {
        return RingtoneManager.getActualDefaultRingtoneUri(pContext, RingtoneManager.TYPE_RINGTONE);
    }

    /**
     * 获取铃声标题列表
     *
     * @return 铃声标题列表
     */
    public static ArrayList<String> getRingTitleList(Context pContext) {
        ArrayList<String> lList = new ArrayList<>();
        RingtoneManager ringtoneManager = new RingtoneManager(pContext);
        ringtoneManager.setType(RingtoneManager.TYPE_RINGTONE);
        Cursor cursor = ringtoneManager.getCursor();
        while (cursor.moveToNext()) {
            lList.add(cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX));
        }
        return lList;
    }

    /**
     * 获取铃声路径列表
     *
     * @return 铃声路径列表
     */
    public static ArrayList<Uri> getRingUriList(Context pContext) {
        ArrayList<Uri> lList = new ArrayList<>();
        RingtoneManager ringtoneManager = new RingtoneManager(pContext);
        ringtoneManager.setType(RingtoneManager.TYPE_RINGTONE);
        Cursor cursor = ringtoneManager.getCursor();
        while (cursor.moveToNext()) {
            lList.add(ringtoneManager.getRingtoneUri(cursor.getPosition()));
        }
        return lList;
    }

    private static MediaPlayer sMediaPlayer;

    /**
     * 播放铃声
     *
     * @param pContext 铃声路径
     * @param pUri     铃声路径
     * @param isLoop   是否循环
     * @throws IllegalStateException
     */
    public static void playRing(Context pContext, Uri pUri, boolean isLoop) throws IllegalStateException {
        if (sMediaPlayer == null) {
            sMediaPlayer = new MediaPlayer();
        } else {
            sMediaPlayer.stop();
            sMediaPlayer.reset();
        }

        try {
            if (pUri == null) {
                pUri = getSystemDefaultRingtoneUri(pContext);
            }
            sMediaPlayer.setDataSource(pContext, pUri);
            sMediaPlayer.setLooping(isLoop);
            sMediaPlayer.prepare();
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        sMediaPlayer.start();
    }

    /**
     * 停止播放铃声
     */
    public static void stopRing() {
        if (sMediaPlayer != null) {
            sMediaPlayer.stop();
        }
    }

    /**
     * 单次震动
     *
     * @param pContext
     * @param milliseconds ：震动的时长，单位是毫秒
     */
    @SuppressLint("MissingPermission")
    public static void vibrate(Context pContext, long milliseconds) {
        Vibrator vib = (Vibrator) pContext.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }

    /**
     * 震动
     *
     * @param pattern  ：自定义震动模式 。数组中数字的含义依次是[静止时长，震动时长，静止时长，震动时长。。。]时长的单位是毫秒
     * @param isRepeat ： 是否反复震动，如果是true，反复震动，如果是false，只震动一次
     */
    @SuppressLint("MissingPermission")
    public static void vibrate(Context pContext, long[] pattern, boolean isRepeat) {
        Vibrator vib = (Vibrator) pContext.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(pattern, isRepeat ? 1 : -1);
    }

    /**
     * 取消震动
     */
    @SuppressLint("MissingPermission")
    public static void stopVibrate(Context pContext) {
        Vibrator vib = (Vibrator) pContext.getSystemService(Service.VIBRATOR_SERVICE);
        vib.cancel();
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
     *
     * @param view
     */
    public static void hideSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

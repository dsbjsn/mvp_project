package com.zygame.mvp_project.util.glideUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zygame.mvp_project.MyApplication;

import java.io.File;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by admin on 2018/1/6
 *
 * @author admin
 */

public class GlideUtil {
    private static String EXTERNAL_CACHE_PATH = MyApplication.getContext().getExternalCacheDir() + ExternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR;
    private static String INTERNAL_CACHE_PATH = MyApplication.getContext().getCacheDir() + "/" + InternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR;

    // 获取Glide磁盘缓存大小
    public static String getCacheSize() {
        try {
            String string = getFormatSize(getFolderSize(new File(INTERNAL_CACHE_PATH)));
            return string;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 清除图片所有缓存
     */
    public static boolean clearImageAllCache() {
        clearCacheDiskSelf();
        clearCacheMemory();
        return deleteFolderFile(EXTERNAL_CACHE_PATH, true);
    }


    /**
     * 清除图片磁盘缓存，调用Glide自带方法
     *
     * @return
     */
    public static boolean clearCacheDiskSelf() {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                ExecutorService lService = Executors.newCachedThreadPool();
                lService.execute(() -> {
                    Glide.get(MyApplication.getContext()).clearDiskCache();
                });
            } else {
                Glide.get(MyApplication.getContext()).clearDiskCache();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 清除Glide内存缓存
     *
     * @return
     */
    public static boolean clearCacheMemory() {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Glide.get(MyApplication.getContext()).clearMemory();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    // 获取指定文件夹内所有文件大小的和
    private static long getFolderSize(File file) {
        long size = 0;
        try {
            if (file == null) {
                return size;
            } else {
                File[] fileList = file.listFiles();
                if (fileList != null && fileList.length != 0) {
                    for (File aFileList : fileList) {
                        if (aFileList.isDirectory()) {
                            size = size + getFolderSize(aFileList);
                        } else {
                            size = size + aFileList.length();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }


    // 格式化单位
    private static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "B";
        }
        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "K";
        }
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "M";
        }
        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "G";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "T";
    }


    /**
     * 按目录删除文件夹文件方法
     *
     * @param filePath
     * @param deleteThisPath
     * @return
     */
    private static boolean deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (TextUtils.isEmpty(filePath)) {
            return true;
        }
        try {
            File file = new File(filePath);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File file1 : files) {
                    deleteFolderFile(file1.getAbsolutePath(), true);
                }
            }
            boolean delete = true;
            if (deleteThisPath) {
                if (!file.isDirectory()) {
                    delete = file.delete();
                } else {
                    if (Objects.requireNonNull(file.listFiles()).length == 0) {
                        delete = file.delete();
                    }
                }
            }
            return delete;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static void getBitMap(Context pContext, String path, GlideLoadBitmapListener pGlideLoadBitmapListener) {
        Glide.with(pContext)
                .asBitmap()
                .load(path)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        if (pGlideLoadBitmapListener != null) {
                            pGlideLoadBitmapListener.complete(resource);
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    public interface GlideLoadBitmapListener {
        void complete(Bitmap pBitmap);
    }
}

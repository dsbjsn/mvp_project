package com.zygame.mvp_project.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.zygame.mvp_project.MyApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 文件管理类
 * <p>
 * 图片下载的文件夹
 * android sdk 30+ 想要存储文件到外部空间，需要MANAGE_EXTERNAL_STORAGE 所有文件权限
 * google play 不推荐此做法，需要使用分区存储
 * 因此下载图片的时候，需要使用多媒体存储，将图片存储到相册文件夹
 * 缓存图片存储在应用内部文件夹下
 */
public class FileUtil {
    /**
     * 本地存储路径
     */
    public static final String SAVE_PIC_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + "/ImageTool/share";
    /**
     * 添加到相册存储路径
     */
    public static final String GALLERY_ROOT_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + "/ImageTool";
//    public static final String GALLERY_ROOT_PATH = Environment.DIRECTORY_PICTURES + File.separator + "/ImageTool";
    /**
     * 应用内部图片缓存地址
     */
    public static final String CACHE_PATH = MyApplication.getContext().getExternalCacheDir().getPath();
    /**
     * 编辑临时缓存地址
     */
    public static final String EDIT_CACHE_PATH = MyApplication.getContext().getExternalCacheDir().getPath() + "/edit_cache";
    /**
     * 应用内存储地址
     */
    public static final String LOCAL_CACHE_PATH = MyApplication.getContext().getExternalFilesDir("").getPath() + "/image_cache";
    /**
     * 拍照缓存地址
     */
    public static final String TAKE_PHOTO_CACHE_PATH = MyApplication.getContext().getExternalFilesDir("").getPath() + "/image_cache/take_photo";
    /**
     * ttf 下载地址
     */
    public static final String TTF_DOWNLOAD_PATH = MyApplication.getContext().getExternalFilesDir("").getPath() + "/ttf";


    /**
     * 创建新图片文件，自动以时间戳命名
     *
     * @return 图片地址
     */
    public static File getNewJPGFile() {
        return getNewJPGFile(LOCAL_CACHE_PATH, System.currentTimeMillis() + ".jpg");
    }

    /**
     * 创建新图片文件
     *
     * @param rootPath 父级文件夹路径
     * @param fileName 文件名
     * @return 文件路径
     */
    public static File getNewJPGFile(String rootPath, String fileName) {
        File dir = new File(rootPath);
        if (!dir.exists()) {
            try {
                dir.mkdirs();
            } catch (Exception pE) {
                return null;
            }
        }

        File file = new File(dir, fileName);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException pE) {
                pE.printStackTrace();
                return null;
            }
        }
        return file;
    }


    /**
     * 对Uri 进行处理
     *
     * @param file 返回 兼容处理后的 Uri
     * @return
     */
    public static Uri getUriForFile(Context pContext, File file) {
        Uri fileUri;
        // Android 7.0系统开始 使用本地真实的Uri路径不安全,使用FileProvider封装共享Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //一定要和 AndroidManifest.xml 中的设置的名字一样
            fileUri = FileProvider.getUriForFile(pContext, pContext.getPackageName() + ".fileprovider", file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }

    /**
     * 递归创建文件夹
     *
     * @param file
     * @return 创建失败返回""
     */
    public static String createFile(File file) {
        try {
            if (file.getParentFile().exists()) {
                Log.i("创建文件", "-----> " + file.getAbsolutePath());
                file.createNewFile();
                return file.getAbsolutePath();
            } else {
                createDir(file.getParentFile().getAbsolutePath());
                file.createNewFile();
                Log.i("创建文件", "-----> " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 递归创建文件夹
     *
     * @param dirPath
     * @return 创建失败返回""
     */
    public static String createDir(String dirPath) {
        try {
            File file = new File(dirPath);
            if (file.getParentFile().exists()) {
                Log.i("创建文件夹", "-----> " + file.getAbsolutePath());
                file.mkdir();
                return file.getAbsolutePath();
            } else {
                createDir(file.getParentFile().getAbsolutePath());
                Log.i("创建文件夹", "-----> " + file.getAbsolutePath());
                file.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dirPath;
    }


    public static List<String> getAllFilesInDir(String dirPath) {
        File f = new File(dirPath);
        if (!f.exists()) {
            return new ArrayList<>();
        }

        File[] files = f.listFiles();

        if (files == null) {
            return new ArrayList<>();
        }

        List<String> lStringList = new ArrayList<>();
        //遍历目录
        for (File _file : files) {
            String filePath = _file.getAbsolutePath();
            lStringList.add(filePath);
        }

        return lStringList;
    }


    /**
     * 删除缓存文件夹
     */
    public static void deleteCacheDirectory() {
        deleteDirectoryFile(EDIT_CACHE_PATH);
    }

    /**
     * 删除文件夹以及目录下的文件
     *
     * @param filePath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String filePath) {
        boolean flag = false;
        try {
            // 如果filePath不以文件分隔符结尾，自动添加文件分隔符
            if (!filePath.endsWith(File.separator)) {
                filePath = filePath + File.separator;
            }
            File dirFile = new File(filePath);
            if (!dirFile.exists() || !dirFile.isDirectory()) {
                return false;
            }
            flag = true;
            File[] files = dirFile.listFiles();
            // 遍历删除文件夹下的所有文件(包括子目录)
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    // 删除子文件
                    flag = deleteFile(files[i].getAbsolutePath());
                } else {
                    // 删除子目录
                    flag = deleteDirectory(files[i].getAbsolutePath());
                }
                if (!flag) {
                    break;
                }
            }
            if (!flag) {
                return false;
            }
            // 删除当前空目录
            return dirFile.delete();
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 删除文件夹下的文件
     *
     * @param filePath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectoryFile(String filePath) {
        boolean flag = false;
        try {
            // 如果filePath不以文件分隔符结尾，自动添加文件分隔符
            if (!filePath.endsWith(File.separator)) {
                filePath = filePath + File.separator;
            }
            File dirFile = new File(filePath);
            if (!dirFile.exists() || !dirFile.isDirectory()) {
                return false;
            }
            flag = true;
            File[] files = dirFile.listFiles();
            if (files.length == 0) {
                return false;
            } else {
                // 遍历删除文件夹下的所有文件(包括子目录)
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isFile()) {
                        // 删除子文件
                        flag = deleteFile(files[i].getAbsolutePath());
                    } else {
                        // 删除子目录
                        flag = deleteDirectory(files[i].getAbsolutePath());
                    }
                    if (!flag) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
        }
        if (!flag) {
            return false;
        }
        return flag;
    }


    /**
     * 根据路径删除指定的目录或文件，无论存在与否
     *
     * @param filePath 要删除的目录或文件
     * @return 删除成功返回 true，否则返回 false。
     */
    public static boolean deleteFolder(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
                // 为文件时调用删除文件方法
                return deleteFile(filePath);
            } else {
                // 为目录时调用删除目录方法
                return deleteDirectory(filePath);
            }
        }
    }


    /**
     * 删除单个文件
     *
     * @param filePath 被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }


    /**
     * 获取保存图片的根目录
     *
     * @return
     */
    public static File getSaveRootDirectory(String fatherFile) {
        File dir = new File(SAVE_PIC_PATH + "/" + fatherFile);
        if (!dir.exists()) {
            try {
                dir.mkdirs();
            } catch (Exception pE) {
                return null;
            }
        }
        return dir;
    }


    /**
     * 缓存需要编辑的图片
     *
     * @param bitmap 图片对象
     */
    public static String cacheEditPic(Bitmap bitmap) {
        String picName = System.currentTimeMillis() + ".png";
        File file = getNewJPGFile(EDIT_CACHE_PATH, picName);
        if (file == null) {
            return null;
        }

        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            Bitmap.CompressFormat lCompressFormat = Bitmap.CompressFormat.PNG;
            bitmap.compress(lCompressFormat, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return file.getPath();
    }

    /**
     * 缓存需要分享的图片
     *
     * @param bitmap    图片对象
     * @param width     最大宽度
     * @param height    最大高度
     * @param cacheSize 最大大小
     * @return
     */
    public static String cacheSharePic(Bitmap bitmap, int width, int height, int cacheSize) {
        Bitmap newBm = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(newBm);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);

        String picName = System.currentTimeMillis() + ".jpg";
        File file = getNewJPGFile(EDIT_CACHE_PATH, picName);
        if (file == null) {
            return null;
        }

        newBm = BitMapUtil.getScaleBitmap(newBm, width, height);


        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            out.write(BitMapUtil.getCompressWebpBitMap(newBm, Bitmap.CompressFormat.JPEG, cacheSize));
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return file.getPath();
    }


    /**
     * 保存图片到应用内文件夹下
     *
     * @param pBitmap    图片信息
     * @param pAtlasName 图集，名称
     * @return
     */
    public static String saveImageToCacheFile(Bitmap pBitmap, String pAtlasName) {
        String picName = System.currentTimeMillis() + ".png";
        File file = getNewJPGFile(LOCAL_CACHE_PATH + "/" + pAtlasName, picName);
        if (file == null) {
            return null;
        }

        FileOutputStream out;
        try {
            out = new FileOutputStream(file);

            Bitmap.CompressFormat lCompressFormat = picName.contains("png") ? Bitmap.CompressFormat.PNG
                    : (picName.contains("jpg") ? Bitmap.CompressFormat.JPEG
                    : Bitmap.CompressFormat.WEBP);

            pBitmap.compress(lCompressFormat, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return file.getPath();
    }

    /**
     * 把bitmap 存储到本地
     * 需要权限
     *
     * @param pContext
     * @param pBitmap      图片信息
     * @param fileName     文件名称
     * @param showInSystem 是否通知系统相册
     */
    public static String saveImage(Context pContext, Bitmap pBitmap, String fatherFileName, String fileName, boolean showInSystem) {
        String rootPath = GALLERY_ROOT_PATH + "/" + fatherFileName;
        Log.i("rootPath", rootPath);
        File file = getNewJPGFile(rootPath, fileName);
        Log.i("saveImage", file.getPath());
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);

            Bitmap.CompressFormat lCompressFormat = fileName.contains("png") ? Bitmap.CompressFormat.PNG
                    : (fileName.contains("jpg") ? Bitmap.CompressFormat.JPEG : Bitmap.CompressFormat.WEBP);

            pBitmap.compress(lCompressFormat, 100, out);
            out.flush();
            out.close();

            if (showInSystem) {
                // 通知图库更新
                MediaScannerConnection.scanFile(pContext, new String[]{file.getAbsolutePath()}, null,
                        (path, uri) -> {
                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            mediaScanIntent.setData(uri);
                            pContext.sendBroadcast(mediaScanIntent);
                        });
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return file.getPath();
    }


    /**
     * 保存到相册
     *
     * @param pContext
     * @param fatherPath 根目录
     * @param fileName   文件名称
     * @param bitmap     图片
     */
    public static void saveImageToGallery(Context pContext, String fatherPath, String fileName, Bitmap bitmap) {
        long mImageTime = System.currentTimeMillis();
        ContentValues values = new ContentValues();

        createDir(fatherPath);

        String path = fatherPath + "/" + fileName;

        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, fileName.contains("jpg") ? "image/jpg" : "image/png");

        values.put(MediaStore.MediaColumns.RELATIVE_PATH, GALLERY_ROOT_PATH);
        values.put(MediaStore.MediaColumns.DATA, path);

        ContentResolver resolver = pContext.getContentResolver();
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        OutputStream out = null;
        try {
            out = resolver.openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            if (uri != null) {
                resolver.delete(uri, null, null);
            }
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 查询相册中是否存在图片
     *
     * @param fatherPath 父级文件夹
     * @param picName    图片名称
     * @return 是否存在
     */
    public static boolean isExitInGallery(Context pContext, String fatherPath, String picName) {
        ContentResolver lContentResolver = pContext.getContentResolver();

        fatherPath = fatherPath + "/" + picName;

        Cursor lCursor = lContentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Images.Media.DISPLAY_NAME + "=?",
                new String[]{picName},
                null);

        if (lCursor != null) {
            while (lCursor.moveToNext()) {
                String path = lCursor.getString(lCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                if (!TextUtils.isEmpty(path)) {
                    if (path.contains(fatherPath)) {
                        return true;
                    }
                }
            }
            lCursor.close();
            return false;
        } else {
            return false;
        }
    }


    /**
     * 保存图片到相册的whatsapp文件夹下
     *
     * @param pContext
     * @param fatherPath 父级文件夹路径
     * @param picName    图片名称
     * @param pBitmap    图片对象
     * @param pCacheSize 最大大小
     */
    public static void saveWhatsAppSticker(Context pContext, String fatherPath, String picName, Bitmap pBitmap, int pCacheSize) {
        //修改图片文件后缀
        picName = picName.replace(".png", ".webp").replace(".jpg", ".webp");

        File file = getNewJPGFile(fatherPath, picName);
        Log.i("WhatsApp", "保存表情包图片:" + file.getPath());

        FileOutputStream out;
        try {

            out = new FileOutputStream(file);
            out.write(BitMapUtil.getCompressWebpBitMap(pBitmap, Bitmap.CompressFormat.WEBP, pCacheSize));

            out.flush();
            out.close();
            Log.i("WhatsApp", "保存表情包图片成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 保存WhatsApp表情包封面图片
     *
     * @param pContext
     * @param fatherPath 父级文件夹路径
     * @param picName    图片名称
     * @param pBitmap
     */
    public static void saveWhatsAppStickerCover(Context pContext, String fatherPath, String picName, Bitmap pBitmap) {
        File file = getNewJPGFile(fatherPath, picName);
        Log.i("WhatsApp", "保存表情包封面图片:" + file.getPath());

        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            pBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            Log.i("WhatsApp", "保存表情包封面图片成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 检查文件是否存在
     *
     * @param path 文件路径
     * @return
     */
    public static boolean checkFileExit(String path) {
        File lFile = new File(path);
        if (lFile != null) {
            return lFile.exists();
        } else {
            return false;
        }
    }


    private static int sBufferSize = 8192;


    /**
     * 网络下载文件写入本地
     *
     * @param path             写入的文件地址
     * @param response
     * @param downloadListener 进度监听
     */
//    public static void writeResponseToDisk(String path, Response<ResponseBody> response, DownloadTTFListener downloadListener) {
//        File file = new File(path);
//        InputStream lInputStream = response.body().byteStream();
//
//        long totalLength = response.body().contentLength();
//        //开始下载
//        downloadListener.progress(0);
//
//        //创建文件
//        if (!file.exists()) {
//            if (!file.getParentFile().exists())
//                file.getParentFile().mkdir();
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//                downloadListener.fail("createNewFile IOException");
//            }
//        }
//
//        OutputStream lOutputStream = null;
//        long currentLength = 0;
//        try {
//            lOutputStream = new BufferedOutputStream(new FileOutputStream(file));
//            byte data[] = new byte[sBufferSize];
//            int len = 0;
//            while ((len = lInputStream.read(data, 0, sBufferSize)) != -1) {
//                lOutputStream.write(data, 0, len);
//                currentLength += len;
//                downloadListener.progress((int) (100 * currentLength / totalLength));
//            }
//
//            downloadListener.complete(file.getAbsolutePath());
//        } catch (IOException e) {
//            e.printStackTrace();
//            downloadListener.fail("IOException");
//        } finally {
//            try {
//                lInputStream.close();
//
//                if (lOutputStream != null) {
//                    lOutputStream.close();
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    /**
     * 获取系统所有相册图片
     *
     * @param pContext
     * @param isThumbnails 是否是缩略图
     * @return
     */
    public static List<String> getDicImages(Context pContext, boolean isThumbnails) {
        List<String> result = new ArrayList<>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        ContentResolver contentResolver = pContext.getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor == null || cursor.getCount() <= 0) {
            return new ArrayList<>();
        }

        Log.i("相册数量", cursor.getCount() + "");

        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndexOrThrow(isThumbnails ? MediaStore.Images.Thumbnails.DATA : MediaStore.Images.Media.DATA);
            String path = cursor.getString(index);
            result.add(path);
        }
        cursor.close();
        Collections.reverse(result);
        return result;
    }
}
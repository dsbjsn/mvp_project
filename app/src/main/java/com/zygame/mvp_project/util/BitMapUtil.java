package com.zygame.mvp_project.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Created by admin on 2018/3/21.
 */

public class BitMapUtil {
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    public static Bitmap decodeResourcesDrawable(Context context, int resID, int requestWidth, int requestHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), resID, options);

        // Calculate inSampleSize
        options.inSampleSize = getBitmapSampleSize(options, requestWidth, requestHeight);
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(context.getResources(), resID, options);
    }

    public static int getBitmapSampleSize(BitmapFactory.Options options, int requestWidth, int requestHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > requestHeight || width > requestWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > requestHeight && (halfWidth / inSampleSize) > requestWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap compressQuality(Bitmap pBitmap, int quality) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        pBitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
        byte[] bytes = bos.toByteArray();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

    }

    /**
     * 获取等比例缩放后的图片
     *
     * @param pBitmap
     * @param maxWidth  最大图片宽度
     * @param maxHeight 最大图片高度
     * @return
     */
    public static Bitmap getScaleBitmap(Bitmap pBitmap, int maxWidth, int maxHeight) {
        if (pBitmap == null) {
            return null;
        }
        if (pBitmap.getWidth() > maxWidth || pBitmap.getHeight() > maxHeight) {
            float s_w = (float) pBitmap.getWidth() / maxWidth;
            float s_h = (float) pBitmap.getHeight() / maxHeight;
            float max_s = Math.max(s_w, s_h);
            Bitmap newBitmap = Bitmap.createScaledBitmap(pBitmap, (int) (pBitmap.getWidth() / max_s), (int) (pBitmap.getHeight() / max_s), false);
            return newBitmap;
        } else {
            return pBitmap;
        }
    }

    /**
     * 获取等比例缩放后的图片
     *
     * @param pBitmap
     * @param width   指定宽度
     * @param height  指定高度
     * @return
     */
    public static Bitmap getSpecifySizeBitmap(Bitmap pBitmap, int width, int height) {
        if (pBitmap == null) {
            return null;
        }

        return Bitmap.createScaledBitmap(pBitmap, width, height, false);
    }


    public static byte[] getCompressWebpBitMap(Bitmap pBitmap,Bitmap.CompressFormat pFormat, int maxSize) {
        boolean goon = true;
        int options = 100;

        ByteArrayOutputStream lOutputStream = new ByteArrayOutputStream();
        pBitmap.compress(pFormat, options, lOutputStream);
        Log.i("SpecifySize", "原大小：" + (lOutputStream.toByteArray().length / 1024) + "KB");

        try {
            while (goon) {
                lOutputStream.reset();
                pBitmap.compress(Bitmap.CompressFormat.WEBP, options, lOutputStream);

                Log.i("SpecifySize", "压缩后大小：" + (lOutputStream.toByteArray().length / 1024) + "KB");
                if (lOutputStream.toByteArray().length / 1024 > maxSize) {
                    options -= 5;
                } else {
                    goon = false;
                }
            }
        } catch (Exception pE) {
            Log.i("SpecifySize", "error：" + pE.getMessage());
        }

        return lOutputStream.toByteArray();
    }
}

package com.zygame.mvp_project.util.glideUtil;

/**
 * Created on 2021/2/4 14
 *
 * @author xjl
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;

import java.security.MessageDigest;

/**
 * @author csc
 * @date 2019-01-18
 */
public class RoundedCornersTransform implements Transformation<Bitmap> {
    private BitmapPool mBitmapPool;

    private float radius;

    private float storkWidth = 0f;

    private int storkColor = Color.BLACK;

    private boolean isLeftTop, isRightTop, isLeftBottom, isRightBottom;


    public void setStorkWidth(float pStorkWidth) {
        storkWidth = pStorkWidth;
    }

    public void setStorkColor(int pStorkColor) {
        storkColor = pStorkColor;
    }

    /**
     * 需要设置圆角的部分
     *
     * @param leftTop     左上角
     * @param rightTop    右上角
     * @param leftBottom  左下角
     * @param rightBottom 右下角
     */
    public void setNeedCorner(boolean leftTop, boolean rightTop, boolean leftBottom, boolean rightBottom) {
        isLeftTop = leftTop;
        isRightTop = rightTop;
        isLeftBottom = leftBottom;
        isRightBottom = rightBottom;
    }

    /**
     * @param context 上下文
     * @param radius  圆角幅度
     */
    public RoundedCornersTransform(Context context, float radius) {
        this.mBitmapPool = Glide.get(context).getBitmapPool();
        this.radius = radius;
    }

    @NonNull
    @Override
    public Resource<Bitmap> transform(@NonNull Context context, @NonNull Resource<Bitmap> resource, int outWidth, int outHeight) {

        Bitmap source = resource.get();
        int finalWidth, finalHeight;
        //输出目标的宽高或高宽比例
        float scale;
        if (outWidth > outHeight) {
            //如果 输出宽度 > 输出高度 求高宽比

            scale = (float) outHeight / (float) outWidth;
            finalWidth = source.getWidth();
            //固定原图宽度,求最终高度
            finalHeight = (int) ((float) source.getWidth() * scale);
            if (finalHeight > source.getHeight()) {
                //如果 求出的最终高度 > 原图高度 求宽高比

                scale = (float) outWidth / (float) outHeight;
                finalHeight = source.getHeight();
                //固定原图高度,求最终宽度
                finalWidth = (int) ((float) source.getHeight() * scale);
            }
        } else if (outWidth < outHeight) {
            //如果 输出宽度 < 输出高度 求宽高比

            scale = (float) outWidth / (float) outHeight;
            finalHeight = source.getHeight();
            //固定原图高度,求最终宽度
            finalWidth = (int) ((float) source.getHeight() * scale);
            if (finalWidth > source.getWidth()) {
                //如果 求出的最终宽度 > 原图宽度 求高宽比

                scale = (float) outHeight / (float) outWidth;
                finalWidth = source.getWidth();
                finalHeight = (int) ((float) source.getWidth() * scale);
            }
        } else {
            //如果 输出宽度=输出高度
            finalHeight = source.getHeight();
            finalWidth = finalHeight;
        }

        //修正圆角
        this.radius *= (float) finalHeight / (float) outHeight;
        Bitmap outBitmap = this.mBitmapPool.get(finalWidth, finalHeight, Bitmap.Config.ARGB_8888);
        if (outBitmap == null) {
            outBitmap = Bitmap.createBitmap(finalWidth, finalHeight, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(outBitmap);
        RectF rectF = new RectF(0.0F, 0.0F, (float) canvas.getWidth(), (float) canvas.getHeight());

        if (storkWidth > 0) {
            Paint lPaint = new Paint();
            lPaint.setAntiAlias(true);
            lPaint.setStyle(Paint.Style.FILL);
            lPaint.setColor(storkColor);

            Log.i("radius", radius + "");
            canvas.drawRoundRect(rectF, radius, radius, lPaint);

            rectF.top += storkWidth;
            rectF.bottom -= storkWidth;
            rectF.left += storkWidth;
            rectF.right -= storkWidth;

            lPaint.setColor(Color.WHITE);
            canvas.drawRoundRect(rectF, radius, radius, lPaint);
        }

        Paint paint = new Paint();
        //关联画笔绘制的原图bitmap
        BitmapShader shader = new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        //计算中心位置,进行偏移
        int width = (source.getWidth() - finalWidth) / 2;
        int height = (source.getHeight() - finalHeight) / 2;
        if (width != 0 || height != 0) {
            Matrix matrix = new Matrix();
            matrix.setTranslate((float) (-width), (float) (-height));
            shader.setLocalMatrix(matrix);
        }

        paint.setShader(shader);
        paint.setAntiAlias(true);
        //先绘制圆角矩形
        canvas.drawRoundRect(rectF, this.radius, this.radius, paint);

        //左上角圆角
        if (!isLeftTop) {
            canvas.drawRect(0, 0, radius, radius, paint);
        }
        //右上角圆角
        if (!isRightTop) {
            canvas.drawRect(canvas.getWidth() - radius, 0, canvas.getWidth(), radius, paint);
        }
        //左下角圆角
        if (!isLeftBottom) {
            canvas.drawRect(0, canvas.getHeight() - radius, radius, canvas.getHeight(), paint);
        }
        //右下角圆角
        if (!isRightBottom) {
            canvas.drawRect(canvas.getWidth() - radius, canvas.getHeight() - radius, canvas.getWidth(), canvas.getHeight(), paint);
        }

        return BitmapResource.obtain(outBitmap, this.mBitmapPool);
    }


    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
    }
}

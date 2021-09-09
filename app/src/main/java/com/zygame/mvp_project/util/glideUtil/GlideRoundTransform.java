package com.zygame.mvp_project.util.glideUtil;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

/**
 * @author admin
 */
public class GlideRoundTransform extends BitmapTransformation {
    private static float radius = 0f;

    private static boolean left_top =true;
    private static boolean left_bottom =true;
    private static boolean right_top =true;
    private static boolean right_bottom =true;
    
    public GlideRoundTransform(Context context) {
        this(context, 5);
    }

    public GlideRoundTransform(Context context, int dp) {
        super();
        radius = Resources.getSystem().getDisplayMetrics().density * dp;
    }

    public GlideRoundTransform(Context context, int dp, boolean corner_1, boolean corner_2, boolean corner_3, boolean corner_4) {
        super();
        radius = Resources.getSystem().getDisplayMetrics().density * dp;
        left_top=corner_1;
        left_bottom=corner_2;
        right_bottom=corner_3;
        right_top=corner_4;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return roundCrop(pool, toTransform);
    }

    private static Bitmap roundCrop(BitmapPool pool, Bitmap source) {
        if (source == null) {
            return null;
        }
        Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());

        canvas.drawRoundRect(rectF, radius, radius, paint);

        //哪个角不是圆角我再把你用矩形画出来
        if (!left_top) {
            canvas.drawRect(0, 0, radius, radius, paint);
        }
        if (!right_top) {
            canvas.drawRect(rectF.right - radius, 0, rectF.right, radius, paint);
        }
        if (!left_bottom) {
            canvas.drawRect(0, rectF.bottom - radius, radius, rectF.bottom, paint);
        }
        if (!right_bottom) {
            canvas.drawRect(rectF.right - radius, rectF.bottom - radius, rectF.right, rectF.bottom, paint);
        }
        return result;
    }

    public String getId() {
        return getClass().getName() + Math.round(radius);
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

    }
}

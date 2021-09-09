package com.zygame.mvp_project.util.glideUtil;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.zygame.mvp_project.util.DpiUtil;

/**
 * Glide设置图片
 *
 * @author admin
 */

public class ImageByGlide {
    /**
     * 设置图，不缓存
     *
     * @param pContext
     * @param uri      图片路径
     * @param iv       容器
     */
    public static void setImageNotCache(Context pContext, String uri, ImageView iv) {
        if (((Activity) pContext).isDestroyed()) {
            return;
        }

        Glide.with(pContext)
                .load(uri)
                .fitCenter()
                .skipMemoryCache(true)
                .into(iv);
    }


    /**
     * 设置图片
     *
     * @param pContext
     * @param uri      图片路径
     * @param iv       容器
     */
    public static void setImage(Context pContext, String uri, ImageView iv) {
        setImage(pContext, uri, iv, 0);
    }

    /**
     * 设置图片
     *
     * @param pContext
     * @param uri         图片路径
     * @param iv          容器
     * @param placeholder 默认底图
     */
    public static void setImage(Context pContext, String uri, ImageView iv, int placeholder) {
        if (((Activity) pContext).isDestroyed()) {
            return;
        }

        if (placeholder <= 0) {
            Glide.with(pContext)
                    .load(uri)
                    .override(iv.getWidth(),iv.getHeight())
                    .into(iv);
        } else {
            Glide.with(pContext)
                    .load(uri)
                    .override(iv.getWidth(),iv.getHeight())
                    .placeholder(ContextCompat.getDrawable(pContext, placeholder))
                    .into(iv);
        }
    }

    /**
     * 设置圆角图片
     *
     * @param pContext
     * @param uri      图片路径
     * @param iv       容器
     * @param radius   圆角大小
     */
    public static void setRoundImage(Context pContext, String uri, ImageView iv, float radius) {
        setRoundImage(pContext, uri, iv, radius, 0);
    }

    /**
     * 设置圆角图片
     *
     * @param pContext
     * @param id       图片id
     * @param iv       容器
     * @param radius   圆角大小
     */
    public static void setRoundImage(Context pContext, int id, ImageView iv, float radius) {
        setRoundImage(pContext, id, iv, radius, 0);
    }

    /**
     * 设置圆角图片
     *
     * @param pContext
     * @param id          图片id
     * @param iv          容器
     * @param radius      圆角大小
     * @param placeholder 默认底图
     */
    public static void setRoundImage(Context pContext, int id, ImageView iv, float radius, int placeholder) {
        if (((Activity) pContext).isDestroyed()) {
            return;
        }

        RoundedCornersTransform transform = new RoundedCornersTransform(pContext, DpiUtil.dipTopx(radius));
        transform.setNeedCorner(true, true, true, true);
        RequestOptions options = new RequestOptions().transform(new CenterCrop(), transform);

        if (placeholder <= 0) {
            Glide.with(pContext)
                    .load(id)
                    .centerCrop()
                    .apply(options)
                    .into(iv);
        } else {
            Glide.with(pContext)
                    .load(id)
                    .placeholder(ContextCompat.getDrawable(pContext, placeholder))
                    .centerCrop()
                    .apply(options)
                    .into(iv);
        }
    }

    /**
     * 设置圆角图片
     *
     * @param pContext
     * @param uri         图片路径
     * @param iv          容器
     * @param radius      圆角大小
     * @param placeholder 默认底图
     */
    public static void setRoundImage(Context pContext, String uri, ImageView iv, float radius, int placeholder) {
        if (((Activity) pContext).isDestroyed()) {
            return;
        }

        RoundedCornersTransform transform = new RoundedCornersTransform(pContext, DpiUtil.dipTopx(radius));
        transform.setNeedCorner(true, true, true, true);
        RequestOptions options = new RequestOptions().transform(new CenterCrop(), transform);

        if (placeholder <= 0) {
            Glide.with(pContext)
                    .load(uri)
                    .centerCrop()
                    .apply(options)
                    .into(iv);
        } else {
            Glide.with(pContext)
                    .load(uri)
                    .centerCrop()
                    .apply(options)
                    .placeholder(placeholder)
                    .into(iv);
        }
    }


    /**
     * 设置圆形图片
     *
     * @param pContext
     * @param uri         图片路径
     * @param iv          容器
     * @param placeholder 默认底图
     */
    public static void setCircleImage(final Context pContext, String uri, ImageView iv, final int placeholder) {
        setCircleImage(pContext, uri, iv, placeholder, false);
    }

    /**
     * 设置圆形图片
     *
     * @param pContext
     * @param id       图片路径
     * @param iv       容器
     */
    public static void setCircleImage(final Context pContext, int id, ImageView iv) {
        if (((Activity) pContext).isDestroyed()) {
            return;
        }

        Glide.with(pContext)
                .load(id)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(iv);
    }

    /**
     * 设置圆形图片
     *
     * @param pContext
     * @param uri             图片路径
     * @param iv              容器
     * @param placeholder     默认底图
     * @param skipMemoryCache 是否缓存
     */
    public static void setCircleImage(final Context pContext, String uri, ImageView iv, int placeholder, boolean skipMemoryCache) {
        if (((Activity) pContext).isDestroyed()) {
            return;
        }

        if (placeholder <= 0) {
            if (skipMemoryCache) {
                Glide.with(pContext)
                        .load(uri)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(iv);
            } else {
                Glide.with(pContext)
                        .load(uri)
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(iv);
            }
        } else {
            if (skipMemoryCache) {
                Glide.with(pContext)
                        .load(uri)
                        .placeholder(ContextCompat.getDrawable(pContext, placeholder))
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(iv);
            } else {
                Glide.with(pContext)
                        .load(uri)
                        .placeholder(ContextCompat.getDrawable(pContext, placeholder))
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(iv);
            }
        }
    }

    /**
     * 设置头像
     *
     * @param pContext
     * @param uri
     * @param iv
     */
    public static void setAvatarImage(Context pContext, String uri, ImageView iv) {
        if (((Activity) pContext).isDestroyed()) {
            return;
        }
        Glide.with(pContext)
                .load(uri)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(iv);
    }


    /**
     * 设置圆角图片
     *
     * @param pContext
     * @param uri         图片路径
     * @param iv          容器
     * @param placeholder 默认底图
     * @param radius      圆角大小
     * @param c1          左上
     * @param c2          左下
     * @param c3          右上
     * @param c4          右下
     */
    public static void setCornerImage(Context pContext, String uri, ImageView iv, int placeholder, float radius, boolean c1, boolean c2, boolean c3, boolean c4) {
        if (((Activity) pContext).isDestroyed()) {
            return;
        }

        if (placeholder <= 0) {
            Glide.with(pContext)
                    .load(uri)
                    .transform(new GlideRoundTransform(pContext, (int) radius, c1, c2, c3, c4))
                    .into(iv);
        } else {
            Glide.with(pContext)
                    .load(uri)
                    .placeholder(new ColorDrawable(ContextCompat.getColor(pContext, placeholder)))
                    .transform(new GlideRoundTransform(pContext, (int) radius, c1, c2, c3, c4))
                    .into(iv);
        }
    }
}

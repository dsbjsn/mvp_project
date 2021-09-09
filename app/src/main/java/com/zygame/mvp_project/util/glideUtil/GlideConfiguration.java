package com.zygame.mvp_project.util.glideUtil;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

/**
 * @author admin
 */

@GlideModule
public class GlideConfiguration extends AppGlideModule {
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        //自定义缓存目录，磁盘缓存给150M 另外一种设置缓存方式
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, "GlideImgCache", 150 * 1024 * 1024));
        //配置图片缓存格式 默认格式为8888
        builder.setDefaultRequestOptions(RequestOptions.formatOf(DecodeFormat.PREFER_RGB_565));
    }
}

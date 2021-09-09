package com.zygame.mvp_project.google.admob;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.MobileAds;

import java.util.List;

/**
 * Created on 2021/9/7 14
 *
 * @author xjl
 */
public class AdHelper {
    private static final String TAG = "AdHelper";
    private static final AdHelper sAdHelper = new AdHelper();

    public static AdHelper getInstance() {
        return sAdHelper;
    }

    /**
     * 是否是测试模式
     */
    public static final boolean Test = true;
    /**
     * 是否预加载广告
     */
    public static final boolean PreloadAd = true;


    /**
     * 广告位信息
     */
    private AdInfoEntity mAdInfoEntity = null;

    public AdHelper setAdInfo(AdInfoEntity pAdInfo) {
        mAdInfoEntity = pAdInfo;
        return this;
    }

    public void setAdInfoEntity(AdInfoEntity pAdInfoEntity) {
        mAdInfoEntity = pAdInfoEntity;
    }

    /**
     * 是否能够播放广告
     */
    private boolean canShowAd = true;

    public boolean isCanShowAd() {
        return canShowAd;
    }

    public AdHelper setCanShowAd(boolean pCanShowAd) {
        canShowAd = pCanShowAd;
        return this;
    }

    public void init(Context pContext) {
        if (mAdInfoEntity == null) {
            Log.e(TAG, "广告信息为空");
            return;
        }

        if (!canShowAd) {
            Log.e(TAG, "不能播放广告，无需初始化");
            return;
        }

        MobileAds.initialize(pContext, initializationStatus -> {
        });

        Log.e(TAG, "AdMob初始化完成");
    }


    /**
     * 获取广告位id列表
     *
     * @return
     */
    public List<String> getAdIds(AdType pType) {
        if (pType == AdType.Banner) {
            if (mAdInfoEntity == null || mAdInfoEntity.getBanner_id() == null || mAdInfoEntity.getBanner_id().isEmpty()) {
                return null;
            } else {
                return mAdInfoEntity.getBanner_id();
            }
        } else if (pType == AdType.Interstitial) {
            if (mAdInfoEntity == null || mAdInfoEntity.getInterstitial_id() == null || mAdInfoEntity.getInterstitial_id().isEmpty()) {
                return null;
            } else {
                return mAdInfoEntity.getInterstitial_id();
            }
        } else if (pType == AdType.Reward) {
            if (mAdInfoEntity == null || mAdInfoEntity.getReward_id() == null || mAdInfoEntity.getReward_id().isEmpty()) {
                return null;
            } else {
                return mAdInfoEntity.getReward_id();
            }
        } else if (pType == AdType.Native) {
            if (mAdInfoEntity == null || mAdInfoEntity.getNative_id() == null || mAdInfoEntity.getNative_id().isEmpty()) {
                return null;
            } else {
                return mAdInfoEntity.getNative_id();
            }
        } else {
            return null;
        }
    }
}

package com.zygame.mvp_project.google.admob;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.zygame.mvp_project.R;
import com.zygame.mvp_project.google.firebase.FirebaseEventUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * google广告工具类
 *
 * @author xjl
 * @time 2020/12/19 9:40
 */
public class AdUtil {
    private static final String TAG = "AdUtil";
    private static final AdUtil S_AD_UTILS = new AdUtil();

    private AdUtil() {
    }

    public static AdUtil getInstance() {
        return S_AD_UTILS;
    }


    /**
     * 最大加载时间
     */
    public static final int LoadingMaxTime = 5000;
    /**
     * 加载失败次数等级
     */
    public final int[] FailTimesLevel = new int[]{20, 50, 100, Integer.MAX_VALUE};
    /**
     * 加载间隔时间等级
     */
    public final int[] SpacingLevel = new int[]{200, 1000, 3000, 5000};


    public void init(AppCompatActivity pActivity) {
//        loadBannerAd(pActivity);
//        loadRewardVideo(pActivity);
        loadInterstitialAd(pActivity);
        loadNativeAd(pActivity);
    }

    /**
     * 获取加载间隔时间
     *
     * @param failTimes 失败次数
     * @return 下次加载间隔时间
     */
    private int getSpacingTime(int failTimes) {
        int spacingTime = 0;
        for (int i = 0; i < FailTimesLevel.length; i++) {
            int level_fail_times = FailTimesLevel[i];
            if (failTimes < level_fail_times) {
                spacingTime = SpacingLevel[i];
                break;
            }
        }
        return spacingTime;
    }

    /***************************************************
     *                  横幅广告
     ***************************************************/

    private AdView mBannerAdView;
    private int indexBanner = 0;
    private boolean loadingBannerAd = false;

    /**
     * 加载banner
     * 测试id：
     *
     * @param pActivity
     */
    private void loadBannerAd(FragmentActivity pActivity) {
        if (!AdHelper.getInstance().isCanShowAd() || loadingBannerAd) {
            Log.e(TAG, "loadBannerAd 跳过");
            return;
        }

        if (AdHelper.getInstance().getAdIds(AdType.Banner) == null) {
            Log.e(TAG, "initBanner 广告位为空");
            return;
        }

        mBannerAdView = new AdView(pActivity);
        mBannerAdView.setAdSize(AdSize.BANNER);

        String bannerId = AdHelper.Test ? "ca-app-pub-3940256099942544/6300978111"
                : AdHelper.getInstance().getAdIds(AdType.Banner).get(indexBanner);

        mBannerAdView.setAdUnitId(bannerId);
        mBannerAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Log.e(TAG, "BannerAd onAdClosed");
            }

            @Override
            public void onAdFailedToLoad(LoadAdError pLoadAdError) {
                super.onAdFailedToLoad(pLoadAdError);
                Log.e(TAG, "BannerAd 加载失败" + pLoadAdError);
                indexBanner++;
                if (indexBanner >= AdHelper.getInstance().getAdIds(AdType.Banner).size()) {
                    indexBanner = 0;
                }
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Log.e(TAG, "BannerAd onAdOpened");
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.e(TAG, "BannerAd 加载成功: " + mBannerAdView.getResponseInfo().getMediationAdapterClassName());
                indexBanner++;
                if (indexBanner >= AdHelper.getInstance().getAdIds(AdType.Banner).size()) {
                    indexBanner = 0;
                }
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                Log.e(TAG, "BannerAd onAdClicked");
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                Log.e(TAG, "BannerAd onAdImpression");
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        mBannerAdView.loadAd(adRequest);
    }

    /**
     * 展示banner
     *
     * @param pActivity
     * @param pFrameLayout banner 容器
     */
    public void showBanner(FragmentActivity pActivity, FrameLayout pFrameLayout) {
        if (!AdHelper.getInstance().isCanShowAd() || pFrameLayout == null) {
            Log.e(TAG, "banner 跳过");
            return;
        }

        if (AdHelper.getInstance().getAdIds(AdType.Banner) == null) {
            Log.e(TAG, "banner 广告位为空");
        }

        if (mBannerAdView == null) {
            loadBannerAd(pActivity);
        } else {
            ViewGroup lViewGroup = (ViewGroup) mBannerAdView.getParent();
            if (lViewGroup != null) {
                lViewGroup.removeAllViews();
            }
        }

        pFrameLayout.setVisibility(View.VISIBLE);
        pFrameLayout.removeAllViews();
        pFrameLayout.addView(mBannerAdView);
    }

    /**
     * 隐藏banner
     */
    public void hideBanner(FrameLayout pFrameLayout) {
        if (pFrameLayout != null) {
            pFrameLayout.setVisibility(View.GONE);
        }
    }

    /***************************************************
     *                  激励视频
     ***************************************************/
    private final Handler mHandleReward = new Handler();
    /**
     * 激励视频实例
     */
    private RewardedAd mRewardedAd = null;
    /**
     * 是否播放激励视频
     */
    private boolean showReward = false;
    /**
     * 激励视频正在播放
     */
    private boolean showingReward = false;
    /**
     * 激励广告正在加载
     */
    private boolean loadingReward = false;
    /**
     * 激励视频是否获得奖励回调接口
     */
    private RewardAdListener mRewardAdListener = null;
    /**
     * 是否获得奖励
     */
    boolean hadGetReward = false;
    /**
     * 激励视频广告当前请求的下标
     */
    private int indexReward = 0;
    /**
     * 激励广告加载失败次数，
     */
    private int failTimesReward = 0;
    /**
     * 加载失败后延时请求时间
     */
    private int spacingReward = 0;

    /**
     * 显示激励视频
     *
     * @param pActivity
     * @param pRewardAdListener 奖励回调接口
     */
    public void showRewardVideo(FragmentActivity pActivity, RewardAdListener pRewardAdListener) {
        showDialog(pActivity);
        mRewardAdListener = pRewardAdListener;
        showReward = true;
        showingReward = false;
        hadGetReward = false;

        if (!AdHelper.getInstance().isCanShowAd()) {
            Log.e(TAG, "showRewardVideo 跳过");
            hadGetReward = true;
            resetRewardAd(pActivity);
            return;
        }

        mHandleReward.postDelayed(() -> resetRewardAd(pActivity), LoadingMaxTime);

        if (mRewardedAd != null) {
            Log.e(TAG, "showRewardVideo 已加载，直接播放");
            rewardAdShowAction(pActivity);
        } else {
            loadRewardVideo(pActivity);
        }
    }


    /**
     * 加载激励视频
     * 测试id:ca-app-pub-3940256099942544/5224354917
     *
     * @param pActivity
     */
    private void loadRewardVideo(FragmentActivity pActivity) {
        if (!AdHelper.getInstance().isCanShowAd() || loadingReward) {
            Log.e(TAG, "loadRewardVideo 跳过");
            return;
        }

        if (AdHelper.getInstance().getAdIds(AdType.Reward) == null) {
            Log.e(TAG, "loadRewardVideo 广告位为空");
            return;
        }

        loadingReward = true;
        mRewardedAd = null;

        String rewardAdId = AdHelper.Test ? "ca-app-pub-3940256099942544/5224354917"
                : AdHelper.getInstance().getAdIds(AdType.Reward).get(indexReward);

        Log.e(TAG, "loadRewardVideo rewardAdId=" + rewardAdId);

        RewardedAd.load(pActivity, rewardAdId,
                new AdRequest.Builder().build(),
                new RewardedAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull RewardedAd pRewardedAd) {
                        super.onAdLoaded(pRewardedAd);
                        mRewardedAd = pRewardedAd;
                        loadingReward = false;
                        Log.e(TAG, "激励 加载成功" + mRewardedAd.getResponseInfo().getMediationAdapterClassName());

                        if (showReward) {
                            rewardAdShowAction(pActivity);
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError pLoadAdError) {
                        super.onAdFailedToLoad(pLoadAdError);
                        Log.e(TAG, "激励 加载失败 " + pLoadAdError.getMessage());

                        failTimesReward += 1;
                        spacingReward = getSpacingTime(failTimesReward);

                        new Handler().postDelayed(() -> {
                            loadingReward = false;
                            indexReward += 1;
                            if (indexReward >= AdHelper.getInstance().getAdIds(AdType.Reward).size()) {
                                indexReward = 0;
                            }
                            loadRewardVideo(pActivity);
                        }, spacingReward);
                    }
                });
    }


    /**
     * 播放激励视频
     *
     * @param pActivity
     */
    private void rewardAdShowAction(FragmentActivity pActivity) {
        mRewardedAd.setOnPaidEventListener(pAdValue -> postAdjustAdInfo(pAdValue, "Reward", mRewardedAd.getAdUnitId()));

        mRewardedAd.show(pActivity, pRewardItem -> hadGetReward = true);

        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError pAdError) {
                super.onAdFailedToShowFullScreenContent(pAdError);
                Log.e(TAG, "激励 onAdFailedToShowFullScreenContent");
                mRewardedAd = null;
                resetRewardAd(pActivity);
            }

            @Override
            public void onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent();
                Log.e(TAG, "激励 onAdShowedFullScreenContent");
                rewardAdShowing();
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent();
                Log.e(TAG, "激励 onAdDismissedFullScreenContent");
                mRewardedAd = null;
                resetRewardAd(pActivity);
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                Log.e(TAG, "激励 onAdImpression");
            }
        });
    }


    /**
     * 展示激励视频时的其他动作
     */
    private void rewardAdShowing() {
        Log.e(TAG, "激励 显示");
        showingReward = true;
        mHandleReward.removeCallbacksAndMessages(null);
    }


    /**
     * 重置激励视频
     */
    private void resetRewardAd(FragmentActivity pActivity) {
        Log.e(TAG, "激励 重置");
        if (mRewardAdListener != null) {
            mRewardAdListener.getAward(hadGetReward);
            mRewardAdListener = null;
        }

        hadGetReward = false;
        indexReward = 0;
        showReward = false;
        showingReward = false;
        mHandleReward.removeCallbacksAndMessages(null);
        hideDialog();

        if (AdHelper.getInstance().isCanShowAd() && AdHelper.PreloadAd && mRewardedAd == null) {
            Log.e(TAG, "激励 预加载");
            loadRewardVideo(pActivity);
        } else {
            if (!AdHelper.getInstance().isCanShowAd()) {
                Log.e(TAG, "激励 不做预加载：canShowAd=false");
            } else if (!AdHelper.PreloadAd) {
                Log.e(TAG, "激励 不做预加载：PreloadAd = false");
            } else if (mRewardedAd != null) {
                Log.e(TAG, "激励 不做预加载：RewardedAd != null");
            } else {
                Log.e(TAG, "激励 不做预加载：未知原因");
            }
        }
    }


    /***************************************************
     *                  插屏激励视频
     ***************************************************/
    private final Handler mHandleItReward = new Handler();
    /**
     * 插屏激励视频实例
     */
    private RewardedInterstitialAd mItRewardedAd = null;
    /**
     * 是否播放插屏激励视频
     */
    private boolean showItReward = false;
    /**
     * 插屏激励视频正在播放
     */
    private boolean showingItReward = false;
    /**
     * 插屏激励广告正在加载
     */
    private boolean loadingItReward = false;
    /**
     * 激励视频广告当前请求的下标
     */
    private int indexItReward = 0;
    /**
     * 插屏激励广告加载失败次数，
     */
    private int failTimesItReward = 0;
    /**
     * 加载失败后延时请求时间
     */
    private int spacingItReward = 0;


    /**
     * 显示插屏激励视频广告
     *
     * @param pActivity
     * @param pRewardAdListener
     */
    public void showItRewardAd(FragmentActivity pActivity, RewardAdListener pRewardAdListener) {
        Log.e(TAG, "插屏激励 显示");
        showDialog(pActivity);

        mRewardAdListener = pRewardAdListener;
        hadGetReward = false;

        showItReward = true;
        showingItReward = false;


        if (!AdHelper.getInstance().isCanShowAd()) {
            Log.e(TAG, "showItRewardAd 跳过:canShowAd=false");
            resetItRewardAd(pActivity);
            return;
        }

        mHandleItReward.postDelayed(() -> resetRewardAd(pActivity), LoadingMaxTime);

        if (mItRewardedAd != null) {
            Log.e(TAG, "插屏激励 已加载，直接播放");
            itRewardShowAction(pActivity);
        } else {
            loadItRewardAd(pActivity);
        }
    }

    /**
     * 加载插屏激励视频
     *
     * @param pActivity
     */
    private void loadItRewardAd(FragmentActivity pActivity) {
        if (!AdHelper.getInstance().isCanShowAd() || loadingItReward) {
            Log.e(TAG, "插屏激励 不能加载");
            return;
        }

        if (AdHelper.getInstance().getAdIds(AdType.ItReward) == null) {
            Log.e(TAG, "插屏激励 广告位为空");
            return;
        }

        loadingItReward = true;
        mItRewardedAd = null;

        String itRewardAdId = AdHelper.Test ? "ca-app-pub-3940256099942544/5354046379"
                : AdHelper.getInstance().getAdIds(AdType.ItReward).get(indexItReward);

        RewardedInterstitialAd.load(pActivity, itRewardAdId,
                new AdRequest.Builder().build(), new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(RewardedInterstitialAd ad) {
                        Log.e(TAG, "插屏激励 加载成功");

                        loadingItReward = false;
                        mItRewardedAd = ad;

                        if (showItReward) {
                            itRewardShowAction(pActivity);
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        Log.e(TAG, "插屏激励 加载失败");

                        failTimesItReward += 1;
                        spacingItReward = getSpacingTime(failTimesItReward);

                        new Handler().postDelayed(() -> {
                            loadingItReward = false;
                            indexItReward = 0;
                            loadItRewardAd(pActivity);
                        }, spacingItReward);
                    }
                });
    }

    /**
     * 播放插屏激励
     *
     * @param pActivity
     */
    private void itRewardShowAction(FragmentActivity pActivity) {
        mItRewardedAd.setOnPaidEventListener(pAdValue -> postAdjustAdInfo(pAdValue, "InterstitialReward", mItRewardedAd.getAdUnitId()));

        mItRewardedAd.show(pActivity, pRewardItem -> hadGetReward = true);
        mItRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                Log.e(TAG, "插屏激励 播放失败");
                mItRewardedAd = null;
                resetItRewardAd(pActivity);
            }

            @Override
            public void onAdShowedFullScreenContent() {
                Log.e(TAG, "插屏激励 开始播放");
                itRewardAdShowing();
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                Log.e(TAG, "插屏激励 关闭");
                mItRewardedAd = null;
                resetItRewardAd(pActivity);
            }
        });
    }

    /**
     * 插屏激励视频展示
     */
    private void itRewardAdShowing() {
        Log.e(TAG, "插屏激励 显示");
        showingItReward = true;
        mHandleItReward.removeCallbacksAndMessages(null);
    }

    private void resetItRewardAd(FragmentActivity pActivity) {
        Log.e(TAG, "插屏激励 重置");
        if (mRewardAdListener != null) {
            mRewardAdListener.getAward(hadGetReward);
            mRewardAdListener = null;
        }

        indexItReward = 0;

        showItReward = false;
        showingItReward = false;
        hadGetReward = false;

        mHandleItReward.removeCallbacksAndMessages(null);

        hideDialog();

        if (!AdHelper.getInstance().isCanShowAd() && AdHelper.PreloadAd && mItRewardedAd == null) {
            Log.e(TAG, "插屏激励 预加载");
            loadItRewardAd(pActivity);
        } else {
            if (!AdHelper.getInstance().isCanShowAd()) {
                Log.e(TAG, "插屏激励 不做预加载：canShowAd=false ");
            } else if (!AdHelper.PreloadAd) {
                Log.e(TAG, "插屏激励 不做预加载：PreloadAd = false");
            } else if (mItRewardedAd != null) {
                Log.e(TAG, "插屏激励 不做预加载：ItRewardedAd != null");
            } else {
                Log.e(TAG, "插屏激励 不做预加载：未知原因");
            }
        }
    }


    /***************************************************
     *                  原生广告
     ***************************************************/
    /**
     * 缓存个数
     */
    private int nativeCacheNumber = 0;
    /**
     * 缓存的原生广告集合
     */
    public static List<NativeAd> mListNative;
    /**
     * 是否正在加载原生广告
     */
    private static boolean loadingNative = false;
    /**
     * 原生广告当前请求的下标
     */
    private int indexNative = 0;
    /**
     * 原生广告加载失败次数，
     */
    private int failTimesNative = 0;
    /**
     * 加载失败后延时请求时间
     */
    private int spacingNative = 0;

    /**
     * 加载原生广告
     *
     * @param pContext
     * @return
     */
    public synchronized NativeAd getNativeAd(Context pContext) {
        Log.e(TAG, "原生 getNativeAd");
        if (mListNative == null || mListNative.isEmpty()) {
            loadNativeAd(pContext);
            return null;
        } else {
            NativeAd lNativeAd = mListNative.get(0);
            mListNative.remove(0);

            FirebaseEventUtils.sendEvent("ad_native_show");

            if (mListNative.isEmpty()) {
                loadNativeAd(pContext);
            }
            return lNativeAd;
        }
    }

    /**
     * 初始化原生广告
     */
    private void loadNativeAd(Context pContext) {
        if (!AdHelper.getInstance().isCanShowAd() || loadingNative) {
            Log.e(TAG, "原生 loadNativeAd 跳过");
            return;
        }

        if (AdHelper.getInstance().getAdIds(AdType.Native) == null) {
            Log.e(TAG, "原生 loadNativeAd 广告位为空");
            return;
        }

        loadingNative = true;

        String nativeId = AdHelper.Test ? "ca-app-pub-3940256099942544/2247696110"
                : AdHelper.getInstance().getAdIds(AdType.Native).get(indexNative);

        Log.e(TAG, "原生 开始加载 " + indexNative + " " + nativeId);

        FirebaseEventUtils.sendEvent("ad_native_load");
        AdLoader lBuild = new AdLoader.Builder(pContext, nativeId)
                .forNativeAd(pNativeAd -> {
                    FirebaseEventUtils.sendEvent("ad_it_native_success");
                    loadingNative = false;

                    pNativeAd.setOnPaidEventListener(pAdValue -> postAdjustAdInfo(pAdValue, "Native", nativeId));

                    nativeCacheNumber += 1;
                    Log.e(TAG, "原生 加载完成 " + nativeCacheNumber);

                    if (mListNative == null) {
                        mListNative = new ArrayList<>();
                    }

                    mListNative.add(pNativeAd);

//                    EventBus.getDefault().post(new MessageEvent(EventBusCode.REFRESH_NATIVE));
                    indexNative = 0;
                }).withAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError pLoadAdError) {
                        super.onAdFailedToLoad(pLoadAdError);
                        Log.e(TAG, "原生 加载失败");
                        FirebaseEventUtils.sendEvent("ad_it_native_fail");

                        failTimesNative += 1;
                        spacingNative = getSpacingTime(failTimesNative);

                        new Handler().postDelayed(() -> {
                            loadingNative = false;

                            indexNative += 1;
                            if (indexNative >= AdHelper.getInstance().getAdIds(AdType.Native).size()) {
                                indexNative = 0;
                            }
                            loadNativeAd(pContext);
                        }, spacingNative);
                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                    }
                })
                .build();

        lBuild.loadAds(new AdRequest.Builder().build(), 3);
    }


    /***************************************************
     *                  插屏广告
     ***************************************************/
    private final Handler mHandleIt = new Handler();
    /**
     * 插屏广告实例
     */
    private InterstitialAd mItAd;
    /**
     * 是否播放插屏广告
     */
    private boolean showIt = false;
    /**
     * 插屏广告正在播放
     */
    private boolean showingIt = false;
    /**
     * 插屏广告正在加载
     */
    private boolean loadingIt = false;
    /**
     * 回调接口
     */
    private InterstitialAdListener mInterstitialAdListener = null;

    /**
     * 插屏广告当前请求的下标
     */
    private int indexIt = 0;
    /**
     * 插屏广告加载失败次数，
     */
    private int failTimesIt = 0;
    /**
     * 加载失败后延时请求时间
     */
    private int spacingIt = 0;


    /**
     * 显示插屏广告
     *
     * @param pActivity
     * @param pInterstitialAdListener 监听
     * @param isShowDialog            是否显示加载弹窗
     */
    public void showInterstitialAd(FragmentActivity pActivity, InterstitialAdListener pInterstitialAdListener,
                                   boolean isShowDialog) {
        mInterstitialAdListener = pInterstitialAdListener;
        showIt = true;

        if (!AdHelper.getInstance().isCanShowAd()) {
            Log.e(TAG, "showInterstitialAd 跳过");
            resetInterstitialAd(pActivity);
            return;
        }

        if (isShowDialog) {
            showDialog(pActivity);
        }

        mHandleIt.postDelayed(() -> resetInterstitialAd(pActivity), LoadingMaxTime);

        if (mItAd != null) {
            Log.e(TAG, "插屏 有缓存 已加载 直接播放");
            itAdShowAction(pActivity);
        } else {
            loadInterstitialAd(pActivity);
        }
    }

    /**
     * 取消播放插屏广告
     * 正在播放时不做处理
     * 广告加载中，加载完成后不播放
     */
    public void cancelShowInterstitial() {
        showIt = false;
    }

    /**
     * 加载插屏广告
     *
     * @param pActivity
     */
    private void loadInterstitialAd(FragmentActivity pActivity) {
        if (!AdHelper.getInstance().isCanShowAd() || loadingIt) {
            Log.e(TAG, "loadInterstitialAd 跳过");
            return;
        }

        if (AdHelper.getInstance().getAdIds(AdType.Interstitial) == null) {
            Log.e(TAG, "loadInterstitialAd 广告位为空");
            return;
        }

        loadingIt = true;
        mItAd = null;

        FirebaseEventUtils.sendEvent("ad_it_load");

        String itAdId = AdHelper.Test ? "ca-app-pub-3940256099942544/8691691433"
                : AdHelper.getInstance().getAdIds(AdType.Interstitial).get(indexIt);

        InterstitialAd.load(pActivity,
                itAdId,
                new AdRequest.Builder().build(),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd pInterstitialAd) {
                        super.onAdLoaded(pInterstitialAd);
                        Log.e(TAG, "插屏 加载成功 " + pInterstitialAd.getResponseInfo().getMediationAdapterClassName());
                        FirebaseEventUtils.sendEvent("ad_it_load_success");

                        loadingIt = false;
                        mItAd = pInterstitialAd;

                        if (showIt) {
                            itAdShowAction(pActivity);
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError pLoadAdError) {
                        super.onAdFailedToLoad(pLoadAdError);
                        Log.e(TAG, "插屏 加载失败 " + pLoadAdError.getMessage());
                        FirebaseEventUtils.sendEvent("ad_it_load_fail");

                        failTimesIt += 1;
                        spacingIt = getSpacingTime(failTimesIt);

                        new Handler().postDelayed(() -> {
                            loadingIt = false;
                            indexIt += 1;
                            if (indexIt >= AdHelper.getInstance().getAdIds(AdType.Interstitial).size()) {
                                indexIt = 0;
                            }
                            loadInterstitialAd(pActivity);
                        }, spacingIt);
                    }
                });
    }


    /**
     * 播放插屏广告
     *
     * @param pActivity
     */
    private void itAdShowAction(FragmentActivity pActivity) {
        mItAd.setOnPaidEventListener(pAdValue -> postAdjustAdInfo(pAdValue, "Interstitial", mItAd.getAdUnitId()));

        mItAd.show(pActivity);
        mItAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError pAdError) {
                super.onAdFailedToShowFullScreenContent(pAdError);
                Log.e(TAG, "插屏 onAdFailedToShowFullScreenContent ");
                mItAd = null;
                resetInterstitialAd(pActivity);
            }

            @Override
            public void onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent();
                Log.e(TAG, "插屏 onAdShowedFullScreenContent ");
                FirebaseEventUtils.sendEvent("ad_it_show");
                interstitialAdShowing();
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent();
                Log.e(TAG, "插屏 onAdDismissedFullScreenContent ");
                mItAd = null;
                resetInterstitialAd(pActivity);
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                Log.e(TAG, "插屏 onAdImpression");
            }
        });
    }


    /**
     * 插屏广告正在播放中
     */
    private void interstitialAdShowing() {
        Log.e(TAG, "插屏 显示");
        showingIt = true;
        mHandleIt.removeCallbacksAndMessages(null);
        if (mInterstitialAdListener != null) {
            mInterstitialAdListener.show();
        }
    }

    /**
     * 插屏结束时能否显示悬浮窗
     */
    public boolean canShowFloatWhenCloseInterstitial = false;

    /**
     * 重置插屏广告
     *
     * @param pActivity
     */
    private void resetInterstitialAd(FragmentActivity pActivity) {
        Log.e(TAG, "插屏 重置");

        if (mInterstitialAdListener != null) {
            mInterstitialAdListener.end();
            mInterstitialAdListener = null;
        }

        indexIt = 0;
        showIt = false;
        showingIt = false;
        mHandleIt.removeCallbacksAndMessages(null);

        hideDialog();

        if (!AdHelper.getInstance().isCanShowAd() && AdHelper.PreloadAd && mItAd == null) {
            Log.e(TAG, "插屏 预加载");
            loadInterstitialAd(pActivity);
        } else {
            if (!AdHelper.getInstance().isCanShowAd()) {
                Log.e(TAG, "插屏 不做预加载：canShowAd=false");
            } else if (mItAd != null) {
                Log.e(TAG, "插屏 不做预加载：InterstitialAd != null");
            } else if (!AdHelper.PreloadAd) {
                Log.e(TAG, "插屏 不做预加载：PreloadAd = false");
            } else {
                Log.e(TAG, "插屏 不做预加载：未知原因");
            }
        }
    }

    private static Dialog mDialog;

    /**
     * 显示加载弹窗
     *
     * @param pActivity
     */
    private void showDialog(FragmentActivity pActivity) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
        mDialog = new Dialog(pActivity);
        mDialog.setContentView(R.layout.loading_dialog);
        mDialog.setCancelable(false);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setOnDismissListener(dialog -> {
            if (showReward && !showingReward) {
                resetRewardAd(pActivity);
            } else if (showIt && !showingIt) {
                resetInterstitialAd(pActivity);
            } else if (showItReward && !showingItReward) {
                resetItRewardAd(pActivity);
            }
        });
        mDialog.show();
    }

    /**
     * 隐藏加载弹窗
     */
    private void hideDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    private void postAdjustAdInfo(AdValue pAdValue, String pPlacement, String pUnitId) {
//        double price = (double) pAdValue.getValueMicros() / 1000000;
//        String currencyCode = pAdValue.getCurrencyCode();
//        LogUtil.d("Adjust 广告上报：" + price + " 平台：" + pPlacement);
//        if (Adjust.isEnabled()) {
//            AdjustAdRevenue adjustAdRevenue = new AdjustAdRevenue(AdjustConfig.AD_REVENUE_ADMOB);
//            adjustAdRevenue.setRevenue(price, currencyCode);
//            adjustAdRevenue.setAdRevenueUnit(pUnitId);
//            adjustAdRevenue.setAdRevenuePlacement(pPlacement);
//
//            Adjust.trackAdRevenue(adjustAdRevenue);
//        }
    }
}

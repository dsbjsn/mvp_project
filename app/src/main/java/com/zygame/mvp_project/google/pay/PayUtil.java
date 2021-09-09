package com.zygame.mvp_project.google.pay;

import android.app.Application;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.zygame.mvp_project.R;
import com.zygame.mvp_project.google.firebase.FirebaseEventUtils;
import com.zygame.mvp_project.util.ToastUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created on 2020/12/18 10
 * 饿汉模式单例类
 *
 * @author xjl
 */
public class PayUtil implements PurchasesUpdatedListener {
    private static final String TAG = "PayUtil";

    private static final PayUtil S_PAY_UTIL = new PayUtil();

    public static PayUtil getInstance() {
        return S_PAY_UTIL;
    }

    private PayUtil() {
    }

    private Application mApplication;

    /**
     * google billing实例
     */
    private BillingClient billingClient;

    /**
     * 是否连接到了google 服务
     */
    private boolean hadConnectGooglePlay = false;

    /**
     * 订阅商品列表
     */
    private final List<String> subIds = Collections.singletonList(SubType.DY_1.getValue());

    /**
     * 订阅商品信息列表
     */
    private List<SkuDetails> mSkuDetailsList = new ArrayList<>();


    public boolean isHadConnectGooglePlay() {
        return hadConnectGooglePlay;
    }


    public List<SkuDetails> getSkuDetailsList() {
        return mSkuDetailsList;
    }


    private CheckVipCallBack mCheckVipCallBack;

    /**
     * google 支付初始化
     *
     * @param pApplication
     * @param pCheckVipCallBack
     */
    public void init(Application pApplication, CheckVipCallBack pCheckVipCallBack) {
        Log.i(TAG, "开始初始化");
        mCheckVipCallBack = pCheckVipCallBack;
        mApplication = pApplication;
        billingClient = BillingClient.newBuilder(pApplication).enablePendingPurchases().setListener(this).build();

        connectGooglePay();
    }

    /**
     * 连接google支付服务
     */
    private void connectGooglePay() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.i(TAG, "服务连接成功");
                    hadConnectGooglePlay = true;
                    //连接google服务成功后，查询商品列表
                    querySkuList(subIds, true);
                    //连接google服务成功后，查询订单
                    queryOrder(false, OrderType.SUB);
                } else {
                    new Handler().postDelayed(() -> connectGooglePay(), 1000);
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                new Handler().postDelayed(() -> connectGooglePay(), 1000);
                if (mCheckVipCallBack != null) {
                    mCheckVipCallBack.isVip(false);
                }
            }
        });
    }


    /**
     * 查询订单
     *
     * @param showTips   是否提升toast 查询结果
     * @param pOrderType 是否是订阅类型
     */
    public void queryOrder(boolean showTips, OrderType pOrderType) {
        if (!hadConnectGooglePlay) {
            ToastUtil.showToast(mApplication.getString(R.string.google_service_is_not_connected));
            return;
        }

        billingClient.queryPurchasesAsync(pOrderType.isSub() ? BillingClient.SkuType.SUBS : BillingClient.SkuType.INAPP, (pBillingResult, pList) -> {
            if (pBillingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                if (!pList.isEmpty()) {
                    for (final Purchase lPurchase : pList) {
                        Log.i(TAG, "订单列表 :" + lPurchase.toString());

                        /*是订阅订单，去进行确认*/
                        if (pOrderType.isSub()) {
                            acknowledged(lPurchase);
                        }
                        /*是消耗商品订单，去进行消耗*/
                        else {
                            if (lPurchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                                consumeOrder(lPurchase, 504);
                            } else {
                                if (showTips) {
                                    ToastUtil.showToast(mApplication.getString(R.string.toast_consume_pending));
                                }
                            }
                        }
                    }
                } else {
                    Log.i(TAG, "订单列表为空");
                    if (showTips) {
                        ToastUtil.showToast(mApplication.getString(R.string.toast_not_consume_order));
                    }

                    if (mCheckVipCallBack != null) {
                        mCheckVipCallBack.isVip(false);
                    }
                }
            }
        });
    }


    /**
     * 查询商品列表
     */
    public void querySkuList(List<String> pStrings, boolean isDy) {
        if (!hadConnectGooglePlay) {
            ToastUtil.showToast(mApplication.getString(R.string.google_service_is_not_connected));
            return;
        }

        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(pStrings).setType(isDy ? BillingClient.SkuType.SUBS : BillingClient.SkuType.INAPP);

        //查询商品信息
        billingClient.querySkuDetailsAsync(params.build(),
                (billingResult, skuDetailsList) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                        mSkuDetailsList.clear();
                        mSkuDetailsList.addAll(skuDetailsList);
                        for (SkuDetails lDetails : skuDetailsList) {
                            Log.d(TAG, "商品列表 SkuDetails=" + lDetails);
                        }
                    } else {
                        Log.d(TAG, "商品列表查询失败");
                        new Handler().postDelayed(() -> querySkuList(pStrings, isDy), 1000);
                    }
                });
    }


    /**
     * 发起支付
     *
     * @param pActivity
     * @param id        商品id
     * @param isDy      是否是订阅
     */
    public void pay(AppCompatActivity pActivity, String id, boolean isDy) {
        if (!hadConnectGooglePlay) {
            ToastUtil.showToast(pActivity.getString(R.string.google_service_is_not_connected));
            return;
        }

        showDialog(pActivity);

        List<String> skuList = new ArrayList<>();
        skuList.add(id);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(isDy ? BillingClient.SkuType.SUBS : BillingClient.SkuType.INAPP);

        //查询商品信息
        billingClient.querySkuDetailsAsync(params.build(),
                (billingResult, skuDetailsList) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                        mSkuDetailsList = skuDetailsList;
                        //商品信息为空
                        if (mSkuDetailsList.isEmpty()) {
                            ToastUtil.showToast(pActivity.getString(R.string.toast_google_play_error_1));
                        } else {
                            boolean isInitiatePayment = false;
                            //遍历商品信息，检查是否有对应商品id
                            String sku;
                            for (SkuDetails skuDetails : mSkuDetailsList) {
                                sku = skuDetails.getSku();
                                if (id.equals(sku)) {
                                    isInitiatePayment = true;
                                    BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                            .setSkuDetails(skuDetails)
                                            .build();
                                    billingClient.launchBillingFlow(pActivity, flowParams);
                                    break;
                                }
                            }

                            if (!isInitiatePayment) {
                                ToastUtil.showToast(pActivity.getString(R.string.toast_google_play_error_2));
                            }
                        }
                    } else {
                        ToastUtil.showToast(pActivity.getString(R.string.toast_google_play_error_1));
                    }
                    hideDialog();
                });
    }


    /**
     * 订单完成回调方法
     *
     * @param billingResult
     * @param purchases
     */
    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        //交易结果返回
        Log.i(TAG, "订单结果返回：" + billingResult.getResponseCode());
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                //订阅订单
                if (purchase.getSkus().get(0).contains("dy")) {
                    acknowledged(purchase);
                }
                //消耗商品订单
                else {
                    //交易成功，去消费掉已购商品
                    if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                        consumeOrder(purchase, 501);
                    }
                }
            }
        }
        //用户主动取消付款
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            FirebaseEventUtils.sendEvent("cancel_subscribe_vip");
        }
        //其他
        else {
            Log.i(TAG, "支付失败:" + billingResult.getResponseCode() + " - " + billingResult.getDebugMessage());
        }
    }

    /**
     * 确认订阅订单
     *
     * @param purchase
     */
    private void acknowledged(Purchase purchase) {
        if (!purchase.isAcknowledged()) {
            AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.getPurchaseToken())
                    .build();

            billingClient.acknowledgePurchase(acknowledgePurchaseParams, pBillingResult -> {
                        Log.i(TAG, "订阅完成确认 " + pBillingResult.getResponseCode() + pBillingResult.getDebugMessage());

                        if (pBillingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            ToastUtil.showToast(mApplication.getString(R.string.dy_success));
                        }

                        queryOrder(false, OrderType.SUB);
                    }
            );

//            SkuDetails lSkuDetails = null;
//            for (SkuDetails lDetails : UserData.sSkuDetailsList) {
//                if (lDetails.getSku().equals(purchase.getSkus().get(0))) {
//                    lSkuDetails = lDetails;
//                    break;
//                }
//            }

            /*上报adjust*/
//            if (lSkuDetails != null) {
//                LogUtil.i("Adjust 订阅上报：" + (lSkuDetails.getPriceAmountMicros() / 1000000));
//                AdjustPlayStoreSubscription subscription = new AdjustPlayStoreSubscription(
//                        lSkuDetails.getPriceAmountMicros() / 1000000,
//                        lSkuDetails.getPriceCurrencyCode(),
//                        purchase.getSkus().get(0),
//                        purchase.getOrderId(),
//                        purchase.getSignature(),
//                        purchase.getPurchaseToken());
//                subscription.setPurchaseTime(purchase.getPurchaseTime());
//
//                Adjust.trackPlayStoreSubscription(subscription);
//            }
        }

        if (mCheckVipCallBack != null) {
            mCheckVipCallBack.isVip(true);
        }

        Log.i(TAG, "该订阅订单已确认");

        if (purchase.isAutoRenewing()) {
            if (purchase.getPurchaseState() == 1) {
                Log.i(TAG, "该订阅订单在有效期内");
            } else {
                Log.i(TAG, "该订阅订单在宽限期内");
            }
        } else {
            Log.i(TAG, "该订阅订单已取消");
        }
    }


    /**
     * 消耗已购商品
     *
     * @param pPurchase
     */
    private void consumeOrder(Purchase pPurchase, int success_code) {
        if (!hadConnectGooglePlay) {
            ToastUtil.showToast(mApplication.getString(R.string.google_service_is_not_connected));
            return;
        }

        if (pPurchase == null) {
            return;
        }

        String token = pPurchase.getPurchaseToken();
        String productId = pPurchase.getSkus().get(0);

        billingClient.consumeAsync(ConsumeParams.newBuilder().setPurchaseToken(token).build(), (billingResult, purchaseToken) -> {
            Log.i(TAG, "订单消耗返回码：" + billingResult.getResponseCode());
        });
    }


    private void showDialog(AppCompatActivity pActivity) {

    }

    private void hideDialog() {

    }
}

package com.zygame.mvp_project.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.zygame.mvp_project.MyApplication;
import com.zygame.mvp_project.util.AppUtil;
import com.zygame.mvp_project.util.Base64Util;
import com.zygame.mvp_project.util.DeviceIdUtil;
import com.zygame.mvp_project.util.MD5;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络请求管理类
 *
 * @author admin
 */
public class NetWorkUtil {
    private static volatile NetWorkUtil net = null;

    private NetWorkUtil() {
    }

    public static synchronized NetWorkUtil getInstance() {
        if (net == null) {
            net = new NetWorkUtil();
            net.init();
        }
        return net;
    }

    public static final String URL = "";
    /**
     * 网络请求后台信息
     */
    public static final String ANDROID = "1";
    public static final String APP_ID = "278016";
    public static final String APP_SECRET = "351A4411-333F-1A5E-1CB9-C1778E5EE8DD";

    /**
     * 网络请求字段加密
     */
    public static final String NETWORK_PSW = "P^39DPPB@MvKKvrR";
    public static final String NETWORK_KEY = "1234567891012345";


    public OkHttpClient mHttpClient;

    /**
     * OKHttp 框架初始化
     */
    private void init() {
        MyHttpLoggingInterceptor loggingInterceptor = new MyHttpLoggingInterceptor(message -> Log.i("api message", message));
        mHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();
    }


    /**
     * 获取Retrofit实例
     *
     * @return
     */
    public Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .client(NetWorkUtil.getInstance().mHttpClient)
                .baseUrl(URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


    /**
     * 接口参数基础map
     *
     * @return
     */
    public Map<String, String> getMap() {
        Map<String, String> lMap = new HashMap<>();
        lMap.put("platform", ANDROID);
        lMap.put("app_id", APP_ID);
        lMap.put("app_secret", APP_SECRET);
        lMap.put("version", String.valueOf(AppUtil.getVersionCode(MyApplication.getContext())));
        lMap.put("phoneId", DeviceIdUtil.getInstance().getPhoneID());
        lMap.put("phone_model", Build.MODEL);
        lMap.put("system_version", String.valueOf(Build.VERSION.SDK_INT));

        return lMap;
    }


    /**
     * 获取签名
     *
     * @param pMap
     * @return
     */
    public String getSign(Map pMap) {
        List<Map.Entry<String, Object>> lList = new ArrayList<Map.Entry<String, Object>>(pMap.entrySet());

        //重写集合的排序方法：按字母顺序
        Collections.sort(lList, (o1, o2) -> (o1.getKey().compareTo(o2.getKey())));

        StringBuilder mapString = new StringBuilder();
        String token = "";
        for (Map.Entry<String, Object> lEntry : lList) {
            if (!lEntry.getKey().equals("token")) {
                Log.d("重新排序", lEntry.getKey() + " | " + lEntry.getValue());
                mapString.append(lEntry.getValue());
            } else {
                token = String.valueOf(lEntry.getValue());
            }
        }

        if (!TextUtils.isEmpty(token)) {
            mapString.append(token);
        }

        String sign = MD5.md5(mapString.toString());
        return sign;
    }


    /**
     * 接口字段加密
     *
     * @param data
     * @return
     */
    private String java_openssl_encrypt(String data) {
        try {

            byte[] keyByte = new byte[32];
            for (int i = 0; i < 32; i++) {
                if (i < NETWORK_PSW.getBytes().length) {
                    keyByte[i] = NETWORK_PSW.getBytes()[i];
                } else {
                    keyByte[i] = 0;
                }
            }

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.getIV();
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyByte, "AES"), new IvParameterSpec(NETWORK_KEY.getBytes()));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
            } else {
                return Base64Util.encode(cipher.doFinal(data.getBytes()));
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException pE) {
            pE.printStackTrace();
        }
        return null;
    }


    /**
     * map转成接口字段
     *
     * @param pStringMap
     * @return
     */
    private String map2String(Map<String, String> pStringMap) {
        StringBuilder lStringBuilder = new StringBuilder();
        if (pStringMap != null) {
            for (String key : pStringMap.keySet()) {
                lStringBuilder.append(key).append("=").append(pStringMap.get(key)).append("&");
            }
        }
        String ls = lStringBuilder.toString().substring(0, lStringBuilder.length() - 1);
        Log.d("map2String -->", ls);
        return ls;
    }


    private RequestBody getBody(String lData) {
        return RequestBody.create(okhttp3.MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"), lData);
    }

    private RequestBody getBody(Map lData) {
        return RequestBody.create(okhttp3.MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"), map2String(lData));
    }


    /**
     * 判断网络是否连接
     *
     * @param context 上下文
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public boolean isConnected(Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        return info != null && info.isConnected();
    }


    /**
     * 获取活动网络信息
     *
     * @param context 上下文
     * @return NetworkInfo
     */
    @SuppressLint("MissingPermission")
    private NetworkInfo getActiveNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

}

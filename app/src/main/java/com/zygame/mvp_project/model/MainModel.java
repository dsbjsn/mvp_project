package com.zygame.mvp_project.model;

import android.text.TextUtils;

import com.zygame.mvp_project.data.entity.BaseNetEntity;
import com.zygame.mvp_project.network.NetApi;
import com.zygame.mvp_project.network.NetWorkUtil;

import java.util.Map;

import io.reactivex.Observable;


/**
 * Created on 2021/9/7 09
 *
 * @author xjl
 */
public class MainModel extends BaseModel {
    private String token = "000";

    @Override
    public void initData() {
    }

    /**
     * 获取token方法
     *
     * @return
     */
    public Observable<BaseNetEntity> getTokenObservable() {
        Map<String, String> lMap = NetWorkUtil.getInstance().getMap();
        lMap.put("sign", NetWorkUtil.getInstance().getSign(lMap));
        return NetWorkUtil.getInstance().getRetrofit().create(NetApi.class).getUserInfo(lMap);
    }

    public String getToken() {
        if (TextUtils.isEmpty(token)) {
            token = "000";
        }
        return token;
    }

    public void setToken(String pUserInfo) {
        token = pUserInfo;
    }
}

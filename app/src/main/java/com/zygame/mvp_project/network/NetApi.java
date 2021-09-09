package com.zygame.mvp_project.network;

//    /**
//     * 下载ttf文件
//     */
//    @Streaming
//    @GET
//    Call<ResponseBody> downloadFile(@Url String url);
//
//    /**
//     * 意见反馈
//     *
//     * @param param get参数
//     * @return
//     */
//    @GET("/Api/ApiFeedBack/add?")
//    Call<BaseEntity> postSuggest(@QueryMap Map<String, String> param);


import com.zygame.mvp_project.data.entity.BaseNetEntity;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * @author xjl
 */
public interface NetApi {

    /**
     * 获取token
     *
     * @param param get参数
     * @return
     */
    @GET("Api/ApiToken?")
    Observable<BaseNetEntity> getUserInfo(@QueryMap Map<String, String> param);
}

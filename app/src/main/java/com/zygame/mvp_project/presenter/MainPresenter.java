package com.zygame.mvp_project.presenter;

import android.util.Log;

import com.zygame.mvp_project.data.entity.BaseNetEntity;
import com.zygame.mvp_project.model.MainModel;
import com.zygame.mvp_project.view.IMainView;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created on 2021/9/7 09
 *
 * @author xjl
 */
public class MainPresenter extends IBasePresenter {
    private IMainView mIMainView;
    private MainModel mMainModel;

    public MainPresenter(IMainView pIMainView) {
        mIMainView = pIMainView;
        mMainModel = new MainModel();
    }

    @Override
    protected void initData() {
        getToken();
    }

    @Override
    protected void initView() {
        mIMainView.findView();
    }

    /**
     * 从服务器获取token
     */
    private void getToken() {
        mMainModel.getTokenObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseNetEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.i("模拟网络请求", "订阅");
                    }

                    @Override
                    public void onNext(@NonNull BaseNetEntity pS) {
                        if (pS.isIs_success()) {
                            Log.i("模拟网络请求", "成功 userInfo=" + pS.toString());
                            mMainModel.setToken(pS.getData().toString());
                        } else {
                            Log.i("模拟网络请求", "失败 userInfo=" + pS.toString());
                        }

                        mIMainView.showToken(mMainModel.getToken());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.i("模拟网络请求", "失败 e=" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i("模拟网络请求", "完成");
                    }
                });
    }
}

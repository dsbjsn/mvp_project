package com.zygame.mvp_project.presenter;

/**
 * Created on 2021/9/7 09
 *
 * @author xjl
 */
public abstract class IBasePresenter {
    /**
     * 初始化
     */
    public void init() {
        initView();
        initData();
    }

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 初始化控件
     */
    protected abstract void initView();
}

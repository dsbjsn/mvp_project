package com.zygame.mvp_project.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.zygame.mvp_project.evnetBus.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * @author admin
 */
public abstract class BaseFragment extends Fragment {
    protected String mTag;
    protected Fragment mFragment;
    protected AppCompatActivity mActivity;
    protected View rootView;

    public BaseFragment() {
    }

    protected boolean isVisible = false;
    protected boolean isPrepared = false;

    @Override
    public void onAttach(Context context) {
        mTag = getClass().getSimpleName() + "................>>>";
        super.onAttach(context);
        mFragment = this;
        mActivity = (AppCompatActivity) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(this.getClass().getSimpleName(), "------------>>>onCreate");
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(this.getClass().getSimpleName(), "------------>>>onStart");
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.i(this.getClass().getSimpleName(), "------------>>>onResume");
        if (getUserVisibleHint()) {
            onVisibilityChangedToUser(true, false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(this.getClass().getSimpleName(), "------------>>>onPause");
        if (getUserVisibleHint()) {
            onVisibilityChangedToUser(false, false);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(this.getClass().getSimpleName(), "------------>>>onStop");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(this.getClass().getSimpleName(), "------------>>>onActivityResult");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(this.getClass().getSimpleName(), "------------>>>onDestroy");
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(this.getClass().getSimpleName(), "------------>>>onDestroyView");
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isResumed()) {
            onVisibilityChangedToUser(isVisibleToUser, true);
        }
    }

    /**
     * 当Fragment对用户的可见性发生了改变的时候就会回调此方法
     *
     * @param isVisibleToUser                      true：用户能看见当前Fragment；false：用户看不见当前Fragment
     * @param isHappenedInSetUserVisibleHintMethod true：本次回调发生在setUserVisibleHintMethod方法里；false：发生在onResume或onPause方法里
     */
    public void onVisibilityChangedToUser(boolean isVisibleToUser, boolean isHappenedInSetUserVisibleHintMethod) {
        isVisible = isVisibleToUser;
        if (isVisibleToUser) {
            Log.i(this.getClass().getSimpleName(), "------------>>>onPageStart");
            if (isPrepared) {
                onVisible();
            }
        } else {
            Log.i(this.getClass().getSimpleName(), "------------>>>onPageEnd");
            onHide();
        }
    }


    /**
     * EventBus 接收事件通知
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event) {
        eventBusMsg(event);
    }


    public abstract void eventBusMsg(MessageEvent pEvent);


    /**
     * 初始化views
     */
    protected void setRootView(int id, LayoutInflater inflater, ViewGroup container) {
        rootView = inflater.inflate(id, container, false);
        initViews();
        isPrepared = true;
        if (isVisible) {
            onVisible();
        }
    }

    /**
     * 初始化views
     */
    protected abstract void initViews();

    /**
     * fragment 显示
     */
    protected abstract void onVisible();

    /**
     * fragment 隐藏
     */
    protected abstract void onHide();

    /**
     * 页面的三种状态
     * 内容页
     * 空页
     * 加载页
     */
    private View contentV, emptyV, loadingV;


    /**
     * 设置页面状态view
     *
     * @param contentViewId 内容控件id
     * @param emptyViewId   空控件id
     * @param loadingViewId 加载控件id
     */
    protected void setStateViewsId(int contentViewId, int emptyViewId, int loadingViewId) {
        this.contentV = rootView.findViewById(contentViewId);
        this.emptyV = rootView.findViewById(emptyViewId);
        this.loadingV = rootView.findViewById(loadingViewId);
        setState(State.LOADING);
    }

    /**
     * 设置页面状态view
     *
     * @param contentV 内容控件
     * @param emptyV   空控件
     * @param loadingV 加载控件
     */
    protected void setStateViews(View contentV, View emptyV, View loadingV) {
        this.contentV = contentV;
        this.emptyV = emptyV;
        this.loadingV = loadingV;
        setState(State.LOADING);
    }

    /**
     * 设置页面状态
     *
     * @param pState
     */
    protected void setState(State pState) {
        if (contentV == null || emptyV == null || loadingV == null) {
            Log.e("setState", "views is not find");
            return;
        }

        switch (pState) {
            case CONTENT:
                contentV.setVisibility(View.VISIBLE);
                emptyV.setVisibility(View.GONE);
                loadingV.setVisibility(View.GONE);
                break;
            case EMPTY:
                contentV.setVisibility(View.GONE);
                emptyV.setVisibility(View.VISIBLE);
                loadingV.setVisibility(View.GONE);
                break;
            case LOADING:
                contentV.setVisibility(View.GONE);
                emptyV.setVisibility(View.GONE);
                loadingV.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    /**
     * 页面状态
     */
    protected enum State {
        //显示内容
        CONTENT,
        //显示空
        EMPTY,
        //显示加载中
        LOADING
    }
}

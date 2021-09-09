package com.zygame.mvp_project.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.zygame.mvp_project.evnetBus.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by admin on 2018/7/10
 */
public abstract class BaseDialogFragment extends DialogFragment {
    protected Context mContext;
    protected FragmentActivity mActivity;

    protected Dialog mDialog;
    protected View contentView;

    /**
     * 是否已显示
     */
    private boolean isShowed = false;

    /**
     * EventBus 通知消息
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event) {
        eventBusMsg(event);
    }

    protected abstract void eventBusMsg(MessageEvent pEvent);

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mDialog != null && !backKeyIsEnable) {
            mDialog.setOnKeyListener((dialog, keyCode, event) -> keyCode == KeyEvent.KEYCODE_BACK);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mActivity = getActivity();
        mContext = getContext();
    }

    @Override
    public void onDismiss(DialogInterface pDialog) {
        super.onDismiss(pDialog);
        isShowed = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    public void show(FragmentManager pManager, String pTag) {
        if (TextUtils.isEmpty(pTag)) {
            show(pManager);
        } else {
            if (!isAdded() && !isVisible() && !isRemoving() && !isShowed) {
                FragmentTransaction ft = pManager.beginTransaction();
                ft.add(this, pTag);
                ft.commitAllowingStateLoss();
                isShowed = true;
            } else {
                new IllegalStateException("fragment dialog show error");
            }
        }
    }

    public void show(FragmentManager pManager) {
        if (!isAdded() && !isVisible() && !isRemoving() && !isShowed) {
            FragmentTransaction ft = pManager.beginTransaction();
            String tag = String.valueOf(Math.random() * 1000);
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
            isShowed = true;
        } else {
            new IllegalStateException("fragment dialog show error");
        }
    }

    /**
     * 是否屏蔽返回键
     */
    private boolean backKeyIsEnable = true;

    /**
     * 设置是否屏蔽返回键
     *
     * @param pBackKeyIsEnable
     */
    protected void setBackEnable(boolean pBackKeyIsEnable) {
        backKeyIsEnable = pBackKeyIsEnable;
    }
}

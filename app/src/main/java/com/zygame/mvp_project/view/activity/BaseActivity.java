package com.zygame.mvp_project.view.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zygame.mvp_project.util.AppUtil;
import com.zygame.mvp_project.evnetBus.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author admin
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected AppCompatActivity mActivity;
    protected Context mContext;
    public Dialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(this.getClass().getSimpleName(), "--------------> onCreate");
        mActivity = this;
        mContext = this;
        EventBus.getDefault().register(this);

        AppUtil.setStatusBarMode(this, true);

        initDialog();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(this.getClass().getSimpleName(), "--------------> onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(this.getClass().getSimpleName(), "--------------> onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(this.getClass().getSimpleName(), "--------------> onPause");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(this.getClass().getSimpleName(), "--------------> onActivityResult");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(this.getClass().getSimpleName(), "--------------> onDestroy");
        EventBus.getDefault().unregister(this);
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event) {
        eventBusMsg(event);
    }


    public abstract void eventBusMsg(MessageEvent pEvent);



    private void initDialog() {
//        progressDialog = new Dialog(this);
//        progressDialog.setContentView(R.layout.loading_dialog);
//        progressDialog.setCancelable(false);
//        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    /**
     * 上次点击时间
     */
    protected long lastClickTime = 0;
    protected long clickIntervalTime = 600;

    protected boolean checkRepeatClick() {
        if (System.currentTimeMillis() - lastClickTime > clickIntervalTime) {
            lastClickTime = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }
}

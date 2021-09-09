package com.zygame.mvp_project.view.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.zygame.mvp_project.R;
import com.zygame.mvp_project.presenter.MainPresenter;
import com.zygame.mvp_project.evnetBus.MessageEvent;
import com.zygame.mvp_project.view.IMainView;

public class MainActivity extends BaseActivity implements IMainView {
    TextView lTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainPresenter lMainPresenter = new MainPresenter(this);
        lMainPresenter.init();
    }

    @Override
    public void eventBusMsg(MessageEvent pEvent) {
    }

    @Override
    public void findView() {
        lTextView = findViewById(R.id.tv);
    }

    @Override
    public void showToken(String token) {
        lTextView.setText(token);
    }
}
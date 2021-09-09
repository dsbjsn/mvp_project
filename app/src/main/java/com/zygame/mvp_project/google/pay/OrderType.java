package com.zygame.mvp_project.google.pay;

/**
 * Created on 2021/7/17 10
 *
 * @author xjl
 */
public enum OrderType {
    SUB,
    CONSUME;

    public boolean isSub() {
        return this == SUB;
    }

    public boolean isConsume() {
        return this == CONSUME;
    }
}

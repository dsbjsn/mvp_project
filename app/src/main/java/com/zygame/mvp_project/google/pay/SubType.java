package com.zygame.mvp_project.google.pay;

/**
 * Created on 2021/7/16 14
 *
 * @author xjl
 */
public enum SubType {
    DY_1(4.99f, "dy_1");

    private final float price;
    private final String value;

    private SubType(float price, String id) {
        this.price = price;
        this.value = id;
    }

    public String getValue() {
        return value;
    }

    public float getPrice() {
        return price;
    }
}

package com.zygame.mvp_project.google.admob;

import java.util.List;

/**
 * Created on 2021/2/20 16
 *
 * @author xjl
 */
public class AdInfoEntity {
    /**
     * 是否显示广告
     */
    private Boolean is_show_ad;
    private List<String> banner_id;
    private List<String> interstitial_id;
    private List<String> reward_id;
    private List<String> native_id;

    public Boolean isIs_show_ad() {
        return is_show_ad;
    }

    public void setIs_show_ad(Boolean is_show_ad) {
        this.is_show_ad = is_show_ad;
    }

    public List<String> getBanner_id() {
        return banner_id;
    }

    public void setBanner_id(List<String> banner_id) {
        this.banner_id = banner_id;
    }

    public List<String> getInterstitial_id() {
        return interstitial_id;
    }

    public void setInterstitial_id(List<String> interstitial_id) {
        this.interstitial_id = interstitial_id;
    }

    public List<String> getReward_id() {
        return reward_id;
    }

    public void setReward_id(List<String> reward_id) {
        this.reward_id = reward_id;
    }

    public List<String> getNative_id() {
        return native_id;
    }

    public void setNative_id(List<String> native_id) {
        this.native_id = native_id;
    }
}

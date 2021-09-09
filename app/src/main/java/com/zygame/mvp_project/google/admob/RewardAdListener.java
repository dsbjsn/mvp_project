package com.zygame.mvp_project.google.admob;

/**
 * @author xjl
 * @time 2020/12/3 19:00
 * @remark 监听激励视频播放完成
 */

public interface RewardAdListener {
    /**
     * 是否获得奖励
     * @param isGet
     */
    void getAward(boolean isGet);
}

package com.xszn.ime.inter;

/**
 * @author yeyang
 * @name qskd_tip_demo
 * @class name：VideoPlayCall
 * @class describe
 * @time 2019-11-22 18:28
 * @change
 * @chang time
 * @class describe
 */
public interface VideoPlayCall {
    /**
     * name 视频名称
     * true 成功  false   失败
     * @param flag
     */
    void playErr(String name,boolean flag);
}
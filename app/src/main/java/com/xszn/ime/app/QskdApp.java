package com.xszn.ime.app;

import android.app.Application;

import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.xszn.ime.service.AppDownloadStatusListener;

import androidx.annotation.NonNull;

/**
 * @author yeyang
 * @name qskd_tip_demo
 * @class name：com.xszn.ime.app
 * @class describe
 * @time 2019-11-21 18:04
 * @change
 * @chang time
 * @class describe
 */
public class QskdApp extends Application {

//    public static String PROCESS_NAME_XXXX = "process_name_xxxx";

    private static QskdApp mInstance;

    @NonNull
    public static QskdApp getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        this.mInstance = this;

        //强烈建议在应用对应的Application#onCreate()方法中调用，避免出现content为null的异常
        TTAdSdk.init(mInstance,
                new TTAdConfig.Builder()
                        .appId("5018834")
                        .useTextureView(false) //使用TextureView控件播放视频,默认为SurfaceView,当有SurfaceView冲突的场景，可以使用TextureView
                        .appName("APP测试媒体")
                        .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)
                        .allowShowNotify(true) //是否允许sdk展示通知栏提示
                        .allowShowPageWhenScreenLock(true) //是否在锁屏场景支持展示广告落地页
                        .debug(true) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                        .globalDownloadListener(new AppDownloadStatusListener(mInstance)) //下载任务的全局监听
                        .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI, TTAdConstant.NETWORK_STATE_3G) //允许直接下载的网络状态集合
                        .supportMultiProcess(false) //是否支持多进程，true支持
                        .build());

        //如果明确某个进程不会使用到广告SDK，可以只针对特定进程初始化广告SDK的content
        //if (PROCESS_NAME_XXXX.equals(processName)) {
        //   TTAdSdk.init(context, config);
        //}
    }
}

package com.xszn.ime.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.xszn.ime.R;
import com.xszn.ime.inter.VideoPlayCall;
import com.xszn.ime.utils.LogUtils;
import com.xszn.ime.utils.MyTools;

import androidx.annotation.Nullable;

/**
 * @author yeyang
 * @name qskd_tip_demo
 * @class name：com.xszn.ime.activity
 * @class describe
 * @time 2019-11-22 12:11
 * @change
 * @chang time
 * @class describe
 */
public class VideoPlayActivity extends Activity {

    private static String TAG = VideoPlayActivity.class.getSimpleName() + ": ";

    private ProgressBar mProgressBar;
    private String mAd_id;
    private String mAd_Name;
    private String mAd_Type;

    private static String AD_ID = "AD_ID";
    private static String AD_NAME = "AD_NAME";
    private static String AD_TYPE = "AD_TYPE";
    private static String C_FULLVIDEO = "C_FULLVIDEO";//全屏
    private static String C_INSPIRE = "C_INSPIRE";//激励

    private static boolean isLoadCsj;
    public static boolean csjIsReady;
    public static boolean csjIsPlaying;
    private static boolean mHasShowDownloadActive = false;
    private TTAdNative mTTAdNative;
    private TTRewardVideoAd mttRewardVideoAd;
    private TTFullScreenVideoAd mTTFullScreenVideoAd;

    private static FirstActivity mActivity;

    public static void launch(Context context, String name, String adId, String adType) {
        Intent intent = new Intent(context, VideoPlayActivity.class);
        mActivity = (FirstActivity) context;
        intent.putExtra(AD_NAME, name);
        intent.putExtra(AD_ID, adId);
        intent.putExtra(AD_TYPE, adType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        initView();
    }

    private void initView() {
        mProgressBar = findViewById(R.id.progressBar);
        mAd_Name = getIntent().getStringExtra(AD_NAME);
        mAd_id = getIntent().getStringExtra(AD_ID);
        mAd_Type = getIntent().getStringExtra(AD_TYPE);
        mTTAdNative = mActivity.getTTAdNative();
        if (mAd_Type == C_FULLVIDEO) {//全屏
            LogUtils.d(TAG + "全屏");
            loadFullScreenVideoAd(this, mAd_id, TTAdConstant.VERTICAL, mActivity.getVideoPlayCall());
        } else if (mAd_Type == C_INSPIRE) {//激励
            LogUtils.d(TAG + "激励");
            loadCSJAd(this, mAd_id, TTAdConstant.VERTICAL, mActivity.getVideoPlayCall());
        }
    }

    private void loadCSJAd(final Activity activity, final String adId, int vertical, final VideoPlayCall videoPlayCall) {
        if (videoPlayCall == null) {
            LogUtils.d("回调方法为空");
            return;
        }
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(adId)
                .setSupportDeepLink(true)
//                .setImageAcceptedSize(1080, 1920)
                .setImageAcceptedSize(MyTools.getScreenWidth(activity), MyTools.getScreenHeight(activity))
//                .setRewardName("金币") //奖励的名称
//                .setRewardAmount(3)  //奖励的数量
                .setUserID("")//用户id,必传参数,如果不是服务器回调，可以为空
                .setMediaExtra("") //激励视频奖励透传参数，字符串，如果用json对象，必须使用序列化为String类型,可为空
                .setOrientation(vertical) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .build();
        //step5:请求广告
        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                LogUtils.d("onError:" + code + "..." + message);
                closeActivity(videoPlayCall, false);
            }

            //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
            @Override
            public void onRewardVideoCached() {
                LogUtils.d("onRewardVideoCached:");
                mProgressBar.setVisibility(View.GONE);
                mttRewardVideoAd.showRewardVideoAd(VideoPlayActivity.this);
            }

            //视频广告的素材加载完毕，比如视频url等，在此回调后，可以播放在线视频，网络不好可能出现加载缓冲，影响体验。
            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
                LogUtils.d("onRewardVideoAdLoad:");
                isLoadCsj = false;
                csjIsReady = true;
                mttRewardVideoAd = ad;
//                mttRewardVideoAd.setShowDownLoadBar(false);
                mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                        isLoadCsj = false;
                        LogUtils.d("onAdShow:");
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        LogUtils.d("onAdVideoBarClick:");
                    }

                    @Override
                    public void onAdClose() {
                        LogUtils.d("onAdClose:");
                        closeActivity(videoPlayCall, true);
//                        isLoadCsj = false;
//
//                        loadCSJAd(activity, adId, TTAdConstant.VERTICAL);
                    }

                    //视频播放完成回调
                    @Override
                    public void onVideoComplete() {
                        LogUtils.d("onVideoComplete:");
                        isLoadCsj = false;
                        csjIsReady = false;
                        csjIsPlaying = false;
                    }

                    @Override
                    public void onVideoError() {
                        LogUtils.d("onVideoError:");
                        isLoadCsj = false;
                        csjIsReady = false;
                        csjIsPlaying = false;
                        closeActivity(videoPlayCall, false);
                    }

                    //视频播放完成后，奖励验证回调，rewardVerify：是否有效，rewardAmount：奖励梳理，rewardName：奖励名称
                    @Override
                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {
                        LogUtils.d("onRewardVerify:" + rewardVerify + "..." + rewardAmount + "..." + rewardName);
                    }

                    public void onSkippedVideo() {
                        LogUtils.d("onSkippedVideo:");
                    }
                });
                mttRewardVideoAd.setDownloadListener(new TTAppDownloadListener() {
                    @Override
                    public void onIdle() {
                        mHasShowDownloadActive = false;
                    }

                    @Override
                    public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                        if (!mHasShowDownloadActive) {
                            mHasShowDownloadActive = true;
                            LogUtils.d("下载中，点击下载区域暂停onDownloadActive:" + totalBytes + "..." + currBytes + "..." + fileName + "..." + appName);
                        }
                    }

                    @Override
                    public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                        LogUtils.d("下载暂停，点击下载区域继续onDownloadPaused:" + totalBytes + "..." + currBytes + "..." + fileName + "..." + appName);
                    }

                    @Override
                    public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                        LogUtils.d("下载失败，点击下载区域重新下载onDownloadFailed:" + totalBytes + "..." + currBytes + "..." + fileName + "..." + appName);
                    }

                    @Override
                    public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                        LogUtils.d("下载失败，点击下载区域重新下载onDownloadFinished:" + totalBytes + "..." + fileName + "..." + appName);
                    }

                    @Override
                    public void onInstalled(String fileName, String appName) {
                        LogUtils.d("安装完成，点击下载区域打开onInstalled:" + fileName + "..." + appName);
                    }
                });
            }
        });
    }

    /**
     * 播放全屏视频代码
     *
     * @param videoPlayCall
     */
    @SuppressWarnings("SameParameterValue")
    private void loadFullScreenVideoAd(final Activity activity, String adId, int vertical, final VideoPlayCall videoPlayCall) {
        if (videoPlayCall == null) {
            LogUtils.d("回调方法为空");
            return;
        }
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(adId)
                .setSupportDeepLink(true)
//                .setImageAcceptedSize(1080, 1920)
                .setImageAcceptedSize(MyTools.getScreenWidth(activity), MyTools.getScreenHeight(activity))
//                .setRewardName("金币") //奖励的名称
//                .setRewardAmount(3)  //奖励的数量
                .setUserID("")//用户id,必传参数,如果不是服务器回调，可以为空
                .setMediaExtra("") //激励视频奖励透传参数，字符串，如果用json对象，必须使用序列化为String类型,可为空
                .setOrientation(vertical) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .build();
        //step5:请求广告
        mTTAdNative.loadFullScreenVideoAd(adSlot, new TTAdNative.FullScreenVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.d("TipDemo", "onError-" + message);
                videoPlayCall.playErr(mAd_Name, false);
            }

            @Override
            public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ad) {
                mTTFullScreenVideoAd = ad;
                mTTFullScreenVideoAd.setFullScreenVideoAdInteractionListener(new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                        LogUtils.d(TAG + "onAdShow");
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        LogUtils.d(TAG + "onAdVideoBarClick");
                    }

                    @Override
                    public void onAdClose() {
                        LogUtils.d(TAG + "onAdClose");
                        closeActivity(videoPlayCall, true);
                    }

                    @Override
                    public void onVideoComplete() {
                        LogUtils.d(TAG + "onVideoComplete");
                        videoPlayCall.playErr(mAd_Name, true);
                    }

                    @Override
                    public void onSkippedVideo() {
                        LogUtils.d(TAG + "onSkippedVideo");
                        videoPlayCall.playErr(mAd_Name, true);
                    }

                });
            }

            @Override
            public void onFullScreenVideoCached() {
                LogUtils.d(TAG + "onFullScreenVideoCached");
                mProgressBar.setVisibility(View.GONE);
                mTTFullScreenVideoAd.showFullScreenVideoAd(VideoPlayActivity.this);
            }
        });
    }

    /**
     * 关闭页面
     */
    private void closeActivity(VideoPlayCall videoPlayCall, boolean flag) {
        videoPlayCall.playErr(mAd_Name, flag);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActivity = null;
    }
}

package com.xszn.ime.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.xszn.ime.R;
import com.xszn.ime.config.TTAdManagerHolder;
import com.xszn.ime.inter.CanClose;
import com.xszn.ime.inter.VideoPlayCall;
import com.xszn.ime.utils.LogUtils;
import com.xszn.ime.web.JsApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import wendu.dsbridge.DWebView;
import wendu.dsbridge.OnReturnValue;


public class FirstActivity extends Activity {

    private static String TAG = FirstActivity.class.getSimpleName() + ": ";

    private static String DATA_URL = "data_url";

    private String mDataUrl = "";

    public static void launch(Context context) {
        Intent intent = new Intent(context, FirstActivity.class);
        context.startActivity(intent);
    }

    public static void launch(Context context, String url) {
        Intent intent = new Intent(context, FirstActivity.class);
        intent.putExtra(DATA_URL, url);
        context.startActivity(intent);
    }

    private JsApi mJsApi;

    private DWebView mWebView;

    private TTAdNative mTTAdNative;

    private VideoPlayCall mVideoPlayCall;
    private String mAdKey = "5018834";
    private String mAdId = "918834024";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        initView();
        mVideoPlayCall = new VideoPlayCall() {
            @Override
            public void playErr(String name, boolean flag) {
                toTellServer(name, flag);
            }
        };
    }

    public TTAdNative getTTAdNative() {
        return mTTAdNative;
    }

    public VideoPlayCall getVideoPlayCall() {
        return mVideoPlayCall;
    }

    private void initView() {
        mDataUrl = getIntent().getStringExtra(DATA_URL);

        per();
        new Thread(new Runnable() {
            @Override
            public void run() {
                initCSJSdk(FirstActivity.this, mAdKey, mAdId);
            }
        }).start();
        DWebView.setWebContentsDebuggingEnabled(true);
        mWebView = findViewById(R.id.webview);
        mJsApi = new JsApi(this, new CanClose() {
            @Override
            public void close() {
                FirstActivity.this.finish();
            }
        });
        mWebView.addJavascriptObject(mJsApi, null);
        mWebView.loadUrl(mDataUrl);
    }


    /**
     * 告诉服务器视频是否播放成功
     *
     * @param name
     * @param flag
     */
    public void toTellServer(String name, boolean flag) {
        LogUtils.d(TAG + name + "..." + flag);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            if (flag) {
                jsonObject.put("display", flag);
            } else {
                jsonObject.put("noad", flag);
            }
            LogUtils.d(TAG + jsonObject.toString());
            mWebView.callHandler("adresult", new Object[]{jsonObject}, new OnReturnValue<String>() {
                @Override
                public void onValue(String retValue) {

                }
            });
        } catch (JSONException e) {
            LogUtils.d(TAG + e.toString());
            e.printStackTrace();
        }
    }

    public void notification(boolean isActive) {
        LogUtils.d(TAG + "notification-" + isActive);
        if (mWebView != null) {
            //DidBecomeActive前台  WillResignActive后台
            String msg = isActive ? "DidBecomeActive" : "WillResignActive";
            mWebView.callHandler("notification", new Object[]{msg}, new OnReturnValue<String>() {
                @Override
                public void onValue(String retValue) {
                    LogUtils.d(TAG + retValue);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        notification(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        notification(false);
    }

    private void per() {
        XXPermissions.with(this)
                // 可设置被拒绝后继续申请，直到用户授权或者永久拒绝
                //.constantRequest()
                // 支持请求6.0悬浮窗权限8.0请求安装权限
                //.permission(Permission.SYSTEM_ALERT_WINDOW, Permission.REQUEST_INSTALL_PACKAGES)
                // 不指定权限则自动获取清单中的危险权限
                .permission(Permission.Group.STORAGE, Permission.Group.LOCATION)
                .request(new OnPermission() {

                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {
                        if (isAll) {
                            Toast.makeText(FirstActivity.this, "获取权限成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(FirstActivity.this, "获取权限成功，部分权限未正常授予", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                        if (quick) {
                            Toast.makeText(FirstActivity.this, "被永久拒绝授权，请手动授予权限", Toast.LENGTH_SHORT).show();
                            //如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.gotoPermissionSettings(FirstActivity.this);
                        } else {
                            Toast.makeText(FirstActivity.this, "获取权限失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void initCSJSdk(Activity activity, String adKey, String adId) {
        TTAdManagerHolder.init(activity, adKey);
        //step1:初始化sdk
        TTAdManager ttAdManager = TTAdManagerHolder.get();
        //step2:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdManagerHolder.get().requestPermissionIfNecessary(activity);
        //step3:创建TTAdNative对象,用于调用广告请求接口
        mTTAdNative = ttAdManager.createAdNative(activity);
    }

    /**
     * 拦截返回键
     * js发送调用 通知返回键被按下
     */
    @Override
    public void onBackPressed() {
        if (mWebView != null) {
            mWebView.callHandler("onBack", new OnReturnValue<String>() {
                @Override
                public void onValue(String retValue) {
                    LogUtils.d(TAG + "onBack");
                }
            });
        }
    }

}

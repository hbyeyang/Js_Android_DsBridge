package com.xszn.ime.web;

import android.content.Context;
import android.os.CountDownTimer;
import android.webkit.JavascriptInterface;

import com.google.gson.Gson;
import com.xszn.ime.activity.OpenNewActivity;
import com.xszn.ime.activity.VideoPlayActivity;
import com.xszn.ime.bean.OpenNewBean;
import com.xszn.ime.bean.PlayCodeBean;
import com.xszn.ime.inter.CanClose;
import com.xszn.ime.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import wendu.dsbridge.CompletionHandler;

/**
 * @author yeyang
 * @name qskd_tip_demo
 * @class name：com.xszn.ime.web
 * @class describe
 * @time 2019-11-22 11:39
 * @change
 * @chang time
 * @class describe
 */
public class JsApi {

    private Context mContext;
    private CanClose mCanClose;
    private static String TAG = JsApi.class.getSimpleName() + ": ";

    public JsApi(Context context) {
        mContext = context;
    }

    public JsApi(Context context, CanClose canClose) {
        mContext = context;
        mCanClose = canClose;
    }

    public JsApi() {
    }

    @JavascriptInterface
    public String testSyn(Object msg) {
        return msg + "［syn call］";
    }

    @JavascriptInterface
    public void testAsyn(Object msg, CompletionHandler<String> handler) {
        handler.complete(msg + " [ asyn call]");
    }

    @JavascriptInterface
    public String testNoArgSyn(Object arg) throws JSONException {
        return "testNoArgSyn called [ syn call]";
    }

    @JavascriptInterface
    public void testNoArgAsyn(Object arg, CompletionHandler<String> handler) {
        handler.complete("testNoArgAsyn   called [ asyn call]");
    }


    //@JavascriptInterface
    //without @JavascriptInterface annotation can't be called
    public String testNever(Object arg) throws JSONException {
        JSONObject jsonObject = (JSONObject) arg;
        return jsonObject.getString("msg") + "[ never call]";
    }

    @JavascriptInterface
    public void callProgress(Object args, final CompletionHandler<Integer> handler) {

        new CountDownTimer(11000, 1000) {
            int i = 10;

            @Override
            public void onTick(long millisUntilFinished) {
                //setProgressData can be called many times util complete be called.
                handler.setProgressData((i--));

            }

            @Override
            public void onFinish() {
                //complete the js invocation with data; handler will be invalid when complete is called
                handler.complete(0);

            }
        }.start();
    }

    /*1、dsBridge.register('onBack', () =>{}) //监听安卓的右下角返回，app 不做任何处理
    // 2、dsBridge.call(‘openNewWebView',{title:'XXX',url:XXX,()=>{}} //在新的 webview 打开指 定 url，不影响原页面
    3、dsBridge.call(‘closeWebView', ()=>{}) //退出游戏首页时，H5 页面通知 app 关闭当前 webview
    4、监控 app 进入后台及前台激活
        dsBridge.register('notification', (data, responseCallback) =>{
        if (data == 'DidBecomeActive') { //前台激活成功

        }else if (data == ‘WillResignActive'){
        }
    })*/

//    @JavascriptInterface
//    public void displayAD(Object arg) throws JSONException {
//        LogUtils.d(TAG + arg.toString());
//    }

    @JavascriptInterface
    public void displayAD(Object arg, CompletionHandler<String> handler) {
        handler.complete("displayAD");
        LogUtils.d(TAG + arg.toString());
        PlayCodeBean playCodeBean = new Gson().fromJson(arg.toString(), PlayCodeBean.class);
        LogUtils.d(TAG + playCodeBean.toString());
        if (playCodeBean != null) {
            String name = playCodeBean.getName();
            List<PlayCodeBean.AdconfigEntity> adconfig = playCodeBean.getAdconfig();
            if (adconfig != null && adconfig.size() > 0) {
                PlayCodeBean.AdconfigEntity adconfigEntity = adconfig.get(0);
                if (adconfigEntity != null) {
                    int code = adconfigEntity.getCode();
                    String sub_type = adconfigEntity.getSub_type();
                    VideoPlayActivity.launch(mContext, name, String.valueOf(code),sub_type);
                } else {
                    LogUtils.d(TAG + "代码为空");
                }
            } else {
                LogUtils.d(TAG + "代码为空");
            }
        } else {
            LogUtils.d(TAG + "代码为空");
        }
    }

    @JavascriptInterface
    public void openNewWebView(Object arg, CompletionHandler<String> handler) {
        handler.complete("openNewWebView");
        LogUtils.d(TAG + arg.toString());
        OpenNewBean openNewBean = new Gson().fromJson(arg.toString(), OpenNewBean.class);
        OpenNewActivity.launch(mContext, openNewBean.getTitle(), openNewBean.getUrl());
    }

    @JavascriptInterface
    public void closeWebView(Object arg, CompletionHandler<String> handler) {
        handler.complete("closeWebView");
        LogUtils.d(TAG + "closeWebView");
        mCanClose.close();
    }

    @JavascriptInterface
    public void onBack(Object arg, CompletionHandler<String> handler) {
        handler.complete("onBack");
        LogUtils.d(TAG);
    }

}


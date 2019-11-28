package com.xszn.ime.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xszn.ime.R;
import com.xszn.ime.inter.CanClose;
import com.xszn.ime.utils.LogUtils;
import com.xszn.ime.web.JsApi;

import androidx.annotation.Nullable;
import wendu.dsbridge.DWebView;
import wendu.dsbridge.OnReturnValue;

/**
 * @author yeyang
 * @name qskd_tip_demo
 * @class name：com.xszn.ime
 * @class describe
 * @time 2019-11-25 14:56
 * @change
 * @chang time
 * @class describe
 */
public class OpenNewActivity extends Activity {

    private static String TAG = OpenNewActivity.class.getSimpleName() + ": ";
    private static String DATA_TITLE = "data_title";
    private static String DATA_URL = "data_url";
    private String mTitle;
    private String mUrl;
    private TextView mTvTitle;
    private DWebView mWebView;
    private JsApi mJsApi;

    public static void launch(Context context, String title, String url) {
        Intent intent = new Intent(context, OpenNewActivity.class);
        intent.putExtra(DATA_TITLE, title);
        intent.putExtra(DATA_URL, url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_new);
        initView();
    }

    private void initView() {
        mTvTitle = findViewById(R.id.tv_title);
        mWebView = findViewById(R.id.webview);

        mTvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        mTitle = intent.getStringExtra(DATA_TITLE);
        mUrl = intent.getStringExtra(DATA_URL);

        mTvTitle.setText(mTitle);
        DWebView.setWebContentsDebuggingEnabled(true);
        mWebView = findViewById(R.id.webview);
        mJsApi = new JsApi(this, new CanClose() {
            @Override
            public void close() {
                OpenNewActivity.this.finish();
            }
        });
        mWebView.addJavascriptObject(mJsApi, null);
        mWebView.loadUrl(mUrl);
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

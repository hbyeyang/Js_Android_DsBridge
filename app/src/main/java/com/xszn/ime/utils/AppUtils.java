package com.xszn.ime.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * @author yeyang
 * @name Letu_KBooks_App_Android
 * @class name：com.lt.ad.library.util
 * @class describe
 * @time 2019/3/8 3:16 PM
 * @change
 * @chang time
 * @class describe
 */
public class AppUtils {
    /**
     * 获取应用程序名称
     */
    public static synchronized String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}

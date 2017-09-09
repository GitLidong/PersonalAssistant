package com.lidong.speech;

/**
 * Created by LiDong on 2017/5/8.
 */

public class AppInfo {
    private String appPackageName;
    private String appName;

    public AppInfo(String appPackageName,String appName){
        this.appPackageName=appPackageName;
        this.appName=appName;
    }

    public String getAppName() {
        return appName;
    }

    public String getAppPackageName() {
        return appPackageName;
    }
}

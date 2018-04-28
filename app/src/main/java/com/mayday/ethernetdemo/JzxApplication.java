package com.mayday.ethernetdemo;

import android.app.Application;
import android.content.Context;

/**
 * Function:
 * Created by TianMing.Xiong on 18-4-9.
 */

public class JzxApplication extends Application {

    private static Context mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
    }

    public static Context getAppContext() {
        return mApp;
    }
}

package com.example.a3gz.weather.ui;

import android.app.Application;
import android.content.Context;

/**
 * Created by 3gz on 2017/5/19.
 */

public class MyApplication extends Application {

    private static Context context;
    @Override
    public void onCreate(){
        context=getApplicationContext();
    }
    public static Context getContextObject(){
        return context;
    }
}

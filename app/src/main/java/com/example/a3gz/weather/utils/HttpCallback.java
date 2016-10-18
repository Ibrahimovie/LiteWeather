package com.example.a3gz.weather.utils;

/**
 * Created by 3gz on 2016/10/18.
 */

public interface HttpCallback {

    void onFinish(String response);
    void onError(Exception e);
}

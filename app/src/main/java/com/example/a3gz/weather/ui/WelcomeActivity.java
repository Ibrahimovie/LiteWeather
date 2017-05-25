package com.example.a3gz.weather.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.a3gz.weather.R;

//import static com.baidu.location.d.j.R;

/**
 * Created by 3gz on 2017/5/19.
 */

public class WelcomeActivity extends Activity {

    private static final long SPLASH_DELAY_MILLIS = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_layout);
        // 使用Handler的postDelayed方法，3秒后执行跳转到MainActivity
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent intent=new Intent(WelcomeActivity.this,WeatherActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DELAY_MILLIS);
    }
}

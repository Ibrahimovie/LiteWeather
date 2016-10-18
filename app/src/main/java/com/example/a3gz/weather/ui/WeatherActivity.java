package com.example.a3gz.weather.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a3gz.weather.module.City;
import com.example.a3gz.weather.R;
import com.example.a3gz.weather.service.AutoUpdateService;
import com.example.a3gz.weather.utils.HttpCallback;
import com.example.a3gz.weather.utils.HttpUtil;
import com.example.a3gz.weather.utils.Utility;

/**
 * Created by 3gz on 2016/10/18.
 */

public class WeatherActivity extends Activity {
    //和风天气KEY
    public static final String WEATHER_KEY = "9cbae27f98b443c2ae5aa879e4350962";

    private ProgressDialog mProgressDialog;//进度条
    private SharedPreferences mSharedPreferences;//数据存储对象
    private SharedPreferences.Editor mEditor;
    public static final int REQUEST_CODE = 1;

    private Button mChangeCityButton;//小房子按钮
    private TextView mTextView_cityName;//标题栏城市名称
    private Button mRefreshButton;//刷新按钮
    private TextView mTextView_updateTime;//更新时间
    private TextView mTextView_current_date;//当前日期
    private TextView mTextView_weather_desp;//具体的天气情况
    private TextView mTextView_textView_temp1;//最低温度
    private TextView mTextView_textView_temp2;//最高温度

    private City mCity_current = new City();//当前显示的城市对象


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //有米广告管理器实例
        //AdManager.getInstance(this).init("8c8f79aef6457ac0", "d71c14f920b0e968", false);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);

        //实例化本地存储
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();

        //变更城市
        mChangeCityButton = (Button) findViewById(R.id.changeCity);
        mChangeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //启动ChooseCityActivity
                Intent intent = new Intent(WeatherActivity.this, ChooseCityActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        //实例化各个组建
        mTextView_cityName = (TextView) findViewById(R.id.city_name);
        mTextView_updateTime = (TextView) findViewById(R.id.publishTime);
        mTextView_current_date = (TextView) findViewById(R.id.current_date);
        mTextView_weather_desp = (TextView) findViewById(R.id.weather_desp);
        mTextView_textView_temp1 = (TextView) findViewById(R.id.temp1);
        mTextView_textView_temp2 = (TextView) findViewById(R.id.temp2);

        //刷新按钮
        mRefreshButton = (Button) findViewById(R.id.refresh);
        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //从服务器更新
                updateWeatherFromServer();
            }
        });

        //第一次安装的时候，判断本地存储还没有数据，默认获取hz的数据
        if (mSharedPreferences.getString("city_code", null) == null) {
            mCity_current.setCity_code("CN101210101");
            updateWeatherFromServer();
        } else {
            //有数据则从本地取出来，也就是上次访问的城市
            loadWeatherData(mSharedPreferences.getString("city_code", null), mSharedPreferences.getString("city_name_ch", null), mSharedPreferences.getString("update_time", null), mSharedPreferences.getString("data_now", null), mSharedPreferences.getString("txt_d", null), mSharedPreferences.getString("txt_n", null), mSharedPreferences.getString("tmp_min", null), mSharedPreferences.getString("tmp_max", null));
            //然后从服务器更新一次
            updateWeatherFromServer();
        }
        //启动自动更新服务（不过我这里没怎么使用到自动更新，我这里都是打开后实时更新的，可以打开后不从服务器更新，只从本地获取）
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);

        //有米广告栏，不多做解释，第一行代码里面解释很清楚，也很简单
        /**
        AdView adView = new AdView(this, AdSize.FIT_SCREEN);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.adLayout);
        linearLayout.addView(adView);
         */
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //数据从本地取出来
                loadWeatherData(mSharedPreferences.getString("city_code", null), mSharedPreferences.getString("city_name_ch", null), mSharedPreferences.getString("update_time", null), mSharedPreferences.getString("data_now", null), mSharedPreferences.getString("txt_d", null), mSharedPreferences.getString("txt_n", null), mSharedPreferences.getString("tmp_min", null), mSharedPreferences.getString("tmp_max", null));
            }
        }
    }

    //刷新各组件数据的封装
    private void loadWeatherData(String city_code, String city_name, String update_time, String current_data, String txt_d, String txt_n, String tmp_min, String tmp_max) {

        mTextView_cityName.setText(city_name);
        mTextView_updateTime.setText(update_time);
        mTextView_current_date.setText(current_data);

        if (txt_d.equals(txt_n)) {
            mTextView_weather_desp.setText(txt_d);
        } else {
            mTextView_weather_desp.setText(txt_d + "转" + txt_n);
        }
        mTextView_textView_temp1.setText(tmp_min + "℃");
        mTextView_textView_temp2.setText(tmp_max + "℃");

        mCity_current.setCity_name_ch(city_name);
        mCity_current.setCity_code(city_code);

    }

    //从服务器更新数据（CityChooseActivity中有相似方法）
    private void updateWeatherFromServer() {
        String address = "https://api.heweather.com/x3/weather?cityid=" + mCity_current.getCity_code() + "&key=" + WeatherActivity.WEATHER_KEY;
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallback() {
            @Override
            public void onFinish(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Utility.handleWeatherResponse(mEditor, response)) {
                            loadWeatherData(mSharedPreferences.getString("city_code", null), mSharedPreferences.getString("city_name_ch", null), mSharedPreferences.getString("update_time", null), mSharedPreferences.getString("data_now", null), mSharedPreferences.getString("txt_d", null), mSharedPreferences.getString("txt_n", null), mSharedPreferences.getString("tmp_min", null), mSharedPreferences.getString("tmp_max", null));
                            closeProgressDialog();
                        }
                    }
                });
            }

            @Override
            public void onError(final Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(WeatherActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showProgressDialog() {

        if (mProgressDialog == null) {

            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage("正在同步数据...");
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }

    private void closeProgressDialog() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }
}

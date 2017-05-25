package com.example.a3gz.weather.ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.example.a3gz.weather.City;
import com.example.a3gz.weather.R;
import com.example.a3gz.weather.db.WeatherDB;
import com.example.a3gz.weather.service.AutoUpdateService;
import com.example.a3gz.weather.utils.HttpCallback;
import com.example.a3gz.weather.utils.HttpUtil;
import com.example.a3gz.weather.utils.Utility;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import java.util.List;

/**
 * Created by 3gz on 2016/10/18.
 */

public class WeatherActivity extends ActionBarActivity {

    //和风天气KEY
    public static final String WEATHER_KEY = "9cbae27f98b443c2ae5aa879e4350962";
//    百度地图KEY
    public static final String MAP_KEY = "kGLBri67AyocHaUw4Ytn0vBxelum9riR";
//    日志Tag
    protected static final String ACTIVITY_TAG="request";

    private ProgressDialog mProgressDialog;//进度条
    private SharedPreferences mSharedPreferences;//SharedPreferences数据存储对象
    private SharedPreferences.Editor mEditor;//SharedPreferences操作对象
    private WeatherDB weatherDB;//数据库操作对象
    public static final int REQUEST_CODE = 1;

    private TextView mTextView_cityName;//标题栏城市名称
    private TextView mTextView_weather_desp;//具体的天气情况
    private TextView mTextView_textView_temp1;//最低温度
    private TextView mTextView_textView_temp2;//最高温度

    private TextView date_222;
    private TextView date_333;
    private TextView date_444;
    private TextView date_555;
    private TextView date_666;
    private TextView date_777;

    private TextView weather_2;
    private TextView weather_3;
    private TextView weather_4;
    private TextView weather_5;
    private TextView weather_6;
    private TextView weather_7;

    private TextView temperature_222_h;
    private TextView temperature_222_l;
    private TextView temperature_333_h;
    private TextView temperature_333_l;
    private TextView temperature_444_h;
    private TextView temperature_444_l;
    private TextView temperature_555_h;
    private TextView temperature_555_l;
    private TextView temperature_666_h;
    private TextView temperature_666_l;
    private TextView temperature_777_h;
    private TextView temperature_777_l;


    private TextView wearing_tippp;

    private Drawer.Result drawerResult = null;


    private City mCity_current = new City();//当前显示的城市对象

    private String provider;

    private LocationManager locationManager;

    private City local_city;

    private PullRefreshLayout pullRefreshLayout;

    private String addr;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);

        //实例化本地存储
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();
        weatherDB = WeatherDB.getInstance(this);//获取数据库处理对象

        //实例化各个组建
        mTextView_cityName = (TextView) findViewById(R.id.city_name);

        mTextView_cityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
                new Thread(){
                    public void run(){

                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        String city_name=mSharedPreferences.getString("city_name_ch", null);//传过去城市名
                        StringBuilder weather_general =new StringBuilder();
                        String weather_d=mSharedPreferences.getString("txt_d", null);
                        String weather_n=mSharedPreferences.getString("txt_n", null);
                        if (!weather_d.equals("")&&!weather_n.equals("")&&weather_d!=null&&weather_n!=null){
                            if (weather_d.equals(weather_n)){
                                weather_general.append("今天： ").append(weather_d);
                            }else{
                                weather_general.append("今天： ").append(weather_d).append("转").append(weather_n);
                            }
                        }
                        String tmp_max=mSharedPreferences.getString("tmp_max", null);
                        weather_general.append(" 。最高温度").append(tmp_max+"°"+",");
                        String tmp_min=mSharedPreferences.getString("tmp_min", null);
                        weather_general.append("最低温度").append(tmp_min+"°"+"。");
                        String air_qlty=mSharedPreferences.getString("air_quality",null);
                        weather_general.append("空气质量"+air_qlty+"。");
                        String we_general=weather_general.toString();//传过去天气概况

                        Log.v(ACTIVITY_TAG,"weather_general"+we_general);

                        String sr=mSharedPreferences.getString("info_sr",null);
                        String ss=mSharedPreferences.getString("info_ss",null);
                        String rain=mSharedPreferences.getString("info_rain",null);
                        String wet=mSharedPreferences.getString("info_wet",null);
                        String wind_dir=mSharedPreferences.getString("info_wind_dir",null);
                        String wind_spd=mSharedPreferences.getString("info_wind_spd",null);
                        String vis=mSharedPreferences.getString("info_see",null);
                        String rain_s=mSharedPreferences.getString("info_rain_sum",null);
                        String pres=mSharedPreferences.getString("info_press",null);
                        String purple=mSharedPreferences.getString("info_purple",null);
                        String outdoor=mSharedPreferences.getString("info_outdoor",null);
                        String sport=mSharedPreferences.getString("info_sport",null);

                        String sun_r="日出:    "+sr;//传过去日出
                        String sun_s="日落:    "+ss;//传过去日落
                        String rain_pro="降水概率:    "+rain+"%";//降水概率
                        String humidity="相对湿度:    "+wet+"%";//相对湿度
                        String wind="风速:    "+wind_dir+" 每秒"+wind_spd+"米";//风速
                        String vision="能见度:    "+vis+"公里";//能见度
                        String rain_sum="降水量:    "+rain_s+"毫米";//降水量
                        String air_press="气压:    "+pres+"百帕";//气压
                        String purple_exp="紫外线指数:    "+purple;//紫外线指数
                        String outdoor_exp="出行指数:    "+outdoor;//出行指数
                        String sport_exp="运动指数:    "+sport;//运动指数

                        Intent intent=new Intent(WeatherActivity.this,WeatherInfoActivity.class);
                        intent.putExtra("city_name",city_name);
                        intent.putExtra("weather_general",we_general);
                        intent.putExtra("sun_r",sun_r);
                        intent.putExtra("sun_s",sun_s);
                        intent.putExtra("rain_pro",rain_pro);
                        intent.putExtra("humidity",humidity);
                        intent.putExtra("wind",wind);
                        intent.putExtra("vision",vision);
                        intent.putExtra("rain_sum",rain_sum);
                        intent.putExtra("air_press",air_press);
                        intent.putExtra("purple_exp",purple_exp);
                        intent.putExtra("outdoor_exp",outdoor_exp);
                        intent.putExtra("sport_exp",sport_exp);

                        startActivity(intent);
                        closeProgressDialog();
                    }
                }.start();
            }
        });


        mTextView_weather_desp = (TextView) findViewById(R.id.weather_desp);
        mTextView_textView_temp1 = (TextView) findViewById(R.id.temp1);
        mTextView_textView_temp2 = (TextView) findViewById(R.id.temp2);
        date_222 = (TextView) findViewById(R.id.date_2);
        date_333 = (TextView) findViewById(R.id.date_3);
        date_444 = (TextView) findViewById(R.id.date_4);
        date_555 = (TextView) findViewById(R.id.date_5);
        date_666 = (TextView) findViewById(R.id.date_6);
        date_777 = (TextView) findViewById(R.id.date_7);

        weather_2 = (TextView) findViewById(R.id.weather_2);
        weather_3 = (TextView) findViewById(R.id.weather_3);
        weather_4 = (TextView) findViewById(R.id.weather_4);
        weather_5 = (TextView) findViewById(R.id.weather_5);
        weather_6 = (TextView) findViewById(R.id.weather_6);
        weather_7 = (TextView) findViewById(R.id.weather_7);

        temperature_222_h = (TextView) findViewById(R.id.temperature_2_h);
        temperature_222_l = (TextView) findViewById(R.id.temperature_2_l);
        temperature_333_h = (TextView) findViewById(R.id.temperature_3_h);
        temperature_333_l = (TextView) findViewById(R.id.temperature_3_l);
        temperature_444_h = (TextView) findViewById(R.id.temperature_4_h);
        temperature_444_l = (TextView) findViewById(R.id.temperature_4_l);
        temperature_555_h = (TextView) findViewById(R.id.temperature_5_h);
        temperature_555_l = (TextView) findViewById(R.id.temperature_5_l);
        temperature_666_h = (TextView) findViewById(R.id.temperature_6_h);
        temperature_666_l = (TextView) findViewById(R.id.temperature_6_l);
        temperature_777_h = (TextView) findViewById(R.id.temperature_7_h);
        temperature_777_l = (TextView) findViewById(R.id.temperature_7_l);

        wearing_tippp = (TextView) findViewById(R.id.wearing_tip);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pullRefreshLayout=(PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateWeatherFromServer();
                        pullRefreshLayout.setRefreshing(false);
                    }
                }, 1000);

            }
        });


        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providerList=locationManager.getAllProviders();
        if (providerList.contains(LocationManager.GPS_PROVIDER)){
            provider=LocationManager.GPS_PROVIDER;
        }else if (providerList.contains(LocationManager.NETWORK_PROVIDER)){
            provider=LocationManager.NETWORK_PROVIDER;
        }else{
            return;
        }
        final Location location=locationManager.getLastKnownLocation(provider);
        locationManager.requestLocationUpdates(provider,5000,1,locationListener);

        drawerResult = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.local).withIcon(FontAwesome.Icon.faw_home),
                        new PrimaryDrawerItem().withName(R.string.city).withIcon(FontAwesome.Icon.faw_cog),
                        new PrimaryDrawerItem().withName("map").withIcon(FontAwesome.Icon.faw_adn)
                )
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {

                        InputMethodManager inputMethodManager = (InputMethodManager) WeatherActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(WeatherActivity.this.getCurrentFocus().getWindowToken(), 0);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                    }
                }).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,
                                            long id, IDrawerItem drawerItem) {
                        if (drawerItem instanceof Nameable) {
                            if (position == 1) {
                                showProgressDialog();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(500);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        localWeatherInfo(location);
                                        closeProgressDialog();

                                    }
                                }).start();

                            } else if (position == 2) {
                                Intent intent = new Intent(WeatherActivity.this, ChooseCityActivity.class);
                                startActivityForResult(intent, REQUEST_CODE);
                            }else if (position==3){
                                Intent intent2 = new Intent(WeatherActivity.this, MapActivity.class);
                                startActivityForResult(intent2,REQUEST_CODE);
                            }

                        }

                    }
                }).build();


        Intent intent2=getIntent();
        //第一次安装的时候，判断本地存储还没有数据，默认获取hz的数据
        if (mSharedPreferences.getString("city_code", null) == null) {
            mCity_current.setCity_code("CN101210101");
            updateWeatherFromServer();
        } else if (intent2.getStringExtra("addresses")!=null){
            addr=intent2.getStringExtra("addresses");
            showProgressDialog();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    queryMapWeather(addr);
                }
            }).start();

        } else {
            //有数据则从本地取出来，也就是上次访问的城市
            loadWeatherData(mSharedPreferences.getString("city_code", null),
                    mSharedPreferences.getString("city_name_ch", null),
                    mSharedPreferences.getString("update_time", null),
                    mSharedPreferences.getString("data_now", null),
                    mSharedPreferences.getString("txt_d", null),
                    mSharedPreferences.getString("txt_n", null),
                    mSharedPreferences.getString("tmp_min", null),
                    mSharedPreferences.getString("tmp_max", null),
                    mSharedPreferences.getString("date_2", null),
                    mSharedPreferences.getString("date_3", null),
                    mSharedPreferences.getString("date_4", null),
                    mSharedPreferences.getString("date_5", null),
                    mSharedPreferences.getString("date_6", null),
                    mSharedPreferences.getString("date_7", null),
                    mSharedPreferences.getString("txt_d_2", null),
                    mSharedPreferences.getString("txt_n_2", null),
                    mSharedPreferences.getString("txt_d_3", null),
                    mSharedPreferences.getString("txt_n_3", null),
                    mSharedPreferences.getString("txt_d_4", null),
                    mSharedPreferences.getString("txt_n_4", null),
                    mSharedPreferences.getString("txt_d_5", null),
                    mSharedPreferences.getString("txt_n_5", null),
                    mSharedPreferences.getString("txt_d_6", null),
                    mSharedPreferences.getString("txt_n_6", null),
                    mSharedPreferences.getString("txt_d_7", null),
                    mSharedPreferences.getString("txt_n_7", null),
                    mSharedPreferences.getString("tmp_min_2", null),
                    mSharedPreferences.getString("tmp_max_2", null),
                    mSharedPreferences.getString("tmp_min_3", null),
                    mSharedPreferences.getString("tmp_max_3", null),
                    mSharedPreferences.getString("tmp_min_4", null),
                    mSharedPreferences.getString("tmp_max_4", null),
                    mSharedPreferences.getString("tmp_min_5", null),
                    mSharedPreferences.getString("tmp_max_5", null),
                    mSharedPreferences.getString("tmp_min_6", null),
                    mSharedPreferences.getString("tmp_max_6", null),
                    mSharedPreferences.getString("tmp_min_7", null),
                    mSharedPreferences.getString("tmp_max_7", null),
                    mSharedPreferences.getString("wearing_tip", null)
            );

            //然后从服务器更新一次
            updateWeatherFromServer();
        }





        //启动自动更新服务
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);


        NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(1);



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //数据从本地取出来
                loadWeatherData(mSharedPreferences.getString("city_code", null),
                        mSharedPreferences.getString("city_name_ch", null),
                        mSharedPreferences.getString("update_time", null),
                        mSharedPreferences.getString("data_now", null),
                        mSharedPreferences.getString("txt_d", null),
                        mSharedPreferences.getString("txt_n", null),
                        mSharedPreferences.getString("tmp_min", null),
                        mSharedPreferences.getString("tmp_max", null),
                        mSharedPreferences.getString("date_2", null),
                        mSharedPreferences.getString("date_3", null),
                        mSharedPreferences.getString("date_4", null),
                        mSharedPreferences.getString("date_5", null),
                        mSharedPreferences.getString("date_6", null),
                        mSharedPreferences.getString("date_7", null),
                        mSharedPreferences.getString("txt_d_2", null),
                        mSharedPreferences.getString("txt_n_2", null),
                        mSharedPreferences.getString("txt_d_3", null),
                        mSharedPreferences.getString("txt_n_3", null),
                        mSharedPreferences.getString("txt_d_4", null),
                        mSharedPreferences.getString("txt_n_4", null),
                        mSharedPreferences.getString("txt_d_5", null),
                        mSharedPreferences.getString("txt_n_5", null),
                        mSharedPreferences.getString("txt_d_6", null),
                        mSharedPreferences.getString("txt_n_6", null),
                        mSharedPreferences.getString("txt_d_7", null),
                        mSharedPreferences.getString("txt_n_7", null),
                        mSharedPreferences.getString("tmp_min_2", null),
                        mSharedPreferences.getString("tmp_max_2", null),
                        mSharedPreferences.getString("tmp_min_3", null),
                        mSharedPreferences.getString("tmp_max_3", null),
                        mSharedPreferences.getString("tmp_min_4", null),
                        mSharedPreferences.getString("tmp_max_4", null),
                        mSharedPreferences.getString("tmp_min_5", null),
                        mSharedPreferences.getString("tmp_max_5", null),
                        mSharedPreferences.getString("tmp_min_6", null),
                        mSharedPreferences.getString("tmp_max_6", null),
                        mSharedPreferences.getString("tmp_min_7", null),
                        mSharedPreferences.getString("tmp_max_7", null),
                        mSharedPreferences.getString("wearing_tip", null)


                );
            }
        }
    }

    //刷新各组件数据的封装
    private void loadWeatherData(String city_code, String city_name, String update_time, String current_data,
                                 String txt_d, String txt_n, String tmp_min, String tmp_max, String date_2,
                                 String date_3, String date_4, String date_5, String date_6, String date_7,
                                 String txt_d_2, String txt_n_2, String txt_d_3, String txt_n_3, String txt_d_4, String txt_n_4,
                                 String txt_d_5, String txt_n_5, String txt_d_6, String txt_n_6, String txt_d_7, String txt_n_7,
                                 String tmp_min_2, String tmp_max_2, String tmp_min_3, String tmp_max_3, String tmp_min_4, String tmp_max_4,
                                 String tmp_min_5, String tmp_max_5, String tmp_min_6, String tmp_max_6, String tmp_min_7, String tmp_max_7, String wearing_tip) {

        mTextView_cityName.setText(city_name);
//        mTextView_updateTime.setText(update_time);
//        mTextView_current_date.setText(current_data);
        date_222.setText(date_2);
        date_333.setText(date_3);
        date_444.setText(date_4);
        date_555.setText(date_5);
        date_666.setText(date_6);
        date_777.setText(date_7);

        if (txt_d.equals(txt_n)) {
            mTextView_weather_desp.setText(txt_d);
        } else {
            mTextView_weather_desp.setText(txt_d + "转" + txt_n);
        }
        if (txt_d_2.equals(txt_n_2)) {
            weather_2.setText(txt_d_2);
        } else {
            weather_2.setText(txt_d_2 + "转" + txt_n_2);
        }
        if (txt_d_3.equals(txt_n_3)) {
            weather_3.setText(txt_d_3);
        } else {
            weather_3.setText(txt_d_3 + "转" + txt_n_3);
        }
        if (txt_d_4.equals(txt_n_4)) {
            weather_4.setText(txt_d_4);
        } else {
            weather_4.setText(txt_d_4 + "转" + txt_n_4);
        }
        if (txt_d_5.equals(txt_n_5)) {
            weather_5.setText(txt_d_5);
        } else {
            weather_5.setText(txt_d_5 + "转" + txt_n_5);
        }
        if (txt_d_6.equals(txt_n_6)) {
            weather_6.setText(txt_d_6);
        } else {
            weather_6.setText(txt_d_6 + "转" + txt_n_6);
        }
        if (txt_d_7.equals(txt_n_7)) {
            weather_7.setText(txt_d_7);
        } else {
            weather_7.setText(txt_d_7 + "转" + txt_n_7);
        }
        mTextView_textView_temp1.setText(tmp_min + "°");
        mTextView_textView_temp2.setText(tmp_max + "°");

        temperature_222_h.setText(tmp_max_2);
        temperature_222_l.setText(tmp_min_2);
        temperature_333_h.setText(tmp_max_3);
        temperature_333_l.setText(tmp_min_3);
        temperature_444_h.setText(tmp_max_4);
        temperature_444_l.setText(tmp_min_4);
        temperature_555_h.setText(tmp_max_5);
        temperature_555_l.setText(tmp_min_5);
        temperature_666_h.setText(tmp_max_6);
        temperature_666_l.setText(tmp_min_6);
        temperature_777_h.setText(tmp_max_7);
        temperature_777_l.setText(tmp_min_7);

        wearing_tippp.setText("        " + wearing_tip);


        mCity_current.setCity_name_ch(city_name);
        mCity_current.setCity_code(city_code);

    }

    //从服务器更新数据
    private void updateWeatherFromServer() {
        String addressess = "https://api.heweather.com/x3/weather?cityid=" + mCity_current.getCity_code() + "&key=" + WeatherActivity.WEATHER_KEY;
//        showProgressDialog();
        Log.v(ACTIVITY_TAG,"weatherrequest========="+addressess);
        HttpUtil.sendHttpRequest(addressess, new HttpCallback() {
            @Override
            public void onFinish(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Utility.handleWeatherResponse(mEditor, response)) {
                            loadWeatherData(mSharedPreferences.getString("city_code", null),
                                    mSharedPreferences.getString("city_name_ch", null),
                                    mSharedPreferences.getString("update_time", null),
                                    mSharedPreferences.getString("data_now", null),
                                    mSharedPreferences.getString("txt_d", null),
                                    mSharedPreferences.getString("txt_n", null),
                                    mSharedPreferences.getString("tmp_min", null),
                                    mSharedPreferences.getString("tmp_max", null),
                                    mSharedPreferences.getString("date_2", null),
                                    mSharedPreferences.getString("date_3", null),
                                    mSharedPreferences.getString("date_4", null),
                                    mSharedPreferences.getString("date_5", null),
                                    mSharedPreferences.getString("date_6", null),
                                    mSharedPreferences.getString("date_7", null),
                                    mSharedPreferences.getString("txt_d_2", null),
                                    mSharedPreferences.getString("txt_n_2", null),
                                    mSharedPreferences.getString("txt_d_3", null),
                                    mSharedPreferences.getString("txt_n_3", null),
                                    mSharedPreferences.getString("txt_d_4", null),
                                    mSharedPreferences.getString("txt_n_4", null),
                                    mSharedPreferences.getString("txt_d_5", null),
                                    mSharedPreferences.getString("txt_n_5", null),
                                    mSharedPreferences.getString("txt_d_6", null),
                                    mSharedPreferences.getString("txt_n_6", null),
                                    mSharedPreferences.getString("txt_d_7", null),
                                    mSharedPreferences.getString("txt_n_7", null),
                                    mSharedPreferences.getString("tmp_min_2", null),
                                    mSharedPreferences.getString("tmp_max_2", null),
                                    mSharedPreferences.getString("tmp_min_3", null),
                                    mSharedPreferences.getString("tmp_max_3", null),
                                    mSharedPreferences.getString("tmp_min_4", null),
                                    mSharedPreferences.getString("tmp_max_4", null),
                                    mSharedPreferences.getString("tmp_min_5", null),
                                    mSharedPreferences.getString("tmp_max_5", null),
                                    mSharedPreferences.getString("tmp_min_6", null),
                                    mSharedPreferences.getString("tmp_max_6", null),
                                    mSharedPreferences.getString("tmp_min_7", null),
                                    mSharedPreferences.getString("tmp_max_7", null),
                                    mSharedPreferences.getString("wearing_tip", null)

                            );
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


    private void localWeatherInfo(Location location){


        StringBuilder address=new StringBuilder();
        address.append("http://api.map.baidu.com/geocoder/v2/?callback=renderReverse&location=");
        address.append(location.getLatitude()).append(",");
        address.append(location.getLongitude());
        address.append("&output=json&pois=1&ak=").append(MAP_KEY).
                append("&mcode=C3:CF:DA:34:CA:AC:16:F5:C9:06:FF:63:C1:62:47:38:78:5E:C5:13;com.example.a3gz.weather");
        String addresses=address.toString();
        Log.v(ACTIVITY_TAG,"REQUEST========="+addresses);

        showProgressDialog();
        HttpUtil.sendLocalHttpRequest(addresses, new HttpCallback() {
            @Override
            public void onFinish(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        local_city=Utility.handleLocationResponse(weatherDB,response);

                        Log.v(ACTIVITY_TAG,"BEFORE QUERYLOCALWEATHERFROMSERVER");

                        queryLocalWeatherFromServer();

                        Log.v(ACTIVITY_TAG,"AFTER QUERYLOCALWEATHERFROMSERVER");

                        closeProgressDialog();

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
            mProgressDialog.setProgress(1000);
            mProgressDialog.setMessage("loading...");
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }

    private void closeProgressDialog() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    LocationListener locationListener=new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            localWeatherInfo(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    private void queryLocalWeatherFromServer() {

        String address = "https://api.heweather.com/x3/weather?cityid=" + local_city.getCity_code()
                + "&key=" + WeatherActivity.WEATHER_KEY;
        showProgressDialog();

        HttpUtil.sendHttpRequest(address, new HttpCallback() {
            @Override
            public void onFinish(final String response) {
                //将从服务器获取的JSON数据进行解析
                if (Utility.handleWeatherResponse(mEditor, response)) {
                    //对线程的处理
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (Utility.handleWeatherResponse(mEditor, response)) {
                                loadWeatherData(mSharedPreferences.getString("city_code", null),
                                        mSharedPreferences.getString("city_name_ch", null),
                                        mSharedPreferences.getString("update_time", null),
                                        mSharedPreferences.getString("data_now", null),
                                        mSharedPreferences.getString("txt_d", null),
                                        mSharedPreferences.getString("txt_n", null),
                                        mSharedPreferences.getString("tmp_min", null),
                                        mSharedPreferences.getString("tmp_max", null),
                                        mSharedPreferences.getString("date_2", null),
                                        mSharedPreferences.getString("date_3", null),
                                        mSharedPreferences.getString("date_4", null),
                                        mSharedPreferences.getString("date_5", null),
                                        mSharedPreferences.getString("date_6", null),
                                        mSharedPreferences.getString("date_7", null),
                                        mSharedPreferences.getString("txt_d_2", null),
                                        mSharedPreferences.getString("txt_n_2", null),
                                        mSharedPreferences.getString("txt_d_3", null),
                                        mSharedPreferences.getString("txt_n_3", null),
                                        mSharedPreferences.getString("txt_d_4", null),
                                        mSharedPreferences.getString("txt_n_4", null),
                                        mSharedPreferences.getString("txt_d_5", null),
                                        mSharedPreferences.getString("txt_n_5", null),
                                        mSharedPreferences.getString("txt_d_6", null),
                                        mSharedPreferences.getString("txt_n_6", null),
                                        mSharedPreferences.getString("txt_d_7", null),
                                        mSharedPreferences.getString("txt_n_7", null),
                                        mSharedPreferences.getString("tmp_min_2", null),
                                        mSharedPreferences.getString("tmp_max_2", null),
                                        mSharedPreferences.getString("tmp_min_3", null),
                                        mSharedPreferences.getString("tmp_max_3", null),
                                        mSharedPreferences.getString("tmp_min_4", null),
                                        mSharedPreferences.getString("tmp_max_4", null),
                                        mSharedPreferences.getString("tmp_min_5", null),
                                        mSharedPreferences.getString("tmp_max_5", null),
                                        mSharedPreferences.getString("tmp_min_6", null),
                                        mSharedPreferences.getString("tmp_max_6", null),
                                        mSharedPreferences.getString("tmp_min_7", null),
                                        mSharedPreferences.getString("tmp_max_7", null),
                                        mSharedPreferences.getString("wearing_tip", null)

                                );
                                closeProgressDialog();
                            }

                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(WeatherActivity.this, "数据同步失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    private void queryMapWeather(String address){


        HttpUtil.sendLocalHttpRequest(address, new HttpCallback() {
            @Override
            public void onFinish(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        local_city=Utility.handleLocationResponse(weatherDB,response);

                        queryLocalWeatherFromServer();


                        closeProgressDialog();

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




    protected void onDestroy(){
        super.onDestroy();
        if (locationManager!=null){
            locationManager.removeUpdates(locationListener);

        }
    }




}

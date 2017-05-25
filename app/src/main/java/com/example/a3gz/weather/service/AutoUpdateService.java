package com.example.a3gz.weather.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.a3gz.weather.R;
import com.example.a3gz.weather.receiver.AutoUpdateReceiver;
import com.example.a3gz.weather.ui.WeatherActivity;
import com.example.a3gz.weather.utils.HttpCallback;
import com.example.a3gz.weather.utils.HttpUtil;
import com.example.a3gz.weather.utils.Utility;

/**
 * Created by 3gz on 2016/10/18.
 */

public class AutoUpdateService extends Service{

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String city;
    String weather;
    String weather_d;
    String weather_n;
    String temp_max;
    String temp_min;
    protected static final String ACTIVITY_TAG="NOTIFICATION";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
                notifyWeather();
            }
        }).start();

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int time = 8*60*60 * 1000;
        long triggerTime = SystemClock.currentThreadTimeMillis() + time;
        Intent intent1 = new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent1, 0);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    private void updateWeather() {

        String address = "https://api.heweather.com/x3/weather?cityid=CN101210101&key=" + WeatherActivity.WEATHER_KEY;

        HttpUtil.sendHttpRequest(address, new HttpCallback() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(editor, response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void notifyWeather(){

        String address = "https://api.heweather.com/x3/weather?cityid=CN101210101&key=" + WeatherActivity.WEATHER_KEY;
        HttpUtil.sendHttpRequest(address, new HttpCallback() {
            @Override
            public void onFinish(String response) {
                Log.v(ACTIVITY_TAG,"respnose=========="+response);
                Utility.handleWeatherResponse(editor, response);
                city=sharedPreferences.getString("city_name_ch",null);
                Log.v(ACTIVITY_TAG,"city=========="+city);

                weather_d=sharedPreferences.getString("txt_d", null);
                weather_n=sharedPreferences.getString("txt_n", null);
                if (weather_d.equals(weather_n)) {
                    weather=weather_d;
                } else {
                    weather=weather_d + "转" + weather_n;
                }
                temp_max=sharedPreferences.getString("tmp_max", null);
                temp_min=sharedPreferences.getString("tmp_min", null);

                NotificationManager notificationManager =(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Notification notification=new Notification(R.drawable.app_ico_notify,"天气", System.currentTimeMillis());
                Intent intent=new Intent(AutoUpdateService.this,WeatherActivity.class);
                PendingIntent pendingIntent=PendingIntent.getActivity(AutoUpdateService.this,0,intent,
                        PendingIntent.FLAG_CANCEL_CURRENT);
                notification.setLatestEventInfo(AutoUpdateService.this,"当前城市："+city,weather+"    气温："
                        +temp_min+" ~ "+temp_max,pendingIntent);
                notificationManager.notify(1,notification);

            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });

    }

}

package com.example.a3gz.weather.utils;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.example.a3gz.weather.City;
import com.example.a3gz.weather.db.WeatherDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 3gz on 2016/10/18.
 * 解析服务器返回的数据
 */

public class Utility {

    protected static final String ACTIVITY_TAG="handleResponse";
    protected static final String ACTIVITY_TAGE="handleLocationResponse";

//解析位置
    public synchronized static City handleLocationResponse(WeatherDB weatherDB,String response){

        Log.v(Utility.ACTIVITY_TAGE,"response"+response);

        City city3=new City();
        if (!TextUtils.isEmpty(response)) {
            try {
                //Json解析
                String response1=response.substring(29,response.length()-1);
                Log.v(Utility.ACTIVITY_TAGE,"response1"+response1);
                JSONObject result =new JSONObject(response1).getJSONObject("result");
                JSONObject address_component=result.getJSONObject("addressComponent");
                String city1=address_component.getString("city");
                String city=city1.substring(0,city1.length()-1);
                Log.v(Utility.ACTIVITY_TAGE,"city:"+city);
                city3=weatherDB.getCityByName(city);



            } catch (Exception e) {
                e.printStackTrace();
            }
            return city3;
        }
        return null;

    }


    //处理从服务器获取的数据
    public synchronized static boolean handleCityResponse(WeatherDB weatherDB, String response) {

        if (!TextUtils.isEmpty(response)) {
            try {
                //Json解析
                JSONArray jsonArray = new JSONObject(response).getJSONArray("city_info");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject city_info = jsonArray.getJSONObject(i);
                    City city = new City();
                    String city_name_ch = city_info.getString("city");
                    String city_name_en = "";
                    String city_code = city_info.getString("id");
                    city.setCity_code(city_code);
                    city.setCity_name_en(city_name_en);
                    city.setCity_name_ch(city_name_ch);
                    weatherDB.saveCity(city);//将解析出来的数据存储到City表

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    //处理从服务器返回的天气信息
    //数据是JSON,存储到sharedpreferences文件中
    public synchronized static boolean handleWeatherResponse(SharedPreferences.Editor editor, String response) {

        Log.v(Utility.ACTIVITY_TAG,"response"+response);
       if (!TextUtils.isEmpty(response)) {
            try {

                JSONArray jsonArray = new JSONObject(response).getJSONArray("HeWeather data service 3.0");
                JSONObject weather_info_all = jsonArray.getJSONObject(0);
                JSONObject weather_info_basic = weather_info_all.getJSONObject("basic");
                JSONObject weather_info_qlty = weather_info_all.getJSONObject("aqi");
                JSONObject weather_info_quality=weather_info_qlty.getJSONObject("city");
                editor.putString("air_quality", weather_info_quality.getString("qlty"));//空气质量
                editor.putString("city_name_ch", weather_info_basic.getString("city"));//城市名
                editor.putString("city_code", weather_info_basic.getString("id"));

                JSONObject weather_info_basic_update = weather_info_basic.getJSONObject("update");
                //然后再根据这个对象获取名称是loc的数据信息
                editor.putString("update_time", weather_info_basic_update.getString("loc"));

                //关于天气的所有信息都是在daily_forecast名称下面，daily_forecast后面是[符号，说明这也是一个JSON数组
                //所以先根据名称获取JSONArray对象
                JSONArray weather_info_daily_forecast = weather_info_all.getJSONArray("daily_forecast");

                //第一个元素是当前的日期相关的天气数据，获取出来的是一个JSONObject
                //今天的天气
                JSONObject weather_info_now_forecast = weather_info_daily_forecast.getJSONObject(0);
                //date是可以直接获取的，因为date后面是没有｛｝的
                editor.putString("data_now", weather_info_now_forecast.getString("date"));//当前日期
                editor.putString("info_rain", weather_info_now_forecast.getString("pop"));//降水概率
                editor.putString("info_wet", weather_info_now_forecast.getString("hum"));//相对湿度
                editor.putString("info_see", weather_info_now_forecast.getString("vis"));//能见度
                editor.putString("info_rain_sum", weather_info_now_forecast.getString("pcpn"));//降水量
                editor.putString("info_press", weather_info_now_forecast.getString("pres"));//气压
                //tmp节点是当前的温度，包含最低和最高,这是一个JSONObject
                JSONObject weather_info_now_forecast_tmp = weather_info_now_forecast.getJSONObject("tmp");
                editor.putString("tmp_min", weather_info_now_forecast_tmp.getString("min"));
                editor.putString("tmp_max", weather_info_now_forecast_tmp.getString("max"));

                //cond是当前的实际天气描述，获取方法和tmp是一样的
                JSONObject weather_info_now_forecast_cond = weather_info_now_forecast.getJSONObject("cond");
                editor.putString("txt_d", weather_info_now_forecast_cond.getString("txt_d"));//天气情况前
                editor.putString("txt_n", weather_info_now_forecast_cond.getString("txt_n"));//天气情况后

//                日出日落
                JSONObject weather_info_sun= weather_info_now_forecast.getJSONObject("astro");
                editor.putString("info_sr", weather_info_sun.getString("sr"));
                editor.putString("info_ss", weather_info_sun.getString("ss"));
//风
                JSONObject weather_info_wind= weather_info_now_forecast.getJSONObject("wind");
                editor.putString("info_wind_dir", weather_info_wind.getString("dir"));//风向
                editor.putString("info_wind_spd", weather_info_wind.getString("spd"));//风速


                //第2天
                JSONObject weather_info_2_forecast = weather_info_daily_forecast.getJSONObject(1);
                editor.putString("date_2",weather_info_2_forecast.getString("date").substring(5));

                JSONObject weather_info_2_forecast_cond=weather_info_2_forecast.getJSONObject("cond");
                editor.putString("txt_d_2", weather_info_2_forecast_cond.getString("txt_d"));//天气情况前
                editor.putString("txt_n_2", weather_info_2_forecast_cond.getString("txt_n"));//天气情况后

                JSONObject weather_info_2_forecast_tmp = weather_info_2_forecast.getJSONObject("tmp");
                editor.putString("tmp_min_2", weather_info_2_forecast_tmp.getString("min"));
                editor.putString("tmp_max_2", weather_info_2_forecast_tmp.getString("max"));

                //第3天
                JSONObject weather_info_3_forecast = weather_info_daily_forecast.getJSONObject(2);
                editor.putString("date_3",weather_info_3_forecast.getString("date").substring(5));

                JSONObject weather_info_3_forecast_cond=weather_info_3_forecast.getJSONObject("cond");
                editor.putString("txt_d_3", weather_info_3_forecast_cond.getString("txt_d"));//天气情况前
                editor.putString("txt_n_3", weather_info_3_forecast_cond.getString("txt_n"));//天气情况后

                JSONObject weather_info_3_forecast_tmp = weather_info_3_forecast.getJSONObject("tmp");
                editor.putString("tmp_min_3", weather_info_3_forecast_tmp.getString("min"));
                editor.putString("tmp_max_3", weather_info_3_forecast_tmp.getString("max"));

                //第4天
                JSONObject weather_info_4_forecast = weather_info_daily_forecast.getJSONObject(3);
                editor.putString("date_4",weather_info_4_forecast.getString("date").substring(5));

                JSONObject weather_info_4_forecast_cond=weather_info_4_forecast.getJSONObject("cond");
                editor.putString("txt_d_4", weather_info_4_forecast_cond.getString("txt_d"));//天气情况前
                editor.putString("txt_n_4", weather_info_4_forecast_cond.getString("txt_n"));//天气情况后

                JSONObject weather_info_4_forecast_tmp = weather_info_4_forecast.getJSONObject("tmp");
                editor.putString("tmp_min_4", weather_info_4_forecast_tmp.getString("min"));
                editor.putString("tmp_max_4", weather_info_4_forecast_tmp.getString("max"));

                //第5天
                JSONObject weather_info_5_forecast = weather_info_daily_forecast.getJSONObject(4);
                editor.putString("date_5",weather_info_5_forecast.getString("date").substring(5));

                JSONObject weather_info_5_forecast_cond=weather_info_5_forecast.getJSONObject("cond");
                editor.putString("txt_d_5", weather_info_5_forecast_cond.getString("txt_d"));//天气情况前
                editor.putString("txt_n_5", weather_info_5_forecast_cond.getString("txt_n"));//天气情况后

                JSONObject weather_info_5_forecast_tmp = weather_info_5_forecast.getJSONObject("tmp");
                editor.putString("tmp_min_5", weather_info_5_forecast_tmp.getString("min"));
                editor.putString("tmp_max_5", weather_info_5_forecast_tmp.getString("max"));

                //第6天
                JSONObject weather_info_6_forecast = weather_info_daily_forecast.getJSONObject(5);
                editor.putString("date_6",weather_info_6_forecast.getString("date").substring(5));

                JSONObject weather_info_6_forecast_cond=weather_info_6_forecast.getJSONObject("cond");
                editor.putString("txt_d_6", weather_info_6_forecast_cond.getString("txt_d"));//天气情况前
                editor.putString("txt_n_6", weather_info_6_forecast_cond.getString("txt_n"));//天气情况后

                JSONObject weather_info_6_forecast_tmp = weather_info_6_forecast.getJSONObject("tmp");
                editor.putString("tmp_min_6", weather_info_6_forecast_tmp.getString("min"));
                editor.putString("tmp_max_6", weather_info_6_forecast_tmp.getString("max"));

                //第7天
                JSONObject weather_info_7_forecast = weather_info_daily_forecast.getJSONObject(6);
                editor.putString("date_7",weather_info_7_forecast.getString("date").substring(5));

                JSONObject weather_info_7_forecast_cond=weather_info_7_forecast.getJSONObject("cond");
                editor.putString("txt_d_7", weather_info_7_forecast_cond.getString("txt_d"));//天气情况前
                editor.putString("txt_n_7", weather_info_7_forecast_cond.getString("txt_n"));//天气情况后

                JSONObject weather_info_7_forecast_tmp = weather_info_7_forecast.getJSONObject("tmp");
                editor.putString("tmp_min_7", weather_info_7_forecast_tmp.getString("min"));
                editor.putString("tmp_max_7", weather_info_7_forecast_tmp.getString("max"));


                JSONObject suggestions = weather_info_all.getJSONObject("suggestion");
                JSONObject wearing = suggestions.getJSONObject("drsg");
                editor.putString("wearing_tip",wearing.getString("txt"));//穿衣指数
                JSONObject ziwaixian = suggestions.getJSONObject("uv");
                editor.putString("info_purple",ziwaixian.getString("txt"));//紫外线指数
                JSONObject outdoor = suggestions.getJSONObject("trav");
                editor.putString("info_outdoor",outdoor.getString("txt"));//出行指数
                JSONObject sport = suggestions.getJSONObject("sport");
                editor.putString("info_sport",sport.getString("txt"));//运动指数
                //最后提交
                editor.commit();
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}

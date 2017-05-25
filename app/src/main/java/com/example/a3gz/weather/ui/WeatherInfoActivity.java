package com.example.a3gz.weather.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.a3gz.weather.R;

/**
 * Created by 3gz on 2017/5/5.
 */

public class WeatherInfoActivity extends Activity{

    private TextView info_city_name;
    private TextView info_general_weather;
    private TextView info_sr;
    private TextView info_ss;
    private TextView info_rain;
    private TextView info_wet;
    private TextView info_wind;
    private TextView info_see;
    private TextView info_rain_sum;
    private TextView info_press;
    private TextView info_purple;
    private TextView info_outdoor;
    private TextView info_sport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        setContentView(R.layout.weather_info);
        info_city_name=(TextView) findViewById(R.id.info_city_name);
        info_general_weather=(TextView) findViewById(R.id.info_general_weather);
        info_sr=(TextView) findViewById(R.id.info_sr);
        info_ss=(TextView) findViewById(R.id.info_ss);
        info_rain=(TextView) findViewById(R.id.info_rain);
        info_wet=(TextView) findViewById(R.id.info_wet);
        info_wind=(TextView) findViewById(R.id.info_wind);
        info_see=(TextView) findViewById(R.id.info_see);
        info_rain_sum=(TextView) findViewById(R.id.info_rain_sum);
        info_press=(TextView) findViewById(R.id.info_press);
        info_purple=(TextView) findViewById(R.id.info_purple);
        info_outdoor=(TextView) findViewById(R.id.info_outdoor);
        info_sport=(TextView) findViewById(R.id.info_sport);

        Intent intent=getIntent();
        String city_name=intent.getStringExtra("city_name");
        String weather_general=intent.getStringExtra("weather_general");
        String sun_r=intent.getStringExtra("sun_r");
        String sun_s=intent.getStringExtra("sun_s");
        String rain_pro=intent.getStringExtra("rain_pro");
        String humidity=intent.getStringExtra("humidity");
        String wind=intent.getStringExtra("wind");
        String vision=intent.getStringExtra("vision");
        String rain_sum=intent.getStringExtra("rain_sum");
        String air_press=intent.getStringExtra("air_press");
        String purple_exp=intent.getStringExtra("purple_exp");
        String outdoor_exp=intent.getStringExtra("outdoor_exp");
        String sport_exp=intent.getStringExtra("sport_exp");

        info_city_name.setText(city_name);
        info_general_weather.setText(weather_general);
        info_sr.setText(sun_r);
        info_ss.setText(sun_s);
        info_rain.setText(rain_pro);
        info_wet.setText(humidity);
        info_wind.setText(wind);
        info_see.setText(vision);
        info_rain_sum.setText(rain_sum);
        info_press.setText(air_press);
        info_purple.setText(purple_exp);
        info_outdoor.setText(outdoor_exp);
        info_sport.setText(sport_exp);

    }


}

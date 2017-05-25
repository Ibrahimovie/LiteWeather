package com.example.a3gz.weather.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.a3gz.weather.City;
import com.example.a3gz.weather.R;
import com.example.a3gz.weather.db.WeatherDB;

import java.util.List;

import static com.example.a3gz.weather.ui.WeatherActivity.MAP_KEY;
import static com.example.a3gz.weather.ui.WeatherActivity.REQUEST_CODE;


/**
 * Created by 3gz on 2017/5/19.
 */

public class MapActivity extends Activity {

    protected static final String ACTIVITY_TAG="LATITUDE";
    private MapView mapView;
    private BaiduMap baiduMap;
    private LocationManager locationManager;
    private String provider;
    private boolean isFirstLocate=true;
    private City local_city;
    private SharedPreferences mSharedPreferences;//SharedPreferences数据存储对象
    private SharedPreferences.Editor mEditor;//SharedPreferences操作对象
    private WeatherDB weatherDB;//数据库操作对象
    private ProgressDialog mProgressDialog;//进度条


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.map_layout);
        
        mapView = (MapView) findViewById(R.id.map_view);
        baiduMap=mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        List<String> providerList=locationManager.getAllProviders();
        if (providerList.contains(LocationManager.GPS_PROVIDER)){
            provider=LocationManager.GPS_PROVIDER;
        }else if (providerList.contains(LocationManager.NETWORK_PROVIDER)){
            provider=LocationManager.NETWORK_PROVIDER;
        }else{
            return;
        }
        Location location=locationManager.getLastKnownLocation(provider);
        if (location!=null){
            navigateTo(location);
        }
        locationManager.requestLocationUpdates(provider,5000,1, locationListener);
    }

    private void navigateTo(Location location){
        if (isFirstLocate){
            LatLng ll=new LatLng(location.getLatitude(),location.getLongitude());
            MapStatusUpdate update= MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);
            update=MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);
            isFirstLocate=false;
        }

        MyLocationData.Builder locationBuilder=new MyLocationData.Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData =locationBuilder.build();
        baiduMap.setMyLocationData(locationData);

        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                StringBuilder address=new StringBuilder();
                address.append("http://api.map.baidu.com/geocoder/v2/?callback=renderReverse&location=");
                address.append(latLng.latitude).append(",");
                address.append(latLng.longitude);
                address.append("&output=json&pois=1&ak=").append(MAP_KEY).
                        append("&mcode=C3:CF:DA:34:CA:AC:16:F5:C9:06:FF:63:C1:62:47:38:78:5E:C5:13;com.example.a3gz.weather");
                String addresses=address.toString();
                Intent intent=new Intent(MapActivity.this,WeatherActivity.class);
                intent.putExtra("addresses",addresses);
                startActivityForResult(intent,REQUEST_CODE);
                overridePendingTransition(R.anim.push_bottom_in,R.anim.push_bottom_out);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });


    }
    LocationListener locationListener=new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location!=null){
                navigateTo(location);
            }
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
}

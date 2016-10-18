package com.example.a3gz.weather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.a3gz.weather.service.AutoUpdateService;

/**
 * Created by 3gz on 2016/10/18.
 */

public class AutoUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent intent_for_service = new Intent(context, AutoUpdateService.class);
        context.startService(intent_for_service);

    }
}

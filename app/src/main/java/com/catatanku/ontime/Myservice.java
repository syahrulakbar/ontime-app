package com.catatanku.ontime;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.security.Provider;

public class Myservice extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        // START YOUR TASKS
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // STOP YOUR TASKS
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }
}

package com.example.p2_vaio.status;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by p2 on 18/12/17.
 */

public class FireApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}

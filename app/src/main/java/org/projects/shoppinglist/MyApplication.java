package org.projects.shoppinglist;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by Kristoffer on 05-05-2016.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);

    }
}


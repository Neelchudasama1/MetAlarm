package com.metalarm;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by qtm-purvesh on 20/5/16.
 */
public class MyApplication extends Application {

    private Context context;

    public Context getContext() { return context; }

    public void setContext(Context context_) { context = context_; }

    @Override
    public void onCreate() {
        super.onCreate();
        // Mint.initAndStartSession(context, "78f9bddd");
        this.context = getApplicationContext();

    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
}

package com.curry.stephen.cachevideo;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.danikula.videocache.HttpProxyCacheServer;

import java.io.File;

/**
 * Created by LingChong on 2016/9/9 0009.
 */
public class MyApplication extends Application {
    private HttpProxyCacheServer mHttpProxyCacheServer;

    private static final String TAG = MyApplication.class.getSimpleName();

    public static HttpProxyCacheServer getProxy(Context context) {
        MyApplication myApplication = (MyApplication) context.getApplicationContext();
        return myApplication.mHttpProxyCacheServer == null ?
                (myApplication.mHttpProxyCacheServer = myApplication.newProxy()) : myApplication.mHttpProxyCacheServer;
    }

    private File getCacheFile() {
        return new File(Environment.getExternalStorageDirectory(), "orange1.mp4");
    }

    private HttpProxyCacheServer newProxy() {
        Log.d(TAG, String.format("Cache File Path: %s.", getCacheFile().getAbsoluteFile()));
        return new HttpProxyCacheServer.Builder(this).cacheDirectory(getCacheFile()).build();
    }
}

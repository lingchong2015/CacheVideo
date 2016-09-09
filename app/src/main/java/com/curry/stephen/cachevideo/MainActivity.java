package com.curry.stephen.cachevideo;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.VideoView;

import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;

import java.io.File;

public class MainActivity extends AppCompatActivity implements CacheListener {

    private String mURL;

    private VideoView mVideoView;
    private ImageView mImageView;
    private SeekBar mSeekBar;

    private final VideoProgressUpdater mVideoProgressUpdater = new VideoProgressUpdater();
    private static final String TEST_VIDEO_URL = "https://raw.githubusercontent.com/danikula/AndroidVideoCache/master/files/orange1.mp4";
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMembers();
        initViews();
        checkCachedState();
        startVideo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
        MyApplication.getProxy(this).unregisterCacheListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoProgressUpdater.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoProgressUpdater.stop();
    }

    private void initMembers() {
        mURL = TEST_VIDEO_URL;
    }

    private void initViews() {
        mVideoView = (VideoView) findViewById(R.id.video_view);
        mImageView = (ImageView) findViewById(R.id.image_view);
        mSeekBar = (SeekBar) findViewById(R.id.progress_bar);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int videoPosition = mVideoView.getDuration() * mSeekBar.getProgress() / 100;
                mVideoView.seekTo(videoPosition);
                mVideoView.start();
            }
        });
    }

    private void checkCachedState() {
        HttpProxyCacheServer proxy = MyApplication.getProxy(this);
        boolean fullyCached = proxy.isCached(mURL);
        setCachedState(fullyCached);
    }

    private void startVideo() {
        HttpProxyCacheServer httpProxyCacheServer = MyApplication.getProxy(this);
        httpProxyCacheServer.registerCacheListener(this, mURL);
        mVideoView.setVideoPath(httpProxyCacheServer.getProxyUrl(mURL));
        mVideoView.start();
    }

    private void setCachedState(boolean cached) {
        int statusIconId = cached ? R.drawable.ic_cloud_done : R.drawable.ic_cloud_download;
        mImageView.setImageResource(statusIconId);
    }

    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
        mSeekBar.setSecondaryProgress(percentsAvailable);
        setCachedState(percentsAvailable == 100);
        Log.d(TAG, String.format("onCacheAvailable. percents: %d, file: %s, url: %s", percentsAvailable, cacheFile, url));
    }

    private void updateVideoProgress() {
        int videoProgress = mVideoView.getCurrentPosition() * 100 / mVideoView.getDuration();
        mSeekBar.setProgress(videoProgress);
    }

    private final class VideoProgressUpdater extends Handler {

        public void start() {
            sendEmptyMessage(0);
        }

        public void stop() {
            removeMessages(0);
        }

        @Override
        public void handleMessage(Message msg) {
            updateVideoProgress();
            sendEmptyMessageDelayed(0, 500);
        }
    }
}

package com.adrianczuczka.songle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

public class NetworkActivity extends FragmentActivity implements DownloadCallback<String> {

    // Keep a reference to the NetworkFragment, which owns the AsyncTask object
    // that is used to execute network ops.
    private NetworkFragment mNetworkFragment;

    // Boolean telling us whether a download is in progress, so we don't trigger overlapping
    // downloads with consecutive button clicks.
    private boolean mDownloading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);
        String url = getIntent().getStringExtra("url");
        mNetworkFragment = NetworkFragment.getInstance(getFragmentManager(), url);
        startDownload();
    }

    private void startDownload() {
        if (!mDownloading && mNetworkFragment != null) {
            // Execute the async download.
            mNetworkFragment.startDownload(this);
            mDownloading = true;
        }
    }

    public void updateFromDownload(String result) {
        // Update your UI here based on result of download.
        Intent resultIntent = new Intent();
        resultIntent.putExtra("string", result);
        Log.e("gameUI", String.valueOf(getIntent().hasExtra("number")));
        if(getIntent().hasExtra("number")){
            resultIntent.putExtra("number", getIntent().getStringExtra("number"));
        }
        if (getIntent().hasExtra("kml")){
            resultIntent.putExtra("kml", getIntent().getStringExtra("kml"));
        }
        if (getIntent().hasExtra("title")){
            resultIntent.putExtra("title", getIntent().getStringExtra("title"));
        }
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }


    /*public void sendStream(InputStream stream) {
        // Update your UI here based on result of download.
        Intent returnIntent = getIntent();
        returnIntent.putExtra("stream", stream);
        returnIntent;
    }*/

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {
        switch (progressCode) {
            // You can add UI behavior for progress updates here.
            case DownloadCallback.Progress.ERROR:
            //...
                break;
            case DownloadCallback.Progress.CONNECT_SUCCESS:
            //...
                break;
            case DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS:
            //...
                break;
            case DownloadCallback.Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:
            //...
                break;
            case DownloadCallback.Progress.PROCESS_INPUT_STREAM_SUCCESS:
            //...
                break;
        }
    }

    @Override
    public void finishDownloading() {
        mDownloading = false;
        if (mNetworkFragment != null) {
            mNetworkFragment.cancelDownload();
        }
    }
}

package com.adrianczuczka.songle;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.maps.android.data.kml.KmlLayer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

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
        Log.e("GameUI", "made it to network activity");
        setContentView(R.layout.activity_network);
        mNetworkFragment = NetworkFragment.getInstance(getFragmentManager(), "http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/01/map4.kml");
        startDownload();
    }

    private void startDownload() {
        if (!mDownloading && mNetworkFragment != null) {
            // Execute the async download.
            Log.e("GameUI", "made it to startDownload");
            mNetworkFragment.startDownload(this);
            mDownloading = true;
        }
    }

    public void updateFromDownload(String result) {
        // Update your UI here based on result of download.
        Intent resultIntent = new Intent();
        resultIntent.putExtra("kmlString", result);
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
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
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
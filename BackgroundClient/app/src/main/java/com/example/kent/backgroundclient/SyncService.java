package com.example.kent.backgroundclient;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SyncService extends Service implements GoogleApiClient.ConnectionCallbacks {

    private static SyncAdapter m_syncAdapter = null;
    private static final Object m_syncAdapterLock = new Object();

    private GoogleApiClient m_googleApiClient;

    private static final String TAG = "SyncService";

    @Override
    public void onCreate() {

        buildGoogleApiClient();
        m_googleApiClient.connect();
        synchronized (m_syncAdapterLock) {
            if (m_syncAdapter == null) {
                m_syncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public void onDestroy() {
        m_googleApiClient.disconnect();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return m_syncAdapter.getSyncAdapterBinder();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "No location permission");
            return;
        }

        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(m_googleApiClient);
        if (lastLocation == null) {
            Log.e(TAG, "Last location is null");
        } else {

            // Save coordinates to file since sync will be done before getting location fix
            String filename = getString(R.string.coordinates_file_name);
            String data = lastLocation.getLatitude() + "," + lastLocation.getLongitude();
            try {
                FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
                fos.write(data.getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "onConnectionSuspended");
    }

    private void buildGoogleApiClient() {
        m_googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
    }
}

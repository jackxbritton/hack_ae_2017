package com.example.kent.backgroundclient;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SyncResult;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private final String TAG = "SyncAdapter";
    private final String SYNC_URL = "http://www.google.com?";
    private final String SYNC_USER_SIG = "sig=";
    private final String SYNC_BATTERY_LEVEL = "batteryLevel=";

    private ContentResolver m_contentResolver;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        m_contentResolver = context.getContentResolver();
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);

        m_contentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.e(TAG, "onPerformSync called");

        Context context = getContext();

        // Get user id
        String filename = context.getString(R.string.id_file_name);
        File file = new File(context.getFilesDir(), filename);
        String id = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            id = br.readLine();
            Log.e(TAG, "User id: " + id);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get battery status
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, intentFilter);
        if (batteryStatus == null) {
            Log.e(TAG, "Failed to get battery status");
            return;
        }

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level * 100.0f / scale;

        // Build get request
        String url = SYNC_URL.concat(SYNC_USER_SIG).concat(id).concat(SYNC_BATTERY_LEVEL).concat(String.valueOf(batteryPct));
        Log.e(TAG, "Request: " + url);

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "Response: " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Request error: " + error.getMessage());
            }
        });

        // Add request to queue
        queue.add(stringRequest);
    }
}

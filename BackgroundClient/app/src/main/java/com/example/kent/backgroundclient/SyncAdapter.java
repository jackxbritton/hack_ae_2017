package com.example.kent.backgroundclient;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
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

    private final String SYNC_URL = "http://ec2-35-164-1-247.us-west-2.compute.amazonaws.com:5000";
    private final String SYNC_ENDPOINT = "/push?";
    private final String SYNC_UUID = "uuid=";
    private final String SYN_LAT = "lat=";
    private final String SYN_LON = "lon=";
    private final String SYNC_BATTERY_LEVEL = "bat=";
    private final String SYNC_TIMESTAMP = "ts=";

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.e(TAG, "onPerformSync called");

        Context context = getContext();

        // Build get request
        String url = SYNC_URL.concat(SYNC_ENDPOINT);

        // Get user id
        String id_filename = context.getString(R.string.id_file_name);
        File id_file = new File(context.getFilesDir(), id_filename);
        String id = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(id_file));
            id = br.readLine();
            Log.e(TAG, "Id: " + id);
        } catch (IOException e) {
            e.printStackTrace();
        }
        url = url.concat(SYNC_UUID).concat(id);

        // Read coordinates from file since it takes a while to get a location fix
        // Coordinates are saved to a file every interval and read at the next interval
        String coordinates_filename = context.getString(R.string.coordinates_file_name);
        File coordinates_file = new File(context.getFilesDir(), coordinates_filename);
        String coordinates = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(coordinates_file));
            coordinates = br.readLine();
            Log.e(TAG, "Coordinates: " + coordinates);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!coordinates.equals("")) {
            String[] coords = coordinates.split(",");
            double lat = Double.valueOf(coords[0]);
            double lon = Double.valueOf(coords[1]);
            Log.e(TAG, "lat: " + lat + ", lon: " + lon);

            url = url.concat("&").concat(SYN_LAT).concat(String.valueOf(lat))
                    .concat("&").concat(SYN_LON).concat(String.valueOf(lon));
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
        Log.e(TAG, "Battery percentage: " + batteryPct);
        url = url.concat("&").concat(SYNC_BATTERY_LEVEL).concat(String.valueOf(batteryPct));

        long ts = System.currentTimeMillis() / 1000;
        Log.e(TAG, "Timestamp: " + ts);
        url = url.concat("&").concat(SYNC_TIMESTAMP).concat(String.valueOf(ts));

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

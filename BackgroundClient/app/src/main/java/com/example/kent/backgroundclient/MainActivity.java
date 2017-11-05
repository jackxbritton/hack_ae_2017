package com.example.kent.backgroundclient;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks {

    public static final String ACCOUNT = "me";
    public static final long SYNC_INTERVAL = 15 * 60L;

    private final int PERMISSION_ACCESS_COARSE_LOCATION = 0;
    private static final String TAG = "MainActivity";

    private GoogleApiClient m_googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String filename = getString(R.string.id_file_name);
        File file = new File(getFilesDir(), filename);

        if (!file.isFile()) {
            // Create file and generate unique id
            FileOutputStream fos;
            try {
                String uniqueID = UUID.randomUUID().toString();
                Log.e(TAG, "Creating new id: " + uniqueID);
                fos = openFileOutput(filename, Context.MODE_PRIVATE);
                fos.write(uniqueID.getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        buildGoogleApiClient();
        createSyncAccount(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }

        if (!m_googleApiClient.isConnected()) {
            m_googleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        m_googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "Location permission granted");
                }
                break;
        }
    }

    private void buildGoogleApiClient() {
        m_googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(m_googleApiClient);

            String filename = getString(R.string.coordinates_file_name);
            File file = new File(getFilesDir(), filename);

            if (!file.isFile()) {
                FileOutputStream fos;
                try {
                    fos = openFileOutput(filename, Context.MODE_PRIVATE);
                    String data = lastLocation.getLatitude() + "," + lastLocation.getLongitude();
                    Log.e(TAG, "Coordinates: " + data);
                    fos.write(data.getBytes());
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "onConnectionSuspended");
    }

    @SuppressWarnings("MissingPermission")
    private void createSyncAccount(Context context) {

        String accountType = context.getString(R.string.account_type);
        String authority = context.getString(R.string.content_authority);

        Account account = new Account(ACCOUNT, accountType);
        AccountManager manager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        boolean created = false;

        // Attempt to explicitly create the account with no password or extra data
        if (manager.addAccountExplicitly(account, null, null)) {

            // Inform the system that this account is eligible for auto sync when the network is up
            ContentResolver.setSyncAutomatically(account, authority, true);
            ContentResolver.addPeriodicSync(account, authority, Bundle.EMPTY, SYNC_INTERVAL);
            created = true;
        }

        if (created) {
            ContentResolver.requestSync(account, authority, Bundle.EMPTY);
        }
    }
}

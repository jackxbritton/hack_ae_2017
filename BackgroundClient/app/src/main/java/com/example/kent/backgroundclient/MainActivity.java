package com.example.kent.backgroundclient;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static final String ACCOUNT = "me";
    public static final long SYNC_INTERVAL = 60L;

    private static final String TAG = "MainActivity";

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

        createSyncAccount(this);
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

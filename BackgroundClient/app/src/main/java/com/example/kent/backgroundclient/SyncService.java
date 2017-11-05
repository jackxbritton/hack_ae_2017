package com.example.kent.backgroundclient;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class SyncService extends Service {

    private static SyncAdapter m_syncAdapter = null;
    private static final Object m_syncAdapterLock = new Object();

    @Override
    public void onCreate() {
        synchronized (m_syncAdapterLock) {
            if (m_syncAdapter == null) {
                m_syncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return m_syncAdapter.getSyncAdapterBinder();
    }
}

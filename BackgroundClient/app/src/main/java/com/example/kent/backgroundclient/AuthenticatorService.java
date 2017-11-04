package com.example.kent.backgroundclient;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AuthenticatorService extends Service {

    private Authenticator m_authenticator;

    @Override
    public void onCreate() {
        m_authenticator = new Authenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return m_authenticator.getIBinder();
    }
}

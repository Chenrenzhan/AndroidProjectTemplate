package com.drumge.template;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.drumge.template.log.MLog;

import java.util.concurrent.CopyOnWriteArraySet;

public class NetworkMonitor extends BroadcastReceiver {

    /* Invalid network type. 
     * See #ConnectivityManager for other types. 
     */
    public static final int INVALID_TYPE = -1;

    /**
     * The type means network type.
     */
    public static interface OnNetworkChange {

        void onDisconnected(int type);

        void onConnected(int type);

        void onConnecting(int type);
    }

    private CopyOnWriteArraySet<OnNetworkChange> mListener =
            new CopyOnWriteArraySet<OnNetworkChange>();

    private static final NetworkMonitor sInstance = new NetworkMonitor();

    public static NetworkMonitor instance() {
        return sInstance;
    }

    private NetworkMonitor() {
        super();
    }

    public void addListener(OnNetworkChange l) {
        if (l != null) {
            mListener.add(l);
        }
    }

    public void removeListener(OnNetworkChange l) {
        mListener.remove(l);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mListener.isEmpty()) {
            MLog.warn(this, "NetworkMonitor.onReceive, mListener is empty");
            return;
        }
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isAvailable()) {
                Log.i("dingning", "NetworkMonitor.onReceive, disconnected");
                MLog.warn(this, "NetworkMonitor.onReceive, disconnected");
                for (OnNetworkChange l : mListener) {
                    l.onDisconnected(networkInfo != null ? networkInfo.getType() : INVALID_TYPE);
                }
                return;
            }
            int type = networkInfo.getType();
            if (networkInfo.isConnected()) {
                Log.i("dingning", "NetworkMonitor.onReceive, connected, type = " + type);
                MLog.warn(this, "NetworkMonitor.onReceive, connected, type = %d", type);
                for (OnNetworkChange l : mListener) {
                    l.onConnected(type);
                }
            } else {
                boolean connecting = networkInfo.isConnectedOrConnecting();
                if (connecting) {
                    Log.i("dingning", "NetworkMonitor.onReceive, connecting");
                    MLog.warn(this, "NetworkMonitor.onReceive, connecting, type = %d", type);
                    for (OnNetworkChange l : mListener) {
                        l.onConnecting(type);
                    }
                } else {
                    Log.i("dingning", "NetworkMonitor.onReceive, disconnected");
                    MLog.warn(this, "NetworkMonitor.onReceive, disconnected, type = %d", type);
                    for (OnNetworkChange l : mListener) {
                        l.onDisconnected(type);
                    }
                }
            }
        }

    }

}

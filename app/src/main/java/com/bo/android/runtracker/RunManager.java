package com.bo.android.runtracker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

/**
 * Class RunManager.
 *
 * @author Boris Pronin (<a href="mailto:boprsoft.dev@gmail.com">boprsoft.dev@gmail.com</a>)
 */
public class RunManager {

    public static final String ACTION_LOCATION = "com.bignerdranch.android.runtracker.ACTION_LOCATION";
    private static final String TAG = "RunManager";
    private static RunManager sRunManager;
    private Context mAppContext;
    private LocationManager mLocationManager;

    private RunManager(Context appContext) {
        mAppContext = appContext;
        mLocationManager = (LocationManager) mAppContext.getSystemService(Context.LOCATION_SERVICE);
    }

    public static RunManager get(Context c) {
        if (sRunManager == null) {
            sRunManager = new RunManager(c.getApplicationContext());
        }
        return sRunManager;
    }

    private PendingIntent getLocationPendingIntent(boolean shouldCreate) {
        Intent broadcast = new Intent(ACTION_LOCATION);
        int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
        return PendingIntent.getBroadcast(mAppContext, 0, broadcast, flags);
    }

    public void startLocationUpdates() {
        String provider = LocationManager.GPS_PROVIDER;
        PendingIntent pi = getLocationPendingIntent(true);

        try {
            Location lastKnown = mLocationManager.getLastKnownLocation(provider);
            if (lastKnown != null) {
                lastKnown.setTime(System.currentTimeMillis());
                broadcastLocation(lastKnown);
            }
            mLocationManager.requestLocationUpdates(provider, 0, 0, pi);
        } catch (SecurityException x) {
            Log.e(TAG, "requestLocationUpdates failed", x);
        }
    }

    private void broadcastLocation(Location location) {
        Intent broadcast = new Intent(ACTION_LOCATION);
        broadcast.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
        mAppContext.sendBroadcast(broadcast);
    }

    public void stopLocationUpdates() {
        PendingIntent pi = getLocationPendingIntent(false);
        if (pi != null) {
            mLocationManager.removeUpdates(pi);
            pi.cancel();
        }
    }

    public boolean isTrackingRun() {
        return getLocationPendingIntent(false) != null;
    }
}
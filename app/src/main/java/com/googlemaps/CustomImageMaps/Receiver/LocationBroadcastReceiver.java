package com.googlemaps.CustomImageMaps.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Objects;

public class LocationBroadcastReceiver extends BroadcastReceiver {
    private LocationBroadcastReceiverListener mListener;

    public LocationBroadcastReceiver(LocationBroadcastReceiverListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), "ACT_LOC")) {
            double gps_latitude = intent.getDoubleExtra("latitude", 0f);
            double gps_longitude = intent.getDoubleExtra("longitude", 0f);
            mListener.onReceiveCoordinates(gps_latitude, gps_longitude);
        }
    }

    public interface LocationBroadcastReceiverListener {
        void onReceiveCoordinates(double latitude, double longitude);
    }
}

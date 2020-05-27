package com.zuragames.servicioubicacion.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.zuragames.servicioubicacion.MainActivity;
import com.zuragames.servicioubicacion.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class UbicacionService extends Service{

    public static final String TAG = "UbicacionService";
    public static final String CHANNEL_ID = "UbicacionServiceChannel";

    LocationManager locationManager;
    private boolean isActive;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient mFusedLocationClient;


    @Override
    public void onCreate() {
        super.onCreate();
        configuracionServicioUbicacion();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Servicio Foreground")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);


        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        if (mFusedLocationClient != null)
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void actualizaUbicacion(Location location) {
        Address direccion = null;
        // GPS location can be null if GPS is switched off
        if (location != null) {
            try {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses != null && addresses.size() > 0) {
                    direccion = addresses.get(0);
                }
            } catch (IOException ioException) {
                // Catch network or other I/O problems.
                Log.e(TAG, "servicio no disponible", ioException);
            } catch (IllegalArgumentException illegalArgumentException) {
                // Catch invalid latitude or longitude values.
                Log.e(TAG, "latitud o longitud invalida" + ". " +
                        "Latitud = " + location.getLatitude() +
                        ", Longitud = " +
                        location.getLongitude(), illegalArgumentException);
            }

            if (direccion != null){
                direccion.getAddressLine(0);
                Log.d(TAG, "Latitud: " + location.getLatitude());
                Log.d(TAG, "Longitud: " + location.getLongitude());
            }
            //TODO enviamos la ubicación a algun sitio
        }
    }

    private void configuracionServicioUbicacion(){
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(3 * 1000);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        actualizaUbicacion(location);
                        /*if (mFusedLocationClient != null)
                            mFusedLocationClient.removeLocationUpdates(locationCallback);*/
                    }
                }
            }
        };
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }
}

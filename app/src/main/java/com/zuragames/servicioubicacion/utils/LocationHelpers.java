package com.zuragames.servicioubicacion.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by roberto.zurita on 17/07/2019.
 */

public class LocationHelpers {

    public static final int FINE_PERMISION_REQ_CODE = 8584;
    public static final int COARSE_PERMISION_REQ_CODE = 8585;
    public static final int PERMISION_REQ_CODE = 8586;

    public static final int NO_PERMISSION = 0;
    public static final int FINE_PERMISSION_ONLY = 1;
    public static final int COARSE_PERMISSION_ONLY = 2;
    public static final int FINE_AND_COARSE_PERMISSION = 3;


    public static boolean checkLocation(final Context context){
        return isLocationEnabled(context);
    }

    public static void requestPermissions(final Activity activity){
        int currentPermissions = checkPermissions(activity);
        String[] permissionsToRequest = null;
        switch (currentPermissions){
            case NO_PERMISSION:
                permissionsToRequest = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                break;
            case COARSE_PERMISSION_ONLY:
                permissionsToRequest = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
                break;
            case FINE_PERMISSION_ONLY:
                permissionsToRequest = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION};
                break;
        }

        if(permissionsToRequest != null){
            ActivityCompat.requestPermissions(activity, permissionsToRequest, PERMISION_REQ_CODE);
        }

    }

    public static int checkPermissions(final Activity activity){
        int result = NO_PERMISSION;
        boolean fineLocPermission = (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        boolean coarseLocPermission = (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);

        if(fineLocPermission && coarseLocPermission) return FINE_AND_COARSE_PERMISSION;
        else if(fineLocPermission) return FINE_PERMISSION_ONLY;
        else if(coarseLocPermission) return COARSE_PERMISSION_ONLY;

        return result;
    }

    private static boolean isLocationEnabled(final Context context) {
        LocationManager locationManager = (LocationManager)context.getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}

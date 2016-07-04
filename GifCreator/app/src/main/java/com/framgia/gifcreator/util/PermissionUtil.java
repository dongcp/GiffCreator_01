package com.framgia.gifcreator.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by yue on 04/07/2016.
 */
public class PermissionUtil {

    public final static int MY_PERMISSION_REQUEST = 1;

    public static boolean checkPermission(AppCompatActivity activity) {
        if (!isNetworkPermissionGranted(activity) ||
                !isNetworkPermissionGranted(activity) || !isStoragePermissionGranted(activity)) {
            ActivityCompat.requestPermissions(
                    activity, new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_NETWORK_STATE}, MY_PERMISSION_REQUEST);
        }
        return true;
    }

    public static boolean isNetworkPermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isCameraPermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isStoragePermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
}

package com.framgia.gifcreator.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by yue on 04/07/2016.
 */
public class NetworkUtil {

    public static boolean isNetworkAvailable(Context context) {
        if (PermissionUtil.isNetworkPermissionGranted(context)) {
            ConnectivityManager check = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (check == null) return false;
            NetworkInfo networkInfo = check.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnectedOrConnecting();
        }
        return false;
    }
}

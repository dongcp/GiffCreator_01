package com.framgia.gifcreator.util;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;

/**
 * Created by yue on 28/06/2016.
 */
public class AppHelper {

    public static int getDimen(Context context, int id) {
        return context.getResources().getDimensionPixelSize(id);
    }

    public static void showSnackbar(CoordinatorLayout coordinatorLayout, int resStringId) {
        Snackbar.make(coordinatorLayout, resStringId, Snackbar.LENGTH_SHORT).show();
    }
}

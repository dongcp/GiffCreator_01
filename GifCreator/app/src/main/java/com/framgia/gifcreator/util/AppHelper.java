package com.framgia.gifcreator.util;

import android.content.Context;

/**
 * Created by yue on 28/06/2016.
 */
public class AppHelper {

    public static int getDimen(Context context, int id) {
        return context.getResources().getDimensionPixelSize(id);
    }
}

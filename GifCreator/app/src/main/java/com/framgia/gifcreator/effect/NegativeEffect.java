package com.framgia.gifcreator.effect;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

/**
 * Created by VULAN on 6/14/2016.
 */
public class NegativeEffect extends EditingEffect {

    @Override
    public Bitmap applyEffect(Bitmap baseBitmap) {
        return setNegativeEffect(baseBitmap);
    }

    public Bitmap setNegativeEffect(Bitmap mBaseBitmap) {
        int width = mBaseBitmap.getWidth();
        int height = mBaseBitmap.getHeight();
        float[] negativeMatrix = {
                -1, 0, 0, 0, 255,
                0, -1, 0, 0, 255,
                0, 0, -1, 0, 255,
                0, 0, 0, 1, 0
        };
        Bitmap negativeBitmap = Bitmap.createBitmap(width, height, mBaseBitmap.getConfig());
        ColorFilter colorFilter = new ColorMatrixColorFilter(negativeMatrix);
        Canvas canvas = new Canvas(negativeBitmap);
        Paint paint = new Paint();
        paint.setColorFilter(colorFilter);
        canvas.drawBitmap(mBaseBitmap, 0, 0, paint);
        return negativeBitmap;
    }
}

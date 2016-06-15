package com.framgia.gifcreator.effect;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

/**
 * Created by VULAN on 6/14/2016.
 */
public class GrayScaleEffect extends EditingEffect {

    public Bitmap doGrayScale(Bitmap baseBitmap) {
        int width = baseBitmap.getWidth();
        int height = baseBitmap.getHeight();
        float[] grayScaleMatrix = {
                0.213f, 0.715f, 0.072f, 0.0f, 0.0f,
                0.213f, 0.715f, 0.072f, 0.0f, 0.0f,
                0.213f, 0.715f, 0.072f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f, 0.0f,
        };
        ColorMatrix colorMatrix = new ColorMatrix(grayScaleMatrix);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        Bitmap grayScaleBitmap = Bitmap.createBitmap(width, height, baseBitmap.getConfig());
        Canvas canvas = new Canvas(grayScaleBitmap);
        canvas.drawBitmap(baseBitmap, 0, 0, paint);
        return grayScaleBitmap;
    }

    @Override
    public Bitmap applyEffect(Bitmap baseBitmap) {
        return doGrayScale(baseBitmap);
    }
}

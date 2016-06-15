package com.framgia.gifcreator.effect;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class RotationEffect extends EditingEffect {

    private int mDegree;

    public RotationEffect(int mDegree) {
        this.mDegree = mDegree;
    }

    public Bitmap rotate(Bitmap mBitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(mDegree);
        return Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
    }

    @Override
    public Bitmap applyEffect(Bitmap baseBitmap) {
        return rotate(baseBitmap);
    }
}

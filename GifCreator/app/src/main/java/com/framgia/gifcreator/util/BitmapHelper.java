package com.framgia.gifcreator.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by dongc on 6/6/2016.
 */
public class BitmapHelper {

    public static Bitmap decodeFile(String path, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
//        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
//        float scaleW = (float) reqWidth / bitmap.getWidth();
//        float scaleH = (float) reqHeight / bitmap.getHeight();
//        float scale = (scaleW > scaleH) ? scaleW : scaleH;
//        Matrix matrix = new Matrix();
//        matrix.postScale(scale, scale);
        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int reqWidth, int reqHeight) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        byte[] imageData = convertBitmapToByteArray(bitmap);
        BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
//        Bitmap image = BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);
//        float scaleW = (float) reqWidth / image.getWidth();
//        float scaleH = (float) reqHeight / image.getHeight();
//        float scale = (scaleW > scaleH) ? scaleW : scaleH;
//        Matrix matrix = new Matrix();
//        matrix.postScale(scale, scale);
        return BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);
    }

    public static byte[] convertBitmapToByteArray(Bitmap bitmap) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public static int[] getImageSize(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        int[] size = new int[2];
        size[0] = options.outWidth;
        size[1] = options.outHeight;
        return size;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}

package com.framgia.gifcreator.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yue on 20/06/2016.
 */
public class FileUtil {

    private final static String DATE_FORMAT = "yyyyMMdd_HHmmss";
    private final static String IMAGE_NAME_PREFIX = "IMG";
    private final static String IMAGE_EXTENSION = ".jpg";

    public static String getAppFolderPath(Context context) {
        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + context.getPackageName());
        if (!folder.exists()) folder.mkdirs();
        return folder.getAbsolutePath();
    }

    public static String saveImage(Context context, Bitmap image) throws IOException {
        File storageDir = new File(getAppFolderPath(context));
        File imageFile = new File(storageDir, getImageName());
        FileOutputStream out = new FileOutputStream(imageFile);
        image.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.flush();
        out.close();
        return imageFile.getAbsolutePath();
    }

    public static String getImageName() {
        String timeStamp = new SimpleDateFormat(DATE_FORMAT).format(new Date());
        return IMAGE_NAME_PREFIX + timeStamp + IMAGE_EXTENSION;
    }
}

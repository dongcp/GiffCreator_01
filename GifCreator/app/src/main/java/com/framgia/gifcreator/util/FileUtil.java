package com.framgia.gifcreator.util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

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
    private final static String SPLIT_PHOTO_ID = ":";
    private final static String PHOTO_TYPE_PRIMARY = "primary";
    private final static String DOCUMENT_EXTERNAL_STORAGE = "com.android.externalstorage.documents";
    private final static String DOCUMENT_DOWNLOAD = "com.android.providers.downloads.documents";
    private final static String DOCUMENT_MEDIA = "com.android.providers.media.documents";
    private final static String DIR_DOWNLOAD_PATH = "content://downloads/public_downloads";
    private final static int PHOTO_TYPE_INDEX = 0;
    private final static int PHOTO_ID_INDEX = 1;

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

    public static String getPath(Context context, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(columnIndex);
        cursor.close();
        return s;
    }

    public static String getImageName() {
        String timeStamp = new SimpleDateFormat(DATE_FORMAT).format(new Date());
        return IMAGE_NAME_PREFIX + timeStamp + IMAGE_EXTENSION;
    }

    public static String getGalleryPhotoPath(Context context, Uri uri) {
        final boolean isKitkat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitkat && DocumentsContract.isDocumentUri(context, uri)) {
            final String wholeId = DocumentsContract.getDocumentId(uri);
            if (isExternalStorageDocument(uri)) {
                final String[] splits = wholeId.split(SPLIT_PHOTO_ID);
                final String type = splits[PHOTO_TYPE_INDEX];
                if (type.equalsIgnoreCase(PHOTO_TYPE_PRIMARY)) {
                    return Environment.getExternalStorageDirectory() +
                            File.separator + splits[PHOTO_ID_INDEX];
                }
            }
            if (isDownloadsDocument(uri)) {
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse(DIR_DOWNLOAD_PATH), Long.valueOf(wholeId));
                return getDataColumn(context, contentUri, null, null);
            }
            if (isMediaDocument(uri)) {
                final String[] splits = wholeId.split(SPLIT_PHOTO_ID);
                Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{splits[PHOTO_ID_INDEX]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else {
            return getDataColumn(context, uri, null, null);
        }
        return null;
    }

    private static String getDataColumn(
            Context context, Uri uri, String selection, String[] selectionArgs) {
        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().
                query(uri, projection, selection, selectionArgs, null);
        if (cursor == null) return null;
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
        String photoPath = cursor.getString(columnIndex);
        cursor.close();
        return photoPath;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return DOCUMENT_EXTERNAL_STORAGE.equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return DOCUMENT_DOWNLOAD.equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return DOCUMENT_MEDIA.equals(uri.getAuthority());
    }
}

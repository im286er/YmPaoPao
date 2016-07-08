package com.yeming.paopao.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * author   YeMing(yeming_1001@163.com)
 * Date:    2015-01-26 21:42
 * version: V1.0
 * Description:   文件处理
 */
public class FileUtil {

    private static final String TAG = "FileUtil";

    /**
     * @param uri
     * @return 返回路径
     */
    public static String getFilePathByUri(Uri uri) {
        if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * 相机拍照后图片路径uri
     * @return
     */
    public static Uri getOutputMediaFileUri() {
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyCameraApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp
                + ".jpg");

        return Uri.fromFile(mediaFile);
    }

    /**
     * @param context
     * @param preferExternal
     * @param dirName
     * @return
     *
     * 获取缓存图片文件目录
     */
    public static File getCacheDirectory(Context context, boolean preferExternal,String dirName) {
        File appCacheDir = null;
        if (preferExternal && MEDIA_MOUNTED
                .equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) {
            appCacheDir = getExternalCacheDir(context,dirName);
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
            Log.w("Can't define system cache directory! '%s' will be used.", cacheDirPath);
            appCacheDir = new File(cacheDirPath);
        }
        return appCacheDir;
    }

    /**
     * @param context
     * @param dirName
     * @return
     * 获取扩展目录
     */
    private static File getExternalCacheDir(Context context,String dirName) {
        File appCacheDir2 = new File(new File(Environment.getExternalStorageDirectory(), context.getPackageName()), "cache");
        File appCacheDir = new File(appCacheDir2, dirName);
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                Log.w(TAG,"Unable to create external cache directory");
                return null;
            }
            try {
                new File(appCacheDir, ".nomedia").createNewFile();
            } catch (IOException e) {
                Log.i(TAG,"Can't create \".nomedia\" file in application external cache directory");
            }
        }
        return appCacheDir;
    }
    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";
    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return perm == PackageManager.PERMISSION_GRANTED;
    }
}

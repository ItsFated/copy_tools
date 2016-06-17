package lf.simpleutils.android;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by Jason on 2016/5/9.
 * 获取安卓的存储路径
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public final class StorageUtils {
    private static final String DEFAULT_FOLDER_NAME = "SimpleData";

    /**
     * 判断是否存在 SD Card
     * @return true 则可用
     */
    public static boolean isSdCardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 获取外部存储文件夹，卸载APP不会自动删除
     * @return 已经创建好的文件，如果不存在SDCard返回空
     */
    public static File getExternalStorageFolder(String path) {
        if (!isSdCardAvailable()) return null;
        if (TextUtils.isEmpty(path)) path = DEFAULT_FOLDER_NAME;
        File folder = new File(Environment.getExternalStorageDirectory(), path);
        if (!folder.exists()) folder.mkdirs();
        else L.W("qiu", "getExternalStorageFolder");
        return folder;
    }

    /**
     * 获取外部存储文件夹
     * @return 这个不会返回空，总是会返回一个可用的外部存储文件夹
     */
    public static File getAvailableExternalStorageFolder(Context context,String path) {
        if (TextUtils.isEmpty(path)) path = DEFAULT_FOLDER_NAME;
        File folder = new File(context.getExternalFilesDir(null), path);
        if (!folder.exists()) folder.mkdirs();
        return folder;
    }

    /**
     * 获取内存存储路径
     * @param path 内部路径
     * @return 已经创建的文件
     */
    public static File getInternalStorageFolder(Context context, String path) {
        if (TextUtils.isEmpty(path)) path = DEFAULT_FOLDER_NAME;
        File folder = new File(context.getFilesDir(), path);
        if (!folder.exists()) folder.mkdirs();
        return folder;
    }

    /**
     * 自动判断SDCard挂载点是否可用，然后返回一个用于存储数据的文件夹
     * @param path 内部文件夹路径
     * @return SDCard可用返回SDCard的存储文件夹，不可用返回系统提供的外部存储文件夹文件夹，加上内部文件夹的路径的文件夹
     */
    public static File autoGetAvailabeExternalFolder(Context context, String path) {
        return isSdCardAvailable() ? getExternalStorageFolder(path) : getAvailableExternalStorageFolder(context, path);
    }
}

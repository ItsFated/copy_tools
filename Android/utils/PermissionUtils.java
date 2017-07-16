package com.im_hero.livedemo;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Runtime permission
 * @author Jason
 * @version 1.0
 */
public final class PermissionUtils {

    /**
     * 请求授权
     *
     * @param callback 这个回调用连个方法，其中一个是Activity已经实现的{@link Activity#onRequestPermissionsResult(int, String[], int[])}，还有一个是提示用户以拒绝的权限
     * @param requestCode 请求授权的请求码
     * @param permissions 需要请求的权限
     */
    public static void request(Activity aty, RequestPermissionsResultCallback callback, int requestCode, String... permissions) {
        final int len = permissions.length;
        String[] deniedPermissions = new String[len];
        int deniedCount = 0;
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < len; i++) {
            if (ActivityCompat.checkSelfPermission(aty, permissions[i]) == PackageManager.PERMISSION_DENIED) {
                deniedPermissions[deniedCount++] = permissions[i];
            }
        }

        if (deniedCount == 0) {
            return;
        }

        String[] temp = new String[deniedCount];
        System.arraycopy(deniedPermissions, 0, temp, 0, deniedCount);
        deniedPermissions = temp;

        for (int i = 0; i < deniedCount; i++) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(aty, deniedPermissions[i])) {
                callback.shouldShowRequestPermissionRationale(deniedPermissions);
                return;
            }
        }
        ActivityCompat.requestPermissions(aty, deniedPermissions, requestCode);
    }

    private PermissionUtils(){}

    public interface RequestPermissionsResultCallback {
        /**
         * 这个回调在申请用户已经拒绝过一次的权限时调用（以拒绝过的权限需要用户自己手动授权，无法在APP内授权）
         * @param deniedPermissions 所有被拒绝的权限
         */
        void shouldShowRequestPermissionRationale(String[] deniedPermissions);

        /**
         * 同 {@link Activity#onRequestPermissionsResult(int, String[], int[])}
         */
        void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);
    }
}

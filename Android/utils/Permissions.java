package com.goowi.common.java.permission;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.util.SparseArray;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 运行时权限工具
 * <ol>
 *     <li>将权限分为三种：已获得，已拒绝和需提醒，它们的发送顺序是：需提醒 > 已拒绝 > 已获得，
 *     先发送的可以通过回调返回值控制是否继续发送后面的权限</li>
 *     <li>请求时会按照优先级返回，高优先级可以直接阻断低优先级的发送，均在主线程中发送</li>
 * </ol>
 * 演示：
 * <pre>
 *     Permissions permissions = new Permissions(this) // 设置默认监听器
 *                                      .setDefaultPermissionsDenied(this)
 *                                      .setDefaultPermissionsGranted(this)
 *                                      .setDefaultPermissionsShowRationale(this);
 *     permissions.request(Manifest.permission.RECORD_AUDIO); // 结果发送到默认监听器
 *     permissions.request(new Permissions.OnPermissions(){
 *         public boolean onPermissions(PermissionStatus status, String[] permissions) {
 *         }
 *     }, Manifest.permission.RECORD_AUDIO); // 请求结果发送到传入的监听器
 *     permissions.request(new Permissions.OnBundlePermissions(){
 *         public void onBundlePermissions(String[] granted, String[] denied, String[] showRationale) {
 *         }
 *     }, Manifest.permission.RECORD_AUDIO); // 一次性请求所有权限，并一次性获取所有结果
 * </pre>
 * @author Jason
 * @version 1.0
 */
public class Permissions {
    private static final String TAG = "Permissions";

    private static final String[] EMPTY = new String[0];

    private static final int DEFAULT_REQUEST_CODE = 0;
    /** 奇数 */
    private static final AtomicInteger REQUEST_CODE = new AtomicInteger(1);
    /** 偶数 */
    private static final AtomicInteger ON_BUNDLE_REQUEST_CODE = new AtomicInteger(2);

    private static SparseArray<OnPermissions> ON_PERMISSIONS;
    private static SparseArray<OnBundlePermissions> ON_BUNDLE_PERMISSIONS;

    private static WeakReference<PermissionsFragment> permissionsFragment;

    private Activity activity;
    private PermissionsGranted permissionsGranted;
    private PermissionsDenied permissionsDenied;
    private PermissionsShowRationale permissionsShowRationale;

    public Permissions(Activity activity) {
        this.activity = activity;
    }

    /**
     * 设置已获得的权限监听器
     * @param listener 监听器
     */
    public Permissions setDefaultPermissionsGranted(PermissionsGranted listener) {
        permissionsGranted = listener;
        return this;
    }

    /**
     * 设置已拒绝的权限监听器
     * @param listener 监听器
     */
    public Permissions setDefaultPermissionsDenied(PermissionsDenied listener) {
        permissionsDenied = listener;
        return this;
    }

    /**
     * 设置需提醒的权限监听器
     * @param listener 监听器
     */
    public Permissions setDefaultPermissionsShowRationale(PermissionsShowRationale listener) {
        permissionsShowRationale = listener;
        return this;
    }

    /**
     * 请求权限，一次请求一组权限，这样可以保证他们的状态是一致的
     * @param permissions 权限列表
     */
    public Permissions request(String... permissions) {
        request(DEFAULT_REQUEST_CODE, permissions);
        return this;
    }

    /**
     * 请求权限，一次请求一组权限，这样可以保证他们的状态是一致的
     * @param onPermissions 请求结果回调
     * @param permissions 权限列表
     */
    public Permissions request(OnPermissions onPermissions, String... permissions) {
        if (onPermissions != null) {
            if (ON_PERMISSIONS == null) ON_PERMISSIONS = new SparseArray<>(1);
            int requestCode = REQUEST_CODE.getAndAdd(2);
            ON_PERMISSIONS.append(requestCode, onPermissions);
            request(requestCode, permissions);
        }
        return this;
    }

    /**
     * 请求权限，一次性请求所有的权限，结果分为三类打包，一次返回
     * @param onBundlePermissions 所有权限请求结果
     * @param permissions 权限列表
     */
    public Permissions request(OnBundlePermissions onBundlePermissions, String... permissions) {
        if (onBundlePermissions != null) {
            if (ON_BUNDLE_PERMISSIONS == null) ON_BUNDLE_PERMISSIONS = new SparseArray<>(1);
            int requestCode = ON_BUNDLE_REQUEST_CODE.getAndAdd(2);
            ON_BUNDLE_PERMISSIONS.append(requestCode, onBundlePermissions);
            request(requestCode, permissions);
        }
        return this;
    }

    private void request(int requestCode, String... permissions) {
        final int permissionsLength = permissions.length;
        if (permissionsLength > 0) {
            if (hasAllPermissions(permissions) // 已拥有所有权限
                    || Build.VERSION.SDK_INT < Build.VERSION_CODES.M /* 无运行时权限功能 */) {
                sendPermissions(requestCode, permissions, checkPermissions(activity, permissions));
            } else {
                getPermissionsFragment().requestPermissions(permissions, requestCode);
            }
        }
    }

    private void sendPermissions(int requestCode, String[] permissions, PermissionStatus[] statuses) {
        if (DEFAULT_REQUEST_CODE == requestCode) {
            sendPermissionsShowRationale(permissions, checkPermissions(activity, permissions));
        } else if ((requestCode & 1) != 0) {
            sendOnPermissions(requestCode, permissions, statuses);
        } else {
            sendOnBundlePermissions(requestCode, permissions, statuses);
        }
    }

    private void sendPermissionsGranted(String[] permissions, PermissionStatus[] statuses) {
        String[] permissionsGranted = filterPermissions(permissions, statuses, PermissionStatus.GRANTED);
        if (permissionsGranted.length > 0 && this.permissionsGranted != null) {
            this.permissionsGranted.onPermissionsGranted(permissionsGranted);
        }
    }

    private void sendPermissionsDenied(String[] permissions, PermissionStatus[] statuses) {
        String[] permissionsDenied;
        if (this.permissionsDenied == null
                || (permissionsDenied = filterPermissions(permissions, statuses, PermissionStatus.DENIED)).length == 0
                || this.permissionsDenied.onPermissionsDenied(permissionsDenied)) {
                sendPermissionsGranted(permissions, statuses);
        }
    }

    private void sendPermissionsShowRationale(String[] permissions, PermissionStatus[] statuses) {
        String[] permissionsShowRationale;
        if (this.permissionsShowRationale == null
                || (permissionsShowRationale = filterPermissions(permissions, statuses, PermissionStatus.SHOW_RATIONALE)).length == 0
                || this.permissionsShowRationale.onPermissionsShowRationale(permissionsShowRationale)) {
                sendPermissionsDenied(permissions, statuses);
        }
    }

    private void sendOnPermissions(int requestCode, String[] permissions, PermissionStatus[] statuses) {
        OnPermissions onPermissions = ON_PERMISSIONS.get(requestCode);
        if (onPermissions != null) {
            ON_PERMISSIONS.delete(requestCode);
            String[] filtered;
            if ((filtered = filterPermissions(permissions, statuses, PermissionStatus.SHOW_RATIONALE)).length == 0
                    || onPermissions.onPermissions(PermissionStatus.SHOW_RATIONALE, filtered)){
                if ((filtered = filterPermissions(permissions, statuses, PermissionStatus.DENIED)).length == 0
                        || onPermissions.onPermissions(PermissionStatus.DENIED, filtered)){
                    if ((filtered = filterPermissions(permissions, statuses, PermissionStatus.GRANTED)).length > 0) {
                        onPermissions.onPermissions(PermissionStatus.GRANTED, filtered);
                    }
                }
            }
        }
    }

    private void sendOnBundlePermissions(int requestCode, String[] permissions, PermissionStatus[] statuses) {
        OnBundlePermissions onBundlePermissions = ON_BUNDLE_PERMISSIONS.get(requestCode);
        if (onBundlePermissions != null) {
            ON_BUNDLE_PERMISSIONS.delete(requestCode);
            String[] showRationale = filterPermissions(permissions, statuses, PermissionStatus.SHOW_RATIONALE);
            String[] denied = filterPermissions(permissions, statuses, PermissionStatus.DENIED);
            String[] granted = filterPermissions(permissions, statuses, PermissionStatus.GRANTED);
            onBundlePermissions.onPermissions(granted, denied, showRationale);
        }
    }

    /**
     * 是否拥有所有权限
     * @param permissions 需要检查的权限列表
     * @return 是否拥有所有权限
     */
    public boolean hasAllPermissions(String... permissions) {
        switch (permissions.length) {
            case 0: return true;
            case 1: return checkPermission(activity, permissions[0]) == PackageManager.PERMISSION_GRANTED;
            default: {
                for (String permission : permissions) {
                    if (PackageManager.PERMISSION_GRANTED != checkPermission(activity, permission)) return false;
                }
                return true;
            }
        }
    }

    private PermissionsFragment getPermissionsFragment() {
        PermissionsFragment f = permissionsFragment != null ? permissionsFragment.get() : null;
        if (f == null) {
            f = new PermissionsFragment().setPermissions(this);
            permissionsFragment = new  WeakReference<>(f);
        }
        FragmentManager fm = activity.getFragmentManager();
        if (fm.findFragmentByTag(TAG) == null) {
            fm.beginTransaction().add(android.R.id.content, f, TAG).commitAllowingStateLoss();
            fm.executePendingTransactions();
        }
        return f;
    }

    /**
     * 检查单个权限的是否已获取
     * @param permission 单个权限
     * @return 权限是否已获取
     */
    public static int checkPermission(Context context, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.checkSelfPermission(permission);
        } else {
            return context.checkPermission(permission, Process.myPid(), Process.myUid());
        }
    }

    /**
     * 检查多个权限的是否已获取
     * @param permissions 权限列表
     * @return 权限是否已获取
     */
    public static PermissionStatus[] checkPermissions(Activity activity, String... permissions) {
        PermissionStatus[] results = new PermissionStatus[permissions.length];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < permissions.length; i++) {
                switch (activity.checkSelfPermission(permissions[i])) {
                    case PackageManager.PERMISSION_GRANTED: results[i] = PermissionStatus.GRANTED; break;
                    case PackageManager.PERMISSION_DENIED: {
                        if (activity.shouldShowRequestPermissionRationale(permissions[i])) {
                            results[i] = PermissionStatus.SHOW_RATIONALE;
                        } else {
                            results[i] = PermissionStatus.DENIED;
                        }
                    } break;
                }
            }
        } else {
            for (int i = 0; i < permissions.length; i++) {
                switch (activity.checkPermission(permissions[i], Process.myPid(), Process.myUid())) {
                    case PackageManager.PERMISSION_GRANTED: results[i] = PermissionStatus.GRANTED; break;
                    case PackageManager.PERMISSION_DENIED: results[i] = PermissionStatus.DENIED;break;
                }
            }
        }
        return results;
    }

    /**
     * 过滤权限的请求结果
     * @param permissions 权限列表
     * @param grantedResults 权限请求结果
     * @param granted 要提取的权限
     * @return 提取好的权限列表
     */
    public static String[] filterPermissions(String[] permissions, PermissionStatus[] grantedResults, final PermissionStatus granted) {
        final int len = permissions.length;
        if (len != grantedResults.length) {
            throw new IllegalArgumentException("permissions.length must equal to grantedResults.length");
        }
        String[] result = new String[len];
        int counter = 0;
        for (int i = 0; i < len; i++) {
            if (grantedResults[i] == granted) result[counter++] = permissions[i];
        }
        if (counter > 0) {
            String[] r = new String[counter];
            System.arraycopy(result, 0, r, 0, counter);
            return r;
        } else return EMPTY;
    }

    /**
     * 权限的三种状态
     * <ol>
     *     <li>{@link Permissions.PermissionStatus#GRANTED}  已获取的权限</li>
     *     <li>{@link Permissions.PermissionStatus#DENIED}  已拒绝的权限</li>
     *     <li>{@link Permissions.PermissionStatus#SHOW_RATIONALE}  被用户拒绝过的权限，即需提醒权限</li>
     * </ol>
     */
    public enum PermissionStatus {
        GRANTED, DENIED, SHOW_RATIONALE
    }

    /**
     * 已获取的权限列表
     */
    public interface PermissionsGranted {
        /**
         * 已获取的权限列表
         * @param permissions 列表长度可能为0
         */
        void onPermissionsGranted(String[] permissions);
    }

    /**
     * 未拥有的权限列表
     */
    public interface PermissionsDenied {
        /**
         * 未拥有的权限列表
         * @param permissions 列表长度可能为0
         * @return 是否继续发送低优先级的权限
         */
        boolean onPermissionsDenied(String[] permissions);
    }

    /**
     * 用户拒绝过的权限列表
     */
    public interface PermissionsShowRationale {
        /**
         * 用户拒绝过的权限列表
         * @param permissions 列表长度可能为0
         * @return 是否继续发送低优先级的权限
         */
        boolean onPermissionsShowRationale(String[] permissions);
    }

    /**
     * 所有状态的权限都使用这一个回调接口
     */
    public interface OnPermissions {

        /**
         * 返回请求权限的状态
         * @param status 状态
         * @param permissions 相同状态的权限
         * @return 是否继续通知下一个状态的权限
         */
        boolean onPermissions(PermissionStatus status, String[] permissions);

    }

    /**
     * 将所有权限打包，作为请求结果返回，分为三类
     */
    public interface OnBundlePermissions {
        /**
         * 将请求的权限分为三类发送，均不会为空，长度可能为零
         * @param granted 已获得的权限
         * @param denied 已拒绝的权限
         * @param showRationale 需提醒的权限
         */
        void onPermissions(String[] granted, String[] denied, String[] showRationale);
    }

    /**
     * 用于做获取请求权限结果的回调使用的Fragment
     */
    public static final class PermissionsFragment extends Fragment {
        private WeakReference<Permissions> permissions;

        public PermissionsFragment setPermissions(Permissions permissions) {
            this.permissions = new WeakReference<>(permissions);
            return this;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            Permissions p = this.permissions.get();
            if (p != null) {
                p.sendPermissions(requestCode, permissions, checkPermissions(p.activity, permissions));
                p.activity.getFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
                p.activity.getFragmentManager().executePendingTransactions();
            }
        }
    }
}

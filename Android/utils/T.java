package lf.simpleutils.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by Lion on 4/2/2016.
 * 封装Toast的常用方式
 */
public class T {
    public T() { throw new Error("不要实例化此类"); }

    private static Toast toast;

    private static Context context;

    @SuppressLint("ShowToast")
    public static void setupContext(Context context) {
        T.context = context;
        toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
    }

    public static void showLong(int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
    }

    public static void showLong(CharSequence msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void showShort(int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    public static void showShort(CharSequence msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void singletonLong(int resId) {
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setText(resId);
        toast.show();
    }

    public static void singletonLong(CharSequence s) {
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setText(s);
        toast.show();
    }

    public static void singletonShort(int resId) {
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setText(resId);
        toast.show();
    }

    public static void singletonShort(CharSequence s) {
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setText(s);
        toast.show();
    }
}

package lf.simpleutils.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Jason on 2016/4/7.
 * Android XML 工具类的封装
 */
@SuppressLint("CommitPrefEdits")
public final class S {
    private static SharedPreferences xml;

    public static void setupContext(Context context, String name) {
        xml = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor edit() {
        if (xml != null) return xml.edit();
        throw new Error("没有设置上下文");
    }

    public static SharedPreferences xml(){
        if (xml != null) return xml;
        throw new Error("没有设置上下文");
    }
}

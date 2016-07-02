package com.learn_faster.fasterui;

import java.io.PrintWriter;
import java.io.StringWriter;

import static android.util.Log.DEBUG;
import static android.util.Log.ERROR;
import static android.util.Log.INFO;
import static android.util.Log.VERBOSE;
import static android.util.Log.WARN;
import static android.util.Log.println;
/**
 * 日志类，用于代替TAG的存在<br/>
 * AndroidStudio 中配合以下Live Template使用：
 * <h4>log.i("$METHOD_NAME$()", $args$);</h4>
 * <ul>
 *     <li>$METHOD_NAME$ = methodName()</li>
 *     <li>$args$ = groovyScript("'\"' + _1.collect { it + ' = [\" , ' + it + ' , \"]'}.join(', ') + '\"'", methodParameters())</li>
 * </ul>
 * @author Jason
 */
@SuppressWarnings("WeakerAccess")
public final class Log {
    private static final String TAG = "[LOG]";
    /** 系统的行分隔符 */
    private static final char LINE_SEPARATOR = '\n';
    /** Object... 参数为null时的替代字符串 */
    private static final String OBJECTS_NULL = TAG + " ---> Objects = null";
    /** Throwable 参数为null时的替代字符串 */
    private static final String THROW_NULL = TAG + " ---> Throwable = null";
    /** 日志总开关，设置为 false 不再记录任何日志 */
    public static boolean ON = true;
    /** 每个实例的标签 */
    public String tag = TAG;
    /** 每个实例的开关 */
    public boolean on = ON;

    /** 默认TAG="[LOG]"，默认打开日志记录 */
    public Log(){}
    /** 改变日志的TAG */
    public Log(String tag){ this.tag = tag; }
    /** 是否打开日志 */
    public Log(boolean on) { this.on = on; }
    /** 改变日志的TAG，是否打开日志记录 */
    public Log(String tag, boolean on){ this.tag = tag; this.on = on; }

    // /////////////////////////////////////////////////////////////////
    // ////实例相关记录方法 开始位置
    // /////////////////////////////////////////////////////////////////

    public void v(Object... logMe)               { if (on) V(tag, logMe); }

    public void v(Throwable tr, Object... logMe) { if (on) V(tag, tr, logMe); }

    public void d(Object... logMe)               { if (on) D(tag, logMe); }

    public void d(Throwable tr, Object... logMe) { if (on) D(tag, tr, logMe); }

    public void i(Object... logMe)               { if (on) I(tag, logMe); }

    public void i(Throwable tr, Object... logMe) { if (on) I(tag, tr, logMe); }

    public void w(Object... logMe)               { if (on) W(tag, logMe); }

    public void w(Throwable tr, Object... logMe) { if (on) W(tag, tr, logMe); }

    public void e(Object... logMe)               { if (on) E(tag, logMe); }

    public void e(Throwable tr, Object... logMe) { if (on) E(tag, tr, logMe); }

    // /////////////////////////////////////////////////////////////////
    // ////实例相关记录方法 结束位置
    // /////////////////////////////////////////////////////////////////

    // /////////////////////////////////////////////////////////////////
    // ////类相关记录方法 开始位置
    // /////////////////////////////////////////////////////////////////

    public static void V(String tag, Object... logMe)               { if (ON) doLog(VERBOSE, tag, logMe); }

    public static void V(String tag, Throwable tr, Object... logMe) { if (ON) doLog(VERBOSE, tag, tr, logMe); }

    public static void D(String tag, Object... logMe)               { if (ON) doLog(DEBUG, tag, logMe); }

    public static void D(String tag, Throwable tr, Object... logMe) { if (ON) doLog(DEBUG, tag, tr, logMe); }

    public static void I(String tag, Object... logMe)               { if (ON) doLog(INFO, tag, logMe); }

    public static void I(String tag, Throwable tr, Object... logMe) { if (ON) doLog(INFO, tag, tr, logMe); }

    public static void W(String tag, Object... logMe)               { if (ON) doLog(WARN, tag, logMe); }

    public static void W(String tag, Throwable tr, Object... logMe) { if (ON) doLog(WARN, tag, tr, logMe); }

    public static void E(String tag, Object... logMe)               { if (ON) doLog(ERROR, tag, logMe); }

    public static void E(String tag, Throwable tr, Object... logMe) { if (ON) doLog(ERROR, tag, tr, logMe); }

    private static void doLog(int level, String tag, Object... log){
        println(level, tag, pairedObjectsToString(null, log));
    }

    private static void doLog(int level, String tag, Throwable tr, Object... log){
        println(level, tag, pairedObjectsToString(null, log) + getStackTraceString(tr));
    }

    // /////////////////////////////////////////////////////////////////
    // ////类相关记录方法 结束位置
    // /////////////////////////////////////////////////////////////////


    /**
     * Change from {@link android.util.Log}<br/>
     * Handy function to get a loggable stack trace from a Throwable
     * @param tr An exception to log
     */
    public static String getStackTraceString(Throwable tr) {
        if (tr == null) return LINE_SEPARATOR + THROW_NULL;
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.append(LINE_SEPARATOR);
        pw.append("[thr]-> ");
        tr.printStackTrace(pw);
        pw.close();
        return sw.toString();
    }

    /**
     * 将对象的 toString() 全部拼接起来<br/>
     * <ul>
     *     <li>参数个数为奇数，第一个参数独占一行，后面的参数将以键值对形式拼接，拼接结果占一行</li>
     *     <li>参数个数为偶数，参数将以键值对形式拼接，拼接结果占一行</li>
     * </ul>
     * @param builder 拼接字符串到此对象
     * @param logMe 许多对象
     * @return 拼接好的字符串
     */
    public static String pairedObjectsToString(StringBuilder builder, Object... logMe){
        if(logMe == null) return OBJECTS_NULL;
        int len = logMe.length;
        if (len > 0){
            if(builder == null) builder = new StringBuilder();
            byte startIndex = 0;
            if((len & 1) == 1) {
                builder.append('[').append(startIndex++).append("]-> ").append(logMe[0]).append(LINE_SEPARATOR);
            }
            for (int i = startIndex; i<len; i++) builder.append('[').append(startIndex++).append("]-> ").append(logMe[i++]).append(" = [").append(logMe[i]).append(']').append(LINE_SEPARATOR);
            return builder.toString();
        } else return "{}";
    }

}

package com.lf.db;

import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * 日志类
 * @author Lion
 */
public class Log {
	protected static final String TAG = "[LOG]";
	/** 用于计算时间字符串 */
    private static final Calendar t = Calendar.getInstance();
	/** 系统的行分隔符 */
    protected static final String LINE_SEPARATOR = System.lineSeparator();
	/** Object... 参数为null时的替代字符串 */
    protected static final String OBJECTS_NULL = TAG + " ---> Objects = null";
    /** Throwable 参数为null时的替代字符串 */
    protected static final String THROW_NULL = TAG + " ---> Throwable = null";	
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

	public void v(Object... logMe) 				 { doLog(Level.VERBOSE, logMe); }

	public void v(Throwable tr, Object... logMe) { doLog(Level.VERBOSE, tr, logMe); }

	public void t(Object... logMe) 				 { doLog(Level.TRACE, logMe); }

	public void t(Throwable tr, Object... logMe) { doLog(Level.TRACE, tr, logMe); }

	public void d(Object... logMe) 				 { doLog(Level.DEBUG, logMe); }

	public void d(Throwable tr, Object... logMe) { doLog(Level.DEBUG, tr, logMe); }

	public void i(Object... logMe) 				 { doLog(Level.INFO, logMe); }

	public void i(Throwable tr, Object... logMe) { doLog(Level.INFO, tr, logMe); }

	public void e(Object... logMe) 				 { doLog(Level.ERROR, logMe); }

	public void e(Throwable tr, Object... logMe) { doLog(Level.ERROR, tr, logMe); }

	public void w(Object... logMe) 				 { doLog(Level.WARN, logMe); }

	public void w(Throwable tr, Object... logMe) { doLog(Level.WARN, tr, logMe); }

	public void f(Object... logMe) 				 { doLog(Level.FATAL, logMe); }

	public void f(Throwable tr, Object... logMe) { doLog(Level.FATAL, tr, logMe); }
	/**
	 * 实例，记录日志的方法<br/>
	 * 子类可以覆盖，自定义输出格式等等
	 * @param level 日志等级
	 * @param logMe 内容
	 */
	protected void doLog(Level level, Object... logMe){ 
		if(ON && on) System.out.print(timeString() + ' ' + level + ' ' + tag + objectsToString(logMe));
	}
	/**
	 * 实例，记录日志的方法<br/>
	 * 子类可以覆盖，自定义输出格式等等
	 * @param level 日志等级
	 * @param tr 需要记录的异常
	 * @param logMe 内容
	 */
	protected void doLog(Level level, Throwable tr, Object... logMe){ 
		if(ON && on) System.err.print(timeString() + ' ' + level + ' ' + tag + objectsToString(logMe) + getStackTraceString(tr));
	}
	/** 当前时间的字符串，子类可覆盖 */
	protected String timeString(){
		return TIME_STRING();
	}
	
	// /////////////////////////////////////////////////////////////////
	// ////实例相关记录方法 结束位置
	// /////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////
	// ////类相关记录方法 开始位置
	// /////////////////////////////////////////////////////////////////

	public static void V(String tag, Object... logMe)				{ DOLOG(Level.VERBOSE, tag, logMe); }
	
	public static void V(String tag, Throwable tr,Object... logMe)	{ DOLOG(Level.VERBOSE, tag, tr, logMe); }

	public static void T(String tag, Object... logMe)				{ DOLOG(Level.TRACE, tag, logMe); }
	
	public static void T(String tag, Throwable tr,Object... logMe)	{ DOLOG(Level.TRACE, tag, tr, logMe); }
	
	public static void D(String tag, Object... logMe)				{ DOLOG(Level.DEBUG, tag, logMe); }
	
	public static void D(String tag, Throwable tr,Object... logMe)	{ DOLOG(Level.DEBUG, tag, tr, logMe); }
	
	public static void I(String tag, Object... logMe)				{ DOLOG(Level.INFO, tag, logMe); }
	
	public static void I(String tag, Throwable tr,Object... logMe)	{ DOLOG(Level.INFO, tag, tr, logMe); }
	
	public static void E(String tag, Object... logMe)				{ DOLOG(Level.ERROR, tag, logMe); }
	
	public static void E(String tag, Throwable tr,Object... logMe)	{ DOLOG(Level.ERROR, tag, tr, logMe); }
	
	public static void W(String tag, Object... logMe)				{ DOLOG(Level.WARN, tag, logMe); }
	
	public static void W(String tag, Throwable tr,Object... logMe)	{ DOLOG(Level.WARN, tag, tr, logMe); }
	
	public static void F(String tag, Object... logMe)				{ DOLOG(Level.FATAL, tag, logMe); }
	
	public static void F(String tag, Throwable tr,Object... logMe)	{ DOLOG(Level.FATAL, tag, tr, logMe); }
	
	/**
	 * 记录日志的方法
	 * @param LEVEL 日志等级
	 * @param TAG 标签
	 * @param logMe 内容
	 */
	private static void DOLOG(Level LEVEL, String TAG, Object... logMe){ 
		if(ON) System.out.print(TIME_STRING() + ' ' + LEVEL + ' ' + TAG + objectsToString(logMe));
	}
	/**
	 * 记录日志的方法
	 * @param LEVEL 日志等级
	 * @param TAG 标签
	 * @param tr 需要记录的异常
	 * @param logMe 内容
	 */
	private static void DOLOG(Level LEVEL, String TAG, Throwable tr, Object... logMe){ 
		if(ON) System.err.print(TIME_STRING() + ' ' + LEVEL + ' ' + TAG + objectsToString(logMe) + getStackTraceString(tr));
	}
    /** 获取当前时间的字符串，不怕多线程 */
    public static String TIME_STRING(){
		t.setTimeInMillis(System.currentTimeMillis());
        return t.get(Calendar.YEAR)+"-"+(t.get(Calendar.MONTH) + 1)+'-'+t.get(Calendar.DAY_OF_MONTH)+' '+ t.get(Calendar.HOUR_OF_DAY)+':'+t.get(Calendar.MINUTE)+':'+t.get(Calendar.SECOND);
    }
	
	// /////////////////////////////////////////////////////////////////
	// ////类相关记录方法 结束位置
	// /////////////////////////////////////////////////////////////////
	
	
    /**
     * Handy function to get a loggable stack trace from a Throwable
     * @param tr An exception to log
     */
    public static String getStackTraceString(Throwable tr) {
    	if (tr == null) return THROW_NULL + LINE_SEPARATOR;
    	StringWriter sw = new StringWriter();
    	PrintWriter pw = new PrintWriter(sw);
    	pw.append("[thr]-> ");
		tr.printStackTrace(pw);
		pw.append(LINE_SEPARATOR);
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


    /**
     * 日志的等级<br/>
     * <li> Verbose <p> 冗余日志，就看看，没卵用的日志
     * <li> Trace   <p> 跟踪日志，跟踪某个逻辑记录日志
     * <li> Debug   <p> 调试日志，调试期间的日志
     * <li> Info    <p> 记录日志，记录运行状况
     * <li> Warn    <p> 警告日志，记录需要警惕的地方
     * <li> Error   <p> 错误日志，记录可能出错的地方
     * <li> Fatal   <p> 毁灭日志，记录可能导致应用出现问题的日志
     */
	public enum Level{
		VERBOSE ((byte) 0),
		TRACE   ((byte) 1), 
		DEBUG   ((byte) 2), 
		INFO    ((byte) 3), 
		WARN    ((byte) 4), 
		ERROR   ((byte) 5), 
		FATAL   ((byte) 6);
		
		byte level;
		Level(byte level){ this.level = level; }
		
		@Override
		public String toString() { return "[ " + name() + '(' + level + ") ]"; }
	}
}

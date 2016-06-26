package com.lf.db;

/**
 * Created by Lion on 6/26/2016.
 * 数组操作类
 */
public class ArrayUtils {

    /**
     * 将任意数组转换为对象数组（包括基本数据类型）
     * @param arr 数组对象（务必保证是一个数组对象）
     * @return 转换后的数组
     */
    public static Object[] arrayToObjects(Object arr) {
        if (arr == null) return null;
        Object[] result;
        int len;
        if (boolean[].class == arr.getClass()) {
            boolean[] array = (boolean[]) arr;
            len = array.length;
            result = new Boolean[len];
            for (int i = 0; i < len; i++) {
                result[i] = array[i];
            }
        } else if (byte[].class == arr.getClass()) {
            byte[] array = (byte[]) arr;
            len = array.length;
            result = new Byte[len];
            for (int i = 0; i < len; i++) {
                result[i] = array[i];
            }
        } else if (char[].class == arr.getClass()) {
            char[] array = (char[]) arr;
            len = array.length;
            result = new Character[len];
            for (int i = 0; i < len; i++) {
                result[i] = array[i];
            }
        } else if (short[].class == arr.getClass()) {
            short[] array = (short[]) arr;
            len = array.length;
            result = new Short[len];
            for (int i = 0; i < len; i++) {
                result[i] = array[i];
            }
        } else if (int[].class == arr.getClass()) {
            int[] array = (int[]) arr;
            len = array.length;
            result = new Integer[len];
            for (int i = 0; i < len; i++) {
                result[i] = array[i];
            }
        } else if (float[].class == arr.getClass()) {
            float[] array = (float[]) arr;
            len = array.length;
            result = new Float[len];
            for (int i = 0; i < len; i++) {
                result[i] = array[i];
            }
        } else if (long[].class == arr.getClass()) {
            long[] array = (long[]) arr;
            len = array.length;
            result = new Long[len];
            for (int i = 0; i < len; i++) {
                result[i] = array[i];
            }
        } else if (double[].class == arr.getClass()) {
            double[] array = (double[]) arr;
            len = array.length;
            result = new Double[len];
            for (int i = 0; i < len; i++) {
                result[i] = array[i];
            }
        } else {
            try{
                result = (Object[]) arr;
            } catch (ClassCastException e){ return null; }
        }
        return result;
    }

    /**
     * 将任意数组拼接成字符串
     * @param arr 任意数组（务必保证是一个数组，基本数据类型也可以）
     * @return 拼接的字符串
     */
    public static String toString(Object arr){
        if (arr == null) return null;
        Object[] array = arrayToObjects(arr);
        if (array == null) return arr.toString();
        int len = array.length;
        if (len > 0) {
            StringBuilder message = new StringBuilder();
            message.append('{');
            for (Object object : array) message.append(object).append(',');
            message.append('}');
            return message.toString();
        } else return "{}";
    }

}

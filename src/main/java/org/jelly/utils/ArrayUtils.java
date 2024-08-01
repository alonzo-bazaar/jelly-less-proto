package org.jelly.utils;

public class ArrayUtils {
    public static Class<?>[] typeArray(Object[] arr) {
        Class<?>[] paramTypes = new Class<?>[arr.length];
        for (int i = 0; i < arr.length; ++i) {
            paramTypes[i] = arr[i].getClass();
        }
        return paramTypes;
    }

    public static<T> void printArray(T[] arr, String separator) {
        for (int i = 0; i<arr.length-1; ++i) {
            System.out.print(arr[i]);
            System.out.print(separator);
        }
        System.out.print(arr[arr.length-1]);
    }

    public static<T> void printlnArray(T[] arr, String separator) {
        printArray(arr, separator);
        System.out.println();
    }

    public static<T> String renderArr(T[] arr, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i<arr.length-1; ++i) {
            sb.append(arr[i]);
            sb.append(separator);
        }
        sb.append(arr[arr.length-1]);
        return sb.toString();
    }
}

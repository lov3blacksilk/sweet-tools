package com.sweet.android.util;

import java.lang.reflect.Array;
import java.util.List;

/**
 * Created by dingding on 28/04/16.
 */
public class LeUtil {

    /**
     * list转换成数组
     * @param type
     * @param lists
     * @param <T>
     * @return
     */
    public static  <T> T[] listToArray(Class<T> type,List<T>... lists){
        int index =0 ;
        for (List<T> l:lists){
            index +=l.size();
        }
        T[] tArray = (T[]) Array.newInstance(type,index);
        index =0;
        for (List<T> l: lists){
            for (T t: l){
                tArray[index++] = t;
            }
        }

        return tArray;
    }
}

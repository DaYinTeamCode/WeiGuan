package com.androidex.util;


/**
 * 对象工具类
 * Created by yihaibin on 2018/9/28.
 */

public class ObjectUtil {

    /**
     * 检查对象非空
     *
     * @param object
     * @return
     */
    public static boolean isNotNull(Object object) {

        return !isNull(object);
    }

    /**
     * 检查对象集非空
     *
     * @param objects
     * @return
     */
    public static boolean isNotNull(Object... objects) {

        return !isNull(objects);
    }

    /**
     * 检查对象是否为空
     *
     * @param object
     * @return
     */
    public static boolean isNull(Object object) {

        return object == null;
    }

    /**
     * 检查一组对象是否有空
     *
     * @param objects
     * @return true 有一个对象为空 false 每个对象都不为空
     */
    public static boolean isNull(Object... objects) {

        if (CollectionUtil.isEmpty(objects))
            return true;

        for (int i = 0; i < objects.length; i++) {

            if (objects[i] == null)
                return true;
        }

        return false;
    }

    /**
     * 判断两个对象是否相等
     *
     * @param object1
     * @param object2
     * @return
     */
    public static boolean equals(Object object1, Object object2) {

        if (object1 != null && object2 != null)
            return object1.equals(object2);
        else
            return false;
    }
}

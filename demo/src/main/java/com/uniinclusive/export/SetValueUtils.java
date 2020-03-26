package com.uniinclusive.export;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Author: ghlin
 * @Date: 2020/3/21 16:04
 */
public class SetValueUtils {
    public static void setProjectValue(Object object, String field, String value) {
        try {
            Class<?> aClass = aClass = object.getClass();
            String methodName = "set" + field.substring(0, 1).toUpperCase() + field.substring(1);
            Method method = aClass.getMethod(methodName, String.class);
            if (!StringUtils.isBlank(value)) {
                value = value.trim();
            }
            method.invoke(object, value);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

package org.bigbear.impressweibo.support.utils;

import android.support.v7.widget.RecyclerView;
import android.widget.AbsListView;
import android.widget.AdapterView;

import java.lang.reflect.Field;

/**
 * User: qii
 * Date: 14-7-3
 */
public class JavaReflectionUtility {

    public static <T> T getValue(RecyclerView view, String name) {
        final Field field;
        try {
            field = RecyclerView.class.getDeclaredField(name);
            field.setAccessible(true);
            return (T) field.get(view);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void setValue(AdapterView view, String name, int value) {
        final Field field;
        try {
            field = AdapterView.class.getDeclaredField(name);
            field.setAccessible(true);
            field.setInt(view, value);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
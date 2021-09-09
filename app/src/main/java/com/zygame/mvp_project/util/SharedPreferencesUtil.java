/**
 * Copyright 2016 JustWayward Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zygame.mvp_project.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Map;
import java.util.Set;

/**
 * Created by lfh on 2016/8/13
 */
public class SharedPreferencesUtil {
    public static Context sContext;
    public static SharedPreferences sSharedPreferences;
    public static SharedPreferences.Editor sEditor;

    public static void init(Context context, String prefs_name, int mode) {
        sContext = context;
        sSharedPreferences = sContext.getSharedPreferences(prefs_name, mode);
        sEditor = sSharedPreferences.edit();
    }

    private SharedPreferencesUtil() {
    }


    public static boolean getBoolean(String key, boolean defaultVal) {
        return sSharedPreferences.getBoolean(key, defaultVal);
    }

    public static boolean getBoolean(String key) {
        return sSharedPreferences.getBoolean(key, false);
    }


    public static String getString(String key, String defaultVal) {
        return sSharedPreferences.getString(key, defaultVal);
    }

    public static String getString(String key) {
        return sSharedPreferences.getString(key, null);
    }

    public static int getInt(String key, int defaultVal) {
        return sSharedPreferences.getInt(key, defaultVal);
    }

    public static int getInt(String key) {
        return sSharedPreferences.getInt(key, 0);
    }


    public static float getFloat(String key, float defaultVal) {
        return sSharedPreferences.getFloat(key, defaultVal);
    }

    public static float getFloat(String key) {
        return sSharedPreferences.getFloat(key, 0f);
    }

    public static long getLong(String key, long defaultVal) {
        return sSharedPreferences.getLong(key, defaultVal);
    }

    public static long getLong(String key) {
        return sSharedPreferences.getLong(key, 0L);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static Set<String> getStringSet(String key, Set<String> defaultVal) {
        return sSharedPreferences.getStringSet(key, defaultVal);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static Set<String> getStringSet(String key) {
        return sSharedPreferences.getStringSet(key, null);
    }

    public static Map<String, ?> getAll() {
        return sSharedPreferences.getAll();
    }

    public static boolean exists(String key) {
        return sSharedPreferences.contains(key);
    }


    public static void putString(String key, String value) {
        sEditor.putString(key, value);
        sEditor.commit();
    }

    public static void putInt(String key, int value) {
        sEditor.putInt(key, value);
        sEditor.commit();

    }

    public static void putFloat(String key, float value) {
        sEditor.putFloat(key, value);
        sEditor.commit();

    }

    public static void putLong(String key, long value) {
        sEditor.putLong(key, value);
        sEditor.commit();
    }

    public static void putBoolean(String key, boolean value) {
        sEditor.putBoolean(key, value);
        sEditor.commit();
    }

    public void commit() {
        sEditor.commit();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void putStringSet(String key, Set<String> value) {
        sEditor.putStringSet(key, value);
        sEditor.commit();
    }

    public static void putObject(String key, Object object) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(baos);
            out.writeObject(object);
            String objectVal = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            sEditor.putString(key, objectVal);
            sEditor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                baos.close();
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static <T> T getObject(String key, Class<T> clazz) {
        if (sSharedPreferences.contains(key)) {
            String objectVal = sSharedPreferences.getString(key, null);
            byte[] buffer = Base64.decode(objectVal, Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(bais);
                T t = (T) ois.readObject();
                return t;
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    bais.close();
                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void remove(String key) {
        sEditor.remove(key);
        sEditor.commit();
    }

    public static void removeAll() {
        sEditor.clear();
        sEditor.commit();
    }
}

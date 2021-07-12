package moe.minamigo.MiuiEuLocalization;

import android.content.Context;
import java.io.File;
import dalvik.system.DexFile;

public class SystemPropertiesProxy {
    public static String get(Context context, String key) throws IllegalArgumentException {
        try {
            Class SystemProperties = context.getClassLoader().loadClass("android.os.SystemProperties");
            return (String) SystemProperties.getMethod("get", String.class).invoke(SystemProperties, new String(key));
        } catch (IllegalArgumentException iAE) {
            throw iAE;
        } catch (Exception e) {
            return "";
        }
    }

    public static String get(Context context, String key, String def) throws IllegalArgumentException {
        try {
            Class SystemProperties = context.getClassLoader().loadClass("android.os.SystemProperties");
            return (String) SystemProperties.getMethod("get", String.class, String.class).invoke(SystemProperties, new String(key), new String(def));
        } catch (IllegalArgumentException iAE) {
            throw iAE;
        } catch (Exception e) {
            return def;
        }
    }

    public static Integer getInt(Context context, String key, int def) throws IllegalArgumentException {
        Integer.valueOf(def);
        try {
            Class SystemProperties = context.getClassLoader().loadClass("android.os.SystemProperties");
            return (Integer) SystemProperties.getMethod("getInt", String.class, Integer.TYPE).invoke(SystemProperties, new String(key), new Integer(def));
        } catch (IllegalArgumentException iAE) {
            throw iAE;
        } catch (Exception e) {
            return Integer.valueOf(def);
        }
    }

    public static Long getLong(Context context, String key, long def) throws IllegalArgumentException {
        Long.valueOf(def);
        try {
            Class SystemProperties = context.getClassLoader().loadClass("android.os.SystemProperties");
            return (Long) SystemProperties.getMethod("getLong", String.class, Long.TYPE).invoke(SystemProperties, new String(key), new Long(def));
        } catch (IllegalArgumentException iAE) {
            throw iAE;
        } catch (Exception e) {
            return Long.valueOf(def);
        }
    }

    public static Boolean getBoolean(Context context, String key, boolean def) throws IllegalArgumentException {
        Boolean.valueOf(def);
        try {
            Class SystemProperties = context.getClassLoader().loadClass("android.os.SystemProperties");
            return (Boolean) SystemProperties.getMethod("getBoolean", String.class, Boolean.TYPE).invoke(SystemProperties, new String(key), new Boolean(def));
        } catch (IllegalArgumentException iAE) {
            throw iAE;
        } catch (Exception e) {
            return Boolean.valueOf(def);
        }
    }

    public static void set(Context context, String key, String val) throws IllegalArgumentException {
        try {
            new DexFile(new File("/system/app/Settings.apk"));
            context.getClassLoader();
            Class SystemProperties = Class.forName("android.os.SystemProperties");
            SystemProperties.getMethod("set", String.class, String.class).invoke(SystemProperties, new String(key), new String(val));
        } catch (IllegalArgumentException iAE) {
            throw iAE;
        } catch (Exception e) {
        }
    }
}

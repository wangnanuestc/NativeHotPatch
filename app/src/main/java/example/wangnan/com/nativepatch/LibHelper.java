package example.wangnan.com.nativepatch;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Vector;

import dalvik.system.BaseDexClassLoader;
import example.wangnan.com.nativepatch.Utils.ReflectUtils;

/**
 * 必须的工具类
 * Created by wangnan on 2016/10/15.
 */

public class LibHelper {
    //    public static native String closeLibrary();
    private static final String TAG = "LibHelper";

    public static synchronized void unloadAllNativeLibs() {
        try {
            ClassLoader classLoader = MainActivity.class.getClassLoader();
            Field field = ClassLoader.class.getDeclaredField("nativeLibraries");
            field.setAccessible(true);
            Vector<Object> libs = (Vector<Object>) field.get(classLoader);
            Iterator it = libs.iterator();
            while (it.hasNext()) {
                Object object = it.next();
                Method finalize = object.getClass().getDeclaredMethod("finalize");
                finalize.setAccessible(true);
                finalize.invoke(object);
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }


    public static synchronized void unloadNativeLibs(Context context, String libName) {
        try {
            Log.d(TAG, "unloadNativeLibs: libName = " + libName);
            ClassLoader pathClassLoader = context.getClassLoader();

            Object dexPathList = ReflectUtils.getField(pathClassLoader, BaseDexClassLoader.class, "pathList");
            File[] nativeLibraryDirectories = (File[]) ReflectUtils.getField(dexPathList, dexPathList.getClass(), "nativeLibraryDirectories");


            for (int i = 0; i < nativeLibraryDirectories.length; i++) {
                Object object = nativeLibraryDirectories[i];
                Field[] fs = object.getClass().getDeclaredFields();
                for (int k = 0; k < fs.length; k++) {
                    Log.d(TAG, "fs[k].getName() = " + fs[k].getName());
                    if (fs[k].getName().equals("name")) {
                        fs[k].setAccessible(true);
                        String dllPath = fs[k].get(object).toString();
                        Log.d(TAG, "dllPath = " + dllPath);
                        if (dllPath.endsWith(libName)) {
                            Method finalize = object.getClass().getDeclaredMethod("finalize");
                            finalize.setAccessible(true);
                            finalize.invoke(object);
                        }
                    }
                }
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

}



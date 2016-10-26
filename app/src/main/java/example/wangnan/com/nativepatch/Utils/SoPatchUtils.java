package example.wangnan.com.nativepatch.Utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import dalvik.system.BaseDexClassLoader;

/**
 * so 补丁帮助类
 * Created by wangnan on 2016/10/6.
 */

public class SoPatchUtils {
    private static final String TAG = "SoPatchUtils";

    private static final String PATCH_SO_DIR = "so_patch";


    public static void doPatch(Context context) {
        Log.d(TAG, "doPatch()");

        String sdcardPatchSoPath = Environment.getExternalStorageDirectory().getAbsolutePath().concat("/patch.jar");


        File patchSoDir = new File(context.getFilesDir(), PATCH_SO_DIR);
        if (patchSoDir.exists() && patchSoDir.isDirectory()) {
            if (!FileUtils.deleteDir(patchSoDir)) {
                Log.d(TAG, "delete file failed!");
                return;
            }
        }
        patchSoDir.mkdir();
        Log.d(TAG, "patchSoDir = " + patchSoDir.getAbsolutePath());

        String internalPatchJarPath = patchSoDir.getAbsolutePath().concat("/patch.jar");
        boolean loadResult = FileUtils.loadSdcard2Files(sdcardPatchSoPath, internalPatchJarPath);
        if (loadResult) {
            Log.d(TAG, "load2Files success!");
        } else {
            Log.d(TAG, "load2Files failed !");
            return;
        }
        try {
            ZipUtils.decompress(internalPatchJarPath);
            File file = new File(internalPatchJarPath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        String[] abiArray = getAbiList();
        if (abiArray.length > 0) {
            for (String str : abiArray) {
                Log.d(TAG, str);
            }

            String primaryAbi = abiArray[0];
            String patchSoPatchPath = getPrimaryAbiSoFilePath(primaryAbi, patchSoDir.getAbsolutePath());
            Log.d(TAG, "patchSoPatchPath = " + patchSoPatchPath);

            File patchSoPatch = new File(patchSoPatchPath);
            if (patchSoPatch.exists()) {
                Log.d(TAG, "patchSoPatch exist, path = " + patchSoPatch.getAbsolutePath());

                //开始注入补丁路径
                injectSoPath(context, patchSoPatch.getParent());

            } else {
                Log.d(TAG, "patchSoPatch does not exist");
                return;
            }
        }


    }


    private static String[] getAbiList() {
        String abiList = null;
        try {
            abiList = FileUtils.getFileProperties("/system/build.prop", "ro.product.cpu.abilist");
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] abiArray = abiList.split(",");

        return abiArray;
    }


    private static String getPrimaryAbiSoFilePath(String primaryAbi, String patchSoDir) {
        return patchSoDir.concat("/lib/").concat(primaryAbi).concat("/libnative-lib.so");

    }

    /**
     * 向nativeLibraryDirectories数组的第一个位置插入补丁包目录
     *
     * @param context 上下文
     * @param path    补丁包路径
     * @return 是否成功
     */
    private static boolean injectSoPath(Context context, String path) {
        ClassLoader pathClassLoader = context.getClassLoader();
        Log.d(TAG, "pathClassLoader = " + pathClassLoader);
        try {
            File patchDirFile = new File(path);
            Object dexPathList = ReflectUtils.getField(pathClassLoader, BaseDexClassLoader.class, "pathList");
            File[] nativeLibraryDirectories = (File[]) ReflectUtils.getField(dexPathList, dexPathList.getClass(), "nativeLibraryDirectories");

            //当已经有补丁包目录时,不做任何操作
            if (nativeLibraryDirectories != null) {
                if (nativeLibraryDirectories[0].getAbsolutePath().contains(PATCH_SO_DIR)) {
                    Log.d(TAG, "has already patched, return !");
                    return true;
                }


                File[] newNativeLibraryDirectories = new File[nativeLibraryDirectories.length + 1];
                newNativeLibraryDirectories[0] = patchDirFile;
                Log.d(TAG, "injectSoPath==========");
                for (int i = 0; i < nativeLibraryDirectories.length; i++) {
                    Log.d(TAG, nativeLibraryDirectories[i].getAbsolutePath());
                    newNativeLibraryDirectories[i + 1] = nativeLibraryDirectories[i];
                }

                ReflectUtils.setField(dexPathList, dexPathList.getClass(), "nativeLibraryDirectories", newNativeLibraryDirectories);
                verify(context, path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private static void verify(Context context, String path) {
        ClassLoader pathClassLoader = context.getClassLoader();
        Log.d(TAG, "verify ----------------------");
        try {
            File patchDirFile = new File(path);
            Object dexPathList = ReflectUtils.getField(pathClassLoader, BaseDexClassLoader.class, "pathList");
            File[] nativeLibraryDirectories = (File[]) ReflectUtils.getField(dexPathList, dexPathList.getClass(), "nativeLibraryDirectories");

            if (nativeLibraryDirectories != null) {
                for (File file : nativeLibraryDirectories) {
                    Log.d(TAG, file.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

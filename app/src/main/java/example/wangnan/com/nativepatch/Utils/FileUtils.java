package example.wangnan.com.nativepatch.Utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * 文件帮助类
 * Created by wangnan on 2016/10/5.
 */

public class FileUtils {
    private static final String TAG = "FileUtils";

    static boolean loadSdcard2Files(String sdcardPath, String internalPath) {
        boolean result = false;
        do {
            if (TextUtils.isEmpty(sdcardPath)) {
                Log.d(TAG, "path is empty !");
                break;
            }

            File pathFile = new File(sdcardPath);
            if (!pathFile.exists()) {
                Log.d(TAG, "the patch file is not exist !");
                break;
            }

            File sdcardSo = new File(sdcardPath);
            File internalSo = new File(internalPath);

            BufferedInputStream soReader = null;
            OutputStream soWriter = null;

            try {
                soReader = new BufferedInputStream(new FileInputStream(sdcardSo));
                soWriter = new BufferedOutputStream(new FileOutputStream(internalSo));


                byte[] buf = new byte[BUF_SIZE];
                int len;

                while ((len = soReader.read(buf, 0, BUF_SIZE)) > 0) {
                    soWriter.write(buf, 0, len);
                }

                soWriter.close();
                soReader.close();
                result = true;
            } catch (Exception e) {
                e.printStackTrace();

                if (soReader != null) {
                    try {
                        soReader.close();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }

                if (soWriter != null) {
                    try {
                        soWriter.close();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }

        } while (false);

        return result;
    }


    private static final int BUF_SIZE = 2048;

    public static boolean prepareSo(Context context, File soInternalStoragePath, String so_file) {
        BufferedInputStream bis = null;
        OutputStream soWriter = null;

        try {
            bis = new BufferedInputStream(context.getAssets().open(so_file));
            soWriter = new BufferedOutputStream(new FileOutputStream(soInternalStoragePath));
            byte[] buf = new byte[BUF_SIZE];
            int len;
            while ((len = bis.read(buf, 0, BUF_SIZE)) > 0) {
                soWriter.write(buf, 0, len);
            }
            soWriter.close();
            bis.close();
            return true;
        } catch (IOException e) {
            if (soWriter != null) {
                try {
                    soWriter.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
            return false;
        }
    }

    public static String copyAsset(Context context, String assetName, File dir) throws IOException {


        File outFile = new File(dir, assetName);
        if (!outFile.exists()) {
            AssetManager assetManager = context.getAssets();
            InputStream in = assetManager.open(assetName);
            OutputStream out = new FileOutputStream(outFile);
            copyFile(in, out);
            in.close();
            out.close();
        }
        return outFile.getAbsolutePath();
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    protected static String getFileProperties(String file, String key) {
        FileInputStream fileStream = null;
        try {
            String value = null;
            Properties prop = new Properties();
            fileStream = new FileInputStream(new File(file));
            if (fileStream != null) {
                prop.load(fileStream);
                value = prop.getProperty(key);
            }
            return TextUtils.isEmpty(value) ? "UNKNOW" : value;
        } catch (Exception e) {
            return "UNKNOWN";
        } finally {
            try {
                fileStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

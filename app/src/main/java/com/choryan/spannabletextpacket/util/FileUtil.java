package com.choryan.spannabletextpacket.util;

import android.os.Environment;

import com.choryan.spannabletextpacket.base.BaseApplication;
import com.google.gson.Gson;
import com.lltvcn.freefont.core.data.DrawData;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;

import drawn.lltvcn.com.textdemo.MainApplication;

public class FileUtil {
    private static String ROOT_PATH = "SpnnableTestPacket";
    private static String IMG_PATH = "imgs";
    private static String DATA_PATH = "datas";

    static {
        File file = new File(getDataDir());
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static String getRootPath() {
        return getRootPath(ROOT_PATH, false, false);
    }

    public static String getDataDirByName(String name) {
        return getDataDir() + File.separator + name;
    }

    public static String getDataDir() {
        return getRootPath() + DATA_PATH;
    }

    public static String getImgDir() {
        return getRootPath() + IMG_PATH;
    }

    public static boolean checkSDCard() {
        return Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState());
    }

    public static String getRootPath(String name, boolean hasMedia, boolean forceNoSDCard) {
        String path = null;
        if (!forceNoSDCard && checkSDCard()) {
            path = Environment.getExternalStorageDirectory().toString()
                    + File.separator
                    + name
                    + File.separator;

        } else {

            File dataDir = BaseApplication.instance.getFilesDir();
            if (dataDir != null) {
                path = dataDir + File.separator
                        + name
                        + File.separator;
                File file = new File(path);
                if (!file.exists()) {
                    file.mkdirs();
                    file.setExecutable(true, false);
                    file.setReadable(true, false);
                    file.setWritable(true, false);
                }
            } else {
                path = Environment.getDataDirectory().toString() + File.separator
                        + name
                        + File.separator;
            }
        }

        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        if (!file.exists()) { //解决一部分手机将eMMC存储挂载到 /mnt/external_sd 、/mnt/sdcard2 等节点
            String tmpPath = createRootDirInDevMount();
            if (tmpPath != null) {
                path = tmpPath + File.separator + name
                        + File.separator;
            }

            file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }

        }
        return path;
    }

    public static boolean deleteFile(String filepath) {
        if (filepath == null || "".equals(filepath)) {
            return false;
        }
        File file = new File(filepath);
        if (file.exists() && file.isFile())
            return file.delete();
        return false;
    }

    public static void deleteDir(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;

        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDir(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    public static void copyFile(File from, File to) {
        if (null == from || !from.exists()) {
            return;
        }
        if (null == to) {
            return;
        }
        FileInputStream is = null;
        FileOutputStream os = null;
        try {
            is = new FileInputStream(from);
            if (!to.exists()) {
                to.createNewFile();
            }
            os = new FileOutputStream(to);
//			copyFileFast(is, os);
            copyFileUseNormal(from, to);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeIO(is, os);
        }
    }

    public static void copyFileUseNormal(File srcFile, File destFile) throws IOException {
        try {
            copyFile(srcFile, destFile, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void copyFile(File srcFile, File destFile,
                                 boolean preserveFileDate) throws IOException, FileNotFoundException {
        if (srcFile == null)
            throw new NullPointerException("Source must not be null");
        if (destFile == null)
            throw new NullPointerException("Destination must not be null");
        if (!srcFile.exists())
            throw new FileNotFoundException("Source '" + srcFile
                    + "' does not exist");
        if (srcFile.isDirectory())
            throw new IOException("Source '" + srcFile
                    + "' exists but is a directory");
        if (srcFile.getCanonicalPath().equals(destFile.getCanonicalPath()))
            throw new IOException("Source '" + srcFile + "' and destination '"
                    + destFile + "' are the same");
        if (destFile.getParentFile() != null
                && !destFile.getParentFile().exists()
                && !destFile.getParentFile().mkdirs())
            throw new IOException("Destination '" + destFile
                    + "' directory cannot be created");
        if (destFile.exists() && !destFile.canWrite()) {
            throw new IOException("Destination '" + destFile
                    + "' exists but is read-only");
        } else {
            doCopyFile(srcFile, destFile, preserveFileDate);
            return;
        }
    }

    private static void closeQuietly(OutputStream output) {
        try {
            if (output != null)
                output.close();
        } catch (IOException ioe) {
        }
    }

    private static void closeQuietly(InputStream input) {
        try {
            if (input != null)
                input.close();
        } catch (IOException ioe) {
        }
    }

    private static void doCopyFile(File srcFile, File destFile,
                                   boolean preserveFileDate) throws IOException {
        if (destFile.exists() && destFile.isDirectory())
            throw new IOException("Destination '" + destFile
                    + "' exists but is a directory");
        FileInputStream input = new FileInputStream(srcFile);
        try {
            FileOutputStream output = new FileOutputStream(destFile);
            try {
                copy(input, output);
            } finally {
                closeQuietly(output);
            }
        } finally {
            closeQuietly(input);
        }
        if (srcFile.length() != destFile.length())
            throw new IOException("Failed to copy full contents from '"
                    + srcFile + "' to '" + destFile + "'");
        if (preserveFileDate)
            destFile.setLastModified(srcFile.lastModified());
    }

    private static int copy(InputStream input, OutputStream output)
            throws IOException {
        byte buffer[] = new byte[4096];
        int count = 0;
        for (int n = 0; -1 != (n = input.read(buffer)); ) {
            output.write(buffer, 0, n);
            count += n;
        }

        return count;
    }

    private static String createRootDirInDevMount() {
        String path = null;
        ArrayList<String> dirs = getDevMountList();
        if (dirs != null) {
            for (int i = 0; i < dirs.size(); i++) {
                String tmpPath = dirs.get(i);
                if ("/mnt/sdcard2".equals(tmpPath)
                        || "/mnt/external_sd".equals(tmpPath)) {
                    path = tmpPath;
                    break;
                }

            }
        }

        return path;
    }

    /**
     * 遍历 "system/etc/vold.fstab” 文件，获取全部的Android的挂载点信息
     *
     * @return
     */
    public static ArrayList<String> getDevMountList() {
        String[] toSearch = readFile("/etc/vold.fstab").split(" ");
        ArrayList<String> out = new ArrayList<String>();
        for (int i = 0; i < toSearch.length; i++) {
            if (toSearch[i].contains("dev_mount")) {
                if (new File(toSearch[i + 2]).exists()) {
                    out.add(toSearch[i + 2]);
                }
            }
        }
        return out;
    }

    /**
     * 从文件中读取文本
     *
     * @param filePath
     * @return
     */
    public static String readFile(String filePath) {
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
        } catch (Exception e) {
            throw new RuntimeException("jjjj");
        }
        return inputStream2String(is);
    }

    /**
     * 输入流转字符串
     *
     * @param is
     * @return 一个流中的字符串
     */
    public static String inputStream2String(InputStream is) {
        if (null == is) {
            return null;
        }
        StringBuilder resultSb = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            resultSb = new StringBuilder();
            String len;
            while (null != (len = br.readLine())) {
                resultSb.append(len);
            }
        } catch (Exception ex) {
        } finally {
            closeIO(is);
        }
        return null == resultSb ? null : resultSb.toString();
    }

    /**
     * 关闭流
     *
     * @param closeables
     */
    public static void closeIO(Closeable... closeables) {
        if (null == closeables || closeables.length <= 0) {
            return;
        }
        for (Closeable cb : closeables) {
            try {
                if (null == cb) {
                    continue;
                }
                cb.close();
            } catch (IOException e) {
                throw new RuntimeException("");
            }
        }
    }

    public static DrawData getDrawData(String name) {
        String dir = getDataDirByName(name);
        try {
            FileReader reader = new FileReader(dir + File.separator + name + ".txt");
            DrawData data = new Gson().fromJson(reader, DrawData.class);
            reader.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}

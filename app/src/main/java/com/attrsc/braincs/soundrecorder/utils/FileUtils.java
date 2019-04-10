package com.attrsc.braincs.soundrecorder.utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Shuai
 * 07/02/2019.
 */

public class FileUtils {

    private final static String TAG = FileUtils.class.getSimpleName();
    private static File appFolder;

//    public static String getPcmFileAbsolutePath(String fileName) {
//        String directory = Environment.getExternalStorageDirectory().getAbsolutePath();
//
//        File file = new File(directory + File.separator + Constants.APP_NAME);
//        if (!file.exists()){
//            file.mkdirs();
//        }
//        file = new File(directory + File.separator + Constants.APP_NAME + File.separator +  fileName+".pcm");
//        if (!file.exists()){
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return file.getAbsolutePath();
//    }
//
//    public static String getTmpFileAbsolutePath(String fileName) {
//        if (appFolder == null || !appFolder.exists()){
//            String directory = Environment.getExternalStorageDirectory().getAbsolutePath();
//            appFolder = new File(directory + File.separator + Constants.APP_NAME);
//            appFolder.mkdirs();
//        }
//
//        File file = new File(appFolder,  fileName + ".tmp");
//        if (!file.exists()){
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return file.getAbsolutePath();
//    }
//    public static String getAacFileAbsolutePath(String fileName) {
//        if (appFolder == null || !appFolder.exists()){
//            String directory = Environment.getExternalStorageDirectory().getAbsolutePath();
//            appFolder = new File(directory + File.separator + Constants.APP_NAME);
//            appFolder.mkdirs();
//        }
//
//        File file = new File(appFolder,  fileName + ".aac");
//        if (!file.exists()){
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return file.getAbsolutePath();
//    }
//
//    public static String getWavFileAbsolutePath(String fileName) {
//        String directory = Environment.getExternalStorageDirectory().getAbsolutePath();
//        File file = new File(directory + File.separator + Constants.APP_NAME);
//        if (!file.exists()){
//            file.mkdirs();
//        }
//        file = new File(directory + File.separator + Constants.APP_NAME + File.separator + fileName+".wav");
//        if (!file.exists()){
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return file.getAbsolutePath();
//    }
//
//    public static String getDemuxerMuxerAbsolutePath(String fileName){
//        String directory = Environment.getExternalStorageDirectory().getAbsolutePath();
//        File file = new File(directory + File.separator + Constants.APP_NAME);
//        if (!file.exists()){
//            file.mkdirs();
//        }
//        file = new File(directory + File.separator + Constants.APP_NAME + File.separator + fileName);
//        if (!file.exists()){
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return file.getAbsolutePath();
//    }


    public static String getFileUnderAppFolder(String fileName){
        if (appFolder == null || !appFolder.exists()){
            String directory = Environment.getExternalStorageDirectory().getAbsolutePath();
            appFolder = new File(directory + File.separator + Constants.APP_NAME);
            appFolder.mkdirs();
        }
        File file = new File(appFolder, fileName);
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.getAbsolutePath();
    }

    /**
     * 合并多个pcm文件为一个wav文件
     *
     * @param filePathList    pcm文件路径集合
     * @param destinationPath 目标wav文件路径
     * @return true|false
     */
    public static boolean mergeFiles(List<String> filePathList,
                                                 String destinationPath) {
        File[] file = new File[filePathList.size()];
        byte buffer[] = null;

        int TOTAL_SIZE = 0;
        int fileNum = filePathList.size();

        for (int i = 0; i < fileNum; i++) {
            file[i] = new File(filePathList.get(i));
            TOTAL_SIZE += file[i].length();
        }


        //先删除目标文件
        File destfile = new File(destinationPath);
        if (destfile.exists())
            destfile.delete();

        //合成所有的文件的数据，写到目标文件
        try {
            buffer = new byte[1024 * 4]; // Length of All Files, Total Size
            InputStream inStream = null;
            OutputStream ouStream = null;

            ouStream = new BufferedOutputStream(new FileOutputStream(
                    destinationPath));
            for (int j = 0; j < fileNum; j++) {
                inStream = new BufferedInputStream(new FileInputStream(file[j]));
                int size = inStream.read(buffer);
                while (size != -1) {
                    ouStream.write(buffer);
                    size = inStream.read(buffer);
                }
                inStream.close();
            }
            ouStream.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
            return false;
        } catch (IOException ioe) {
            Log.e(TAG, ioe.getMessage());
            return false;
        }
        clearFiles(filePathList);
        Log.i(TAG, "mergeFiles  success!" + new SimpleDateFormat("yyyy-MM-dd hh:mm").format(new Date()));
        return true;

    }

    /**
     * 清除文件
     *
     * @param filePathList
     */
    private static void clearFiles(List<String> filePathList) {
        for (int i = 0; i < filePathList.size(); i++) {
            File file = new File(filePathList.get(i));
            if (file.exists()) {
                file.delete();
            }
        }
    }
}

package com.hmdp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DeleteOldTsFiles {


    public static final long LASTTIMELONG = 1000 * 2 *60;

    public static final String FILEPATH ="D:\\aaaaaa";

    public static void main(String[] args) throws InterruptedException {
        String arg = args[0];

        File dir = new File(arg);
        if (dir.exists()) {
            while (true){
                deleteTsFiles(dir);
                Thread.sleep(1000*60);
            }
        } else {
            System.out.println("指定路径不存在！");
        }
    }


    public static void deleteTsFiles(File dir) {
        // 获取当前日期
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String currentDate = dateFormat.format(calendar.getTime());
        System.out.println("系统当前日期为=-----"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));

        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                String[] subdirectories = file.list();

                for (String subdir : subdirectories) {
                    File subdirectory = new File(file.getPath() + "\\" + subdir);
                    // 判断是否为yyyyMMdd格式的文件夹名称
                    if (subdirectory.isDirectory() && subdir.matches("\\d{8}")) {
                        // 如果该文件夹的名称小于当前日期，则删除该文件夹和子文件夹
                        if (subdir.compareTo(currentDate) < 0) {
                            deleteDirectory(subdirectory);
                        }else {
                            delete(subdirectory);
                        }
                    }

                }

            }

        }
    }

    // 删除文件夹和其中的文件和子文件夹
    private static void deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            for (File file : files) {
                deleteDirectory(file);
            }
        }
        directory.delete();
    }


    //根据路径删除下面的.ts文件
    public static void delete(File pathFile){
        File[] filesSec = pathFile.listFiles();
        for (File file: filesSec) {
            String fileName = file.getName();
            // 判断后缀是否为.ts
            if (fileName.endsWith(".ts")) {
                Calendar calendar = Calendar.getInstance();
                // 获取当前时间毫秒数, 一个小时之前
                long currentMillisecond = calendar.getTimeInMillis() - LASTTIMELONG;
                // 获取文件最后修改时间毫秒数
                long lastModifiedMillisecond = file.lastModified();

                // 判断文件修改日期是否在当前日期之前
                if (lastModifiedMillisecond < currentMillisecond) {
                    boolean result = file.delete();
                    if (result) {
                        System.out.println("已成功删除文件：" + file.getAbsolutePath());
                    } else {
                        System.out.println("无法删除文件：" + file.getAbsolutePath());
                    }
                }

            }else {
                System.out.println("不是后缀为.ts文件：" + file.getAbsolutePath());
            }
        }
    }

}

package com.attackt.logivisual.utils;

import java.io.File;

/**
 * 文件操作Utils
 */
public class FileUtil {
    /**
     * 获取文件路径
     * @return 路径
     */
    public static String getBasePath(){
        String path=FileUtil.class.getClassLoader().getResource("").getPath();
        File file = new File(path).getParentFile().getParentFile();
        return file.getPath();
    }
}

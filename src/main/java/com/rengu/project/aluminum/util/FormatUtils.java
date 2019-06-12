package com.rengu.project.aluminum.util;

import org.apache.commons.io.FilenameUtils;

/**
 * com.rengu.project.aluminum.util
 *
 * @author hanchangming
 * @date 2019-06-10
 */
public class FormatUtils {

    public static String formatPath(String path) {
        String reg0 = "\\\\＋";
        String reg = "\\\\＋|/＋";
        String temp = path.trim().replaceAll(reg0, "/");
        temp = temp.replaceAll(reg, "/");
        if (temp.endsWith("/")) {
            temp = temp.substring(0, temp.length() - 1);
        }
        if (System.getProperty("file.separator").equals("\\")) {
            temp = temp.replace('/', '\\');
        }
        return FilenameUtils.separatorsToUnix(temp);
    }
}

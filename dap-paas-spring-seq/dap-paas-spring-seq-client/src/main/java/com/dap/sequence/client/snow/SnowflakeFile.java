package com.dap.sequence.client.snow;

import java.io.*;
import java.net.URL;

import com.dap.sequence.api.exception.ExceptionEnum;
import com.dap.sequence.api.exception.SequenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SnowflakeFile {

    private static final Logger LOGGER = LoggerFactory.getLogger(SnowflakeFile.class);
    
	public static String FILE_SEPARATOR = "/";
	public static String FILE_NAME = "seq-snowflake-clock.data";
    /**
     * 获取到的文件路径信息，可能保护file:
     */
    private static final String SYS_FILE = "file:";

    public static void writerClock(long clock) {
        String filePath = getFilePath();
        FileWriter writer = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                // 创建文件
                boolean result = file.createNewFile();
                LOGGER.warn("创建文件是否成功:{}", result);
            }
            writer = new FileWriter(file);
            // 向文件写入内容
            writer.write(clock + "");
            writer.flush();
        } catch (Exception e) {
            LOGGER.warn("序列雪花算法时钟ID写入临时文件:{}失败：", filePath, e);
        } finally {
            streamClose(writer);
        }
    }


    public static long readerClock() {
        File file = new File(getFilePath());
        FileReader fr = null;
        try {
            if (!file.exists()) {
                boolean result = file.createNewFile();
                LOGGER.warn("创建文件是否成功:{}", result);
                return 0L;
            }
            // 创建 FileReader 对象
            fr = new FileReader(file);
            char[] a = new char[50];
            // 从数组中读取内容
            int arr = fr.read(a);
            LOGGER.warn("文件读取数组大小:{}", arr);
            String s = new String(a);
            return Long.parseLong(s.trim());
        } catch (Exception e) {
            LOGGER.warn("序列雪花算法时钟ID读取临时文件失败.msg = {}", e.getMessage(), e);
            return -1;
        } finally {
            streamClose(fr);
        }
    }

    public static String getFilePath() {
        return getProjectHomePath() + File.separator + FILE_NAME;
    }

    public static String getProjectHomePath() {
        URL resource = SnowflakeFile.class.getClassLoader().getResource("");
        String rootPath = new File(resource.getFile()).getAbsolutePath();
        rootPath = rootPath.endsWith(File.separator + "classes") || rootPath.endsWith(File.separator + "classes" + File.separator)
                ? replaceLast(rootPath, "classes", "") : rootPath;
        if (rootPath.contains(SYS_FILE)) {
            rootPath = rootPath.substring(0, rootPath.indexOf(SYS_FILE));
        }
        return rootPath.substring(0, rootPath.lastIndexOf(File.separator));
    }

    public static String replaceLast(String text, String strToReplace, String replaceWithThis) {
        return text.replaceFirst("(?s)" + strToReplace + "(?!.*?" + strToReplace + ")", replaceWithThis);
    }

    /**
     * 关闭流
     *
     * @param closeable closeable
     */
    public static void streamClose(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            LOGGER.error("streamClose error.msg = {}", e.getMessage(), e);
            throw new SequenceException(ExceptionEnum.CLOSE_STREAM_ERROR);
        }
    }
}

package com.hs.util.file;

import org.apache.log4j.Logger;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @Author: songhao
 * @Date: 2020/9/14
 * @Description: 文件处理工具类
 */
public class FileUtil {

    static Logger logger = Logger.getLogger(FileUtil.class);

    public final static String ENCODE_UTF8 = "utf-8";
    public final static int FLUSH_LINE = 10000;

    /**
     * 获取文件的扩展名
     * @param filename 文件名称
     * @return 文件的扩展名
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     * 获取路径的父路径
     * @param path 文件路径
     * @return 父路径
     */
    public static String getParentPath(String path){
        File file = createFile(path);
        return file.getParent();
    }

    /**
     * 获取路径的子路径集合
     * @param path
     * @return
     */
    public static String[] getChildPaths(String path){
        File file = new File(path);
        String[] childs = file.list();
        for(int i = 0; i < childs.length; i++){
            childs[i] = path + File.separator + childs[i];
        }
        return childs;
    }

    /**
     * 判断目录是否存在，存在就返回true，不存在就返回false
     * @param path 文件目录
     * @return true表示存在，false表示不存在
     */
    public static boolean isExists(String path){
        File file = new File(path);
        if(file.exists()) {
            return true;
        }
        return false;
    }

    /**
     * 根据目录进行创建
     * @param path 文件路径
     */
    public static void mkdirs(String path){
        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
    }

    /**
     * 通过路径创建一个文件
     * @param path 文件路径
     * @return
     */
    public static File createFile(String path){
        File file = new File(path);
        return file;
    }

    /**
     * 根据文件路径获取reader
     * @param path
     * @return
     * @throws Exception
     */
    public static BufferedReader getReader(String path) throws Exception{
        BufferedReader br = null;
        try{
            br = new BufferedReader(new InputStreamReader(new FileInputStream(path),ENCODE_UTF8));
        }catch(Exception e){
            throw e;
        }
        return br;
    }

    /**
     * 根据文件路径获取writer
     * @param path
     * @param isAppend
     * @return
     * @throws Exception
     */
    public static BufferedWriter getWriter(String path,boolean isAppend) throws Exception{
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(path, isAppend)));
        } catch (Exception e) {
            throw e;
        }
        return writer;
    }

    /**
     * 读取下一行文本
     * @param reader
     * @return
     * @throws Exception
     */
    public static String readNextLine(BufferedReader reader) throws Exception{
        String tempLine = null;
        if(reader == null){
            return tempLine;
        }
        try{
            tempLine = reader.readLine();
        }catch(Exception e){
            throw e;
        }
        return tempLine;
    }

    /**
     * 写内容到文件中去
     * @param path 文件路径
     * @param lineList 内容
     * @param isAppend 是否追加写内容
     */
    public static void writeFile(String path, List<String> lineList, boolean isAppend) throws Exception{
        BufferedWriter writer = null;
        try{
            writer = getWriter(path,isAppend);
            int linecount = 0;
            for(String line : lineList){
                linecount ++;
                writer.write(line);
                writer.newLine();
                if(linecount % FLUSH_LINE == 0){
                    writer.flush();
                }
            }
        }catch (Exception e){
            throw e;
        }finally {
            closeWrite(writer);
        }
    }

    /**
     * 写内容到文件中去
     * @param writer
     * @param linecontent
     */
    public static void writeContent(BufferedWriter writer,String linecontent) throws Exception{
        try{
            writer.write(linecontent);
            writer.newLine();
        }catch(Exception e){
            throw e;
        }
    }

    /**
     * 实现两个文件的高效拷贝
     * @param sourcepath
     * @param destpath
     */
    public static void copyFile(String sourcepath,String destpath){
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(sourcepath);
            fo = new FileOutputStream(destpath);
            in = fi.getChannel();//得到对应的文件通道
            out = fo.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        } finally {
            try {
                if(fi != null){
                    fi.close();
                }
                if(in != null){
                    in.close();
                }
                if(fo != null){
                    fo.close();
                }
                if(out != null){
                    out.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
        }
    }

    /**
     * 通过关键词在文件中出现的次数
     * @param path
     * @param word
     */
    public static int queryWcByFile(String path,String word){
        BufferedReader reader = null;
        int count = 0;
        try{
            reader = getReader(path);
            String tempLine = null;
            while((tempLine = reader.readLine()) != null){
                if(tempLine.contains(word)){
                    count++;
                }
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }finally {
            closeReader(reader);
        }
        return count;
    }

    /**
     * 将一个文件夹中的文件合并，pattern为支持的正则表达式
     * @param folder
     * @param pattern
     * @param mergePath
     */
    public static void merge(String folder,String pattern,String mergePath){
        File file = new File(folder);
        if(!file.exists()){
            logger.error("路径：" + folder + ",不存在,无法合并");
            return;
        }
        if(!file.isDirectory()){
            logger.error("路径：" + folder + ",不是一个文件夹,无法合并");
            return;
        }
        File[] childFiles = file.listFiles();
        logger.info("获取到子文件数量:[" + childFiles.length + "]");
        if(childFiles.length > 0){
            for(File childFile : childFiles){
                try{
                    boolean isMatch = Pattern.matches(pattern, childFile.getName());
                    if(isMatch){
                        logger.info("匹配到文件:" + childFile.getAbsolutePath());
                        merge(childFile.getAbsolutePath(),mergePath);
                    }
                }catch (Exception e){
                    logger.error(e.getStackTrace(),e);
                }
            }
        }
    }

    /**
     * 将一个文件的内容写入到另一个文件
     * @param sourcePath
     * @param mergePath
     */
    private static void merge(String sourcePath,String mergePath){
        BufferedWriter bw = null;
        BufferedReader br = null;
        try{
            bw = getWriter(mergePath,true);
            br = getReader(sourcePath);
            String tempLine = null;
            int flush = 0;
            while((tempLine = br.readLine()) != null){
                flush++;
                bw.write(tempLine);
                bw.newLine();
                if(flush % FLUSH_LINE == 0){
                    bw.flush();
                }
            }
        }catch (Exception e){
            logger.error(e.getStackTrace(),e);
        }finally {
            closeWrite(bw);
            closeReader(br);
        }
    }

    /**
     * 关闭Reader
     * @param reader
     * @throws Exception
     */
    public static void closeReader(Reader reader){
        if(reader != null){
            try{
                reader.close();
            }catch (Exception e){
                logger.error(e.getMessage(),e);
            }
        }
    }

    /**
     * 关闭Writer
     * @param writer
     * @throws Exception
     */
    public static void closeWrite(Writer writer){
        if(writer != null){
            try{
                writer.flush();
                writer.close();
            }catch (Exception e){
                logger.error(e.getMessage(),e);
            }
        }
    }

    /**
     * 获取所有的记录列表
     * @param path
     * @return
     */
    public static List<String> getLines(String path){
        List<String> lines = new ArrayList<String>();
        BufferedReader reader = null;
        try{
            reader = getReader(path);
            String tempLine = null;
            while((tempLine = reader.readLine()) != null){
                lines.add(tempLine);
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }finally {
            closeReader(reader);
        }
        return lines;
    }

    /**
     * 获取一个文件的记录行数
     * @param path
     * @return
     */
    public static int getLineCount(String path){
        int lines = 0;
        File file = new File(path);
        if(!file.exists()){
            logger.warn("文件路径:[" + path + "]不存在...");
            return lines;
        }
        LineNumberReader lnr = null;
        try{
            lnr = new LineNumberReader(new FileReader(file));
            while (lnr.readLine() != null){
                lines++;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(lnr != null){
                try {
                    lnr.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(),e);
                }
            }
        }
        return lines;
    }
}

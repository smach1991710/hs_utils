package com.hs.util.string;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

/**
 * @Author: songhao
 * @Date: 2020/9/14
 * @Description: 编码类
 */
public class CharsetUtil {

    public static final String ISO_8859_1 = "ISO-8859-1";
    public static final String UTF_8 = "UTF-8";
    public static final String GBK = "GBK";

    public static final Charset CHARSET_ISO_8859_1;
    public static final Charset CHARSET_UTF_8;
    public static final Charset CHARSET_GBK;

    public CharsetUtil() {
    }

    /**
     * 根据指定的编码返回CharSet
     * @param charsetName
     * @return
     * @throws UnsupportedCharsetException
     */
    public static Charset charset(String charsetName) throws UnsupportedCharsetException {
        return StringUtil.isNull(charsetName) ? Charset.defaultCharset() : Charset.forName(charsetName);
    }

    /**
     * 对字符串进行编码转换
     * @param source
     * @param srcCharset
     * @param destCharset
     * @return
     */
    public static String convert(String source, String srcCharset, String destCharset) {
        return convert(source, Charset.forName(srcCharset), Charset.forName(destCharset));
    }

    /**
     * 对字符串进行编码转换
     * @param source
     * @param srcCharset
     * @param destCharset
     * @return
     */
    public static String convert(String source, Charset srcCharset, Charset destCharset) {
        if (null == srcCharset) {
            srcCharset = StandardCharsets.ISO_8859_1;
        }

        if (null == destCharset) {
            destCharset = StandardCharsets.UTF_8;
        }

        return StringUtil.isNotNull(source) && !srcCharset.equals(destCharset) ? new String(source.getBytes(srcCharset), destCharset) : source;
    }

    public static String systemCharsetName() {
        return systemCharset().name();
    }

    /**
     * 判断当前系统是否是windows
     * @return
     */
    public static boolean isWindows() {
        return '\\' == File.separatorChar;
    }

    /**
     * 返回系统编码
     * @return
     */
    public static Charset systemCharset() {
        return isWindows() ? CHARSET_GBK : defaultCharset();
    }

    public static String defaultCharsetName() {
        return defaultCharset().name();
    }

    public static Charset defaultCharset() {
        return Charset.defaultCharset();
    }

    static {
        CHARSET_ISO_8859_1 = StandardCharsets.ISO_8859_1;
        CHARSET_UTF_8 = StandardCharsets.UTF_8;
        CHARSET_GBK = Charset.forName("GBK");
    }
}

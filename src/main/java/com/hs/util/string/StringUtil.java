package com.hs.util.string;

import com.hs.util.reflect.ReflectUtil;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: songhao
 * @Date: 2020/9/14
 * @Description: 字符串处理工具类
 */
public class StringUtil {

    private static final String PASSWORD_CRYPT_KEY = "songhao";
    private final static String DES = "DES";


    /**
     * 截掉字符串后面的一部分
     * @param value 原始字符串
     * @param end 最后一部分
     * @return
     */
    public static String substrEnd(String value,String end){
        int index = value.indexOf(end);
        if(index >= 0){
            value = value.substring(0,index);
        }
        return value;
    }

    /**
     * 判断字符串是否为空，为空就返回true，否则就返回false
     * @param value
     * @return
     */
    public static boolean isNull(String value){
        if(value == null || "".equals(value.trim()) || "null".equalsIgnoreCase(value.trim())){
            return true;
        }
        return false;
    }

    /**
     * 判断字符串是否为空，为空就返回false，否则就返回true
     * @param value
     * @return
     */
    public static boolean isNotNull(String value){
        return !isNull(value);
    }


    /**
     * 是否是以什么开头的字符串
     * @param value
     * @param startValue
     * @return
     */
    public static boolean startWith(String value,String startValue){
        if(value.startsWith(startValue)){
            return true;
        }
        return false;
    }

    /**
     * 是以什么开头的字符串，不区分大小写
     * @param value
     * @param startValue
     * @return
     */
    public static boolean startWithIgnoreCase(String value,String startValue){
        String lowerValue = value.toLowerCase();
        String lowerStartValue = startValue.toLowerCase();
        return startWith(lowerValue,lowerStartValue);
    }

    /**
     * 判断是否包含什么字符串
     * @param value
     * @param containsValue
     * @return
     */
    public static boolean contains(String value,String containsValue){
        if(value.contains(containsValue)){
            return true;
        }
        return false;
    }

    /**
     * 判断是否包含什么字符串，不区分大小写
     * @param value
     * @param containsValue
     * @return
     */
    public static boolean containsIgnoreCase(String value,String containsValue){
        String lowerValue = value.toLowerCase();
        String lowerContainsValue = containsValue.toLowerCase();
        return startWith(lowerValue,lowerContainsValue);
    }

    /**
     * 将第一个字母改成大写
     * @param value
     * @return
     */
    public static String upperFirst(String value){
        if (null == value) {
            return null;
        } else {
            if (value.length() > 0) {
                char firstChar = value.charAt(0);
                if (Character.isLowerCase(firstChar)) {
                    return Character.toUpperCase(firstChar) + subSuf(value, 1,value.length());
                }
            }

            return value.toString();
        }
    }

    /**
     * 将字符串的第一个字母改成小写
     * @param value
     * @return
     */
    public static String lowerFirst(String value){
        if (null == value) {
            return null;
        } else {
            if (value.length() > 0) {
                char firstChar = value.charAt(0);
                if (Character.isUpperCase(firstChar)) {
                    return Character.toLowerCase(firstChar) + subSuf(value, 1,value.length());
                }
            }

            return value.toString();
        }
    }

    /**
     * 判断一个字符重复的次数
     * @param value
     * @param ch
     * @return
     */
    public static int repeat(String value,char ch){
        int repeats = 0;
        if(isNull(value)){
            return repeats;
        }
        int length = value.length();
        for(int i = 0; i < length; i++){
            char temp = value.charAt(i);
            if(temp == ch){
                repeats++;
            }
        }
        return repeats;
    }

    /**
     * 判断一个字符串重复的次数
     * @param str
     * @param key
     * @return
     */
    public static int repeat(String str,String key)
    {
        int count = 0;
        int index = 0;
        while((index = str.indexOf(key))!=-1)
        {
            str = str.substring(index + key.length());
            count++;
        }
        return count;
    }


    /**
     * 字符串格式化
     * @param template
     * @param params
     * @return
     */
    public static String format(String template, Object... params) {
        if (null == template) {
            return null;
        } else {
            if(params != null && params.length != 0){
                return String.format(template.toString(), params);
            }
            return template.toString();
        }
    }

    /**
     * 格式化字符串
     * @param template
     * @param map
     * @return
     */
    public static String format(String template, Map<?, ?> map) {
        if (null == template) {
            return null;
        } else if (null != map && !map.isEmpty()) {
            String template2 = template.toString();

            Map.Entry entry;
            for(Iterator i$ = map.entrySet().iterator(); i$.hasNext(); template2 = template2.replace("{" + entry.getKey() + "}", utf8Str(entry.getValue()))) {
                entry = (Map.Entry)i$.next();
            }

            return template2;
        } else {
            return template.toString();
        }
    }

    /**
     * 将字符串转换成utf8格式的
     * @param obj
     * @return
     */
    public static String utf8Str(Object obj) {
        return str(obj, CharsetUtil.CHARSET_UTF_8);
    }

    /**
     * 将对象转换成指定编码的字符串
     * @param obj
     * @param charsetName
     * @return
     */
    public static String str(Object obj, String charsetName) {
        return str(obj, Charset.forName(charsetName));
    }

    /**
     * 将对象转换成指定编码的字符串
     * @param obj
     * @param charset
     * @return
     */
    public static String str(Object obj, Charset charset) {
        if (null == obj) {
            return null;
        } else if (obj instanceof String) {
            return (String)obj;
        } else if (obj instanceof byte[]) {
            return str((byte[])((byte[])obj), charset);
        } else if (obj instanceof Byte[]) {
            return str((Byte[])((Byte[])obj), charset);
        } else if (obj instanceof ByteBuffer) {
            return str((ByteBuffer)obj, charset);
        } else {
            return obj.toString();
        }
    }


    /**
     * 对字符串进行截取
     * @param value
     * @param fromIndex
     * @param toIndex
     * @return
     */
    public static String subSuf(String value, int fromIndex, int toIndex) {
        int len = value.length();
        if (fromIndex < 0) {
            fromIndex += len;
            if (fromIndex < 0) {
                fromIndex = 0;
            }
        } else if (fromIndex > len) {
            fromIndex = len;
        }

        if (toIndex < 0) {
            toIndex += len;
            if (toIndex < 0) {
                toIndex = len;
            }
        } else if (toIndex > len) {
            toIndex = len;
        }

        if (toIndex < fromIndex) {
            int tmp = fromIndex;
            fromIndex = toIndex;
            toIndex = tmp;
        }

        return fromIndex == toIndex ? "" : value.toString().substring(fromIndex, toIndex);
    }

    /**
     * 前缀包装
     * @param value
     * @param prefix
     * @return
     */
    public static String wrapPrefix(String value,String prefix){
        StringBuffer sb = new StringBuffer();
        sb.append(prefix);
        sb.append(value);
        return sb.toString();
    }

    /**
     * 后缀包装
     * @param value
     * @param suffix
     * @return
     */
    public static String wrapSuffix(String value,String suffix){
        StringBuffer sb = new StringBuffer();
        sb.append(value);
        sb.append(suffix);
        return sb.toString();
    }

    /**
     * 将字符串转换成一个Float型的数据，如果异常使用默认值
     * @param value
     * @param defaultValue
     * @return
     * @throws Exception
     */
    public static float parseToFloat(String value,float defaultValue) throws Exception{
        if(isNull(value)){
            return defaultValue;
        }
        try{
            return Float.parseFloat(value);
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * 将字符串转换为一个Int型的数据，如果异常使用默认值
     * @param value
     * @param defaultValue
     * @return
     */
    public static int parseToInt(String value,int defaultValue) throws Exception {
        if(isNull(value)){
            return defaultValue;
        }
        try{
            return Integer.parseInt(value);
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * 将字符串转换为一个long型的数据，如果异常使用默认值
     * @param value
     * @param defaultValue
     * @return
     */
    public static long parseToLong(String value,long defaultValue) throws Exception{
        if(isNull(value)){
            return defaultValue;
        }
        try{
            return Long.parseLong(value);
        }catch (Exception e){
            throw e;
        }
    }


    /**
     * 用指定字符串数组相连接，并返回
     * @param values 字符串数组
     * @param joinStr 连接数组的字符串
     * @return
     */
    public static String join(String[] values, String joinStr){
        if(values != null){
            if(values.length==1){
                return values[0];
            }
            StringBuffer sb = new StringBuffer();
            int i = 0;
            for (String value : values) {
                sb.append(value);
                if(i < values.length - 1){
                    sb.append(joinStr);
                }
                i++;
            }
            return sb.toString();
        }
        return null;
    }

    /**
     * 判断是否是一个数字，是就返回true，否则返回false
     * @param params
     * @return
     */
    public static boolean isNumeric(String... params) {
        Pattern pattern = Pattern.compile("^[0-9]+$");
        for (String param : params) {
            if (isNull(param)) {
                return false;
            }
            Matcher matcher = pattern.matcher(param);
            if (!matcher.matches()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 对url字符串进行编码.
     * @param url 要编码的url字符串
     * @return 编码后的字符串
     */
    public static String urlEncoder(String url) throws Exception {
        if (StringUtil.isNull(url)) {
            return null;
        }
        try {
            return java.net.URLEncoder.encode(url, "utf-8");
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 对url字符串进行解码.
     * @param url 要解码的url字符串
     * @return 解码后的字符串
     */
    public static String urlDecoder(String url) throws Exception{
        if (StringUtil.isNull(url)) {
            return null;
        }
        try {
            return URLDecoder.decode(url, "UTF-8");
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 二进制转字符串
     * @param b 字节数组
     * @return 转换成16进制的字符串
     */
    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
        }
        return hs.toUpperCase();
    }

    /**
     * 加密
     * {@link \http://www.blogjava.net/afei0922/articles/126332.html}
     * @param src 数据源
     * @param key 密钥，长度必须是8的倍数
     * @return 返回加密后的数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] src, byte[] key) throws Exception {
        // DES算法要求有一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        // 从原始密匙数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
        // 创建一个密匙工厂，然后用它把DESKeySpec转换成
        // 一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(DES);
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
        // 现在，获取数据并加密
        // 正式执行加密操作
        return cipher.doFinal(src);
    }

    /**
     * 解密
     *
     * @param src 数据源
     * @param key 密钥，长度必须是8的倍数
     * @return 返回解密后的原始数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] src, byte[] key) throws Exception {
        // DES算法要求有一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        // 从原始密匙数据创建一个DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
        // 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
        // 一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(DES);
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
        // 现在，获取数据并解密
        // 正式执行解密操作
        return cipher.doFinal(src);
    }

    /**
     * 密码解密
     *
     * @param data 加密的数据
     * @return 解密后的数据
     * @throws Exception
     */
    public final static String decrypt(String data) throws Exception{
        try {
            return new String(decrypt(hex2byte(data.getBytes()),
                    PASSWORD_CRYPT_KEY.getBytes()));
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 密码加密
     * @param password 原始数据
     * @return 加密后的数据
     * @throws Exception
     */
    public final static String encrypt(String password) throws Exception {
        try {
            return byte2hex(encrypt(password.getBytes(),
                    PASSWORD_CRYPT_KEY.getBytes()));
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 字符串
     * @param b
     * @return
     */
    private static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0)
            throw new IllegalArgumentException("长度不是偶数");
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }


    /**
     * 判断一个字符是否是中文
     * @param c
     * @return
     */
    private static boolean isChinese(char c) {
        return c >= 0x4E00 &&  c <= 0x9FA5;// 根据字节码判断
    }

    /**
     * 判断一个字符串是否含有中文
     * @param str
     * @return
     */
    public static boolean isChinese(String str) {
        if (str == null) return false;
        for (char c : str.toCharArray()) {
            if (isChinese(c))
                return true;// 有一个中文字符就返回
        }
        return false;
    }

    /**
     * 字符串根据字符分割成字符串数组
     * @param str 原字符串
     * @param sep 分割字符
     * @return 分割后的字符串数组
     */
    public static String[] strSplit(String str, char sep) {
        List<String> list = new LinkedList<String>();
        int pe = 0, ps = 0;

        while (pe != -1) {
            pe = str.indexOf(sep, ps);
            String s = "";
            if (pe == -1)
                s = str.substring(ps);
            else if (pe > ps) {
                s = str.substring(ps, pe);
                ps += (pe - ps) + 1;
            } else
                ps += 1;
            list.add(s);
        }

        String[] ss = new String[list.size()];
        list.toArray(ss);
        return ss;
    }

    /**
     * 字符串根据字符串分割成字符串数组
     * @param str 原字符串
     * @param sep 分割字符串
     * @return 分割后的字符串数组
     */
    public static String[] strSplit(String str, String sep) {
        List<String> list = new LinkedList<String>();
        int pe = 0, ps = 0;

        while (pe != -1) {
            pe = str.indexOf(sep, ps);
            String s = "";
            if (pe == -1)
                s = str.substring(ps);
            else if (pe > ps) {
                s = str.substring(ps, pe);
                ps += (pe - ps) + 1;
            } else
                ps += sep.length();
            list.add(s);
        }

        String[] ss = new String[list.size()];
        list.toArray(ss);
        return ss;
    }

    /**
     * 获取url上的参数信息
     * @param url
     * @return
     * @throws Exception
     */
    public static Map<String, String> getQueryParams(String url) throws Exception{
        Map<String, String> params = null;
        try {
            params = new HashMap<String, String>();
            String[] urlParts = url.split("\\?");
            if (urlParts.length > 1) {
                String query = urlParts[1];
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    String key = URLDecoder.decode(pair[0], "UTF-8");
                    String value = "";
                    if (pair.length > 1) {
                        value = URLDecoder.decode(pair[1], "UTF-8");
                    }
                    params.put(key,value);
                }
            }
            return params;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 将一个集合变成一个String对象
     * @param collection 集合对象
     * @param delimiter 分隔符
     * @return 返回集合中的每个元素通过分隔符分割的字符串
     */
    public static String toStrings(Collection collection,String delimiter){
        StringBuffer buffer = new StringBuffer();
        if(collection == null || collection.size() == 0){
            return buffer.toString();
        }
        int size = collection.size();
        int index = 0;
        for(Object obj : collection){
            buffer.append(obj);
            if(index < size - 1){
                buffer.append(delimiter);
            }
            index++;
        }
        return buffer.toString();
    }

    /**
     * 获取实体的某个字段信息
     * @param collection 集合对象
     * @param fieldName 字段名
     * @param delimiter 分割符
     * @return 拼接后的字符串
     */
    public static String toStrings(Collection collection,String fieldName,String delimiter){
        StringBuffer buffer = new StringBuffer();
        if(collection == null || collection.size() == 0){
            return buffer.toString();
        }
        int size = collection.size();
        int index = 0;
        for(Object obj : collection){
            buffer.append(ReflectUtil.getFieldValue(obj,fieldName));
            if(index < size - 1){
                buffer.append(delimiter);
            }
            index++;
        }
        return buffer.toString();
    }
}

package com.wyzk.lottery.utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 字符串相关的常用方法
 *
 * @author 鹿振
 */
public class StringUtil {
    /**
     * @Title:hexString2Bytes
     * @Description:16进制字符串转字节数组
     * @param src
     *            16进制字符串
     * @return 字节数组
     * @throws
     */
    public static byte[] hexString2Bytes(String src) {
        int l = src.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            ret[i] = (byte) Integer
                    .valueOf(src.substring(i * 2, i * 2 + 2), 16).byteValue();
        }
        return ret;
    }

    /**
     * Mac地址转换
     *
     * @param resBytes
     * @return
     */
    public static String byteToMac(byte[] resBytes) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < resBytes.length; i++) {
            String hex = Integer.toHexString(resBytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            buffer.append(hex.toUpperCase());
        }
        return buffer.toString();
    }

    /**
     * 判断参数是否为数�?
     *
     * @param strNum 待判断的数字参数
     * @return true表示参数为数字，false表示参数非数�?
     */
    public static boolean isNum(final String strNum) {
        return strNum.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
    }

    /**
     * @param c
     * @return
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    /**
     * 把一个对象数组用分隔字符串连接成一个字符串。
     *
     * @param objs        对象数组
     * @param splitString 分割字符串
     * @return 连接后的字符串
     */
    public static <T> String join(T[] objs, String splitString) {
        return join(objs, 0, objs.length, splitString);
    }

    /**
     * 把一个对象数组用分隔字符串连接成一个字符串。
     *
     * @param objs        对象数组
     * @param start       对象数组的开始位置（包含）
     * @param end         对象数组的结束位置（不包含）
     * @param splitString 分割字符串
     * @return 连接后的字符串
     */
    public static <T> String join(T[] objs, int start, int end, String splitString) {
        StringBuilder s = new StringBuilder();
        for (int i = start; i < end; i++) {
            if (i != start) {
                s.append(splitString);
            }
            s.append(objs[i]);
        }
        return s.toString();
    }

    /**
     * 把一个对象列表用分隔字符串连接成一个字符串。
     *
     * @param objList     对象列表
     * @param splitString 分割字符串
     * @return 连接后的字符串
     */
    public static String join(List<?> objList, String splitString) {
        return join(objList, 0, objList.size(), splitString);
    }

    /**
     * 把一个对象列表用分隔字符串连接成一个字符串。
     *
     * @param objList     对象列表
     * @param start       对象列表的开始位置（包含）
     * @param end         对象列表的结束位置（不包含）
     * @param splitString 分割字符串
     * @return 连接后的字符串
     */
    public static String join(List<?> objList, int start, int end, String splitString) {
        StringBuilder s = new StringBuilder();
        for (int i = start; i < end; i++) {
            if (i != start) {
                s.append(splitString);
            }
            s.append(objList.get(i));
        }
        return s.toString();
    }

    /**
     * 把一个对象数组的列表的某一列数据用分隔字符串连接成一个字符串。
     *
     * @param objList     对象数组的列表
     * @param splitString 分割字符串
     * @return 连接后的字符串
     */
    public static <T> String join(List<T[]> objList, int columnIndex, String splitString) {
        StringBuilder s = new StringBuilder();
        if (objList.size() > 0) {
            s.append(objList.get(0)[columnIndex]);
            for (int i = 1, ii = objList.size(); i < ii; i++) {
                s.append(splitString).append(objList.get(i)[columnIndex]);
            }
        }

        return s.toString();
    }

    /**
     * 重复字符规定的次数
     *
     * @param str    要重复的字符串
     * @param repeat 重复的次数，必须大于等于0，为0时返回""。
     * @return 返回重复后的字符串
     */
    public static String repeat(String str, int repeat) {
        if (str == null)
            throw new NullPointerException("重复的字符串不能为null。");

        if (repeat < 0)
            throw new IllegalArgumentException("重复的次数(" + repeat + ")小于底限0。");

        StringBuilder s = new StringBuilder();
        for (int i = 0; i < repeat; i++) {
            s.append(str);
        }

        return s.toString();
    }

    /**
     * 判断两个字符串是否相等，包括为null的情况。
     *
     * @param string  字符串
     * @param another 另一个字符串
     * @return true相等，否则不相等。
     */
    public static boolean equals(String string, String another) {
        if (string != null) {
            return string.equals(another);
        } else if (another != null) {
            return another.equals(string);
        }
        return true;
    }

    /**
     * 判断两个字符串忽略大小写后是否相等，包括为null的情况。
     *
     * @param string  字符串
     * @param another 另一个字符串
     * @return true相等，否则不相等。
     */
    public static boolean equalsIgnoreCase(String string, String another) {
        if (string != null) {
            return string.equalsIgnoreCase(another);
        } else if (another != null) {
            return another.equalsIgnoreCase(string);
        }
        return true;
    }

    /**
     * 判断一个字符串是否包含在一个字符串数组中，包括为null的情况。
     *
     * @param strToFind 要查找的字符串
     * @param strings   字符串数组
     * @return true包含，否则不包含。
     */
    public static boolean contains(String strToFind, String... strings) {
        for (String s : strings) {
            if (equals(s, strToFind)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断一个字符串是否包含在一个字符串数组中，忽略大小写，包括为null的情况。
     *
     * @param strToFind 要查找的字符串
     * @param strings   字符串数组
     * @return true包含，否则不包含。
     */
    public static boolean containsIgnoreCase(String strToFind, String... strings) {
        for (String s : strings) {
            if (equalsIgnoreCase(s, strToFind)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断字符串是否为null或去空格后长度为0
     *
     * @param s 字符串
     * @return true，为null或去空格后长度为0
     */
    public static boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

    /**
     * 判断至少一个字符串是否为null或去空格后长度为0
     *
     * @param ss 字符串数组
     * @return 如果至少一个字符串为null或去空格后长度为0，则返回true。
     */
    public static boolean isEmptyAny(String... ss) {
        for (String s : ss) {
            if (isEmpty(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断所有字符串是否为null或去空格后长度为0
     *
     * @param ss 字符串数组
     * @return 如果所有字符串为null或去空格后长度为0，则返回true。
     */
    public static boolean isEmptyAll(String... ss) {
        for (String s : ss) {
            if (isNotEmpty(s)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串是否不为null或去空格后长度为0
     *
     * @param s 字符串
     * @return true，不为null或去空格后长度为0
     */
    public static boolean isNotEmpty(String s) {
        return s != null && s.trim().length() > 0;
    }

    /**
     * 判断至少一个字符串是否不为null或去空格后长度为0
     *
     * @param ss 字符串数组
     * @return 如果至少一个字符串不为null或去空格后长度为0，则返回true。
     */
    public static boolean isNotEmptyAny(String... ss) {
        for (String s : ss) {
            if (isNotEmpty(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断所有字符串是否不为null或去空格后长度为0
     *
     * @param ss 字符串数组
     * @return 如果所有字符串不为null或去空格后长度为0，则返回true。
     */
    public static boolean isNotEmptyAll(String... ss) {
        for (String s : ss) {
            if (isEmpty(s)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查字符串是否为null或去空格后长度为0，如果是，则抛出IllegalStateException异常
     *
     * @param s 字符串
     * @throws IllegalStateException
     */
    public static void checkEmpty(String s) {
        if (isEmpty(s)) {
            throw new IllegalStateException();
        }
    }

    /**
     * 检查字符串是否为null或去空格后长度为0，如果是，则抛出IllegalStateException异常
     *
     * @param s   字符串
     * @param msg 异常信息
     * @throws IllegalStateException
     */
    public static void checkEmpty(String s, String msg) {
        if (isEmpty(s)) {
            throw new IllegalStateException(msg);
        }
    }

    /**
     * 检查对象是否为null，如果是则抛出NullPointerException异常
     *
     * @param o 检测对象
     */
    public static void checkNull(Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
    }

    /**
     * 检查对象是否为null，如果是则抛出NullPointerException异常
     *
     * @param o   检测对象
     * @param msg 异常信息
     */
    public static void checkNull(Object o, String msg) {
        if (o == null) {
            throw new NullPointerException(msg);
        }
    }

    /**
     * 获得指定字符集的字节数组
     *
     * @param s       字符串
     * @param charset 字符集
     * @return 字节数组
     */
    public static byte[] getBytes(String s, String charset) {
        try {
            return s.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 获得指定字符集的字节数组
     *
     * @param s       字符串
     * @param charset 字符集
     * @return 字节数组
     */
    public static byte[] getBytes(String s, Charset charset) {
        return s.getBytes(charset);
    }

    /**
     * 根据指定字符集将字节数组转换为字符串
     *
     * @param bytes   字节数组
     * @param charset 字符集
     * @return 字符串
     */
    public static String getString(byte[] bytes, String charset) {
        try {
            return new String(bytes, charset);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 根据指定字符集将字节数组转换为字符串
     *
     * @param bytes   字节数组
     * @param charset 字符集
     * @return 字符串
     */
    public static String getString(byte[] bytes, Charset charset) {
        return new String(bytes, charset);
    }

    /**
     * 将16进制字符串转换为字节数组
     *
     * @param hexString 16进制协议头字符串
     * @return 字节数组
     */
    public static byte[] hexStringToByteArray(String hexString) {
        if (hexString.length() % 2 != 0) {
            throw new IllegalArgumentException("16进制数据长度不为2的倍数：" + hexString);
        }

        StringReader stringReader = new StringReader(hexString);
        byte[] bytes = new byte[hexString.length() / 2];
        char[] chars = new char[2];
        try {
            for (int i = 0; stringReader.read(chars) != -1; i++) {
                bytes[i] = (byte) Integer.parseInt(String.valueOf(chars), 16);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        return bytes;
    }


    /**
     * 字符串转换为16进制字符串
     *
     * @param string  字符串
     * @param charset 字符集
     * @return 16进制字符串
     */
    public static String stringToHexString(String string, String charset) {
        byte[] bytes = getBytes(string, charset);
        return byteArrayToHexString(bytes);
    }

    /**
     * 字符串转换为16进制字符串
     *
     * @param string  字符串
     * @param charset 字符集
     * @return 16进制字符串
     */
    public static String stringToHexString(String string, Charset charset) {
        byte[] bytes = getBytes(string, charset);
        return byteArrayToHexString(bytes);
    }

    /**
     * 字节数组转为16进制字符串
     *
     * @param bytes 字节数组
     * @return 16进制字符串
     */
    public static String byteArrayToHexString(byte[] bytes) {
        @SuppressWarnings("resource")
        Formatter fmt = new Formatter(new StringBuilder(bytes.length * 2));
        for (byte b : bytes) {
            fmt.format("%02x", b);
        }
        return fmt.toString().toUpperCase();
    }

    /**
     * 是否是数字格式的字符串
     *
     * @param s 字符串
     * @return true，是
     */
    public static boolean isDigit(String s) {
        return s.matches("^\\d+$");
    }

    /**
     * 是否是26个大小写英文字符组成的字符串
     *
     * @param s 字符串
     * @return true，是
     */
    public static boolean isAlpha(String s) {
        return s.matches("^[a-zA-Z]+$");
    }

    /**
     * 是否只含有大写英文字符
     *
     * @param s 字符串
     * @return true，是
     */
    public static boolean isUpper(String s) {
        return s.matches("^[A-Z]+$");
    }

    /**
     * 是否只含有小写英文字符
     *
     * @param s 字符串
     * @return true，是
     */
    public static boolean isLower(String s) {
        return s.matches("^[a-z]+$");
    }

    /**
     * 是否只含有26个大小写英文字符和数字字符的字符串
     *
     * @param s 字符串
     * @return true，是
     */
    public static boolean isAlnum(String s) {
        return s.matches("^[a-zA-Z\\d]+$");
    }

    /**
     * 是否是整数
     *
     * @param s 字符串
     * @return true，是
     */
    public static boolean isInt(String s) {
        return s.matches("^[+-]?\\d+$");
    }

    /**
     * 是否是浮点数
     *
     * @param s 字符串
     * @return true，是
     */
    public static boolean isFloat(String s) {
        return s.matches("^[+-]?(0\\.\\d+|0|[1-9]\\d*(\\.\\d+)?)$");
    }

    static boolean isDouble(String str)
    {
        try
        {
            Double.parseDouble(str);
            return true;
        }
        catch(NumberFormatException ex){}
        return false;
    }

    /**
     * 是否是邮件地址格式
     *
     * @param s 字符串
     * @return true，是
     */
    public static boolean isEmail(String s) {
        return s.matches("^[a-zA-Z0-9._-]+@([a-zA-Z0-9_-])+(\\.[a-zA-Z0-9_-]+)+$");
    }

    /**
     * 是否是IP地址格式（用点"."分割的四组数字中，第一组数字范围1-223，其他三组数字范围0-255）
     *
     * @param s 字符串
     * @return true，是
     */
    public static boolean isIP(String s) {
        return s.matches("^(0?0?[1-9]|0?[1-9]\\d|1\\d\\d|2[01]\\d|22[0-3])(\\.([01]?\\d?\\d|2[0-4]\\d|25[0-5])){3}$");
    }

    public static String byteArrayToHexZeroString(byte[] bytes) {
        @SuppressWarnings("resource")
        Formatter fmt = new Formatter(new StringBuilder(bytes.length * 2));
        for (byte b : bytes) {
            if (b != 0) {
                fmt.format("%02x", b);
            }
        }
        return fmt.toString();
    }

    public static String capitalize(String str) {
        return changeFirstCharacterCase(str, true);
    }

    /**
     * Uncapitalize a {@code String}, changing the first letter to
     * lower case as per {@link Character#toLowerCase(char)}.
     * No other letters are changed.
     *
     * @param str the String to uncapitalize, may be {@code null}
     * @return the uncapitalized String, {@code null} if null
     */
    public static String uncapitalize(String str) {
        return changeFirstCharacterCase(str, false);
    }

    private static String changeFirstCharacterCase(String str, boolean capitalize) {
        if (str == null || str.length() == 0) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str.length());
        if (capitalize) {
            sb.append(Character.toUpperCase(str.charAt(0)));
        } else {
            sb.append(Character.toLowerCase(str.charAt(0)));
        }
        sb.append(str.substring(1));
        return sb.toString();
    }


    //首字母转小写
    public static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    //首字母转大写
    public static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    public static String getFileName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }
    /*
   * 判断是否为整数
   * @param str 传入的字符串
   * @return 是整数返回true,否则返回false
 */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static void main(String[] args) {
//        String name = "1-2-4-D.xml";
//        String dd = getFileName(name);
//        System.out.println(dd);
//        System.out.println(isEmail("luzhen@163.com"));

        byte[] rr = hexStringToByteArray("0b");
        System.out.println(Arrays.toString(rr));
        //System.out.println(isFloat("2222223"));

        //Double dValue = Double.parseDouble("232251");
        //System.out.println(dValue);
        System.out.println(isInteger("1234567.89"));
    }
}
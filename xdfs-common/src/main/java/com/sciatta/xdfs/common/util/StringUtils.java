package com.sciatta.xdfs.common.util;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rain on 2023/8/27<br>
 * All Rights Reserved(C) 2017 - 2023 SCIATTA <br> <p/>
 * 字符串工具类
 */
public class StringUtils {

    public static final String EMPTY_STRING = "";
    public static final String SPACE_STRING = " ";
    public static final String NEW_LINE = System.lineSeparator();

    public static final String PATH_SEPARATOR = "/";
    public static final String QUESTION_SEPARATOR = "?";
    public static final String ASTERISK_SEPARATOR = "*";
    public static final String AND_SEPARATOR = "&";
    public static final String EQUAL_SEPARATOR = "=";
    public static final String COMMA_SEPARATOR = ",";
    public static final String SEMICOLON_SEPARATOR = ";";
    public static final String DOLLAR_SEPARATOR = "$";
    public static final String PIPELINE_SEPARATOR = "|";
    public static final String BAR_SEPARATOR = "-";
    public static final String DOUBLE_BAR_SEPARATOR = "--";
    public static final String COLON_SEPARATOR = ":";
    public static final String DOT_SEPARATOR = ".";
    public static final String PERCENT_SEPARATOR = "%";

    private StringUtils() {

    }

    /**
     * 指定字符串是否是空字符串
     *
     * @param cs 指定字符串
     * @return true，空字符串；false，不是空字符串
     */
    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * 指定字符串是否是空白字符串
     *
     * @param cs 指定字符串
     * @return true，是空白字符串；false，不是空白字符串
     */
    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs != null && (strLen = cs.length()) != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }

        }
        return true;
    }

    /**
     * 指定字符串转换为大写
     *
     * @param cs 指定字符串
     * @return 转换为大写后的字符串
     */
    public static String toUpperCase(CharSequence cs) {
        return cs.toString().toUpperCase();
    }

    /**
     * 指定字符串转换为小写
     *
     * @param cs 指定字符串
     * @return 转换为小写后的字符串
     */
    public static String toLowerCase(CharSequence cs) {
        return cs.toString().toLowerCase();
    }

    /**
     * 从指定位置，查找指定字符串是否包含特定字符串，如果包含返回包含特定字符串的开始位置
     *
     * @param cs         指定字符串
     * @param searchChar 特定字符串
     * @param start      指定位置
     * @return 包含特定字符串的开始位置
     */
    public static int indexOf(CharSequence cs, CharSequence searchChar, int start) {
        return cs.toString().indexOf(searchChar.toString(), start);
    }

    /**
     * 指定字符串包含特定字符的个数
     *
     * @param str 指定字符串
     * @param ch  特定字符
     * @return 包含的个数
     */
    public static int countMatches(CharSequence str, char ch) {
        if (isEmpty(str)) {
            return 0;
        } else {
            int count = 0;

            for (int i = 0; i < str.length(); ++i) {
                if (ch == str.charAt(i)) {
                    ++count;
                }
            }

            return count;
        }
    }

    /**
     * 指定字符串包含特定字符串的个数
     *
     * @param str 指定字符串
     * @param sub 特定字符串
     * @return 包含的个数
     */
    public static int countMatches(CharSequence str, CharSequence sub) {
        if (!isEmpty(str) && !isEmpty(sub)) {
            int count = 0;

            for (int idx = 0; (idx = indexOf(str, sub, idx)) != -1; idx += sub.length()) {
                ++count;
            }

            return count;
        } else {
            return 0;
        }
    }

    /**
     * 将指定字符串转换为属性key
     *
     * @param str    指定字符串
     * @param prefix 前缀
     * @return 属性key；如：myName，前缀是p，则转换为p.my.name；如果不指定前缀，则转换为my.name
     */
    public static String toPropertyKey(String str, String prefix) {
        if (isEmpty(str)) {
            return str;
        }

        str = changeFirstCharacterCase(str, false); // 将第一个字符转换为小写

        StringBuilder sb = new StringBuilder();

        if (!isEmpty(prefix)) {
            sb.append(prefix).append(DOT_SEPARATOR);
        }

        str.chars().forEach(v -> {
            if (Character.isUpperCase(v)) {
                sb.append(DOT_SEPARATOR).append((char) Character.toLowerCase(v));
            } else {
                sb.append((char) v);
            }
        });

        return sb.toString();
    }

    /**
     * 将指定字符串首字母转换为大写或小写字符
     *
     * @param str        指定字符串
     * @param capitalize true，首字母转换为大写；false，首字母转换为小写
     * @return 转换后的字符串
     */
    public static String changeFirstCharacterCase(String str, boolean capitalize) {
        if (isEmpty(str)) {
            return str;
        }

        char baseChar = str.charAt(0);
        char updatedChar;
        if (capitalize) {
            updatedChar = Character.toUpperCase(baseChar);
        } else {
            updatedChar = Character.toLowerCase(baseChar);
        }
        if (baseChar == updatedChar) {
            return str;
        }

        char[] chars = str.toCharArray();
        chars[0] = updatedChar;
        return new String(chars, 0, chars.length);
    }

    /**
     * 将指定字符串重复指定次数
     *
     * @param str    指定字符串
     * @param repeat 指定重复次数
     * @return 指定字符串重复指定次数后的字符串
     */
    public static String repeat(String str, int repeat) {
        if (str == null) {
            return null;
        } else if (repeat <= 0) {
            return "";
        } else {
            int inputLength = str.length();
            if (repeat != 1 && inputLength != 0) {
                if (inputLength == 1 && repeat <= 8192) {
                    return repeat(str.charAt(0), repeat);
                } else {
                    int outputLength = inputLength * repeat;
                    switch (inputLength) {
                        case 1:
                            return repeat(str.charAt(0), repeat);
                        case 2:
                            char ch0 = str.charAt(0);
                            char ch1 = str.charAt(1);
                            char[] output2 = new char[outputLength];

                            for (int i = repeat * 2 - 2; i >= 0; --i) {
                                output2[i] = ch0;
                                output2[i + 1] = ch1;
                                --i;
                            }

                            return new String(output2);
                        default:
                            StringBuilder buf = new StringBuilder(outputLength);

                            for (int i = 0; i < repeat; ++i) {
                                buf.append(str);
                            }

                            return buf.toString();
                    }
                }
            } else {
                return str;
            }
        }
    }

    /**
     * 将指定字符串以指定分隔符结尾并重复指定次数
     *
     * @param str       指定字符串
     * @param separator 指定分隔符
     * @param repeat    指定重复次数
     * @return 指定字符串重复指定次数后的字符串
     */
    public static String repeat(String str, String separator, int repeat) {
        if (str != null && separator != null) {
            String result = repeat(str + separator, repeat);
            return removeEnd(result, separator);
        } else {
            return repeat(str, repeat);
        }
    }

    /**
     * 将指定字符重复指定次数
     *
     * @param ch     指定字符
     * @param repeat 指定重复次数
     * @return 指定字符重复指定次数后的字符串
     */
    public static String repeat(char ch, int repeat) {
        if (repeat <= 0) {
            return "";
        } else {
            char[] buf = new char[repeat];

            for (int i = repeat - 1; i >= 0; --i) {
                buf[i] = ch;
            }

            return new String(buf);
        }
    }

    /**
     * 将指定字符串在末尾移除需要移除的字符串子串
     *
     * @param str    指定字符串
     * @param remove 需要移除的字符串子串
     * @return 在末尾移除需要移除的字符串子串后的字符串
     */
    public static String removeEnd(String str, String remove) {
        if (!isEmpty(str) && !isEmpty(remove)) {
            return str.endsWith(remove) ? str.substring(0, str.length() - remove.length()) : str;
        } else {
            return str;
        }
    }

    /**
     * 将指定字符串在末尾移除需要移除的字符串子串，忽略大小写
     *
     * @param str    指定字符串
     * @param remove 需要移除的字符串子串
     * @return 在末尾移除需要移除的字符串子串后的字符串
     */
    public static String removeEndIgnoreCase(String str, String remove) {
        if (!isEmpty(str) && !isEmpty(remove)) {
            return endsWithIgnoreCase(str, remove) ? str.substring(0, str.length() - remove.length()) : str;
        } else {
            return str;
        }
    }

    /**
     * 指定字符串是否以后缀结尾，忽略大小写
     *
     * @param str    指定字符串
     * @param suffix 后缀
     * @return true，以后缀结尾；false，不是以后缀结尾
     */
    public static boolean endsWithIgnoreCase(CharSequence str, CharSequence suffix) {
        return endsWith(str, suffix, true);
    }

    /**
     * 指定字符串是否以后缀结尾
     *
     * @param str        指定字符串
     * @param suffix     后缀
     * @param ignoreCase 是否忽略大小写。true，忽略大小写；false，不忽略大小写
     * @return true，以后缀结尾；false，不是以后缀结尾
     */
    private static boolean endsWith(CharSequence str, CharSequence suffix, boolean ignoreCase) {
        if (str != null && suffix != null) {
            if (suffix.length() > str.length()) {
                return false;
            } else {
                int strOffset = str.length() - suffix.length();
                return regionMatches(str, ignoreCase, strOffset, suffix, 0, suffix.length());
            }
        } else {
            return str == null && suffix == null;
        }
    }

    /**
     * 指定字符串是否匹配指定子串
     *
     * @param cs         指定字符串
     * @param ignoreCase 是否忽略大小写。true，忽略大小写；false，不忽略大小写
     * @param thisStart  开始匹配位置
     * @param substring  指定子串
     * @param start      匹配指定子串的开始位置
     * @param length     匹配指定子串的长度
     * @return true，匹配；false，不匹配
     */
    public static boolean regionMatches(CharSequence cs, boolean ignoreCase, int thisStart, CharSequence substring, int start, int length) {
        if (cs instanceof String && substring instanceof String) {
            return ((String) cs).regionMatches(ignoreCase, thisStart, (String) substring, start, length);
        } else {
            int index1 = thisStart;
            int index2 = start;
            int tmpLen = length;
            int srcLen = cs.length() - thisStart;
            int otherLen = substring.length() - start;
            if (thisStart >= 0 && start >= 0 && length >= 0) {
                if (srcLen >= length && otherLen >= length) {
                    while (tmpLen-- > 0) {
                        char c1 = cs.charAt(index1++);
                        char c2 = substring.charAt(index2++);
                        if (c1 != c2) {
                            if (!ignoreCase) {
                                return false;
                            }

                            if (Character.toUpperCase(c1) != Character.toUpperCase(c2) && Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                                return false;
                            }
                        }
                    }

                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    /**
     * 将指定字符串按空白字符分割
     *
     * @param str 指定字符串
     * @return 按空白字符分隔后的字符串数组
     */
    public static String[] split(String str) {
        return split(str, null, -1);
    }

    /**
     * 将指定字符串按指定字符分割
     *
     * @param str           指定字符串
     * @param separatorChar 指定分隔字符
     * @return 按指定字符分隔后的字符串数组
     */
    public static String[] split(String str, char separatorChar) {
        return splitWorker(str, separatorChar, false);
    }

    /**
     * 将指定字符串按指定字符串分割
     *
     * @param str            指定字符串
     * @param separatorChars 指定分隔字符串
     * @return 按指定字符串分隔后的字符串数组
     */
    public static String[] split(String str, String separatorChars) {
        return splitWorker(str, separatorChars, -1, false);
    }

    /**
     * 将指定字符串按指定分隔字符串分隔
     *
     * @param str            指定字符串
     * @param separatorChars 指定分隔字符串
     * @param max            最大分隔字符串数组大小；如果超过实际可以分割数组的长度，则按实际返回
     * @return 按指定分隔字符串分隔后的字符串数组
     */
    public static String[] split(String str, String separatorChars, int max) {
        return splitWorker(str, separatorChars, max, false);
    }

    /**
     * 将指定字符串按指定分隔字符串分隔，分割结果会忽略中间的空字符，保留尾部的空字符
     *
     * @param str       指定字符串
     * @param separator 指定分隔字符串
     * @return 按指定分隔字符串分隔后的字符串数组
     */
    public static String[] splitByWholeSeparator(String str, String separator) {
        return splitByWholeSeparatorWorker(str, separator, -1, false);
    }

    /**
     * 将指定字符串按指定分隔字符串分隔，分割结果会忽略中间的空字符，保留尾部的空字符
     *
     * @param str       指定字符串
     * @param separator 指定分隔字符串
     * @param max       最大分隔字符串数组大小；如果超过实际可以分割数组的长度，则按实际返回
     * @return 按指定分隔字符串分隔后的字符串数组
     */
    public static String[] splitByWholeSeparator(String str, String separator, int max) {
        return splitByWholeSeparatorWorker(str, separator, max, false);
    }

    /**
     * 将指定字符串按指定分隔字符串分隔，分割结果会保留中间的空字符，也会保留尾部的空字符
     *
     * @param str       指定字符串
     * @param separator 指定分隔字符串
     * @return 按指定分隔字符串分隔后的字符串数组
     */
    public static String[] splitByWholeSeparatorPreserveAllTokens(String str, String separator) {
        return splitByWholeSeparatorWorker(str, separator, -1, true);
    }

    /**
     * 将指定字符串按指定分隔字符串分隔，分割结果会保留中间的空字符，也会保留尾部的空字符
     *
     * @param str       指定字符串
     * @param separator 指定分隔字符串
     * @param max       最大分隔字符串数组大小；如果超过实际可以分割数组的长度，则按实际返回
     * @return 按指定分隔字符串分隔后的字符串数组
     */
    public static String[] splitByWholeSeparatorPreserveAllTokens(String str, String separator, int max) {
        return splitByWholeSeparatorWorker(str, separator, max, true);
    }

    /**
     * 将指定字符串按空白字符分割，分割结果会保留中间的空字符，也会保留尾部的空字符
     *
     * @param str 指定字符串
     * @return 按空白字符分隔后的字符串数组
     */
    public static String[] splitPreserveAllTokens(String str) {
        return splitWorker(str, null, -1, true);
    }

    /**
     * 将指定字符串按指定字符分割，分割结果会保留中间的空字符，也会保留尾部的空字符
     *
     * @param str           指定字符串
     * @param separatorChar 指定分隔字符
     * @return 按指定字符分隔后的字符串数组
     */
    public static String[] splitPreserveAllTokens(String str, char separatorChar) {
        return splitWorker(str, separatorChar, true);
    }

    /**
     * 将指定字符串按指定字符串分割，分割结果会保留中间的空字符，也会保留尾部的空字符
     *
     * @param str            指定字符串
     * @param separatorChars 指定分隔字符串
     * @return 按指定字符串分隔后的字符串数组
     */
    public static String[] splitPreserveAllTokens(String str, String separatorChars) {
        return splitWorker(str, separatorChars, -1, true);
    }

    /**
     * 将指定字符串按指定字符串分割，分割结果会保留中间的空字符，也会保留尾部的空字符
     *
     * @param str            指定字符串
     * @param separatorChars 指定分隔字符串
     * @param max            最大分隔字符串数组大小；如果超过实际可以分割数组的长度，则按实际返回
     * @return 按指定字符串分隔后的字符串数组
     */
    public static String[] splitPreserveAllTokens(String str, String separatorChars, int max) {
        return splitWorker(str, separatorChars, max, true);
    }

    /**
     * 将指定字符串按字符类型分隔，即字母归字母，数字归数字，特殊字符归特殊字符
     *
     * @param str 指定字符串
     * @return 按字符类型分隔后的字符串数组
     */
    public static String[] splitByCharacterType(String str) {
        return splitByCharacterType(str, false);
    }

    /**
     * 将指定字符串按字符类型分隔，字母带驼峰约束，即字母归字母，数字归数字，特殊字符归特殊字符
     *
     * @param str 指定字符串
     * @return 按字符类型分隔后的字符串数组
     */
    public static String[] splitByCharacterTypeCamelCase(String str) {
        return splitByCharacterType(str, true);
    }

    /**
     * 打印异常的堆栈信息
     *
     * @param e 异常
     * @return 堆栈信息字符串
     */
    public static String toString(Throwable e) {
        StringWriter w = new StringWriter();
        PrintWriter p = new PrintWriter(w);
        p.print(e.getClass().getName() + ": ");
        if (e.getMessage() != null) {
            p.print(e.getMessage() + "\n");
        }
        p.println();
        try {
            e.printStackTrace(p);
            return w.toString();
        } finally {
            p.close();
        }
    }

    /**
     * 将指定字符串加密为md5字符串
     *
     * @param input 指定字符串
     * @return md5字符串
     */
    public static String md5(CharSequence input) {
        HashFunction md5 = Hashing.md5();
        HashCode md5HashCode = md5.hashString(input, Charsets.UTF_8);
        return md5HashCode.toString();
    }

    // private

    private static String[] splitByCharacterType(String str, boolean camelCase) {
        if (str == null) {
            return null;
        }
        if (str.isEmpty()) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        final char[] c = str.toCharArray();
        final List<String> list = new ArrayList<>();
        int tokenStart = 0;
        int currentType = Character.getType(c[tokenStart]);
        for (int pos = tokenStart + 1; pos < c.length; pos++) {
            final int type = Character.getType(c[pos]);
            if (type == currentType) {
                continue;
            }
            if (camelCase && type == Character.LOWERCASE_LETTER && currentType == Character.UPPERCASE_LETTER) {
                final int newTokenStart = pos - 1;
                if (newTokenStart != tokenStart) {
                    list.add(new String(c, tokenStart, newTokenStart - tokenStart));
                    tokenStart = newTokenStart;
                }
            } else {
                list.add(new String(c, tokenStart, pos - tokenStart));
                tokenStart = pos;
            }
            currentType = type;
        }
        list.add(new String(c, tokenStart, c.length - tokenStart));
        return list.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    private static String[] splitByWholeSeparatorWorker(String str, String separator, int max, boolean preserveAllTokens) {
        if (str == null) {
            return null;
        }

        final int len = str.length();

        if (len == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }

        if (separator == null || EMPTY_STRING.equals(separator)) {
            // Split on whitespace.
            return splitWorker(str, null, max, preserveAllTokens);
        }

        final int separatorLength = separator.length();

        final ArrayList<String> substrings = new ArrayList<>();
        int numberOfSubstrings = 0;
        int beg = 0;
        int end = 0;
        while (end < len) {
            end = str.indexOf(separator, beg);

            if (end > -1) {
                if (end > beg) {
                    numberOfSubstrings += 1;

                    if (numberOfSubstrings == max) {
                        end = len;
                        substrings.add(str.substring(beg));
                    } else {
                        // The following is OK, because String.substring( beg, end ) excludes
                        // the character at the position 'end'.
                        substrings.add(str.substring(beg, end));

                        // Set the starting point for the next search.
                        // The following is equivalent to beg = end + (separatorLength - 1) + 1,
                        // which is the right calculation:
                        beg = end + separatorLength;
                    }
                } else {
                    // We found a consecutive occurrence of the separator, so skip it.
                    if (preserveAllTokens) {
                        numberOfSubstrings += 1;
                        if (numberOfSubstrings == max) {
                            end = len;
                            substrings.add(str.substring(beg));
                        } else {
                            substrings.add(EMPTY_STRING);
                        }
                    }
                    beg = end + separatorLength;
                }
            } else {
                // String.substring( beg ) goes from 'beg' to the end of the String.
                substrings.add(str.substring(beg));
                end = len;
            }
        }

        return substrings.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    private static String[] splitWorker(String str, char separatorChar, boolean preserveAllTokens) {
        if (str == null) {
            return null;
        }
        final int len = str.length();
        if (len == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        final List<String> list = new ArrayList<>();
        int i = 0;
        int start = 0;
        boolean match = false;
        boolean lastMatch = false;
        while (i < len) {
            if (str.charAt(i) == separatorChar) {
                if (match || preserveAllTokens) {
                    list.add(str.substring(start, i));
                    match = false;
                    lastMatch = true;
                }
                start = ++i;
                continue;
            }
            lastMatch = false;
            match = true;
            i++;
        }
        if (match || preserveAllTokens && lastMatch) {
            list.add(str.substring(start, i));
        }
        return list.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    private static String[] splitWorker(String str, String separatorChars, int max, boolean preserveAllTokens) {
        if (str == null) {
            return null;
        }
        final int len = str.length();
        if (len == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        final List<String> list = new ArrayList<>();
        int sizePlus1 = 1;
        int i = 0;
        int start = 0;
        boolean match = false;
        boolean lastMatch = false;
        if (separatorChars == null) {
            // Null separator means use whitespace
            while (i < len) {
                if (Character.isWhitespace(str.charAt(i))) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else if (separatorChars.length() == 1) {
            // Optimise 1 character case
            final char sep = separatorChars.charAt(0);
            while (i < len) {
                if (str.charAt(i) == sep) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else {
            // standard case
            while (i < len) {
                if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        }
        if (match || preserveAllTokens && lastMatch) {
            list.add(str.substring(start, i));
        }
        return list.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

}

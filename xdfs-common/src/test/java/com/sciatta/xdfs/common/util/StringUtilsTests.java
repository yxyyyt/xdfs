package com.sciatta.xdfs.common.util;

import com.google.common.base.Charsets;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by Rain on 2023/8/27<br>
 * All Rights Reserved(C) 2017 - 2023 SCIATTA <br> <p/>
 * StringUtilsTests
 */
public class StringUtilsTests {

    @Test
    public void testCountMatches() {
        int i = StringUtils.countMatches("%sabc", "%s");
        assertEquals(1, i);

        i = StringUtils.countMatches("%sdog!%scat?%d", "%s");
        assertEquals(2, i);
    }

    @Test
    public void testToPropertyKey() {
        String s = StringUtils.toPropertyKey("myName", "p");
        assertEquals("p.my.name", s);

        s = StringUtils.toPropertyKey("myName", "");
        assertEquals("my.name", s);

        s = StringUtils.toPropertyKey("MyName", "");
        assertEquals("my.name", s);

        s = StringUtils.toPropertyKey("MyName", "p");
        assertEquals("p.my.name", s);
    }

    @Test
    public void testSplit() {
        String str;
        String separator;
        String[] ans;

        // public static String[] split(String str, String separatorChars, int max)
        str = "1abc1abc1abc1abc1";   //4个abc
        separator = "abc";
        ans = StringUtils.split(str, separator, 3);
        assertEquals(3, ans.length);
        assertEquals("1", ans[0]);
        assertEquals("1", ans[1]);
        assertEquals("1abc1abc1", ans[2]);

        ans = StringUtils.split(str, separator, 4);
        assertEquals(4, ans.length);
        assertEquals("1", ans[0]);
        assertEquals("1", ans[1]);
        assertEquals("1", ans[2]);
        assertEquals("1abc1", ans[3]);

        ans = StringUtils.split(str, separator, 5);
        assertEquals(5, ans.length);
        assertEquals("1", ans[0]);
        assertEquals("1", ans[1]);
        assertEquals("1", ans[2]);
        assertEquals("1", ans[3]);
        assertEquals("1", ans[4]);

        ans = StringUtils.split(str, separator, 6);
        assertEquals(5, ans.length);
        assertEquals("1", ans[0]);
        assertEquals("1", ans[1]);
        assertEquals("1", ans[2]);
        assertEquals("1", ans[3]);
        assertEquals("1", ans[4]);

        // public static String[] split(String str)
        str = "1abc1abc1abc1abc1";   //4个abc
        ans = StringUtils.split(str);
        assertEquals(1, ans.length);
        assertEquals("1abc1abc1abc1abc1", ans[0]);

        str = "1 1 1 1";
        ans = StringUtils.split(str);
        assertEquals(4, ans.length);
        assertEquals("1", ans[0]);
        assertEquals("1", ans[1]);
        assertEquals("1", ans[2]);
        assertEquals("1", ans[3]);

        // public static String[] split(String str, char separatorChar)
        str = "1a1a1a";
        ans = StringUtils.split(str, 'a');
        assertEquals(3, ans.length);
        assertEquals("1", ans[0]);
        assertEquals("1", ans[1]);
        assertEquals("1", ans[2]);

        str = "1a1a1a1";
        ans = StringUtils.split(str, 'a');
        assertEquals(4, ans.length);
        assertEquals("1", ans[0]);
        assertEquals("1", ans[1]);
        assertEquals("1", ans[2]);
        assertEquals("1", ans[3]);

        // public static String[] split(String str, String separatorChars)
        str = "1abc1abc1abc1abc";
        ans = StringUtils.split(str, "abc");
        assertEquals(4, ans.length);
        assertEquals("1", ans[0]);
        assertEquals("1", ans[1]);
        assertEquals("1", ans[2]);
        assertEquals("1", ans[3]);

        str = "1abc1abc1abc1abc1";
        ans = StringUtils.split(str, "abc");
        assertEquals(5, ans.length);
        assertEquals("1", ans[0]);
        assertEquals("1", ans[1]);
        assertEquals("1", ans[2]);
        assertEquals("1", ans[3]);
        assertEquals("1", ans[4]);

        // public static String[] splitByWholeSeparator(String str, String separator)
        str = "1abc1abc1abc1abc";
        ans = StringUtils.splitByWholeSeparator(str, "abc");
        assertEquals(5, ans.length);
        assertEquals("1", ans[0]);
        assertEquals("1", ans[1]);
        assertEquals("1", ans[2]);
        assertEquals("1", ans[3]);
        assertEquals("", ans[4]);   // 保留尾部空字符

        str = "1abc1abc1abc1abc1";
        ans = StringUtils.splitByWholeSeparator(str, "abc");
        assertEquals(5, ans.length);
        assertEquals("1", ans[0]);
        assertEquals("1", ans[1]);
        assertEquals("1", ans[2]);
        assertEquals("1", ans[3]);
        assertEquals("1", ans[4]);

        str = "1abc1abcabc1abc1abc";    // 分割结果会忽略中间的空字符，保留尾部的空字符
        ans = StringUtils.splitByWholeSeparator(str, "abc");
        assertEquals(5, ans.length);
        assertEquals("1", ans[0]);
        assertEquals("1", ans[1]);
        assertEquals("1", ans[2]);
        assertEquals("1", ans[3]);
        assertEquals("", ans[4]);

        // public static String[] splitByWholeSeparatorPreserveAllTokens(String str, String separator)
        str = "1abc1abcabc1abc1abc";    // 分割结果会保留中间的空字符，也会保留尾部的空字符
        ans = StringUtils.splitByWholeSeparatorPreserveAllTokens(str, "abc");
        assertEquals(6, ans.length);
        assertEquals("1", ans[0]);
        assertEquals("1", ans[1]);
        assertEquals("", ans[2]);
        assertEquals("1", ans[3]);
        assertEquals("1", ans[4]);
        assertEquals("", ans[5]);

        // public static String[] splitPreserveAllTokens(String str)
        str = "1 1 1  1 ";
        ans = StringUtils.splitPreserveAllTokens(str); // 按空白字符分割，分割结果会保留中间的空字符，也会保留尾部的空字符
        assertEquals(6, ans.length);
        assertEquals("1", ans[0]);
        assertEquals("1", ans[1]);
        assertEquals("1", ans[2]);
        assertEquals("", ans[3]);
        assertEquals("1", ans[4]);
        assertEquals("", ans[5]);

        // public static String[] splitByCharacterType(String str)
        str = "ABCabc123  #$%你好";
        ans = StringUtils.splitByCharacterType(str);
        assertEquals(8, ans.length);
        assertEquals("ABC", ans[0]);
        assertEquals("abc", ans[1]);
        assertEquals("123", ans[2]);
        assertEquals("  ", ans[3]);
        assertEquals("#", ans[4]);
        assertEquals("$", ans[5]);
        assertEquals("%", ans[6]);
        assertEquals("你好", ans[7]);

        // public static String[] splitByCharacterTypeCamelCase(String str)
        str = "ABCabc123  #$%你好";
        ans = StringUtils.splitByCharacterTypeCamelCase(str);
        assertEquals(8, ans.length);
        assertEquals("AB", ans[0]);
        assertEquals("Cabc", ans[1]);
        assertEquals("123", ans[2]);
        assertEquals("  ", ans[3]);
        assertEquals("#", ans[4]);
        assertEquals("$", ans[5]);
        assertEquals("%", ans[6]);
        assertEquals("你好", ans[7]);
    }

    @Test
    public void testExceptionWithStackTrace() {
        try {
            int a = 1 / 0;
        } catch (Throwable e) {
            String s = StringUtils.toString(e);
            System.err.println(s);
        }
    }

    @Test
    public void testMd5() {
        String test = StringUtils.md5("test");
        assertEquals("098f6bcd4621d373cade4e832627b4f6", test);
    }

    @Test
    public void testHashing() {
        String msg = StringUtils.repeat('-', 200);
        int testCount = 100000;
        long sha1Time = 0L;
        long md5Time = 0L;
        long murmur3Time = 0L;
        double sha1Average = 0D;
        double md5Average = 0D;
        double murmur3Average = 0D;

        long one = System.currentTimeMillis();
        HashFunction sha1 = Hashing.sha1();
        for (int i = 0; i < testCount; i++) {
            Stopwatch w = Stopwatch.createStarted();
            HashCode hashCode = sha1.hashString(msg + i, Charsets.UTF_8);
            String str = hashCode.toString();
            System.out.printf("sh1's hashCode:%s,length:%s,it consumes:%s%n", str, str.length(),
                    w);
        }
        long two = System.currentTimeMillis();
        sha1Time = two - one;

        HashFunction md5 = Hashing.md5();
        for (int i = 0; i < testCount; i++) {
            Stopwatch w = Stopwatch.createStarted();
            HashCode md5HashCode = md5.hashString(msg + i, Charsets.UTF_8);
            String md5HashCodeStr = md5HashCode.toString();
            System.out.printf("md5's hashCode:%s,length:%s,it consumes:%s%n", md5HashCodeStr,
                    md5HashCodeStr.length(), w);
        }
        long three = System.currentTimeMillis();
        md5Time = three - two;

        HashFunction murmur3 = Hashing.murmur3_32();
        Set<String> set = Sets.newHashSet();
        for (int i = 0; i < testCount; i++) {
            Stopwatch w = Stopwatch.createStarted();
            HashCode murmur3HashCode = murmur3.hashString(msg + i, Charsets.UTF_8);
            String murmur3HashCodeStr = murmur3HashCode.toString();
            System.out.printf("murmur3's hashCode:%s,length:%s,it consumes:%s%n",
                    murmur3HashCodeStr, murmur3HashCodeStr.length(), w);
            set.add(murmur3HashCodeStr);
        }
        long four = System.currentTimeMillis();
        murmur3Time = four - three;

        sha1Average = sha1Time / testCount;
        md5Average = md5Time / testCount;
        murmur3Average = murmur3Time / testCount;

        System.out.println("set size : " + set.size());
        System.out
                .printf("sha1 sum time:%s seconds,average time:%s ms\nmd5 sum time:%s seconds,average time:%s ms\nmurmur3 sum time:%s seconds,average time:%s ms%n",
                        DateTimeUtils.msToS(sha1Time), sha1Average,
                        DateTimeUtils.msToS(md5Time), md5Average,
                        DateTimeUtils.msToS(murmur3Time), murmur3Average);

    }

    @Test
    public void testToUpperCase() {
        String test = "abc";
        String ans = StringUtils.toUpperCase(test);
        assertEquals("ABC", ans);
    }

    @Test
    public void testToLowerCase() {
        String test = "ABC";
        String ans = StringUtils.toLowerCase(test);
        assertEquals("abc", ans);
    }

}

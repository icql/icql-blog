---
title: java_se_string_long_integer
date: 2020-02-29 00:00:00
---

## Integer、Long

valueOf 方法使用了缓存 -128 - 127，parseInt 和 parseLong 没有使用，所以推荐使用 valueOf 方法

``` java
public final class Integer extends Number implements Comparable<Integer> {
    //使用了缓存
    public static Integer valueOf(int i) {
        if (i >= IntegerCache.low && i <= IntegerCache.high)
            return IntegerCache.cache[i + (-IntegerCache.low)];
        return new Integer(i);
    }

    //没有使用缓存
    public static int parseInt(String s, int radix)
                throws NumberFormatException{}

    private static class IntegerCache{
        static final int low = -128;
        static final int high;
        static final Integer cache[];

        static {
            //缓存逻辑
        }
    }
}

public final class Long extends Number implements Comparable<Long> {
    //使用了缓存
    public static Long valueOf(long l) {
        final int offset = 128;
        if (l >= -128 && l <= 127) { // will cache
            return LongCache.cache[(int)l + offset];
        }
        return new Long(l);
    }

    //没有使用缓存
    public static long parseLong(String s, int radix)
              throws NumberFormatException{}

    private static class LongCache {
        private LongCache(){}

        static final Long cache[] = new Long[-(-128) + 127 + 1];

        static {
            for(int i = 0; i < cache.length; i++)
                cache[i] = new Long(i - 128);
        }
    }
}
```

<br/>
<hr/>

## String

String 是不可变的，因为String类是final的，保存数据的char数组也是final，方法返回的都是新的字符串

``` java
public final class String
    implements java.io.Serializable, Comparable<String>, CharSequence {
    //保存的数据
    private final char value[];
}
```
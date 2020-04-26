package com.hs.config;

import java.lang.annotation.*;

/**
 * 清除字符串前后的空格
 */
@Target(value = { ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Trimmed {

    public static enum TrimmerType {
        SIMPLE, ALL_WHITESPACES, EXCEPT_LINE_BREAK;
    }

    TrimmerType value() default TrimmerType.ALL_WHITESPACES;

}
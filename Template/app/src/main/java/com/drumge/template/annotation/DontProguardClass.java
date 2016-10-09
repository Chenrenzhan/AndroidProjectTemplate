package com.drumge.template.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 加入此注释,不需要混淆类
 * 使用方法：
 * 1、在不需要混淆的类，增加注解@DontProguardClass
 * 2、在混淆配置文件中
 * -keep public @interface com.yy.mobile.util.DontProguardClass
 * -keep public @interface com.yy.mobile.util.DontProguardMethod
 * -keep @com.yy.mobile.util.DontProguardClass class * { *; }
 * -keepclassmembers class * {
 * @com.yy.mobile.util.DontProguardMethod <methods>;
 * }
 * 或者直接使用注解保护不被混淆的方法：
 * -keepattributes *Annotation*
 * -keep class * extends java.lang.annotation.Annotation {*;}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DontProguardClass {
}

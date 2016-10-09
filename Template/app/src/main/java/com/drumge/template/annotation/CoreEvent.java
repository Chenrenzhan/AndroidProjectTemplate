/**
 * 用于使用annotation实现监听某个client的某个回调
 */
package com.drumge.template.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Core事件,注解在方法上,把整个类注册到CoreManager.addClient(),
 * 有notifyclient时,该方法能收到通知
 * 绑定带有注解的方法到{@coreClientClass}类中
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface CoreEvent {
	
	Class<?> coreClientClass();
}

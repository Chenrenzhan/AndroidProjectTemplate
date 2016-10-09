/**
 * 所有的core client接口必须继承于此接口
 */
package com.drumge.template.core;

/**
 * 网络请求后调用客户端UI更新的接口
 * 跟IBaseCore 一一对应，也就是一个IBaseCore一般对应一个IcoreClient，一般还有有一个相应的请求协议类
 * 实现此接口的具体接口可以不用给出更新UI的方法，使用注解@CoreEvent来绑定具体的实现方法到继承此接口的接口，但是为了代码理解，一般也会在此接口中加入方法，并注释掉
 */
public interface ICoreClient {

}

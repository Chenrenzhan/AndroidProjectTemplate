package com.drumge.template.cache;


/**
 * 缓存接口
 *
 * @author <a href="mailto:kuanglingxuan@yy.com">匡凌轩</a> V1.0
 */
public interface Cache {
	
	/**
	 * 异步get，无阻塞
	 * @param key 查询键
	 * @param returnCallback 返回值回调处理
	 */
    public void get(String key, ReturnCallback returnCallback);
    
    /**
     * 异步get，无阻塞
     * @param key 查询键
     * @param returncallback 返回值回调处理
     * @param errorCallback 错误回调处理
     */
    public void get(String key, ReturnCallback returncallback,
                    ErrorCallback errorCallback);
    
//    /**
//     * 同步get，会阻塞，请在非主线程中使用
//     * @param key 查询键
//     * @return 返回值
//     */
//    public String get(String key);
    
    /**
     * 异步put，暂时不需要管结果
     * @param key 存储键
     * @param value 存储值
     */
    public void put(String key, String value);
    
    /**
     * 异步put，暂时不需要管结果
     * @param key 存储键
     * @param value 存储值
     * @param expire 超时时间
     */
    public void put(String key, String value, long expire);
    
    /**
     * 删除
     * @param key
     */
    public void remove(String key);

    /**
     * 全部清空
     */
    public void clear();

}

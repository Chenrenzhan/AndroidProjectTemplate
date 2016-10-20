package com.drumge.template.cache;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存客户端工厂
 *
 * @author <a href="mailto:kuanglingxuan@yy.com">匡凌轩</a> V1.0
 */
public class CacheClientFactory {

	/**
	 * 缓冲类型
	 *
	 */
	public enum CacheType{
		PRIVATE,//登陆者私有数据
		PUBLIC//公共数据 
	}
	
	private static final String publicUri = "dataCache"+ File.separator+"public"+ File.separator;
	
	private static final String privateUri = "dataCache"+ File.separator+"private"+ File.separator;
	
	private static Map<CacheType, CacheClient> map = new ConcurrentHashMap<CacheType, CacheClient>();
	
	/**
	 * 获取公共数据CacheClient，根据业务需求只会同时存在1个Public CacheClient
	 * @return CacheClient
	 */
	public static CacheClient getPublic(){
		CacheClient client = map.get(CacheType.PUBLIC);
		if(client == null){
			String uri = publicUri;
			client = new CacheClient(uri);
			map.put(CacheType.PUBLIC, client);
		}
		return client;
	}
	
	/**
	 * 获取私有数据CacheClient，根据业务需求只会同时存在1个Private CacheClient
	 * 如果为null，请使用前调用register注册一次
	 * @return CacheClient
	 */
	public static CacheClient getPrivate(){
		CacheClient client = map.get(CacheType.PRIVATE);
		return client;
	}
	
	/**
	 * 删除私有CacheClient。例如：注销切换用户时请清除当前用户Private CacheClient
	 */
	public static void removePrivate(){
		map.remove(CacheType.PRIVATE);
	}
	
	/**
	 * 注册私有CacheClient
	 * @param uri
	 */
	public static void registerPrivate(String uri){
		uri = privateUri+uri;
		CacheClient	client = new CacheClient(uri);
		map.put(CacheType.PRIVATE, client);
	}
}

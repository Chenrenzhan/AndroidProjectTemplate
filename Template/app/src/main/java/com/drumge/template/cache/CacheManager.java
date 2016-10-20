package com.drumge.template.cache;

import android.content.Context;
import android.text.TextUtils;


import com.drumge.template.BasicConfig;
import com.drumge.template.common.MD5Utils;
import com.drumge.template.log.MLog;
import com.nostra13.universalimageloader.cache.disc.DiskCache;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CacheManager {
    private static final String TAG = "CacheManager";
	
	private static CacheManager mInstance = null;
	
	//LruCache
	private static final int MEM_MAX_SIZE = 1 * 1024 * 1024;// MEM 1MB
	private StringLruCache<String, String> mMemoryCache = null;
	
	//DiskLruCache
	private static final int DISK_MAX_SIZE = 5 * 1024 * 1024;// SD 5MB

	private static final String cachePath = "cacheDir";
	private StringDiskCache mDiskCacke = null;
	
	private Context mContext = null;
	public CacheManager(String fileName){
		mContext = BasicConfig.getInstance().getAppContext();
		
		//内存缓存
		mMemoryCache = new StringLruCache<String, String>(MEM_MAX_SIZE) {
			@Override
			protected int sizeOf(String key, String value) {
				int count = value.getBytes().length;
				return count;
			}
			@Override
			protected void entryRemoved(boolean evicted, String key,
										String oldValue, String newValue) {
				super.entryRemoved(evicted, key, oldValue, newValue);
			}
		};
		
        
		//sdcard或者内置存储
		File cacheDir = DiskCache.getCacheDir(mContext, cachePath + File.separator + fileName);
		mDiskCacke = StringDiskCache.openCache(cacheDir, DISK_MAX_SIZE);
	}
	
//	public static CacheManager instance() 
//	{
//		if(mInstance==null) {
//			mInstance = new CacheManager();
//		}
//		return mInstance;
//	}


	public boolean putCache(String key, String json, long expire) {
		String md5Key = MD5Utils.getMD5String(key);
		if(mDiskCacke != null){
			MLog.info(TAG, "put json to SD key = " + key);
			mDiskCacke.putText(md5Key, json);
		}
		if(mMemoryCache != null){
			MLog.info(TAG, "put json to Memory key = " + key);
			mMemoryCache.put(md5Key, json);
		}
		return false;
	}

	public String getCache(String key) throws NoSuchKeyException, IOException {
		//线程的优先级,设置低点对主线程
		//Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
		
		if (TextUtils.isEmpty(key)) {
			return null;
		}
		String md5Key = MD5Utils.getMD5String(key);
		
		String json = "";
		if(mMemoryCache != null){
			json = mMemoryCache.get(md5Key);
			if (json != null) {
				CacheClient.CachePacket packet = JsonParser.parseJsonObject(json, CacheClient.CachePacket.class);
				long expiredTime = System.currentTimeMillis() - packet.getHeader().getCreateTime();
				
				if(expiredTime > packet.getHeader().getExpired()){
					remove(key);
					return null;
				}
				MLog.info(TAG, "get Json from mem: key = " + key);
				return json;
			}
		}
		//内存中没有
		if(mDiskCacke != null){
			try{
				json = mDiskCacke.get(md5Key);
				if (json != null) {
					mMemoryCache.put(md5Key, json);
					MLog.info(TAG, "get Json from sd: key = " + key);
					return json;
				}
			}catch (final FileNotFoundException e) {
				throw new NoSuchKeyException(key, "no such key");
			} catch (final IOException e) {
				throw e;
			}
		}
		return null;
	}
	/**
	 * 清空key值，释放sd和内存的数据
	 * @param key
	 */
	public void remove(String key, String path) {
		if (TextUtils.isEmpty(key)) {
			return;
		}
		String md5Key = MD5Utils.getMD5String(key);
		
		if(mMemoryCache != null){
			mMemoryCache.remove(md5Key);
		}
		if(mDiskCacke != null){
			//内存中没有
			mDiskCacke.clearCache(md5Key);
		}
	}
	
	/**
	 * 清空key值，释放sd和内存的数据
	 * @param key
	 */
	public void remove(String key) {
		if (TextUtils.isEmpty(key)) {
			return;
		}
		String md5Key = MD5Utils.getMD5String(key);
		
		if(mMemoryCache != null){
			mMemoryCache.remove(md5Key);
		}
		if(mDiskCacke != null){
			//内存中没有
			mDiskCacke.clearCache(md5Key);
		}
	}
    /**
     * 清空anything
     */
	public void clear() {
		if(mMemoryCache != null){
			mMemoryCache.evictAll();
		}
		if(mDiskCacke != null){
			//内存中没有
			mDiskCacke.clearCache();
		}
	}
}

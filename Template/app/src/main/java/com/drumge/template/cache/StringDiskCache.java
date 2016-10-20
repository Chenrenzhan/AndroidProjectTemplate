package com.drumge.template.cache;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.test.suitebuilder.TestMethod;
import android.text.TextUtils;
import android.util.Log;


import com.drumge.template.log.MLog;

import org.w3c.dom.Text;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 用于保存数据到sdcard卡，仅处理String数据
 * @author wshao
 *
 */
public class StringDiskCache {
	private static final String TAG = "DiskLruCache";
	private static final boolean DEBUG = false;
	
	private static final String CACHE_FILENAME_PREFIX = "";
	private static final int MAX_REMOVALS = 4;
	private static final int INITIAL_CAPACITY = 32;
	private static final float LOAD_FACTOR = 0.75f;

	private final File mCacheDir;
	private int cacheSize = 0;
	private int cacheByteSize = 0;
	private final int maxCacheItemSize = 8192; // 8192 item default
	private long maxCacheByteSize = 1024 * 1024 * 16; // 16MB default

	private final Map<String, String> mLinkedHashMap =
			Collections.synchronizedMap(new LinkedHashMap<String, String>(
					INITIAL_CAPACITY, LOAD_FACTOR, true));
	/**
	 * A filename filter to use to identify the cache filenames which have CACHE_FILENAME_PREFIX
	 * prepended.
	 */
	private static final FilenameFilter cacheFileFilter = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String filename) {
			return filename.startsWith(CACHE_FILENAME_PREFIX);
		}
	};

	/**
	 * Used to fetch an instance of DiskLruCache.
	 *
	 * @param context
	 * @param cacheDir
	 * @param maxByteSize
	 * @return
	 */
	public static StringDiskCache openCache(File cacheDir, long maxByteSize) {
		if(cacheDir == null){
			cacheDir = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "cacheDir");
    	}
		
		if (!cacheDir.exists()) {
			if(!cacheDir.mkdirs()) {
				MLog.error(TAG, "ERROR: Cannot create dir "+cacheDir.toString()+"!!!");
				return null;
			}
		}
		
		if (cacheDir.isDirectory() && cacheDir.canWrite()
				&& getUsableSpace(cacheDir) > maxByteSize) {
			MLog.info(TAG, "cacheDir :" + cacheDir.toString());
			return new StringDiskCache(cacheDir, maxByteSize);
		}

		return null;
	}

	/**
	 * Constructor that should not be called directly, instead use
	 * {@link StringDiskCache#openCache(android.content.Context, java.io.File, long)} which runs some extra checks before
	 * creating a DiskLruCache instance.
	 *
	 * @param cacheDir
	 * @param maxByteSize
	 */
	private StringDiskCache(File cacheDir, long maxByteSize) {
		mCacheDir = cacheDir;
		maxCacheByteSize = maxByteSize;
	}

	public void putText(String key, String data) {
		try {
			final String file = createFilePath(mCacheDir, key);
			if (!TextUtils.isEmpty(file)) {
				synchronized (file) {
					if (writeTextFile(data, file)) {
						put(key, file);
						if (DEBUG) {
							Log.d(TAG, "put - Added cache file, " + file);
						}
						
						flushCache();
					}
				}
			}
		} catch (final FileNotFoundException e) {
			MLog.error(TAG, "Error in put: " + e.getMessage());
		} catch (final IOException e) {
			MLog.error(TAG, "Error in put: " + e.getMessage());
		}
	}
	private void put(String key, String file) {
		mLinkedHashMap.put(key, file);
		cacheSize = mLinkedHashMap.size();
		cacheByteSize += new File(file).length();
	}

	/**
	 * Flush the cache, removing oldest entries if the total size is over the specified cache size.
	 * Note that this isn't keeping track of stale files in the cache directory that aren't in the
	 * HashMap. If the images and keys in the disk cache change often then they probably won't ever
	 * be removed.
	 */
	private void flushCache() {
		Entry<String, String> eldestEntry;
		File eldestFile;
		long eldestFileSize;
		int count = 0;

		while (count < MAX_REMOVALS &&
				(cacheSize > maxCacheItemSize || cacheByteSize > maxCacheByteSize)) {
			eldestEntry = mLinkedHashMap.entrySet().iterator().next();
			eldestFile = new File(eldestEntry.getValue());
			eldestFileSize = eldestFile.length();
			mLinkedHashMap.remove(eldestEntry.getKey());
			eldestFile.delete();
			cacheSize = mLinkedHashMap.size();
			cacheByteSize -= eldestFileSize;
			count++;
			if (DEBUG) {
				Log.d(TAG, "flushCache - Removed cache file, " + eldestFile + ", "
						+ eldestFileSize);
			}
		}
	}

	/**
	 * Get an image from the disk cache.
	 *
	 * @param key The unique identifier for the json
	 * @return The json or null if not found
	 */
	public String get(String key) throws FileNotFoundException, IOException {
			String file = mLinkedHashMap.get(key);
			if(TextUtils.isEmpty(file)){
				file = createFilePath(mCacheDir, key);
			}
			
			if (!TextUtils.isEmpty(file)) {
				synchronized (file) {
					File fis = new File(file);
					if (fis.exists()) {
						if (DEBUG) {
							Log.d(TAG, "Disk cache hit (existing file)");
						}
						try {
							
					        String json = checkExpire(fis);
					        if(!TextUtils.isEmpty(json)){
					        	put(key, file);
					        }
							return json;
				        } catch (final FileNotFoundException e) {
							MLog.error(TAG, "Error in get: " + e.getMessage());
							throw new FileNotFoundException();
						} catch (final IOException e) {
							MLog.error(TAG, "Error in get: " + e.getMessage());
							throw e;
						}
					}
				}
			}
			throw new FileNotFoundException();
	}

    public String getRaw(String key) throws FileNotFoundException, IOException {
        String file = mLinkedHashMap.get(key);
        if (TextUtils.isEmpty(file)) {
            file = createFilePath(mCacheDir, key);
        }

        if (!TextUtils.isEmpty(file)) {
            synchronized (file) {
                File f = new File(file);
                if (f.exists()) {
                    if (DEBUG) {
                        Log.d(TAG, "Disk cache hit (existing file)");
                    }
                    try {

                        FileInputStream fis = new FileInputStream(f);
                        String raw = readIs2Bytes(fis);
                        if (!TextUtils.isEmpty(raw)) {
                            put(key, file);
                        }
                        return raw;
                    } catch (final FileNotFoundException e) {
                        MLog.error(TAG, "Error in get: " + e.getMessage());
                        throw new FileNotFoundException();
                    } catch (final Exception e) {
                        MLog.error(TAG, "Error in get: " + e.getMessage());
                        throw e;
                    }
                }
            }
        }
        throw new FileNotFoundException();
    }
    
	private String checkExpire(File newfile) throws FileNotFoundException, UnsupportedEncodingException {
		FileInputStream fis = new FileInputStream(newfile);
		String json = readIs2Bytes(fis);
		
		if(json != null){
			CacheClient.CachePacket packet = JsonParser.parseJsonObject(json, CacheClient.CachePacket.class);
			long expiredTime = System.currentTimeMillis() - packet.getHeader().getCreateTime();
			
			if(expiredTime > packet.getHeader().getExpired()){
				newfile.delete();
				return null;
			}
		}
        return json;
	}

	/**
     * 将输入流数据读取到输出流当中
     */
    private OutputStream readIs2Os(InputStream is , OutputStream os){
        try {
            byte[] bytes = new byte[1024];
            int length = 0 ;
            while((length = is.read(bytes)) != -1){
                os.write(bytes, 0, length);
            }
            try {
            	is.close();
            	os.close();
            } catch (Exception e) {}
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return os ;
    }
    
	/**
     * 将输入流数据读取到输出流当中
     */
	public String readIs2Bytes(InputStream is){
        try {
        	ByteArrayOutputStream baos = new ByteArrayOutputStream();
            readIs2Os(is,baos);
            return new String(baos.toByteArray(),"utf-8") ;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null ;
    }
	
	
	/**
	 * Checks if a specific key exist in the cache.
	 *
	 * @param key The unique identifier for the json
	 * @return true if found, false otherwise
	 */
	public boolean containsKey(String key) {
		// See if the key is in our HashMap
		if (mLinkedHashMap.containsKey(key)) {
			return true;
		}
		return false;
	}

	/**
	 * Removes all disk cache entries from this instance cache dir
	 */
	public void clearCache() {
		clearCache(mCacheDir);
	}

	public void clearCache(String key) {
		String file = mLinkedHashMap.get(key);
		if(TextUtils.isEmpty(file)){
			file = createFilePath(mCacheDir, key);
		}
		
		if (!TextUtils.isEmpty(file)) {
			synchronized (file) {
				File newfile = new File(file);
				if (newfile.exists()) {
					newfile.delete();
				}
			}
		}
	}
	
	/**
	 * Removes all disk cache entries from the given directory. This should not be called directly,
	 * call {@link StringDiskCache#clearCache(android.content.Context, String)} or {@link StringDiskCache#clearCache()}
	 * instead.
	 *
	 * @param cacheDir The directory to remove the cache files from
	 */
	private static void clearCache(File cacheDir) {
		if (cacheDir == null) {
			return;
		}
		final File[] files = cacheDir.listFiles(cacheFileFilter);
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i] != null) {
					files[i].delete();
				}
			}
		}
	}

	/**
	 * Creates a constant cache file path given a target cache directory and an image key.
	 *
	 * @param cacheDir
	 * @param key
	 * @return
	 */
	public static String createFilePath(File cacheDir, String key) {
		try {
			// Use URLEncoder to ensure we have a valid filename, a tad hacky but it will do for
			// this example
			return cacheDir.getPath() + File.separator +
					CACHE_FILENAME_PREFIX + URLEncoder.encode(key.replace("*", ""), "UTF-8");
		} catch (final UnsupportedEncodingException e) {
			MLog.error(TAG, "createFilePath - " + e);
		}

		return null;
	}

	/**
	 * Create a constant cache file path using the current cache directory and an image key.
	 *
	 * @param key
	 * @return
	 */
	public String createFilePath(String key) {
		return createFilePath(mCacheDir, key);
	}

	
	private boolean writeTextFile(String Str, String file)
			throws IOException, FileNotFoundException {
		if(TextUtils.isEmpty(Str))
			return false;
		OutputStream out = null;
		long begin0 = System.currentTimeMillis();
		
		out = new BufferedOutputStream(new FileOutputStream(file));
		out.write(Str.getBytes("UTF-8"));
		out.flush();
			
		if (out != null) {
			try {
				out.close();
            } catch (Exception e) {}
			
		    long end0 = System.currentTimeMillis();
		    MLog.info(TAG, file + ":BufferedOutputStream执行耗时:" + (end0 - begin0) + " 豪秒");
		}
			
		return true;
	}
	
	private static long getUsableSpace(File path) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			 return path.getUsableSpace();
		}
		final StatFs stats = new StatFs(path.getPath());
		return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
	}

}


package com.drumge.template.imageloader;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by DW on 2016/10/9.
 */
public class ImageLoaderConfigs {
    public final static String LOADER_IMAGE_CACHE_DIR = "imageloader/Cache";
    
    public static void initImageloader(Context context){
        if(!ImageLoader.getInstance().isInited()){
            File cacheDir = StorageUtils.getOwnCacheDirectory(context, LOADER_IMAGE_CACHE_DIR);
            ImageLoaderConfiguration config = new ImageLoaderConfiguration
                    .Builder(context)
                    .memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽  
                    .threadPoolSize(3)//线程池内加载的数量  
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现  
                    .memoryCacheSize(2 * 1024 * 1024)
                    .diskCacheSize(50 * 1024 * 1024)
                    .diskCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密  
                    .tasksProcessingOrder(QueueProcessingType.LIFO)
                    .diskCacheFileCount(100) //缓存的文件数量  
                    .diskCache(new UnlimitedDiskCache(cacheDir))//自定义缓存路径  
                    .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                    .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间  
                    .writeDebugLogs() // Remove for release app  
                    .build();//开始构建  
            ImageLoader.getInstance().init(config);
        }
    }
}

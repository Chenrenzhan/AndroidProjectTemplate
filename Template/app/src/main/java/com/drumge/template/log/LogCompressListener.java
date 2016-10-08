package com.drumge.template.log;

/**
 * Created by wangduo on 2016/4/25.
 */
public interface LogCompressListener {
    void onCompressError(int errNo);//压缩失败的回调
    void onCompressFinished(String packPath);//日志压缩完毕后的回调
}

package com.drumge.template.log;

import java.io.File;

/**
 * 日志压缩类需要实现的接口
 * Created by wangduo on 2016/4/19.
 */
public interface LogCompress {
    void compress(File file) throws Exception;
    void decompress(File file) throws Exception;
}

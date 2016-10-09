package com.drumge.template.update;


import com.drumge.template.core.ICoreClient;

/**
 * 更新接口
 * @author zhongyongsheng on 14-7-17.
 */
public interface IUpdateClient extends ICoreClient {

    /**
     * 更新结果
     * @param updateResult
     * @param isForceUpdate
     */
    void onUpdateResult(UpdateResult updateResult, boolean isForceUpdate);

    /**
     * 更新进度
     * @param progress
     * @param total
     */
    void onUpdateProgress(long progress, long total);

}

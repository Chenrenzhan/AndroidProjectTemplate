package com.drumge.template.update;


import com.drumge.template.core.IBaseCore;

/**
 * 更新服务
 * @author zhongyongsheng on 14-7-17.
 */
public interface IUpdateCore extends IBaseCore {

    /**
     * 更新
     * @param updateRequest
     * @param isForceUpdate 是否强制更新
     */
    public void update(UpdateRequest updateRequest, boolean isForceUpdate);

    /**
     * 取更新信息
     * @return 返回null,则没有更新信息
     */
    public NewUpdateInfo  getUpdateInfo();

    /**
     * 如果升级成功则发送成功报告
     */
    public void checkIfNeedReportUpdateSuccess();

}

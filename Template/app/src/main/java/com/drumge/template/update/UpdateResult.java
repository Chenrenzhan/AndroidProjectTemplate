package com.drumge.template.update;

/**
 * 更新结果
 * @author zhongyongsheng on 14-7-18.
 */
public enum UpdateResult {
    Updating,

    Error, Recent, NetworkError,

    NeedDownload, Downloading, DownloadError,

    DownloadSuccess, Ready, InstallError
}

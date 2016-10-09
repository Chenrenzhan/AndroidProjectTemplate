package com.drumge.template.core;

import okio.ByteString;

/**
 * 网络求情协议接口
 */
public interface IEntProtocol {
    /**
     * encode this object to ByteString
     * @param bs target ByteString
     */
    public void toString(ByteString bs);

    /**
     * decode ByteString to this object
     * @param bs target ByteString
     */
    public void unString(ByteString bs);
}

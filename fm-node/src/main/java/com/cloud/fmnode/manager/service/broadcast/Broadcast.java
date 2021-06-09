package com.cloud.fmnode.manager.service.broadcast;

import com.cloud.fmnode.common.ResultCode;

import java.util.Map;

public interface Broadcast {
    public ResultCode broadcast(String msg, Map params);

    public ResultCode broadcastRefreshMemcache(String nodehash);
}

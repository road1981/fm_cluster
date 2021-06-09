package com.cloud.fmnode.client.service.cache;

import com.cloud.fmnode.common.ResultCode;

import java.util.Map;
import java.util.Vector;

public interface MemCache {
    Object getData(Object key);

    void setData(Object key, Object data);

    void removeData(Object key);

    ResultCode setMemCacheRoute(Map routeData);

    String getSelfId();

    Vector<String> getNodeHash();

    Map<String, String> getClientUrlMap();
}

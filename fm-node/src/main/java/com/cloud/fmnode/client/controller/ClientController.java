package com.cloud.fmnode.client.controller;


import com.cloud.fmnode.client.service.cache.MemCache;
import com.cloud.fmnode.common.HttpUtils;
import com.cloud.fmnode.common.MapObj;
import com.cloud.fmnode.common.ResultCode;
import com.cloud.fmnode.entity.MemcacheRoute;
import com.cloud.fmnode.manager.entity.SystemManager;
import com.cloud.fmnode.manager.service.broadcast.Broadcast;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


@RestController
public class ClientController {
    @Resource(name = "memCacheImpl")
    private MemCache memCache;

    @RequestMapping(value="/refreshMemcache")
    public Object refreshMemcache(@RequestBody Map refreshParams) throws Exception {
        return memCache.setMemCacheRoute(refreshParams);
    }

    @RequestMapping(value="/getMemcache")
    public Object getMemcache() throws Exception{
        String selfId = memCache.getSelfId();
        Vector<String> v = memCache.getNodeHash();
        Map urlMap = memCache.getClientUrlMap();

        Map ret = new HashMap();
        ret.put("self", selfId);
        ret.put("nodeHash", v);
        ret.put("nodeSet", urlMap.keySet());

        return ret;
    }
}

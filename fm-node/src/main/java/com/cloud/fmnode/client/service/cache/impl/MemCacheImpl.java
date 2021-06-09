package com.cloud.fmnode.client.service.cache.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.cloud.fmnode.client.service.cache.MemCache;
import com.cloud.fmnode.common.ResultCode;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;


@Service("memCacheImpl")
@Lazy(false)
public class MemCacheImpl implements MemCache, ServletContextAware {
    private static final Logger logger = LoggerFactory.getLogger(MemCacheImpl.class);
    private static CacheManager cacheManager = null;
    private static Cache cacheDefault = null;

    private static String selfId = "";
    private static Vector<String> nodeHash = new Vector<>();
    private static Map<String, String> clientUrlMap = new ConcurrentHashMap<>();

    @Override
    public void setServletContext(ServletContext context) {
        String fileName = this.getClass().getClassLoader().getResource("config/ehcache.xml").getPath();//获取文件路径
        try {
            fileName = URLDecoder.decode(fileName, "utf-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        cacheManager = CacheManager.create(fileName);
        cacheDefault = cacheManager.getCache("com.cluster.cache");
    }

    public Object getData(Object key) {
        Element el = cacheDefault.get(key);

        if (el == null) {
            return null;
        }

        return el.getObjectValue();
    }

    public void setData(Object key, Object data) {
        Element el = new Element(key, data);

        cacheDefault.put(el);
    }

    public void removeData(Object key){
        cacheDefault.remove(key);
    }

    public ResultCode setMemCacheRoute(Map routeData){
        selfId = routeData.get("self") == null ? "": routeData.get("self").toString();
        String nodeHashStr = routeData.get("nodeHash") == null ? "": routeData.get("nodeHash").toString();
        String clientUrlMapStr = routeData.get("clientUrlMap") == null ? "": routeData.get("clientUrlMap").toString();

        logger.error("update ++++++" + selfId + ": " + clientUrlMapStr);
        try {
            nodeHash = JSONObject.parseObject(nodeHashStr, new TypeReference<Vector<String>>() {
            });
            clientUrlMap = JSONObject.parseObject(clientUrlMapStr, new TypeReference<Map<String, String>>() {
            });
        }catch (Exception e){
            e.printStackTrace();
            return ResultCode.getFailure(e.getMessage());
        }

        return ResultCode.getSuccess();
    }

    public String getSelfId(){
        return selfId;
    }

    public Vector<String> getNodeHash(){
        return nodeHash;
    }

    public Map<String, String> getClientUrlMap(){
        return clientUrlMap;
    }
}

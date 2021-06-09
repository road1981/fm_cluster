package com.cloud.fmnode.manager.service.broadcast.impl;

import com.alibaba.fastjson.JSONObject;
import com.cloud.fmnode.common.ResultCode;
import com.cloud.fmnode.common.Tools;
import com.cloud.fmnode.manager.entity.SystemInfo;
import com.cloud.fmnode.manager.entity.SystemManager;
import com.cloud.fmnode.manager.entity.SystemNode;
import com.cloud.fmnode.manager.service.broadcast.Broadcast;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service("broadcastImpl")
public class BroadcastImpl implements Broadcast {
    public ResultCode broadcast(String msg, Map params){
        SystemInfo info = SystemManager.getSystemInfo();

        RestTemplate tpl = new RestTemplate();
        for(SystemNode client: info.clientMap.values()){
            //todo: 用单独的线程发送通知，处理发送失败的情况
            try {
                StringBuilder postUrl = new StringBuilder();

                ResultCode rc = tpl.postForObject(postUrl.append(client.url).append("/").append(msg).toString(), params, ResultCode.class);
                if(!rc.isSuccess()){
                    Tools.errorLogMsg(rc.getMessage());
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return ResultCode.getSuccess();
    }

    public ResultCode broadcastRefreshMemcache(String nodeHash){
        SystemInfo info = SystemManager.getSystemInfo();

        RestTemplate tpl = new RestTemplate();
        Map params = new HashMap();
        params.put("nodeHash", nodeHash);
        params.put("clientUrlMap", JSONObject.toJSONString(info.clientUrlMap));
        for(SystemNode client: info.clientMap.values()){
            params.put("self", client.id);
            //todo: 用单独的线程发送通知，处理发送失败的情况
            try {
                StringBuilder postUrl = new StringBuilder();

                ResultCode rc = tpl.postForObject(postUrl.append(client.url).append("/refreshMemcache").toString(), params, ResultCode.class);
                if(!rc.isSuccess()){
                    Tools.errorLogMsg(rc.getMessage());
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return ResultCode.getSuccess();
    }
}

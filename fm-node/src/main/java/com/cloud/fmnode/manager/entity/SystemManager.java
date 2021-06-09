package com.cloud.fmnode.manager.entity;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Application;
import com.netflix.eureka.EurekaServerContext;
import com.netflix.eureka.EurekaServerContextHolder;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import com.netflix.eureka.resources.StatusResource;
import com.netflix.eureka.util.StatusInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SystemManager {
    private static Map<String, Object> systemCache = new ConcurrentHashMap<>();

    private static PeerAwareInstanceRegistry getRegistry() {
        return getServerContext().getRegistry();
    }

    private static EurekaServerContext getServerContext() {
        return EurekaServerContextHolder.getInstance().getServerContext();
    }

    public static InstanceInfo getSelfInfo(){
        StatusInfo statusInfo;
        try {
            statusInfo = new StatusResource().getStatusInfo();
        }
        catch (Exception e) {
            statusInfo = StatusInfo.Builder.newBuilder().isHealthy(false).build();
        }

        return statusInfo.getInstanceInfo();
    }

    private static SystemNode getNode(InstanceInfo info){
        SystemNode node = new SystemNode();
        node.id = info.getId();
        node.url = info.getHomePageUrl();
        node.metadata = info.getMetadata();
        if(node.metadata == null){
            node.metadata = new HashMap<>();
        }

        return node;
    }

    public static void refreshSystemInfo(){
        List<Application> sortedApplications = getRegistry().getSortedApplications();

        SystemInfo si = new SystemInfo();
        InstanceInfo selfInfo = SystemManager.getSelfInfo();
        si.selfNode = getNode(selfInfo);

        for(int i = 0; i < sortedApplications.size(); ++i){
            Application app = sortedApplications.get(i);

            String name = app.getName();
            List<InstanceInfo> instanceList = app.getInstances();
            if("FM-MANAGER".equals(name)){
                //manager
                for(InstanceInfo info: instanceList){
                    si.currManager = getNode(info);
                    break;
                }
            }
        }

        systemCache.put("si", si);
    }

    public static SystemInfo getSystemInfo(){
        return (SystemInfo)systemCache.get("si");
    }

    //只有Manager需要填充clientMap
    public static void setSystemInfoClientMap(SystemInfo si){
        List<Application> sortedApplications = getRegistry().getSortedApplications();

        for(int i = 0; i < sortedApplications.size(); ++i){
            Application app = sortedApplications.get(i);

            String name = app.getName();
            List<InstanceInfo> instanceList = app.getInstances();
            if("FM-CLIENT".equals(name)){
                //client
                for(InstanceInfo info: instanceList){
                    String id = info.getInstanceId();
                    SystemNode infoNode = getNode(info);
                    si.clientMap.put(id, infoNode);
                    si.clientUrlMap.put(id, infoNode.url);
                }
                break;
            }
        }
    }
}

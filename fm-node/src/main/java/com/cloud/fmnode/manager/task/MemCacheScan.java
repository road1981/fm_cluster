package com.cloud.fmnode.manager.task;

import com.cloud.fmnode.common.Logger;
import com.cloud.fmnode.config.ClusterNodeType;
import com.cloud.fmnode.config.FlyMoreClusterConfig;
import com.cloud.fmnode.entity.MemcacheRoute;
import com.cloud.fmnode.manager.entity.MemCacheCluster;
import com.cloud.fmnode.manager.entity.SystemInfo;
import com.cloud.fmnode.manager.entity.SystemManager;
import com.cloud.fmnode.manager.entity.SystemNode;
import com.cloud.fmnode.manager.service.broadcast.Broadcast;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.eureka.util.StatusInfo;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
@EnableScheduling
public class MemCacheScan implements SchedulingConfigurer {
    static int testI = 1;

    @Resource(name = "broadcastImpl")
    private Broadcast broadcast;

    private void refreshClientMemcache(Map<String, String> metadata){
        Map memcacheMsg = new HashMap();
        memcacheMsg.put("nodehash", metadata.get("nodehash"));
    }

    //eureka server的metadata更新频率为30秒，前端最大的更新延迟为：
    //后端扫描频率 + 30秒 + 前端扫描频率
    private void memCacheScan(){
        SystemManager.refreshSystemInfo();
        SystemInfo si = SystemManager.getSystemInfo();

        SystemNode selfInfo = si.selfNode;

        String selfId = si.selfNode.id;

        if(selfId.equals(selfInfo.id)){
            //当前管理者维护缓存节点信息
            SystemManager.setSystemInfoClientMap(si);

            MemCacheCluster memCacheCluster = new MemCacheCluster();

            Map<String, String> metadata = si.currManager.metadata;

            memCacheCluster.analyzeMetadata(si, metadata);

            if(memCacheCluster.needRefreshNode()){
                ApplicationInfoManager myInfo = ApplicationInfoManager.getInstance();
                myInfo.registerAppMetadata(metadata);

                broadcast.broadcastRefreshMemcache(metadata.get("nodeHash"));
            }
        }
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ClusterNodeType type = FlyMoreClusterConfig.getNodeType();

        if (type.equals(ClusterNodeType.CLUSTER_NODE_TYPE_MANAGER)) {
            taskRegistrar.addTriggerTask(
                    new Runnable() {
                        @Override
                        public void run() {
                            memCacheScan();
                        }
                    },
                    triggerContext -> {
                        //2.1 从数据库获取执行周期
                        String cron = "0/5 * * * * ?";
                        //2.3 返回执行周期(Date)
                        return new CronTrigger(cron).nextExecutionTime(triggerContext);
                    }
            );
        }
    }
}

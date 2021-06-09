package com.cloud.fmnode.config;

import com.cloud.fmnode.common.Logger;
import com.cloud.fmnode.common.ResultCode;
import com.cloud.fmnode.common.Tools;
import org.apache.commons.lang.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FlyMoreClusterConfig {

    private static ClusterNodeType nodeType;
    private static List<String> biosClassArr = new ArrayList<>();      //启动类，决定此节点是管理节点、普通节点或网关节点等
    private final static String activePrefix = "spring.profiles.active=";
    private static String clusterConfig;

    public static ClusterNodeType getNodeType(){
        return nodeType;
    }

    public static List<String> getBiosClassArr(){ return biosClassArr; }

    private static void setClusterConfig(String []args){
        String nodeActive = "";
        for(int i = 0; i < args.length; ++i){
            String arg = args[i];

            int argIndex = arg.indexOf(activePrefix);
            if(argIndex < 0){
                continue;
            }
            nodeActive = arg.substring(argIndex + activePrefix.length());
            nodeActive = nodeActive.trim();
        }

        if(StringUtils.isEmpty(nodeActive)){
            clusterConfig = "bootstrap.yml";
        }else{
            clusterConfig = "bootstrap-" + nodeActive + ".yml";
        }
    }

    private static ResultCode loadClusterConfig(){
        String ymlContent = Tools.readTxtFileAll(clusterConfig, "utf-8");
        Yaml yaml = new Yaml();
        Map yamlMap = yaml.loadAs(ymlContent, Map.class);
        Map flymore = (Map)yamlMap.get("flymore");
        if(flymore == null){
            return Tools.errorLogMsg("no cluster config");
        }
        String serverType = flymore.get("serverType") == null ? "": flymore.get("serverType").toString();
        switch(serverType){
            case "manager":
                nodeType = ClusterNodeType.CLUSTER_NODE_TYPE_MANAGER;
                biosClassArr.add("org.springframework.cloud.netflix.eureka.server.EurekaServerMarkerConfiguration");
                break;
            case "client":
                nodeType = ClusterNodeType.CLUSTER_NODE_TYPE_CLIENT;
                //biosClassArr.add("org.springframework.cloud.client.discovery.EnableDiscoveryClientImportSelector");
                break;
            default:
                return Tools.errorLogMsg("unsupport serverType: " + serverType);
        }

        return ResultCode.getSuccess();
    }

    public static ResultCode initClusterOpt(String []args){
        boolean ret = false;

        setClusterConfig(args);

        return loadClusterConfig();
    }
}
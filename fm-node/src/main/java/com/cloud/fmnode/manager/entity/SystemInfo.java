package com.cloud.fmnode.manager.entity;

import java.util.HashMap;
import java.util.Map;

public class SystemInfo {
    //只有manager需要填充clientMap
    public Map<String, SystemNode> clientMap = new HashMap<>();
    public SystemNode currManager = new SystemNode();
    public SystemNode selfNode = new SystemNode();
    public Map<String, String> clientUrlMap = new HashMap<>();
}

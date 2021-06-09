package com.cloud.fmnode.entity;

import java.util.List;
import java.util.Map;
import java.util.Vector;

public class MemcacheRoute {
    public String selfId;
    public Vector<String> nodeHash;            //index -> nodeId
    public Map<String, String> clientUrlMap;   //nodeId -> url
}

package com.cloud.fmnode.manager.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.cloud.fmnode.client.service.cache.MemCache;
import com.cloud.fmnode.common.ResultCode;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang.StringUtils;

import java.awt.image.RescaleOp;
import java.util.*;

import static com.cloud.fmnode.common.Consts.*;

public class MemCacheCluster {
    //nodeId -> node上的映射数量
    private Set<String> nodeSet = new HashSet<>();
    //映射hash -> nodeId
    private List<String> nodeHash = new ArrayList<>(MAX_CACHE_NODE_NUM);
    //nodeId指向的hash集合
    private Map<String, Set<Integer>> nodeHashSet = new HashMap<>();

    //如果需要更新，则更新下列数据
    public Map<String, String> metadata;

    private boolean _needRefreshNode = false;

    public boolean needRefreshNode(){
        return _needRefreshNode;
    }

    public ResultCode analyzeMetadata(SystemInfo currSystemInfo, Map<String, String> metadata){
        String nodeSetStr = metadata.get(METADATA_NODE_SET);
        String nodeHashStr = metadata.get(METADATA_NODE_HASH);

        ResultCode rc = ResultCode.getSuccess();
        if(StringUtils.isEmpty(nodeSetStr) || StringUtils.isEmpty(nodeHashStr)) {
            //1. 如果metadata中没有数据，则初始化
            rc = loadSystemInfo(currSystemInfo);
            if(rc.isSuccess()){
                this._needRefreshNode = true;
            }
        }else{
            try {
                //2. 判断是否需要更新缓存节点
                //2.1 从meta中恢复nodeNumber和nodeHash
                nodeSet = JSONObject.parseObject(nodeSetStr, new TypeReference<Set<String>>() {});
                nodeHash = JSONObject.parseArray(nodeHashStr, String.class);

                //2.2 判断是否需要更新缓存
                this._needRefreshNode = diff(currSystemInfo);
            } catch (Exception e) {
                e.printStackTrace();
                rc = ResultCode.getFailure(e.getMessage());
            }
        }
        if(this._needRefreshNode){
            setMetadata(metadata);
        }
        return rc;
    }

    private void setMetadata(Map<String, String> metadata){
        String nodeHashStr = JSONArray.toJSONString(nodeHash);
        String nodeSetStr = JSONObject.toJSONString(nodeSet);

        metadata.put(METADATA_NODE_SET, nodeSetStr);
        metadata.put(METADATA_NODE_HASH, nodeHashStr);
    }

    private ResultCode loadSystemInfo(SystemInfo currSystemInfo){
        nodeSet = currSystemInfo.clientMap.keySet();
        if(nodeSet.size() <= 0){
            return ResultCode.getFailure("client empty");
        }

        Set<Integer> hashSet = new HashSet<>();
        for(int i = 0; i < MAX_CACHE_NODE_NUM; ++i){
            hashSet.add(i);
            nodeHash.add("");
        }

        transNode(hashSet, nodeSet);

        return ResultCode.getSuccess();
    }

    /*private ResultCode initMetadata(SystemInfo currSystemInfo, Map<String, String> metadata){
        ResultCode rc = loadSystemInfo(currSystemInfo);
        if(!rc.isSuccess()){
            return rc;
        }

        return ResultCode.getSuccess();
    }*/

    private void switchNode(Set<String> addNodeSet, Set<String> removeNodeSet, int switchNum){
        for(int i = 0; i < switchNum; ++i){
            String removeId = removeNodeSet.iterator().next();
            String addId = addNodeSet.iterator().next();

            nodeSet.add(addId);
            nodeSet.remove(removeId);

            Set<Integer> removeSet = getNodeHashSet(removeId);
            nodeHashSet.put(addId, removeSet);
            nodeHashSet.remove(removeId);

            for(int index: removeSet){
                nodeHash.set(index, addId);
            }

            removeNodeSet.remove(removeId);
            addNodeSet.remove(addId);
        }
    }

    private void transNodeHash(String nodeId, Set<Integer> currNodeHashSet, Set<Integer> hashSet, int transNumber){
        int index = 0;
        for(int i = 0; i < transNumber; ++i){
            if(hashSet.size() <= 0){
                break;
            }
            try {
                index = hashSet.iterator().next();
            }catch (Exception e){
                int a = 1;
            }

            nodeHash.set(index, nodeId);
            currNodeHashSet.add(index);
            hashSet.remove(index);
        }
    }

    private Set<Integer> getNodeHashSet(String nodeId){
        Set<Integer> currNodeHashSet = nodeHashSet.get(nodeId);
        if(currNodeHashSet == null){
            currNodeHashSet = new HashSet<>();

            nodeHashSet.put(nodeId, currNodeHashSet);
        }

        return currNodeHashSet;
    }

    private void transNode(Set<Integer> hashSet, Set<String> newClientSet){
        int hashSetNumber = hashSet.size();
        if(hashSetNumber <= 0 || newClientSet.size() <= 0){
            return;
        }

        int clientNumber = newClientSet.size();
        int hashNumberPerClient = hashSetNumber / clientNumber;
        if(hashSetNumber % clientNumber != 0){
            hashNumberPerClient += 1;
        }

        transNode(hashSet, newClientSet, hashNumberPerClient);
    }

    private void transNode(Set<Integer> hashSet, Set<String> newClientSet, int hashNumberPerClient){
        int hashSetNumber = hashSet.size();
        if(hashSetNumber <= 0 || newClientSet.size() <= 0 || hashNumberPerClient <= 0){
            return;
        }

        int clientNumber = newClientSet.size();
        int lastNumber = hashSetNumber;
        int currClientNumber = 0;
        for(String nodeId: newClientSet){
            ++currClientNumber;
            Set<Integer> currNodeHashSet = getNodeHashSet(nodeId);

            int needTransNum = hashNumberPerClient - currNodeHashSet.size();
            if(lastNumber <= needTransNum || currClientNumber >= clientNumber){
                transNodeHash(nodeId, currNodeHashSet, hashSet, lastNumber);
                break;
            }

            transNodeHash(nodeId, currNodeHashSet, hashSet, needTransNum);
            lastNumber -= needTransNum;
            if(lastNumber <= 0){
                break;
            }
        }
    }

    private void addNode(Set<String> _newClientSet){
        Set<String> newClientSet = new HashSet<>();
        newClientSet.addAll(_newClientSet);
        //1. 将新节点纳入nodeSet
        nodeSet.addAll(newClientSet);

        Set<Integer> hashSet = new HashSet<>();

        int clientNumber = nodeSet.size();
        int hashNumberPerClient = MAX_CACHE_NODE_NUM / clientNumber;
        if(MAX_CACHE_NODE_NUM % clientNumber != 0){
            hashNumberPerClient += 1;
        }

        //2. 收集并删除待转移插槽
        for(Map.Entry<String, Set<Integer>> item: nodeHashSet.entrySet()){
            Set<Integer> hash = item.getValue();

            int needTrans = hash.size() - hashNumberPerClient;
            if(needTrans > 0){
                Iterator<Integer> it = hash.iterator();
                for(int i = 0; i < needTrans; ++i){
                    Integer hashIndex = it.next();
                    hashSet.add(hashIndex);
                    it.remove();
                }
            }else if(needTrans < 0){
                String id = item.getKey();
                newClientSet.add(id);
            }
        }

        //3. 将待转移插槽分配到新节点上
        transNode(hashSet, newClientSet, hashNumberPerClient);
    }

    private void removeNode(Set<String> removeNodeSet){
        Set<Integer> hashSet = new HashSet<>();

        //1. 收集并删除待转移插槽
        for(String removeNodeId: removeNodeSet){
            Set<Integer> removeHashSet = getNodeHashSet(removeNodeId);
            if(removeHashSet.size() > 0){
                hashSet.addAll(removeHashSet);
            }
            nodeHashSet.remove(removeNodeId);
        }

        //2. 删除待删除节点
        nodeSet.removeAll(removeNodeSet);

        //3. 将待转移插槽分配到其他节点上
        transNode(hashSet, nodeSet);
    }

    //根据SystemInfo，计算
    //1. 是否需要更新缓存分配
    //2. 如果需要更新缓存，则计算新的缓存分布
    public boolean diff(SystemInfo currSystemInfo){
        //1. 统计待修改节点
        //待添加节点
        final Set<String> addNodeSetClientMap = currSystemInfo.clientMap.keySet();
        Set<String> addNodeSet = new HashSet<>();
        addNodeSet.addAll(addNodeSetClientMap);
        //待删除节点
        Set<String> removeNodeSet = new HashSet<>();

        for(String id: nodeSet){
            if(currSystemInfo.clientMap.containsKey(id)){
                addNodeSet.remove(id);
            }else{
                removeNodeSet.add(id);
            }
        }

        int addNum = addNodeSet.size();
        int removeNum = removeNodeSet.size();
        if(addNum <= 0 && removeNum <= 0){
            return false;
        }
        //1.1 统计节点Hash Set
        for(int i = 0; i < nodeHash.size(); ++i){
            String nodeId = nodeHash.get(i);

            Set<Integer> set = getNodeHashSet(nodeId);
            set.add(i);
        }

        //2. 根据修改节点，计算新排布范围
        //2.1 首先计算可以直接替换的节点
        int switchNum = Math.min(addNum, removeNum);
        if(switchNum > 0){
            switchNode(addNodeSet, removeNodeSet, switchNum);
            addNum -= switchNum;
            removeNum -= switchNum;
        }
        //2.2 计算剩余节点
        if(addNum > 0) {
            //2.2.1 剩余节点为待添加节点，将其他节点上的插槽均匀分布到待添加节点上
            addNode(addNodeSet);
        }else if(removeNum > 0) {
            //2.2.2 剩余节点为待删除节点，需要将待删除节点上的插槽
            removeNode(removeNodeSet);
        }

        return true;
    }
}

package com.cloud.fmnode.common;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class MapObj extends HashMap implements Map{
    public Map map = null;

    public MapObj(Map data){
        map = data;
    }

    public MapObj() {
        map = new HashMap();
    }

    public MapObj(HttpServletRequest request){
        Map properties = request.getParameterMap();
        Map returnMap = new HashMap();
        Iterator entries = properties.entrySet().iterator();
        Map.Entry entry;
        String name = "";
        String value = "";
        while (entries.hasNext()) {
            entry = (Map.Entry) entries.next();
            name = (String) entry.getKey();
            Object valueObj = entry.getValue();
            if(null == valueObj){
                value = "";
            }else if(valueObj instanceof String[]){
                String[] values = (String[])valueObj;
                for(int i=0;i<values.length;i++){
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length()-1);
            }else{
                value = valueObj.toString();
            }
            returnMap.put(name, value);
        }
        map = returnMap;
    }

    @Override
    public Object get(Object key) {
        return map.get(key);
    }

    public String getString(Object key) {
        return map.get(key) == null?"": map.get(key).toString();
    }

    public double getDouble(Object key) {return map.get(key) == null ? 0 : Double.parseDouble(map.get(key).toString());}

    @SuppressWarnings("unchecked")
    @Override
    public Object put(Object key, Object value) {
        return map.put(key, value);
    }

    //只有page中没有key，才插入key
    public void addPut(Object key, Object value){
        if(!map.containsKey(key)){
            put(key, value);
        }
    }

    //只有page中没有key，才插入key
    public void addPutAll(Map<Object, Object> t){
        for(Entry<Object, Object> entry : t.entrySet()){
            addPut(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Object remove(Object key) {
        return map.remove(key);
    }

    public void clear() {
        map.clear();
    }

    public boolean containsKey(Object key) {
        // TODO Auto-generated method stub
        return map.containsKey(key);
    }

    public boolean containsValue(Object value) {
        // TODO Auto-generated method stub
        return map.containsValue(value);
    }

    public Set entrySet() {
        // TODO Auto-generated method stub
        return map.entrySet();
    }

    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return map.isEmpty();
    }

    public Set keySet() {
        // TODO Auto-generated method stub
        return map.keySet();
    }

    @SuppressWarnings("unchecked")
    public void putAll(Map t) {
        // TODO Auto-generated method stub
        map.putAll(t);
    }

    public int size() {
        // TODO Auto-generated method stub
        return map.size();
    }

    public Collection values() {
        // TODO Auto-generated method stub
        return map.values();
    }
}

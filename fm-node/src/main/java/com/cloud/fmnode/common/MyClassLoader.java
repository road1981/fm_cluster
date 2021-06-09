package com.cloud.fmnode.common;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 描述：
 * 自定义类加载器
 *
 * @author XiangQingSong
 * @create 2020-01-08-15:28
 */


public class MyClassLoader extends URLClassLoader {
    private final Map<String, byte[]> cl = new HashMap<>();

    public MyClassLoader(Map<String, byte[]> cl) {
        super(new URL[0], Thread.currentThread().getContextClassLoader());
        this.cl.putAll(cl);
    }


    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] buf = cl.get(name);
        if (Objects.isNull(buf)) {
            //非动态生成的交给jvm加载器查找加载
            return super.findClass(name);
        }
        //删除加载后的
        cl.remove(name);
        //加载
        return defineClass(name, buf, 0, buf.length);
    }
}
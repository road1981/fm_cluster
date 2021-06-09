package com.cloud.fmnode.common;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassOptions {
    public static List<String> getClassOptionsList(){
        ClassOptions co = new ClassOptions();
        return co._getClassOptionsList();
    };

    private List<String> _getClassOptionsList(){
        List<String> optionList = new ArrayList<>();
        ClassLoader dummyc= getClass().getClassLoader();
        URLClassLoader urlClassLoader=(URLClassLoader)dummyc;
        URL[] urls=urlClassLoader.getURLs();
        String classpath = "";
        for (URL i : urls) {
            classpath += ";" + i.getPath().substring(1);
        }
        optionList.addAll(Arrays.asList("-classpath",System.getProperty("java.class.path")
                + classpath )) ;

        return optionList;
    }
}

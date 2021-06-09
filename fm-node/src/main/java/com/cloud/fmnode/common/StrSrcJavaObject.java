package com.cloud.fmnode.common;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述：
 * 自定义String字符串对象文件处理
 *
 * @author XiangQingSong
 * @create 2020-01-08-15:32
 */
public class StrSrcJavaObject extends SimpleJavaFileObject {
    /**str拼接的类*/
    private String content;
    /**类名*/
    private String name;
    /**编译后的文件内容*/
    private final Map<String, byte[]> clam = new HashMap<String, byte[]>();
    /**
     *
     */
    public StrSrcJavaObject(String name, String count) {
        super(URI.create("string:///" + name.replace(".", "/") + Kind.SOURCE.extension), Kind.SOURCE);
        this.content = count;
        this.name = name;
    }
    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        System.out.println("获取拼接类" + content);
        return content;
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        System.out.println("打开输出流output");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream() {
            @Override
            public void close() throws IOException {
                System.out.println("关闭输出流");
                super.close();
                clam.put(name, this.toByteArray());
            }
        };
        return byteArrayOutputStream;
    }


    public Map<String, byte[]> getBytes() {
        return clam;
    }
}
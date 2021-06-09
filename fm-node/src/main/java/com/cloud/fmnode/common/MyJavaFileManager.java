package com.cloud.fmnode.common;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.io.IOException;

/**
 * 描述：
 * 自定义类编译文件管理器
 *
 * @author XiangQingSong
 * @create 2020-01-08-15:31
 */
public class MyJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

    public MyJavaFileManager(JavaFileManager fileManager) {
        super(fileManager);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        if (kind == JavaFileObject.Kind.CLASS) {
            return (JavaFileObject) sibling;
        }
        return super.getJavaFileForOutput(location, className, kind, sibling);
    }

}
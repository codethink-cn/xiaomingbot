package cn.chuanwise.xiaoming.classloader;

import cn.chuanwise.toolkit.classloader.CollectionClassLoader;

import java.net.URL;
import java.net.URLClassLoader;

public class XiaoMingClassLoader extends CollectionClassLoader {
    final URLAddableURLClassLoader urlAddableURLClassLoader;

    public XiaoMingClassLoader(ClassLoader parent) {
        super(parent);
        urlAddableURLClassLoader = new URLAddableURLClassLoader(new URL[]{}, parent);
        addClassLoader(urlAddableURLClassLoader);
    }

    public void addURL(URL url) {
        urlAddableURLClassLoader.addURL(url);
    }

    private static class URLAddableURLClassLoader extends URLClassLoader {
        public URLAddableURLClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }

        @Override
        public void addURL(URL url) {
            super.addURL(url);
        }
    }
}

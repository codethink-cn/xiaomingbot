package cn.chuanwise.xiaoming.debug;

import cn.chuanwise.toolkit.serialize.Serializer;
import cn.chuanwise.util.MessageDigests;
import cn.chuanwise.util.Preconditions;
import cn.chuanwise.util.Serializers;
import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import cn.chuanwise.xiaoming.bot.XiaoMingBotImpl;
import cn.chuanwise.xiaoming.launcher.SimpleXiaoMingLauncher;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.plugin.PluginHandler;
import cn.chuanwise.xiaoming.plugin.PluginHandlerImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PluginDebuggerBuilder {
    final Serializer serializer = Serializers.newJacksonSerializer();
    final List<PluginHandler> pluginHandlers = new ArrayList<>();

    long code;
    byte[] passwordMd5;
    File workingDirectory;

    public PluginDebugger build() {
        Preconditions.nonNull(passwordMd5, "password");
        final XiaoMingBot xiaoMingBot = new XiaoMingBotImpl(code, passwordMd5);
        if (Objects.nonNull(workingDirectory)) {
            xiaoMingBot.setWorkingDirectory(workingDirectory);
            xiaoMingBot.getMiraiBot().getConfiguration().setWorkingDir(new File(workingDirectory, "launcher"));
        } else {
            xiaoMingBot.getMiraiBot().getConfiguration().setWorkingDir(new File("launcher"));
        }
        xiaoMingBot.getMiraiBot().getConfiguration().fileBasedDeviceInfo();
        return new SimplePluginDebugger(new SimpleXiaoMingLauncher(xiaoMingBot), pluginHandlers);
    }

    public PluginDebuggerBuilder code(long code) {
        this.code = code;
        return this;
    }

    public PluginDebuggerBuilder password(String password) {
        passwordMd5 = MessageDigests.MD5.digest(password.getBytes());
        return this;
    }

    public PluginDebuggerBuilder md5(byte[] passwordMd5) {
        this.passwordMd5 = passwordMd5;
        return this;
    }

    public PluginDebuggerBuilder addPlugin(File file) throws IOException {
        final PluginHandler handler = serializer.deserialize(file, "UTF-8", PluginHandlerImpl.class);
        pluginHandlers.add(handler);
        return this;
    }

    public PluginDebuggerBuilder addPlugin(InputStream inputStream) throws IOException {
        final PluginHandler handler = serializer.deserialize(inputStream, "UTF-8", PluginHandlerImpl.class);
        pluginHandlers.add(handler);
        return this;
    }

    public PluginDebuggerBuilder addPlugin(PluginHandler pluginHandler) {
        pluginHandlers.add(pluginHandler);
        return this;
    }

    public PluginDebuggerBuilder workingDirectory(File workingDirectory) {
        this.workingDirectory = workingDirectory;
        return this;
    }

    public <T extends Plugin> PluginDebuggerBuilder addPlugin(String name, Class<T> mainClass) {
        final PluginHandler pluginHandler = new PluginHandlerImpl();
        pluginHandler.set("name", name);
        pluginHandler.set("main", mainClass.getName());
        return addPlugin(pluginHandler);
    }

    public <T extends Plugin> PluginDebuggerBuilder addPlugin(Class<T> mainClass) {
        return addPlugin(mainClass.getSimpleName(), mainClass);
    }
}

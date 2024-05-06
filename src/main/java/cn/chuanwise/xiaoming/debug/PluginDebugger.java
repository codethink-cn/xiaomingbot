package cn.chuanwise.xiaoming.debug;

import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import cn.chuanwise.xiaoming.launcher.XiaoMingLauncher;
import cn.chuanwise.xiaoming.plugin.PluginHandler;
import org.slf4j.Logger;

import java.util.List;

public interface PluginDebugger {
    XiaoMingBot getXiaoMingBot();

    void run() throws Exception;

    XiaoMingLauncher getLauncher();

    List<PluginHandler> getPluginHandlers();

    Logger getLogger();
}

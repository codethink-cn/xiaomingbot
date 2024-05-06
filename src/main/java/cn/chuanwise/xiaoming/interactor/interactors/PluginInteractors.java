package cn.chuanwise.xiaoming.interactor.interactors;

import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.annotation.Required;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.plugin.PluginManager;
import cn.chuanwise.xiaoming.user.XiaoMingUser;
import cn.chuanwise.xiaoming.util.CommandWords;

import java.util.Map;
import java.util.Objects;

public class PluginInteractors extends SimpleInteractors {
    PluginManager pluginManager;

    @Override
    public void onRegister() {
        pluginManager = xiaoMingBot.getPluginManager();
    }

    @Filter(CommandWords.PLUGIN)
    @Required("plugin.list")
    public void listPlugins(XiaoMingUser user) {
        final Map<String, Plugin> plugins = getXiaoMingBot().getPluginManager().getPlugins();
        if (plugins.isEmpty()) {
            user.sendMessage("没有启动任何插件哦");
        } else {
            user.sendMessage("「插件列表」\n" +
                    CollectionUtil.toIndexString(plugins.values(), x -> {
                        try {
                            return x.getCompleteName() + "（" + x.getStatus().toChinese() + "）";
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }
                    }));
        }
    }

    @Filter(CommandWords.PLUGIN + " {插件名}")
    @Required("plugin.look")
    public void lookPlugin(XiaoMingUser user, @FilterParameter("插件名") Plugin plugin) {
        if (Objects.isNull(plugin)) {
            user.sendError("想偷看内核详情，你这思想很危险嗷！");
        } else {
            user.sendMessage("插件名：" + plugin.getName() + "\n" +
                    "版本：" + plugin.getVersion() + "\n" +
                    "消息交互器：" + xiaoMingBot.getInteractorManager().getInteractors(plugin).size() + " 个\n" +
                    "事件监听器：" + xiaoMingBot.getEventManager().getListeners(plugin).size() + " 个");
        }
    }
}

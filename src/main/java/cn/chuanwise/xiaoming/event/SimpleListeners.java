package cn.chuanwise.xiaoming.event;

import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import cn.chuanwise.xiaoming.object.PluginObject;
import cn.chuanwise.xiaoming.plugin.Plugin;
import lombok.Data;

import java.util.Objects;

@Data
public class SimpleListeners<T extends Plugin> implements Listeners<T>, PluginObject<T> {
    protected transient XiaoMingBot xiaoMingBot;
    protected transient T plugin;

    @Override
    public XiaoMingBot getXiaoMingBot() {
        if (Objects.nonNull(xiaoMingBot)) {
            return xiaoMingBot;
        } else if (Objects.nonNull(plugin)) {
            return plugin.getXiaoMingBot();
        } else {
            return null;
        }
    }
}

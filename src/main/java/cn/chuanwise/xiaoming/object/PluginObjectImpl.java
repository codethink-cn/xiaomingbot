package cn.chuanwise.xiaoming.object;

import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import cn.chuanwise.xiaoming.plugin.Plugin;
import lombok.Getter;
import lombok.Setter;

import java.beans.Transient;
import java.util.Objects;

@Getter
@Setter
public class PluginObjectImpl<T extends Plugin> implements PluginObject<T> {
    protected transient T plugin;

    protected transient XiaoMingBot xiaoMingBot;

    @Override
    @Transient
    public XiaoMingBot getXiaoMingBot() {
        if (Objects.nonNull(xiaoMingBot)) {
            return xiaoMingBot;
        } else if (Objects.nonNull(plugin)) {
            return plugin.getXiaoMingBot();
        } else {
            return null;
        }
    }

    @Override
    public void setPlugin(T plugin) {
        this.plugin = plugin;
        if (Objects.isNull(xiaoMingBot) && Objects.nonNull(plugin)) {
            xiaoMingBot = plugin.getXiaoMingBot();
        }
    }
}

package cn.chuanwise.xiaoming.preservable;

import cn.chuanwise.toolkit.preservable.AbstractPreservable;
import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import cn.chuanwise.xiaoming.object.PluginObject;
import cn.chuanwise.xiaoming.plugin.Plugin;

import java.beans.Transient;
import java.util.Optional;

public class SimplePreservable<T extends Plugin>
        extends AbstractPreservable
        implements PluginObject<T> {

    protected transient T plugin;
    protected transient XiaoMingBot xiaoMingBot;

    @Override
    @Transient
    public T getPlugin() {
        return plugin;
    }

    @Override
    public void setPlugin(T plugin) {
        this.plugin = plugin;
    }

    @Override
    @Transient
    public XiaoMingBot getXiaoMingBot() {
        return Optional.ofNullable(plugin)
                .map(Plugin::getXiaoMingBot)
                .orElse(xiaoMingBot);
    }

    @Override
    public void setXiaoMingBot(XiaoMingBot xiaoMingBot) {
        this.xiaoMingBot = xiaoMingBot;
    }

    public void readyToSave() {
        xiaoMingBot.getFileSaver().readyToSave(this);
    }
}
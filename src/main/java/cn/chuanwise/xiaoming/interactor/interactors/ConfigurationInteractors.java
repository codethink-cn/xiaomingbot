package cn.chuanwise.xiaoming.interactor.interactors;

import cn.chuanwise.util.Times;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.annotation.FilterParameter;
import cn.chuanwise.xiaoming.annotation.Required;
import cn.chuanwise.xiaoming.configuration.Configuration;
import cn.chuanwise.xiaoming.interactor.SimpleInteractors;
import cn.chuanwise.xiaoming.user.XiaoMingUser;
import cn.chuanwise.xiaoming.util.CommandWords;

public class ConfigurationInteractors extends SimpleInteractors {
    Configuration configuration;

    @Override
    public void onRegister() {
        configuration = xiaoMingBot.getConfiguration();
    }

    /**
     * 调试模式开关
     */
    @Filter(CommandWords.ENABLE + CommandWords.DEBUG)
    @Required("core.debug.enable")
    public void onEnableDebug(XiaoMingUser user) {
        if (configuration.isDebug()) {
            user.sendError("调试模式已经开启了");
        } else {
            configuration.setDebug(true);
            getXiaoMingBot().getFileSaver().readyToSave(configuration);
            user.sendMessage("成功启动调试模式");
        }
    }

    @Filter(CommandWords.DISABLE + CommandWords.DEBUG)
    @Required("core.debug.enable")
    public void onDisableDebug(XiaoMingUser user) {
        if (configuration.isDebug()) {
            configuration.setDebug(false);
            getXiaoMingBot().getFileSaver().readyToSave(configuration);
            user.sendMessage("成功关闭调试模式");
        } else {
            user.sendMessage("调试模式已经关闭了");
        }
    }

    /**
     * 保存文件周期等
     */
    @Filter(CommandWords.SET + CommandWords.SAVE + CommandWords.PERIOD + " {周期}")
    @Required("core.config.save.period.set")
    public void onSetSavePeriod(XiaoMingUser user, @FilterParameter("周期") long period) {
        configuration.setSavePeriod(period);
        getXiaoMingBot().getFileSaver().readyToSave(configuration);

        final String periodLength = Times.toTimeLength(period);
        if (configuration.isSaveFileDirectly()) {
            user.sendWarning("成功设置保存文件周期为「" + periodLength + "」，但已开启文件直接保存，该设置作用不显著");
        } else {
            user.sendMessage("成功设置保存文件周期为「" + periodLength + "」");
        }
    }

    @Filter(CommandWords.SET + CommandWords.AUTO + CommandWords.OPTIMIZE + CommandWords.PERIOD + " {周期}")
    @Required("core.config.optimize.period.set")
    public void onSetOptimizePeriod(XiaoMingUser user, @FilterParameter("周期") long period) {
        configuration.setOptimizePeriod(period);
        getXiaoMingBot().getFileSaver().readyToSave(configuration);
        user.sendMessage("成功设置自动优化性能周期为「" + Times.toTimeLength(period) + "」");
    }
}

package cn.chuanwise.xiaoming.optimize;

import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
import cn.chuanwise.xiaoming.plugin.Plugin;
import lombok.Getter;

import java.util.*;

@Getter
public class OptimizerImpl extends ModuleObjectImpl implements Optimizer {
    List<Runnable> coreOptimizeTasks = new ArrayList<>();

    Map<Plugin, List<Runnable>> pluginOptimizeTasks = new HashMap<>();

    public OptimizerImpl(XiaoMingBot xiaoMingBot) {
        super(xiaoMingBot);
        coreOptimizeTasks.add(System::gc);

        coreOptimizeTasks = Collections.unmodifiableList(coreOptimizeTasks);
    }
}

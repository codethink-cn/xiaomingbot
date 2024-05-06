package cn.chuanwise.xiaoming.schedule;

import cn.chuanwise.util.Preconditions;
import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.beans.Transient;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
public class SchedulerImpl extends ModuleObjectImpl implements Scheduler {

    transient final Map<String, Runnable> finalTasks = new ConcurrentHashMap<>();

    @Getter
    transient final ScheduledExecutorService threadPool;

    public SchedulerImpl(XiaoMingBot xiaoMingBot) {
        super(xiaoMingBot);
        this.threadPool = Executors.newScheduledThreadPool(xiaoMingBot.getConfiguration().getMaxMainThreadPoolSize());
    }

    @Transient
    @Override
    public Logger getLogger() {
        return log;
    }

    @Override
    public Map<String, Runnable> getFinalTasks() {
        return Collections.unmodifiableMap(finalTasks);
    }

    @Override
    public void runFinally(String name, Runnable runnable) {
        Preconditions.state(!isStopped(), "scheduler already stopped");
        finalTasks.put(name, runnable);
    }

    @Override
    public Runnable cancelFinally(String name) {
        Preconditions.state(!isStopped(), "scheduler already stopped");

        if (Objects.isNull(name)) {
            return null;
        }
        final Runnable runnable = finalTasks.get(name);
        if (Objects.nonNull(runnable)) {
            finalTasks.remove(name);
        }
        return runnable;
    }
}
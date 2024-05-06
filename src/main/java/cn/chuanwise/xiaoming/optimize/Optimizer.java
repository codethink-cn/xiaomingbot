package cn.chuanwise.xiaoming.optimize;

import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.util.Maps;
import cn.chuanwise.util.Preconditions;
import cn.chuanwise.xiaoming.plugin.Plugin;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;

public interface Optimizer {
    List<Runnable> getCoreOptimizeTasks();

    Map<Plugin, List<Runnable>> getPluginOptimizeTasks();

    default void optimize() {
        Consumer<Runnable> taskRunner = runnable -> {
            try {
                runnable.run();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        };

        getCoreOptimizeTasks().forEach(taskRunner);
        getPluginOptimizeTasks().values().forEach(list -> list.forEach(taskRunner));
    }

    default void runWhileOptimize(Runnable runnable, Plugin plugin) {
        Maps.getOrPutSupply(getPluginOptimizeTasks(), plugin, CopyOnWriteArrayList::new).add(runnable);
    }

    /***
     * 只在下一次优化任务执行时执行
     * @param runnable 优化操作
     * @param plugin 提交该操作的插件
     */
    default void runOnNextOptimize(Runnable runnable, Plugin plugin) {
        Preconditions.nonNull(plugin, "plugin submitted optimize tasks");
        runWhileOptimize(new Runnable() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {
                    cancelOptimizeTask(runnable, plugin);
                }
            }

            /*** 重写 equals 使得 cancelOptimizeTask 能够取消成功 */
            @Override
            public boolean equals(Object obj) {
                return obj == runnable;
            }
        }, plugin);
    }

    /***
     * 只在下一次优化任务执行时执行
     * @param callable 优化操作
     * @param plugin 提交该操作的插件
     * @param <T> 优化操作的返回值
     * @return 获取返回值使用的 {@link Future}
     */
    default <T> Future<T> runOnNextOptimize(Callable<T> callable, Plugin plugin) {
        final FutureTask<T> futureTask = new FutureTask<>(callable);
        runOnNextOptimize(futureTask, plugin);
        return futureTask;
    }

    default void cancelOptimizeTask(Runnable runnable, Plugin plugin) {
        final List<Runnable> runnables = getPluginOptimizeTasks().get(plugin);
        if (CollectionUtil.isEmpty(runnables)) {
            return;
        }

        runnables.remove(runnable);
        if (runnables.isEmpty()) {
            getPluginOptimizeTasks().remove(plugin);
        }
    }
}

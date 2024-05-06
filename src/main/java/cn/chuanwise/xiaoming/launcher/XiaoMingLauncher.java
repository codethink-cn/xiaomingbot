package cn.chuanwise.xiaoming.launcher;

import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import org.slf4j.Logger;

public interface XiaoMingLauncher {
    /**
     * 载入一大堆设置
     *
     * @return
     */
    boolean launch();

    /**
     * 启动小明
     */
    default void start() throws Exception {
        getXiaoMingBot().start();
        setShutdownHook();
    }

    default void setShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // 如果小明此时还没有关闭则关闭
            if (!getXiaoMingBot().isDisabled()) {
                getXiaoMingBot().stop();
            }
        }));
    }

    /**
     * 关闭小明
     */
    void stop();

    XiaoMingBot getXiaoMingBot();

    Logger getLogger();
}

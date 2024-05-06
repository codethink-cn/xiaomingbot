package cn.chuanwise.xiaoming.object;

import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import org.slf4j.Logger;

import java.beans.Transient;

/**
 * 小明本体对象
 */
public interface ModuleObject extends XiaoMingObject {
    /**
     * 获取当前对象的日志
     *
     * @return 日志对象
     */
    @Transient
    Logger getLogger();

    default void flushBotReference(XiaoMingBot xiaoMingBot) {
    }
}

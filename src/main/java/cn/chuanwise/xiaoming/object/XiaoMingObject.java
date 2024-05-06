package cn.chuanwise.xiaoming.object;

import cn.chuanwise.xiaoming.bot.XiaoMingBot;

import java.beans.Transient;

/**
 * 小明对象
 * 目前只有便捷地获得小明本体引用的功能
 *
 * @author Chuanwise
 */
public interface XiaoMingObject {
    /**
     * 获取小明本体引用
     *
     * @return 小明本体
     */
    @Transient
    XiaoMingBot getXiaoMingBot();

    void setXiaoMingBot(XiaoMingBot xiaoMingBot);
}

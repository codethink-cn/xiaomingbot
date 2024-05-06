package cn.chuanwise.xiaoming.object;

import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Transient;

@Getter
public class ModuleObjectImpl extends XiaoMingObjectImpl implements ModuleObject {
    transient Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    public ModuleObjectImpl(XiaoMingBot xiaoMingBot) {
        super(xiaoMingBot);
    }

    @Transient
    @Override
    public Logger getLogger() {
        return logger;
    }
}

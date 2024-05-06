package cn.chuanwise.xiaoming.event;

import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import lombok.Getter;
import lombok.Setter;

public class SimpleXiaoMingEvent implements XiaoMingEvent {
    protected volatile boolean intercept = false;

    @Getter
    @Setter
    protected volatile XiaoMingBot xiaoMingBot;

    @Override
    public boolean isIntercepted() {
        return intercept;
    }

    @Override
    public void intercept() {
        intercept = true;
    }
}

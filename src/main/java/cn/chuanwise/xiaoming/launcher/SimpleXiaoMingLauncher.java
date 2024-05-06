package cn.chuanwise.xiaoming.launcher;

import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

@Slf4j
@Getter
@AllArgsConstructor
public class SimpleXiaoMingLauncher implements XiaoMingLauncher {
    final XiaoMingBot xiaoMingBot;

    @Override
    public boolean launch() {
        return true;
    }

    @Override
    public void stop() {
        xiaoMingBot.stop();
    }

    @Override
    public Logger getLogger() {
        return log;
    }
}

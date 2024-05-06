package cn.chuanwise.xiaoming.util;

import cn.chuanwise.api.Registrable;
import cn.chuanwise.util.Preconditions;
import cn.chuanwise.util.StaticUtil;
import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import cn.chuanwise.xiaoming.plugin.Plugin;

import java.util.Collection;
import java.util.Objects;

public class Registers extends StaticUtil {
    public static void checkRegister(XiaoMingBot xiaoMingBot, Plugin plugin, String objectName) {
        Preconditions.state(xiaoMingBot.getStatus() != XiaoMingBot.Status.ENABLED || Objects.nonNull(plugin),
                "can not register " + objectName + " as xiaoming core");
    }

    public static void checkUnregister(XiaoMingBot xiaoMingBot, Plugin plugin, String objectName) {
        Preconditions.state(xiaoMingBot.getStatus() != XiaoMingBot.Status.ENABLED || Objects.nonNull(plugin),
                "can not unregister " + objectName + "s registered by core");
    }

    public static <T extends Registrable> void register(Collection<T> collection, T... elements) {
        for (T element : elements) {
            collection.add(element);
            element.onRegister();
        }
    }
}
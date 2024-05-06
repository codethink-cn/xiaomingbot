package cn.chuanwise.xiaoming.interactor.context;

import cn.chuanwise.util.Maps;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.interactor.handler.Interactor;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.user.XiaoMingUser;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class InteractorContext {
    final XiaoMingUser user;
    final Interactor interactor;
    final Plugin plugin;
    final Message message;
    final Map<String, String> arguments;
    final Map<String, Object> argumentValues;
    final List<Object> finalArguments;

    public String getArgument(String name) {
        if (Maps.nonEmpty(arguments)) {
            return arguments.get(name);
        } else {
            return null;
        }
    }
}

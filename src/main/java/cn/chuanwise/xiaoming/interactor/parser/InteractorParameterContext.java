package cn.chuanwise.xiaoming.interactor.parser;

import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.interactor.context.InteractorContext;
import cn.chuanwise.xiaoming.interactor.handler.Interactor;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.user.XiaoMingUser;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class InteractorParameterContext<T> extends InteractorContext {
    final Class<T> parameterClass;
    final String parameterName;
    final String inputValue;
    final String defaultValue;

    public InteractorParameterContext(XiaoMingUser user,
                                      Interactor interactor,
                                      Plugin plugin,
                                      Map<String, String> arguments,
                                      List<Object> methodArguments,
                                      Map<String, Object> argumentValues,
                                      Class<T> parameterClass,
                                      Message message,
                                      String parameterName,
                                      String inputValue,
                                      String defaultValue) {
        super(user, interactor, plugin, message, arguments, argumentValues, methodArguments);
        this.parameterClass = parameterClass;
        this.parameterName = parameterName;
        this.inputValue = inputValue;
        this.defaultValue = defaultValue;
    }
}

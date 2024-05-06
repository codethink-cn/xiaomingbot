package cn.chuanwise.xiaoming.interactor.filter;

import cn.chuanwise.pattern.ParameterPattern;
import cn.chuanwise.util.Maps;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.user.XiaoMingUser;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;

@Getter
public class ParameterFilterMatcher extends FilterMatcher {
    ParameterPattern parameterPattern;

    public ParameterFilterMatcher(String format) {
        this.parameterPattern = new ParameterPattern(format);
    }

    @Override
    public boolean apply(XiaoMingUser user, Message message) {
        return parse(message.serialize()).map(Maps::nonEmpty).orElse(false);
    }

    @Override
    public String toUsage() {
        return parameterPattern.getUsage(parameter -> ("[" + parameter + "]"), "  ");
    }

    public boolean matches(String input) {
        return parameterPattern.matches(input);
    }

    public Optional<Map<String, String>> parse(String input) {
        return parameterPattern.parse(input);
    }
}

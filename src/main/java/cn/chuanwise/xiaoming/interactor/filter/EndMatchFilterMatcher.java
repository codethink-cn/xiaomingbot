package cn.chuanwise.xiaoming.interactor.filter;

import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.user.XiaoMingUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Chuanwise
 */
public class EndMatchFilterMatcher extends RegexFilterMatcher {
    public EndMatchFilterMatcher(Pattern pattern) {
        super(pattern);
    }

    @Override
    public boolean apply(XiaoMingUser user, Message message) {
        final String serialize = message.serialize();
        final Matcher matcher = pattern.matcher(serialize);
        return matcher.find() && matcher.end() == serialize.length();
    }
}

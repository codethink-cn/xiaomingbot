package cn.chuanwise.xiaoming.interactor.filter;

import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.user.XiaoMingUser;

import java.util.regex.Pattern;

public class ContainMatchFilterMatcher extends RegexFilterMatcher {
    public ContainMatchFilterMatcher(Pattern pattern) {
        super(pattern);
    }

    @Override
    public boolean apply(XiaoMingUser user, Message message) {
        return pattern.matcher(message.serialize()).find();
    }
}

package cn.chuanwise.xiaoming.interactor.filter;

import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.user.XiaoMingUser;

public class EqualIgnoreCaseFilterMatcher extends StringFilterMatcher {
    public EqualIgnoreCaseFilterMatcher(String string) {
        super(string);
    }

    @Override
    public boolean apply(XiaoMingUser user, Message message) {
        return string.equalsIgnoreCase(message.serialize());
    }
}

package cn.chuanwise.xiaoming.interactor.filter;

import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.user.XiaoMingUser;

public class EndEqualFilterMatcher extends StringFilterMatcher {
    public EndEqualFilterMatcher(String string) {
        super(string);
    }

    @Override
    public boolean apply(XiaoMingUser user, Message message) {
        return message.serialize().endsWith(string);
    }
}

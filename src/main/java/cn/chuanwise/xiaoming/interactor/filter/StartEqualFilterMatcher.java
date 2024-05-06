package cn.chuanwise.xiaoming.interactor.filter;

import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.user.XiaoMingUser;

public class StartEqualFilterMatcher extends StringFilterMatcher {
    public StartEqualFilterMatcher(String string) {
        super(string);
    }

    @Override
    public boolean apply(XiaoMingUser user, Message message) {
        return message.serialize().startsWith(string);
    }
}
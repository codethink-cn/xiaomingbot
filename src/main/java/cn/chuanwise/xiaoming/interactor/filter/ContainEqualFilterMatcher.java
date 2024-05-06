package cn.chuanwise.xiaoming.interactor.filter;

import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.user.XiaoMingUser;

public class ContainEqualFilterMatcher extends StringFilterMatcher {
    public ContainEqualFilterMatcher(String string) {
        super(string);
    }

    @Override
    public boolean apply(XiaoMingUser user, Message message) {
        return message.serialize().contains(string);
    }
}

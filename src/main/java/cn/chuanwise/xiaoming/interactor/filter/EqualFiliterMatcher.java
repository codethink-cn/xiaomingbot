package cn.chuanwise.xiaoming.interactor.filter;

import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.user.XiaoMingUser;

import java.util.Objects;

public class EqualFiliterMatcher extends StringFilterMatcher {
    public EqualFiliterMatcher(String string) {
        super(string);
    }

    @Override
    public boolean apply(XiaoMingUser user, Message message) {
        return Objects.equals(message.serialize(), string);
    }
}

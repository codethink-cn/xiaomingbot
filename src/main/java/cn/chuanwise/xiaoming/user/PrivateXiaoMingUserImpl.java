package cn.chuanwise.xiaoming.user;

import cn.chuanwise.xiaoming.contact.contact.PrivateContact;
import lombok.Getter;

@Getter
public class PrivateXiaoMingUserImpl extends XiaoMingUserImpl<PrivateContact> implements PrivateXiaoMingUser {
    final PrivateContact contact;

    public PrivateXiaoMingUserImpl(PrivateContact contact) {
        super(contact.getXiaoMingBot(), contact.getCode());
        this.contact = contact;
    }

    @Override
    public long getCode() {
        return contact.getCode();
    }

    @Override
    public String getName() {
        return contact.getName();
    }

    @Override
    public String getCompleteName() {
        return contact.getAliasAndCode();
    }
}

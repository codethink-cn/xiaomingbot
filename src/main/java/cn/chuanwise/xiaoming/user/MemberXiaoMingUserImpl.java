package cn.chuanwise.xiaoming.user;

import cn.chuanwise.xiaoming.contact.contact.MemberContact;
import lombok.Getter;

@Getter
public class MemberXiaoMingUserImpl extends XiaoMingUserImpl<MemberContact> implements MemberXiaoMingUser {
    final MemberContact contact;

    public MemberXiaoMingUserImpl(MemberContact memberContact) {
        super(memberContact.getXiaoMingBot(), memberContact.getCode());
        this.contact = memberContact;
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

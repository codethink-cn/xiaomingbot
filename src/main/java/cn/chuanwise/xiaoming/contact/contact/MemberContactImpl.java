package cn.chuanwise.xiaoming.contact.contact;

import lombok.Getter;
import net.mamoe.mirai.contact.NormalMember;

import java.util.concurrent.TimeUnit;

/**
 * 表示群中的成员。不一定是小明用户
 *
 * @author Chuanwise
 */
@Getter
public class MemberContactImpl extends XiaoMingContactImpl<NormalMember> implements MemberContact {
    final GroupContact groupContact;

    public MemberContactImpl(GroupContact groupContact, NormalMember miraiContact) {
        super(groupContact.getXiaoMingBot(), miraiContact);
        this.groupContact = groupContact;
    }

    @Override
    public NormalMember getMiraiContact() {
        return miraiContact;
    }

    @Override
    public void mute(long timeMillis) {
        miraiContact.mute((int) TimeUnit.MILLISECONDS.toSeconds(timeMillis));
    }

    @Override
    public void flush() {
        getAccount().flush();
    }

    @Override
    public boolean addTag(String tag) {
        return getAccount().addTag(tag);
    }

    @Override
    public boolean hasTag(String tag) {
        return getAccount().hasTag(tag);
    }

    @Override
    public boolean removeTag(String tag) {
        return getAccount().removeTag(tag);
    }
}
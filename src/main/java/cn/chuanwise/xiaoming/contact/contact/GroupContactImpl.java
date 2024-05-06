package cn.chuanwise.xiaoming.contact.contact;

import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import lombok.Getter;
import net.mamoe.mirai.contact.Group;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class GroupContactImpl extends XiaoMingContactImpl<Group> implements GroupContact {
    public GroupContactImpl(XiaoMingBot xiaoMingBot, Group miraiContact) {
        super(xiaoMingBot, miraiContact);
    }

    @Override
    public MemberContact getBotMember() {
        return new MemberContactImpl(this, miraiContact.getBotAsMember());
    }

    @Override
    public MemberContact getOwner() {
        return new MemberContactImpl(this, getMiraiContact().getOwner());
    }

    @Override
    public List<MemberContact> getMembers() {
        return getMiraiContact().getMembers().stream()
                .map(member -> new MemberContactImpl(this, member))
                .collect(Collectors.toList());
    }

    @Override
    public void flush() {
        getGroupInformation().flush();
    }

    @Override
    public boolean addTag(String tag) {
        return getGroupInformation().addTag(tag);
    }

    @Override
    public boolean hasTag(String tag) {
        return getGroupInformation().hasTag(tag);
    }

    @Override
    public boolean removeTag(String tag) {
        return getGroupInformation().removeTag(tag);
    }
}
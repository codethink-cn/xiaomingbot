package cn.chuanwise.xiaoming.contact.contact;

import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import lombok.Getter;
import net.mamoe.mirai.contact.Friend;

@Getter
public class PrivateContactImpl extends XiaoMingContactImpl<Friend> implements PrivateContact {
    public PrivateContactImpl(XiaoMingBot xiaoMingBot, Friend miraiContact) {
        super(xiaoMingBot, miraiContact);
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
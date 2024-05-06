package cn.chuanwise.xiaoming.contact.contact;

import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.util.Tags;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.event.MessageEvent;
import cn.chuanwise.xiaoming.user.ConsoleXiaoMingUser;
import net.mamoe.mirai.contact.Friend;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public interface ConsoleContact extends XiaoMingContact<Friend> {
    @Override
    default Friend getMiraiContact() {
        return getXiaoMingBot().getMiraiBot().getAsFriend();
    }

    @Override
    default String getName() {
        return "后台";
    }

    @Override
    default String getAvatarUrl() {
        return getMiraiContact().getAvatarUrl();
    }

    @Override
    default String getAlias() {
        return "后台";
    }

    @Override
    default String getAliasAndCode() {
        return "后台";
    }

    @Override
    default Optional<Message> nextMessage(long timeout, Predicate<Message> filter) throws InterruptedException {
        return getXiaoMingBot().getContactManager()
                .nextMessageEvent(timeout, messageEvent -> messageEvent.getUser() instanceof ConsoleXiaoMingUser && filter.test(messageEvent.getMessage()))
                .map(MessageEvent::getMessage);
    }

    @Override
    default Set<String> getTags() {
        return CollectionUtil.asSet(Tags.ALL, "console");
    }
}

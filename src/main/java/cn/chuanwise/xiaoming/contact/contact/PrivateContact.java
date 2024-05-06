package cn.chuanwise.xiaoming.contact.contact;

import cn.chuanwise.xiaoming.account.Account;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.event.MessageEvent;
import cn.chuanwise.xiaoming.user.PrivateXiaoMingUser;
import net.mamoe.mirai.contact.Friend;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public interface PrivateContact extends XiaoMingContact<Friend> {
    @Override
    default Optional<Message> nextMessage(long timeout, Predicate<Message> filter) throws InterruptedException {
        return getXiaoMingBot()
                .getContactManager()
                .nextMessageEvent(timeout,
                        x -> x.getUser() instanceof PrivateXiaoMingUser
                                && x.getUser().getCode() == getCode()
                                && filter.test(x.getMessage()))
                .map(MessageEvent::getMessage);
    }

    default Account getAccount() {
        return getXiaoMingBot().getAccountManager().createAccount(getCode());
    }

    @Override
    default String getAvatarUrl() {
        return getMiraiContact().getAvatarUrl();
    }

    @Override
    default String getName() {
        return getMiraiContact().getNick();
    }

    @Override
    default String getAlias() {
        final Account account = getAccount();
        return Objects.nonNull(account) ? account.getAlias() : getName();
    }

    @Override
    default String getAliasAndCode() {
        return getAlias() + "（" + getCodeString() + "）";
    }

    default String getRemark() {
        return getMiraiContact().getRemark();
    }

    default void delete() {
        getMiraiContact().delete();
    }

    default void nudge() {
        getMiraiContact().nudge();
    }

    default PrivateXiaoMingUser getUser() {
        return getXiaoMingBot().getReceptionistManager().getReceptionist(getCode()).getPrivateXiaoMingUser().orElseThrow(NoSuchElementException::new);
    }

    @Override
    default Set<String> getTags() {
        return getXiaoMingBot().getAccountManager().getTags(getCode());
    }
}

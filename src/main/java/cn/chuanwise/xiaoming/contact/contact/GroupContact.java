package cn.chuanwise.xiaoming.contact.contact;

import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.event.MessageEvent;
import cn.chuanwise.xiaoming.group.GroupInformation;
import cn.chuanwise.xiaoming.group.GroupInformationManager;
import cn.chuanwise.xiaoming.user.GroupXiaoMingUser;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.GroupSettings;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public interface GroupContact extends XiaoMingContact<Group> {
    default Optional<Message> atSend(long code, String message) {
        return atSend(code, MiraiCode.deserializeMiraiCode(getXiaoMingBot().getLanguageManager().format(message)));
    }

    default Optional<Message> atSend(long code, MessageChain messages) {
        return sendMessage(new At(code).plus(" ").plus(messages));
    }

    default Optional<Message> atSend(long code, Message messages) {
        return atSend(code, messages.getMessageChain());
    }

    default GroupInformation getGroupInformation() {
        final GroupInformationManager manager = getXiaoMingBot().getGroupInformationManager();

        return manager.getGroupInformation(getCode())
                .orElseGet(() -> manager.addGroupInformation(getCode()));
    }

    @Override
    default Optional<Message> nextMessage(long timeout, Predicate<Message> filter) throws InterruptedException {
        return getXiaoMingBot()
                .getContactManager()
                .nextMessageEvent(timeout,
                        x -> x.getUser() instanceof GroupXiaoMingUser
                                && ((GroupXiaoMingUser) x.getUser()).getGroupCode() == getCode()
                                && filter.test(x.getMessage()))
                .map(MessageEvent::getMessage);
    }

    @Override
    default String getAliasAndCode() {
        return getXiaoMingBot().getGroupInformationManager().getAliasAndCode(getCode());
    }

    @Override
    default String getAvatarUrl() {
        return getMiraiContact().getAvatarUrl();
    }

    @Override
    default String getName() {
        return getMiraiContact().getName();
    }

    default void setName(String name) {
        getMiraiContact().setName(name);
    }

    @Override
    default String getAlias() {
        return getXiaoMingBot().getGroupInformationManager()
                .getAlias(getCode())
                .orElseGet(this::getName);
    }

    default Optional<Message> atReply(Message quote, long target, MessageChain messages) {
        return replyMessage(quote, new At(target).plus(" ").plus(messages));
    }

    default Optional<Message> atReply(Message quote, long target, String message) {
        return atReply(quote, target, MiraiCode.deserializeMiraiCode(message));
    }

    default Optional<Message> atReply(Message quote, long target, Message message) {
        return atReply(quote, target, message.getMessageChain());
    }

    /**
     * 获得群成员信息
     *
     * @param qq 群成员 QQ
     * @return 群成员信息。如果没有找到，返回 null
     */
    default Optional<MemberContact> getMember(long qq) {
        return getXiaoMingBot().getContactManager().getMemberContact(getCode(), qq);
    }

    MemberContact getBotMember();

    MemberContact getOwner();

    List<MemberContact> getMembers();

    default boolean quit() {
        return getMiraiContact().quit();
    }

    default GroupSettings getSettings() {
        return getMiraiContact().getSettings();
    }

    @Override
    default Set<String> getTags() {
        return getXiaoMingBot().getGroupInformationManager().getTags(getCode());
    }
}

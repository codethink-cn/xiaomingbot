package cn.chuanwise.xiaoming.contact;

import cn.chuanwise.annotation.Checked;
import cn.chuanwise.util.Functions;
import cn.chuanwise.xiaoming.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.contact.contact.MemberContact;
import cn.chuanwise.xiaoming.contact.contact.PrivateContact;
import cn.chuanwise.xiaoming.contact.contact.XiaoMingContact;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.event.MessageEvent;
import cn.chuanwise.xiaoming.event.SendMessageEvent;
import cn.chuanwise.xiaoming.group.GroupInformation;
import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.xiaoming.user.GroupXiaoMingUser;
import cn.chuanwise.xiaoming.user.MemberXiaoMingUser;
import cn.chuanwise.xiaoming.user.PrivateXiaoMingUser;
import cn.chuanwise.xiaoming.user.XiaoMingUser;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface ContactManager extends ModuleObject {
    List<MessageEvent> getRecentMessageEvents();

    PrivateContact getBotPrivateContact();

    Optional<PrivateContact> getPrivateContact(long code);

    Optional<GroupContact> getGroupContact(long code);

    Optional<MemberContact> getMemberContact(long groupCode, long accountCode);

    void onNextMessageEvent(MessageEvent messageEvent);

    Optional<MessageEvent> nextMessageEvent(long timeout, Predicate<MessageEvent> filter) throws InterruptedException;

    List<SendMessageEvent> getRecentSentMessageEvents();

    List<SendMessageEvent> getSendMessageList();

    Future<Optional<Message>> readyToSend(SendMessageEvent event);

    default Optional<MessageEvent> nextMessageEvent(long timeout) throws InterruptedException {
        return nextMessageEvent(timeout, Functions.predicateTrue());
    }

    default Optional<MessageEvent> nextGroupMessage(long code, long timeout) throws InterruptedException {
        return nextMessageEvent(timeout, messageEvent -> {
            final XiaoMingUser user = messageEvent.getUser();
            return user instanceof GroupXiaoMingUser && ((GroupXiaoMingUser) user).getGroupCode() == code;
        });
    }

    default Optional<MessageEvent> nextGroupMessage(String tag, long timeout) throws InterruptedException {
        return nextMessageEvent(timeout, messageEvent -> {
            final XiaoMingUser user = messageEvent.getUser();
            return user instanceof GroupXiaoMingUser && ((GroupXiaoMingUser) user).getContact().hasTags(tag);
        });
    }

    default Optional<MessageEvent> nextGroupMemberMessage(long groupCode, long accountCode, long timeout) throws InterruptedException {
        return nextMessageEvent(timeout, messageEvent -> {
            final XiaoMingUser user = messageEvent.getUser();
            return user instanceof GroupXiaoMingUser && ((GroupXiaoMingUser) user).getGroupCode() == groupCode && user.getCode() == accountCode;
        });
    }

    default Optional<MessageEvent> nextGroupMemberMessage(String groupTag, String accountTag, long timeout) throws InterruptedException {
        return nextMessageEvent(timeout, messageEvent -> {
            final XiaoMingUser user = messageEvent.getUser();
            return user instanceof GroupXiaoMingUser && ((GroupXiaoMingUser) user).getContact().hasTag(groupTag) && user.hasTag(accountTag);
        });
    }

    default Optional<MessageEvent> nextGroupMemberMessage(long groupCode, String accountTag, long timeout) throws InterruptedException {
        return nextMessageEvent(timeout, messageEvent -> {
            final XiaoMingUser user = messageEvent.getUser();
            return user instanceof GroupXiaoMingUser && ((GroupXiaoMingUser) user).getGroupCode() == groupCode && user.hasTag(accountTag);
        });
    }

    default Optional<MessageEvent> nextGroupMemberMessage(String groupTag, long accountCode, long timeout) throws InterruptedException {
        return nextMessageEvent(timeout, messageEvent -> {
            final XiaoMingUser user = messageEvent.getUser();
            return user instanceof GroupXiaoMingUser && ((GroupXiaoMingUser) user).getContact().hasTag(groupTag) && user.getCode() == accountCode;
        });
    }

    default Optional<MessageEvent> nextPrivateMessage(long code, long timeout) throws InterruptedException {
        return nextMessageEvent(timeout, messageEvent -> {
            final XiaoMingUser user = messageEvent.getUser();
            return user instanceof PrivateXiaoMingUser && user.getCode() == code;
        });
    }

    default Optional<MessageEvent> nextPrivateMessage(String tag, long timeout) throws InterruptedException {
        return nextMessageEvent(timeout, messageEvent -> {
            final XiaoMingUser user = messageEvent.getUser();
            return user instanceof PrivateXiaoMingUser && user.hasTag(tag);
        });
    }

    default Optional<MessageEvent> nextMemberMessage(long code, long timeout) throws InterruptedException {
        return nextMessageEvent(timeout, messageEvent -> {
            final XiaoMingUser user = messageEvent.getUser();
            return user instanceof MemberXiaoMingUser && ((MemberXiaoMingUser) user).getGroupCode() == code;
        });
    }

    default Optional<MessageEvent> nextMemberMessage(String tag, long timeout) throws InterruptedException {
        return nextMessageEvent(timeout, messageEvent -> {
            final XiaoMingUser user = messageEvent.getUser();
            return user instanceof MemberXiaoMingUser && ((MemberXiaoMingUser) user).getGroupContact().hasTag(tag);
        });
    }

    default Optional<Message> sendGroupMessage(long group, String message, Object... arguments) {
        message = getXiaoMingBot().getLanguageManager().formatAdditional(message, Functions.nullFunction(), arguments);

        final String finalMessage = message;
        return getGroupContact(group).flatMap(contact -> contact.sendMessage(finalMessage));
    }

    default Optional<Message> sendGroupMessage(long group, MessageChain messages) {
        return getGroupContact(group).flatMap(contact -> contact.sendMessage(messages));
    }

    default List<Message> sendGroupMessage(String tag, String message, Object... arguments) {
        message = getXiaoMingBot().getLanguageManager().formatAdditional(message, Functions.nullFunction(), arguments);

        final String finalMessage = message;
        return getXiaoMingBot().getGroupInformationManager()
                .searchGroupsByTag(tag)
                .stream()
                .map(GroupInformation::getContact)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(contact -> contact.sendMessage(finalMessage))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    default List<Message> sendGroupMessage(String tag, MessageChain messages) {
        return getXiaoMingBot().getGroupInformationManager()
                .searchGroupsByTag(tag)
                .stream()
                .map(GroupInformation::getContact)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(contact -> contact.sendMessage(messages))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    default Optional<Message> sendPrivateMessage(long code, String message, Object... arguments) {
        final String finalMessage = getXiaoMingBot()
                .getLanguageManager()
                .formatAdditional(message, Functions.nullFunction(), arguments);

        return getPrivateContact(code)
                .flatMap(contact -> contact.sendMessage(finalMessage));
    }

    default Optional<Message> sendPrivateMessage(long code, MessageChain messages) {
        return getPrivateContact(code)
                .flatMap(contact -> contact.sendMessage(messages));
    }

    default Optional<Message> sendMemberMessage(long group, long code, String message, Object... arguments) {
        final String finalMessage = getXiaoMingBot().getLanguageManager().formatAdditional(message, Functions.nullFunction(), arguments);

        return getGroupContact(group)
                .flatMap(contact -> contact.getMember(code))
                .flatMap(member -> member.sendMessage(finalMessage));
    }

    default Optional<Message> sendMemberMessage(long group, long code, MessageChain messages) {
        return getGroupContact(group)
                .flatMap(contact -> contact.getMember(code))
                .flatMap(member -> member.sendMessage(messages));
    }

    List<MemberContact> getMemberContactPossibly(long code);

    List<XiaoMingContact> getPrivateContactPossibly(long code);

    List<GroupContact> getGroupContacts();

    List<PrivateContact> getPrivateContacts();

    default Optional<XiaoMingContact> sendPrivateMessagePossibly(long code, MessageChain messageChain) {
        // try send private message
        final Optional<PrivateContact> optionalPrivateContact = getPrivateContact(code);
        if (optionalPrivateContact.isPresent()) {
            final PrivateContact privateContact = optionalPrivateContact.get();
            try {
                privateContact.sendMessage(messageChain);
                return Optional.of(privateContact);
            } catch (Exception ignored) {
            }
        }

        // try send group temp message
        for (GroupContact groupContact : getGroupContacts()) {
            final Optional<MemberContact> optionalMemberContact = groupContact.getMember(code);
            if (optionalMemberContact.isPresent()) {
                final MemberContact memberContact = optionalMemberContact.get();
                try {
                    memberContact.sendMessage(messageChain);
                    return Optional.of(memberContact);
                } catch (Exception ignored) {
                }
            }
        }

        return Optional.empty();
    }

    @Checked
    default Optional<XiaoMingContact> sendPrivateMessagePossibly(long code, String message, Object... arguments) {
        final String finalMessage = getXiaoMingBot().getLanguageManager().formatAdditional(message, Functions.nullFunction(), arguments);
        final MessageChain messageChain = MiraiCode.deserializeMiraiCode(finalMessage);

        return sendPrivateMessagePossibly(code, messageChain);
    }

    default Optional<GroupContact> sendGroupAtMessagePossibly(long code, MessageChain messageChain) {
        final MessageChain messages = new At(code).plus(" ").plus(messageChain);

        // try send group at message
        for (GroupContact groupContact : getGroupContacts()) {
            final Optional<MemberContact> optionalMemberContact = groupContact.getMember(code);
            if (optionalMemberContact.isPresent()) {
                try {
                    groupContact.sendMessage(messages);
                    return Optional.of(groupContact);
                } catch (Exception ignored) {
                }
            }
        }
        return Optional.empty();
    }

    default Optional<GroupContact> sendGroupAtMessagePossibly(long code, String message, Object... arguments) {
        final String finalMessage = getXiaoMingBot().getLanguageManager().formatAdditional(message, Functions.nullFunction(), arguments);
        final MessageChain messageChain = MiraiCode.deserializeMiraiCode(finalMessage);

        return sendGroupAtMessagePossibly(code, messageChain);
    }

    default Optional<XiaoMingContact> sendMessagePossibly(long code, MessageChain messageChain) {
        // try to send private message
        final Optional<XiaoMingContact> optionalXiaoMingContact = sendPrivateMessagePossibly(code, messageChain);
        if (optionalXiaoMingContact.isPresent()) {
            return optionalXiaoMingContact;
        }

        // try to send group msg
        messageChain = new At(code).plus(" ").plus(messageChain);
        for (GroupContact groupContact : getGroupContacts()) {
            final Optional<MemberContact> optionalMemberContact = groupContact.getMember(code);
            if (optionalMemberContact.isPresent()) {
                final MemberContact memberContact = optionalMemberContact.get();
                try {
                    memberContact.sendMessage(messageChain);
                    return Optional.of(memberContact);
                } catch (Exception ignored) {
                }
            }
        }

        return Optional.empty();
    }

    default Optional<XiaoMingContact> sendMessagePossibly(long code, String message, Object... arguments) {
        final String finalMessage = getXiaoMingBot().getLanguageManager().formatAdditional(message, Functions.nullFunction(), arguments);
        final MessageChain messageChain = MiraiCode.deserializeMiraiCode(finalMessage);
        return sendMessagePossibly(code, messageChain);
    }
}

package cn.chuanwise.xiaoming.contact;

import cn.chuanwise.toolkit.box.Box;
import cn.chuanwise.toolkit.sized.SizedCopyOnWriteArrayList;
import cn.chuanwise.util.ObjectUtil;
import cn.chuanwise.util.Preconditions;
import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import cn.chuanwise.xiaoming.contact.contact.*;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.contact.message.MessageImpl;
import cn.chuanwise.xiaoming.event.MessageEvent;
import cn.chuanwise.xiaoming.event.SendMessageEvent;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
import lombok.AccessLevel;
import lombok.Getter;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.OnlineMessageSource;
import net.mamoe.mirai.message.data.PlainText;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
public class ContactManagerImpl extends ModuleObjectImpl implements ContactManager {
    final List<MessageEvent> recentMessageEvents;

    final List<SendMessageEvent> recentSentMessageEvents;

    final List<SendMessageEvent> sendMessageList = new CopyOnWriteArrayList<>();

    final Box<Thread> sendMessageLoop = Box.empty();
    final PrivateContact botPrivateContact;
    @Getter(AccessLevel.NONE)
    final Object recentMessageConditionalVariable = new Object();

    public ContactManagerImpl(XiaoMingBot xiaoMingBot) {
        super(xiaoMingBot);
        recentMessageEvents = Collections.synchronizedList(new SizedCopyOnWriteArrayList<>(xiaoMingBot.getConfiguration().getMaxRecentMessageBufferSize()));
        recentSentMessageEvents = Collections.synchronizedList(new SizedCopyOnWriteArrayList<>(xiaoMingBot.getConfiguration().getMaxRecentMessageBufferSize()));

        botPrivateContact = new PrivateContactImpl(xiaoMingBot, xiaoMingBot.getMiraiBot().getAsFriend());
    }

    @Override
    public Future<Optional<Message>> readyToSend(SendMessageEvent event) {
        recentSentMessageEvents.add(event);
        sendMessageList.add(event);

        sendMessageLoop.ifEmpty(() -> getXiaoMingBot().getScheduler().run(() -> {
            try {
                sendMessageLoop.set(Thread.currentThread());

                while (!sendMessageList.isEmpty()) {
                    final SendMessageEvent cursor = sendMessageList.remove(0);
                    final MessageChain messageChain = cursor.getMessageChain();

                    try {
                        if (!cursor.isCancelled()) {
                            final XiaoMingContact contact = cursor.getTarget();
                            MessageReceipt<?> messageReceipt = null;
                            final String forwardGroupTag = xiaoMingBot.getConfiguration().getForwardGroupTag();

                            if (contact instanceof GroupContact) {
                                // group send
                                if (xiaoMingBot.getConfiguration().isEnableGroupSend()) {
                                    messageReceipt = contact.getMiraiContact().sendMessage(messageChain);
                                }
                                // member send
                                if (Objects.isNull(messageReceipt) && xiaoMingBot.getConfiguration().isEnableMemberSend()) {
                                    final long accountCode = contact.getCode();
                                    final List<MemberContact> contacts = getMemberContactPossibly(accountCode);
                                    final MessageChain messages = new PlainText("【群聊 " + contact.getAliasAndCode() + " 发送转临时会话发送】").plus(messageChain);
                                    final Optional<MessageReceipt<?>> optionalMessageReceipt = sendMessagePossibly(contacts, messages);
                                    if (optionalMessageReceipt.isPresent()) {
                                        messageReceipt = optionalMessageReceipt.get();
                                    }
                                }
                                // private send
                                if (Objects.isNull(messageReceipt) && xiaoMingBot.getConfiguration().isEnablePrivateSend()) {
                                    final long accountCode = contact.getCode();
                                    final Optional<PrivateContact> optionalPrivateContact = getPrivateContact(accountCode);
                                    if (optionalPrivateContact.isPresent()) {
                                        final PrivateContact privateContact = optionalPrivateContact.get();
                                        final MessageChain messages = new PlainText("【群聊 " + contact.getAliasAndCode() + " 发送转私聊发送】").plus(messageChain);
                                        messageReceipt = privateContact.getMiraiContact().sendMessage(messages);
                                    }
                                }
                            }
                            if (contact instanceof MemberContact) {
                                // member send
                                if (xiaoMingBot.getConfiguration().isEnableMemberSend()) {
                                    messageReceipt = contact.getMiraiContact().sendMessage(messageChain);
                                }
                                // group send
                                if (Objects.isNull(messageReceipt) && xiaoMingBot.getConfiguration().isEnableGroupSend()) {
                                    final MessageChain groupAtMessage = new At(contact.getCode()).plus(" 【临时会话 " + contact.getAliasAndCode() + " 发送转群聊发送】").plus(messageChain);

                                    // try group send
                                    final GroupContact groupContact = ((MemberContact) contact).getGroupContact();
                                    try {
                                        if (groupContact.hasTag(forwardGroupTag)) {
                                            messageReceipt = groupContact.getMiraiContact().sendMessage(groupAtMessage);
                                        }
                                    } catch (Exception ignored) {
                                    }

                                    if (Objects.isNull(messageReceipt)) {
                                        final long accountCode = contact.getCode();
                                        final List<GroupContact> contacts = getMemberContactPossibly(accountCode)
                                                .stream()
                                                .filter(x -> x.getGroupInformation().hasTag(forwardGroupTag))
                                                .map(MemberContact::getGroupContact)
                                                .collect(Collectors.toList());
                                        final Optional<MessageReceipt<?>> optionalMessageReceipt = sendMessagePossibly(contacts, groupAtMessage);
                                        if (optionalMessageReceipt.isPresent()) {
                                            messageReceipt = optionalMessageReceipt.get();
                                        }
                                    }
                                }
                                // private send
                                if (Objects.isNull(messageReceipt) && xiaoMingBot.getConfiguration().isEnablePrivateSend()) {
                                    final long accountCode = contact.getCode();
                                    final Optional<PrivateContact> optionalPrivateContact = getPrivateContact(accountCode);
                                    if (optionalPrivateContact.isPresent()) {
                                        final PrivateContact privateContact = optionalPrivateContact.get();
                                        final MessageChain messages = new PlainText("【群聊 " + contact.getAliasAndCode() + " 发送转私聊发送】").plus(messageChain);
                                        messageReceipt = privateContact.getMiraiContact().sendMessage(messages);
                                    }
                                }
                            }
                            if (contact instanceof PrivateContact) {
                                // private send
                                if (xiaoMingBot.getConfiguration().isEnablePrivateSend()) {
                                    messageReceipt = contact.getMiraiContact().sendMessage(messageChain);
                                }
                                // member send
                                if (Objects.isNull(messageReceipt) && xiaoMingBot.getConfiguration().isEnableMemberSend()) {
                                    final long accountCode = contact.getCode();
                                    final List<MemberContact> contacts = getMemberContactPossibly(accountCode);
                                    final MessageChain messages = new PlainText("【私聊发送转临时会话发送】").plus(messageChain);
                                    final Optional<MessageReceipt<?>> optionalMessageReceipt = sendMessagePossibly(contacts, messages);
                                    if (optionalMessageReceipt.isPresent()) {
                                        messageReceipt = optionalMessageReceipt.get();
                                    }
                                }
                                // group send
                                if (Objects.isNull(messageReceipt) && xiaoMingBot.getConfiguration().isEnableGroupSend()) {
                                    final MessageChain groupAtMessage = new At(contact.getCode()).plus(" 【私聊发送转群聊发送】").plus(messageChain);

                                    final List<GroupContact> groupContacts = getMemberContactPossibly(contact.getCode())
                                            .stream()
                                            .filter(x -> x.getGroupInformation().hasTag(forwardGroupTag))
                                            .map(MemberContact::getGroupContact).collect(Collectors.toList());
                                    final Optional<MessageReceipt<?>> optionalMessageReceipt = sendMessagePossibly(groupContacts, groupAtMessage);
                                    if (optionalMessageReceipt.isPresent()) {
                                        messageReceipt = optionalMessageReceipt.get();
                                    }
                                }
                            }

                            Preconditions.state(Objects.nonNull(messageReceipt), "无法向 " + contact.getName() + " 发送消息：" + messageChain.serializeToMiraiCode());

                            final OnlineMessageSource.Outgoing source = messageReceipt.getSource();
                            cursor.getMessageBox().set(new MessageImpl(xiaoMingBot, source.getOriginalMessage(), source.getTime()));
                        }
                    } catch (Exception exception) {
                        event.setCause(exception);
                    }

                    synchronized (cursor) {
                        cursor.notifyAll();
                    }

                    try {
                        Thread.sleep(Math.max(getXiaoMingBot().getConfiguration().getSendMessagePeriod(), 0));
                    } catch (InterruptedException exception) {
                        getLogger().error("发送消息循环被打断", exception);
                    }
                }
            } finally {
                sendMessageLoop.clear();
            }
        }));

        return getXiaoMingBot().getScheduler().run(() -> {
            final Box<Message> messageBox = event.getMessageBox();
            if (messageBox.isPresent()) {
                return messageBox.toOptional();
            } else {
                return Optional.ofNullable(messageBox.nextValue());
            }
        });
    }

    @Override
    public List<SendMessageEvent> getSendMessageList() {
        return Collections.unmodifiableList(sendMessageList);
    }

    @Override
    public List<SendMessageEvent> getRecentSentMessageEvents() {
        return Collections.unmodifiableList(recentSentMessageEvents);
    }

    @Override
    public List<MessageEvent> getRecentMessageEvents() {
        return Collections.unmodifiableList(recentMessageEvents);
    }

    @Override
    public Optional<MessageEvent> nextMessageEvent(long timeout, Predicate<MessageEvent> filter) throws InterruptedException {
        if (ObjectUtil.waitUtil(recentMessageConditionalVariable, timeout, () -> filter.test(recentMessageEvents.get(recentMessageEvents.size() - 1)))) {
            return Optional.of(recentMessageEvents.get(recentMessageEvents.size() - 1));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<PrivateContact> getPrivateContact(long code) {
        return Optional.ofNullable(getXiaoMingBot().getMiraiBot().getFriend(code))
                .map(contact -> new PrivateContactImpl(xiaoMingBot, contact));
    }

    @Override
    public Optional<GroupContact> getGroupContact(long code) {
        return Optional.ofNullable(getXiaoMingBot().getMiraiBot().getGroup(code))
                .map(contact -> new GroupContactImpl(xiaoMingBot, contact));
    }

    @Override
    public Optional<MemberContact> getMemberContact(long groupCode, long accountCode) {
        final Optional<GroupContact> optionalGroupContact = getGroupContact(groupCode);
        if (!optionalGroupContact.isPresent()) {
            return Optional.empty();
        }
        final GroupContact groupContact = optionalGroupContact.get();
        return Optional.ofNullable(groupContact.getMiraiContact().get(accountCode))
                .map(x -> new MemberContactImpl(groupContact, x));
    }

    @Override
    public void onNextMessageEvent(MessageEvent messageEvent) {
        recentMessageEvents.add(messageEvent);
        synchronized (recentMessageConditionalVariable) {
            recentMessageConditionalVariable.notifyAll();
        }
    }

    @Override
    public List<GroupContact> getGroupContacts() {
        return xiaoMingBot.getMiraiBot()
                .getGroups()
                .stream()
                .map(contact -> new GroupContactImpl(xiaoMingBot, contact))
                .collect(Collectors.toList());
    }

    @Override
    public List<PrivateContact> getPrivateContacts() {
        return xiaoMingBot.getMiraiBot()
                .getFriends()
                .stream()
                .map(contact -> new PrivateContactImpl(xiaoMingBot, contact))
                .collect(Collectors.toList());
    }

    @Override
    public List<XiaoMingContact> getPrivateContactPossibly(long code) {
        final List<XiaoMingContact> results = new ArrayList<>();

        // find user in private contact
        getPrivateContact(code).ifPresent(results::add);

        // iterate all groups and get this member
        final List<MemberContact> memberContacts = getGroupContacts()
                .stream()
                .map(contact -> contact.getMember(code))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        results.addAll(memberContacts);

        return Collections.unmodifiableList(results);
    }

    @Override
    public List<MemberContact> getMemberContactPossibly(long code) {
        return Collections.unmodifiableList(getGroupContacts().stream()
                .map(contact -> contact.getMember(code))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList()));
    }

    private <T extends XiaoMingContact<?>> Optional<MessageReceipt<?>> sendMessagePossibly(List<T> contacts, MessageChain messages) {
        for (T contact : contacts) {
            try {
                return Optional.of(contact.getMiraiContact().sendMessage(messages));
            } catch (Exception ignored) {
            }
        }

        return Optional.empty();
    }
}

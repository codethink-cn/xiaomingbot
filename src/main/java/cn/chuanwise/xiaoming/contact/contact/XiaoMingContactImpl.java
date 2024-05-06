package cn.chuanwise.xiaoming.contact.contact;

import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.event.SendMessageEvent;
import cn.chuanwise.xiaoming.object.XiaoMingObjectImpl;
import lombok.Getter;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.Objects;
import java.util.Optional;

@Getter
public abstract class XiaoMingContactImpl<C extends Contact> extends XiaoMingObjectImpl implements XiaoMingContact<C> {
    final C miraiContact;

    public XiaoMingContactImpl(XiaoMingBot xiaoMingBot, C miraiContact) {
        super(xiaoMingBot);
        this.miraiContact = miraiContact;
    }

    @Override
    public Optional<Message> sendMessage(MessageChain messages) {
        final SendMessageEvent event = new SendMessageEvent(this, messages);

        xiaoMingBot.getEventManager().callEvent(event);

        final Throwable cause = event.getCause();
        if (Objects.nonNull(cause)) {
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else {
                throw new RuntimeException("无法发送消息", cause);
            }
        }

        if (event.isCancelled()) {
            return Optional.empty();
        } else {
            return event.getMessageBox().toOptional();
        }
    }
}

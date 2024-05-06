package cn.chuanwise.xiaoming.event;

import cn.chuanwise.toolkit.box.Box;
import cn.chuanwise.xiaoming.contact.contact.XiaoMingContact;
import cn.chuanwise.xiaoming.contact.message.Message;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.mamoe.mirai.message.data.MessageChain;

@EqualsAndHashCode(callSuper = true)
@Data
public class SendMessageEvent
        extends SimpleXiaoMingCancellableEvent {
    final XiaoMingContact target;
    final MessageChain messageChain;
    final long time = System.currentTimeMillis();
    final Box<Message> messageBox = Box.empty();
    boolean MessageChainChanged;
    Throwable cause;

    public SendMessageEvent(XiaoMingContact target, MessageChain messageChain) {
        this.target = target;
        this.messageChain = messageChain;
        this.MessageChainChanged = false;
    }

    public SendMessageEvent(XiaoMingContact target, MessageChain messageChain, boolean isMessageChainChanged) {
        this.target = target;
        this.messageChain = messageChain;
        this.MessageChainChanged = isMessageChainChanged;
    }
}
package cn.chuanwise.xiaoming.contact.message;

import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import cn.chuanwise.xiaoming.object.XiaoMingObjectImpl;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.message.data.MessageChain;

@EqualsAndHashCode(callSuper = false)
public class MessageImpl extends XiaoMingObjectImpl implements Message {
    @Getter
    final int[] internalMessageCode, messageCode;
    @Getter
    final long time;

    @Getter
    MessageChain messageChain;
    String serializedMessageChain;

    @Getter
    MessageChain originalMessageChain;
    String serializedOriginalMessageChain;

    public MessageImpl(XiaoMingBot xiaoMingBot, MessageChain messageChain) {
        this(xiaoMingBot, messageChain, System.currentTimeMillis());
    }

    public MessageImpl(XiaoMingBot xiaoMingBot, MessageChain messageChain, long time) {
        this(xiaoMingBot, messageChain, null, null, time);
    }

    public MessageImpl(XiaoMingBot xiaoMingBot,
                       MessageChain messageChain,
                       int[] messageCode,
                       int[] internalMessageCode,
                       long time) {
        setXiaoMingBot(xiaoMingBot);
        setMessageChain(messageChain);
        setOriginalMessageChain(messageChain);
        this.time = time;

        this.messageCode = messageCode;
        this.internalMessageCode = internalMessageCode;
    }

    @Override
    public void setMessageChain(MessageChain messageChain) {
        this.messageChain = messageChain;
        serializedMessageChain = messageChain.serializeToMiraiCode();
    }

    @Override
    public void setOriginalMessageChain(MessageChain originalMessageChain) {
        this.originalMessageChain = originalMessageChain;
        serializedOriginalMessageChain = originalMessageChain.serializeToMiraiCode();
    }

    @Override
    public String serialize() {
        return serializedMessageChain;
    }

    @Override
    public String serializeOriginalMessage() {
        return serializedOriginalMessageChain;
    }

    @Override
    public String toString() {
        return serializedMessageChain;
    }
}
package cn.chuanwise.xiaoming.user;

import cn.chuanwise.xiaoming.contact.contact.ConsoleContact;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.contact.message.MessageImpl;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.Optional;

/**
 * @author Chuanwise
 */
@Getter
public class ConsoleXiaoMingUserImpl extends XiaoMingUserImpl<ConsoleContact> implements ConsoleXiaoMingUser {
    final ConsoleContact contact;
    @Setter
    long code;

    public ConsoleXiaoMingUserImpl(ConsoleContact contact) {
        super(contact.getXiaoMingBot(), 0);
        this.contact = contact;
    }


    @Override
    public String getCompleteName() {
        return "后台";
    }

    @Override
    public Optional<Message> sendPrivateMessage(String message, Object... arguments) {
        sendMessage(message, arguments);
        final MessageChain messages = MiraiCode.deserializeMiraiCode(format(message, arguments));
        return Optional.of(new MessageImpl(xiaoMingBot, messages));
    }
}

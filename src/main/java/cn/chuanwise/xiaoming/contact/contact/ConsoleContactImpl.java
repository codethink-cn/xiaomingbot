package cn.chuanwise.xiaoming.contact.contact;

import cn.chuanwise.exception.IllegalOperationException;
import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.contact.message.MessageImpl;
import lombok.Getter;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.Optional;

@Getter
public class ConsoleContactImpl
        extends XiaoMingContactImpl<Friend>
        implements ConsoleContact {

    public ConsoleContactImpl(XiaoMingBot xiaoMingBot) {
        super(xiaoMingBot, xiaoMingBot.getMiraiBot().getAsFriend());
    }

    @Override
    public Optional<Message> sendMessage(MessageChain messages) {
        xiaoMingBot.getConsoleXiaoMingUser().getLogger().info(messages.serializeToMiraiCode());
        return Optional.of(new MessageImpl(xiaoMingBot, messages));
    }

    @Override
    public void flush() {
    }

    @Override
    public boolean addTag(String tag) {
        throw new IllegalOperationException();
    }

    @Override
    public boolean hasTag(String tag) {
        throw new IllegalOperationException();
    }

    @Override
    public boolean removeTag(String tag) {
        throw new IllegalOperationException();
    }
}
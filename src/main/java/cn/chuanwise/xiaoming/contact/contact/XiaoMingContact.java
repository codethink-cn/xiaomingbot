package cn.chuanwise.xiaoming.contact.contact;

import cn.chuanwise.api.OriginalTagMarkable;
import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.util.Functions;
import cn.chuanwise.util.Tags;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.language.LanguageManager;
import cn.chuanwise.xiaoming.message.MessageSendable;
import cn.chuanwise.xiaoming.object.XiaoMingObject;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.utils.ExternalResource;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public interface XiaoMingContact<C extends Contact>
        extends XiaoMingObject, OriginalTagMarkable, MessageSendable<Optional<Message>> {
    String getAliasAndCode();

    C getMiraiContact();

    default long getCode() {
        return getMiraiContact().getId();
    }

    default String getCodeString() {
        return String.valueOf(getCode());
    }

    String getName();

    String getAlias();

    String getAvatarUrl();

    @Override
    Optional<Message> sendMessage(MessageChain messages);

    default Optional<Message> nextMessage(long timeout) throws InterruptedException {
        return nextMessage(timeout, Functions.predicateTrue());
    }

    Optional<Message> nextMessage(long timeout, Predicate<Message> filter) throws InterruptedException;

    default Optional<Message> nextMessage(Predicate<Message> filter) throws InterruptedException {
        return nextMessage(getXiaoMingBot().getConfiguration().getMaxUserInputTimeout(), filter);
    }

    default Optional<Message> nextMessage() throws InterruptedException {
        return nextMessage(getXiaoMingBot().getConfiguration().getMaxUserInputTimeout());
    }

    default Image uploadImage(ExternalResource resource) {
        return getMiraiContact().uploadImage(resource);
    }

    @Override
    default Set<String> getOriginalTags() {
        return CollectionUtil.asSet(getCodeString(), Tags.ALL);
    }

    @Override
    default String format(String format, Object... contexts) {
        final LanguageManager languageManager = getXiaoMingBot().getLanguageManager();

        // 替换 Language 中的字句
        return languageManager.formatAdditional(format, variable -> {
            if (Objects.equals(variable, "contact")) {
                return XiaoMingContact.this;
            } else {
                return null;
            }
        }, contexts);
    }
}

package cn.chuanwise.xiaoming.message;

import cn.chuanwise.util.Arrays;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.object.FormatableObject;
import cn.chuanwise.xiaoming.object.XiaoMingObject;
import cn.chuanwise.xiaoming.util.MiraiCodes;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.QuoteReply;
import net.mamoe.mirai.message.data.SingleMessage;

public interface MessageSendable<M> extends XiaoMingObject, FormatableObject {
    default M sendMessage(String miraiCode, Object... contexts) {
        return sendMessage(MiraiCode.deserializeMiraiCode(format(miraiCode, contexts)));
    }

    default M sendMessage(SingleMessage... elements) {
        return sendMessage(MiraiCodes.asMessageChain(elements));
    }

    default M replyMessage(Message quote, String message, Object... contexts) {
        return replyMessage(quote.getOriginalMessageChain(), MiraiCode.deserializeMiraiCode(format(message, contexts)));
    }

    default M replyMessage(Message quote, SingleMessage... elements) {
        return replyMessage(quote.getOriginalMessageChain(), MiraiCodes.asMessageChain(elements));
    }

    default M replyMessage(Message quote, MessageChain messageChain) {
        return replyMessage(quote.getOriginalMessageChain(), messageChain);
    }

    default M replyMessage(MessageChain quote, String message, Object... contexts) {
        return replyMessage(quote, MiraiCode.deserializeMiraiCode(format(message, contexts)));
    }

    default M replyMessage(MessageChain quote, SingleMessage... elements) {
        return replyMessage(quote, MiraiCodes.asMessageChain(elements));
    }

    default M replyMessage(MessageChain quote, MessageChain messageChain) {
        return sendMessage(new QuoteReply(quote).plus(messageChain));
    }

    M sendMessage(MessageChain messageChain);


    default M sendWarning(String miraiCode, Object... contexts) {
        return sendMessage("Σ(っ °Д °;)っ " + miraiCode, contexts);
    }

    default M sendWarning(SingleMessage firstElement, SingleMessage... remainElements) {
        final SingleMessage[] singleMessages = Arrays.insert(remainElements, 0, firstElement);
        return sendWarning(MiraiCodes.asMessageChain(singleMessages));
    }

    default M sendWarning(MessageChain messageChain) {
        return sendWarning(messageChain.serializeToMiraiCode());
    }

    default M replyWarning(Message quote, String message, Object... contexts) {
        return replyWarning(quote.getOriginalMessageChain(), MiraiCode.deserializeMiraiCode(format(message, contexts)));
    }

    default M replyWarning(Message quote, SingleMessage... elements) {
        return replyWarning(quote.getOriginalMessageChain(), MiraiCodes.asMessageChain(elements));
    }

    default M replyWarning(Message quote, MessageChain messageChain) {
        return replyWarning(quote.getOriginalMessageChain(), messageChain);
    }

    default M replyWarning(MessageChain quote, String message, Object... contexts) {
        return replyWarning(quote, MiraiCode.deserializeMiraiCode(format(message, contexts)));
    }

    default M replyWarning(MessageChain quote, SingleMessage... elements) {
        return replyWarning(quote, MiraiCodes.asMessageChain(elements));
    }

    default M replyWarning(MessageChain quote, MessageChain messageChain) {
        return sendMessage(new QuoteReply(quote).plus("(；′⌒`) ").plus(messageChain));
    }


    default M sendError(String miraiCode, Object... contexts) {
        return sendMessage("ヾ(≧へ≦)〃 " + miraiCode, contexts);
    }

    default M sendError(SingleMessage firstElement, SingleMessage... remainElements) {
        final SingleMessage[] singleMessages = Arrays.insert(remainElements, 0, firstElement);
        return sendError(MiraiCodes.asMessageChain(singleMessages));
    }

    default M sendError(MessageChain messageChain) {
        return sendError(messageChain.serializeToMiraiCode());
    }

    default M replyError(Message quote, String message, Object... contexts) {
        return replyError(quote.getOriginalMessageChain(), MiraiCode.deserializeMiraiCode(format(message, contexts)));
    }

    default M replyError(Message quote, SingleMessage... elements) {
        return replyError(quote.getOriginalMessageChain(), MiraiCodes.asMessageChain(elements));
    }

    default M replyError(Message quote, MessageChain messageChain) {
        return replyError(quote.getOriginalMessageChain(), messageChain);
    }

    default M replyError(MessageChain quote, String message, Object... contexts) {
        return replyError(quote, MiraiCode.deserializeMiraiCode(format(message, contexts)));
    }

    default M replyError(MessageChain quote, SingleMessage... elements) {
        return replyError(quote, MiraiCodes.asMessageChain(elements));
    }

    default M replyError(MessageChain quote, MessageChain messageChain) {
        return sendMessage(new QuoteReply(quote).plus("(ﾟДﾟ*)ﾉ ").plus(messageChain));
    }
}

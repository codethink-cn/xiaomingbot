package cn.chuanwise.xiaoming.user;

import cn.chuanwise.api.OriginalTagMarkable;
import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.util.Tags;
import cn.chuanwise.xiaoming.account.Account;
import cn.chuanwise.xiaoming.contact.contact.XiaoMingContact;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.event.MessageEvent;
import cn.chuanwise.xiaoming.exception.InteractExitedException;
import cn.chuanwise.xiaoming.exception.InteractInterrtuptedException;
import cn.chuanwise.xiaoming.exception.InteractTimeoutException;
import cn.chuanwise.xiaoming.interactor.context.InteractorContext;
import cn.chuanwise.xiaoming.language.LanguageManager;
import cn.chuanwise.xiaoming.message.MessageSendable;
import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.xiaoming.permission.Permission;
import cn.chuanwise.xiaoming.property.PropertyHandler;
import cn.chuanwise.xiaoming.property.PropertyType;
import cn.chuanwise.xiaoming.recept.ReceptionTask;
import cn.chuanwise.xiaoming.recept.Receptionist;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.QuoteReply;
import net.mamoe.mirai.utils.ExternalResource;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public interface XiaoMingUser<C extends XiaoMingContact<?>>
        extends ModuleObject, PropertyHandler, OriginalTagMarkable, MessageSendable<Optional<Message>> {
    ReceptionTask<XiaoMingUser<C>> getReceptionTask();

    void setReceptionTask(ReceptionTask<XiaoMingUser<C>> receptionTask);

    InteractorContext getInteractorContext();

    void setInteractorContext(InteractorContext interactorContext);

    @Override
    default Set<String> getOriginalTags() {
        return CollectionUtil.asSet(Tags.ALL, getCodeString());
    }

    /**
     * 以当前用户的身份替换变量
     *
     * @param format   格式字符串，其中使用 {} 引用下文的变量
     * @param contexts 实参。将按顺序用于替换 format 中的 {}
     * @return 替换后的字符串
     */
    @Override
    default String format(String format, Object... contexts) {
        return formatAdditional(format, x -> null, contexts);
    }

    default String formatAdditional(String format, Function<String, ?> getter, Object... contexts) {
        final LanguageManager languageManager = getXiaoMingBot().getLanguageManager();

        // 替换 Language 中的字句
        return languageManager.formatAdditional(format, variable -> {
            final Object getterObject = getter.apply(variable);
            if (Objects.nonNull(getterObject)) {
                return getterObject;
            }

            final InteractorContext context = getInteractorContext();
            switch (variable) {
                case "user":
                    return XiaoMingUser.this;
                case "contact":
                    return getContact();
                default:
            }
            if (Objects.isNull(context)) {
                return null;
            }
            switch (variable) {
                case "interactor":
                case "command":
                case "interact":
                    return context.getInteractor();
                case "interactors":
                    return context.getInteractor().getInteractors();
                case "plugin":
                    return context.getPlugin();
                case "arguments":
                case "argument":
                case "args":
                case "arg":
                    return context.getArguments();
                default:
                    return null;
            }
        }, contexts);
    }

    @Override
    default Optional<Message> sendMessage(MessageChain messageChain) {
        return getContact().sendMessage(messageChain);
    }

    default boolean isBot() {
        return getCode() == getXiaoMingBot().getMiraiBot().getId();
    }

    C getContact();

    Optional<Message> sendPrivateMessage(String message, Object... arguments);

    default Optional<Message> sendPrivateMessage(MessageChain messages) {
        return sendPrivateMessage(messages.serializeToMiraiCode());
    }

    default Optional<Message> privateReply(Message quote, String message) {
        return privateReply(quote, MiraiCode.deserializeMiraiCode(message));
    }

    default Optional<Message> privateReply(Message quote, Message message) {
        return privateReply(quote, message.getMessageChain());
    }

    default Optional<Message> privateReply(Message quote, MessageChain message) {
        return sendPrivateMessage(new QuoteReply(quote.getOriginalMessageChain()).plus(message).serializeToMiraiCode());
    }

    default boolean hasPermission(String permission) {
        return getXiaoMingBot().getPermissionService().hasPermission(this, permission);
    }

    default boolean hasPermission(Permission permission) {
        return getXiaoMingBot().getPermissionService().hasPermission(this, permission);
    }

    default boolean hasPermissions(String... permissions) {
        for (String node : permissions) {
            if (!hasPermission(node)) {
                return false;
            }
        }
        return true;
    }

    default boolean requirePermission(String permission) {
        if (hasPermission(permission)) {
            return true;
        } else {
            sendError("小明不能帮你做这件事，因为你还缺少权限「" + permission + "」");
            return false;
        }
    }

    default boolean requirePermission(String... nodes) {
        for (String node : nodes) {
            if (!requirePermission(node)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获得该用户的接待员
     *
     * @return 用户接待员
     */
    Receptionist getReceptionist();

    void setReceptionist(Receptionist receptionist);

    default boolean onNextMessage(Message message) {
        setProperty(PropertyType.LAST, message);
        return getXiaoMingBot().getEventManager().callEvent(new MessageEvent(this, message));
    }

    boolean onNextMessage(MessageChain messages);

    default boolean onNextMessage(String message) {
        return onNextMessage(MiraiCode.deserializeMiraiCode(getXiaoMingBot().getLanguageManager().format(message)));
    }

    default Optional<Message> nextMessage(long timeout) throws InterruptedException, InteractExitedException {
        final Optional<Message> optional = getContact().nextMessage(timeout);
        if (optional.isPresent()) {
            final Message message = optional.get();
            final String serializedMessage = message.serialize();

            if (Objects.equals(serializedMessage, "退出")) {
                throw new InteractExitedException();
            } else {
                getXiaoMingBot().getStatistician().increaseCallNumber();

                return optional;
            }
        } else {
            return optional;
        }
    }

    default Optional<Message> nextMessage() throws InterruptedException, InteractExitedException {
        return nextMessage(getXiaoMingBot().getConfiguration().getMaxUserInputTimeout());
    }

    default Message nextMessageOrExit(long timeout) throws InteractExitedException {
        try {
            return nextMessage(timeout)
                    .orElseThrow(() -> new InteractTimeoutException(getInteractorContext(), this, timeout));
        } catch (InterruptedException exception) {
            throw new InteractInterrtuptedException(getInteractorContext(), this);
        }
    }

    default Message nextMessageOrExit() throws InteractExitedException {
        return nextMessageOrExit(getXiaoMingBot().getConfiguration().getMaxUserInputTimeout());
    }

    default Optional<Message> nextPrivateMessage(long timeout) throws InterruptedException {
        return getXiaoMingBot()
                .getContactManager()
                .nextPrivateMessage(getCode(), timeout)
                .map(MessageEvent::getMessage);
    }

    default Optional<Message> nextPrivateMessage() throws InterruptedException {
        return nextPrivateMessage(getXiaoMingBot().getConfiguration().getMaxUserPrivateInputTimeout());
    }

    default Optional<Message> nextGroupMessage(String tag, long timeout) throws InterruptedException {
        return getXiaoMingBot().getContactManager()
                .nextGroupMemberMessage(tag, getCode(), timeout)
                .map(MessageEvent::getMessage);
    }

    default Optional<Message> nextGroupMessage(String tag) throws InterruptedException {
        return nextGroupMessage(tag, getXiaoMingBot().getConfiguration().getMaxUserGroupInputTimeout());
    }

    default Optional<Message> nextGlobalMessage(long timeout) throws InterruptedException {
        return getXiaoMingBot()
                .getContactManager()
                .nextMessageEvent(timeout, messageEvent -> messageEvent.getUser().getCode() == getCode())
                .map(MessageEvent::getMessage);
    }

    default Optional<Message> nextGlobalMessage() throws InterruptedException {
        return nextGlobalMessage(getXiaoMingBot().getConfiguration().getMaxUserInputTimeout());
    }

    /**
     * 获取用户的 QQ
     *
     * @return 用户 QQ
     */
    long getCode();

    default String getCodeString() {
        return String.valueOf(getCode());
    }

    /**
     * 获取用户的 QQ 账号名或备注
     *
     * @return QQ 账号名或备注
     */
    default String getName() {
        return getContact().getName();
    }

    /**
     * 获取用户全名。包含所在的群的名称。
     *
     * @return 用户全名
     */
    String getCompleteName();

    default Account getAccount() {
        return getXiaoMingBot().getAccountManager().createAccount(getCode());
    }

    default String getAliasOrName() {
        return getXiaoMingBot().getAccountManager().getAlias(getCode()).orElseGet(this::getName);
    }

    @Override
    default Set<String> getTags() {
        return getXiaoMingBot().getAccountManager().getTags(getCode());
    }

    default String getAliasAndCode() {
        return getAliasOrName() + "（" + getCodeString() + "）";
    }

    default String getAliasOrCode() {
        return getXiaoMingBot().getAccountManager().getAliasOrCode(getCode());
    }

    void nudge();

    default Image uploadImage(ExternalResource resource) {
        return getContact().uploadImage(resource);
    }
}
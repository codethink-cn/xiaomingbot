package cn.chuanwise.xiaoming.interactor;

import cn.chuanwise.toolkit.container.Container;
import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.util.Reflects;
import cn.chuanwise.xiaoming.annotation.Filter;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.event.InteractorErrorEvent;
import cn.chuanwise.xiaoming.interactor.context.InteractorContext;
import cn.chuanwise.xiaoming.interactor.customizer.InteractorCustomizer;
import cn.chuanwise.xiaoming.interactor.customizer.InteractorInfo;
import cn.chuanwise.xiaoming.interactor.exception.InteractExceptionHandler;
import cn.chuanwise.xiaoming.interactor.exception.SimpleInteractExceptionHandler;
import cn.chuanwise.xiaoming.interactor.handler.Interactor;
import cn.chuanwise.xiaoming.interactor.parser.InteractorParameterContext;
import cn.chuanwise.xiaoming.interactor.parser.InteractorParameterParser;
import cn.chuanwise.xiaoming.interactor.parser.InteractorParameterParserHandler;
import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.xiaoming.object.PluginObject;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.user.XiaoMingUser;
import cn.chuanwise.xiaoming.util.MiraiCodes;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.SingleMessage;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public interface InteractorManager extends ModuleObject {
    /**
     * 和符合条件的指令交互器交互
     *
     * @param user 用户
     * @return 是否交互成功
     * @throws Exception 交互期间抛出的异常
     */
    boolean interactIf(XiaoMingUser user, Message message, Predicate<Interactor> filter);

    boolean interactIf(XiaoMingUser user, MessageChain messages, Predicate<Interactor> filter);

    default boolean interactIf(XiaoMingUser user, String message, Predicate<Interactor> filter) {
        return interactIf(user, MiraiCode.deserializeMiraiCode(message), filter);
    }

    default boolean interactIf(XiaoMingUser user, SingleMessage singleMessage, Predicate<Interactor> filter) {
        return interactIf(user, MiraiCodes.asMessageChain(singleMessage), filter);
    }

    default boolean interact(XiaoMingUser user, Message message) {
        return interactIf(user, message, null);
    }

    default boolean interact(XiaoMingUser user, MessageChain messages) {
        return interactIf(user, messages, null);
    }

    default boolean interact(XiaoMingUser user, String message) {
        return interact(user, MiraiCode.deserializeMiraiCode(message));
    }

    default boolean interact(XiaoMingUser user, SingleMessage singleMessage) {
        return interact(user, MiraiCodes.asMessageChain(singleMessage));
    }

    /**
     * 交互器
     */
    List<Interactor> getInteractors();

    default List<Interactor> getInteractors(Plugin plugin) {
        return CollectionUtil.filter(getInteractors(), new ArrayList<>(), interactor -> (interactor.getPlugin() == plugin));
    }

    void registerInteractor(Interactor interactor);

    default void registerInteractors(Interactors interactors, InteractorCustomizer interactorCustomizer, Plugin plugin) {
        if (interactors instanceof PluginObject) {
            final PluginObject pluginObject = (PluginObject) interactors;
            pluginObject.setPlugin(plugin);
            pluginObject.setXiaoMingBot(getXiaoMingBot());
        }

        interactors.onRegister();
        for (Method method : Reflects.getDeclaredMethods(interactors.getClass())) {
            if (method.getAnnotationsByType(Filter.class).length == 0) {
                continue;
            }
            Interactor handler = new Interactor(method, plugin);

            // 尝试使用自定义设置
            if (Objects.nonNull(interactorCustomizer)) {
                final Optional<InteractorInfo> optionalInteractorInfo = interactorCustomizer.getInteractorInfo(handler.getName());
                if (optionalInteractorInfo.isPresent()) {
                    final InteractorInfo interactorInfo = optionalInteractorInfo.get();

                    handler.setFormats(interactorInfo.getFormats());
                    handler.setRequireAccountTags(interactorInfo.getRequireAccountTags());
                    handler.setRequireGroupTags(interactorInfo.getRequireGroupTags());
                    handler.setPermissions(interactorInfo.getPermissions());
                    handler.setUsage(interactorInfo.getUsage());
                } else {
                    final InteractorInfo interactorInfo = new InteractorInfo();

                    interactorInfo.setName(handler.getName());
                    interactorInfo.setFormats(handler.getFormats());
                    interactorInfo.setRequireAccountTags(handler.getRequireAccountTags());
                    interactorInfo.setRequireGroupTags(handler.getRequireGroupTags());
                    interactorInfo.setPermissions(handler.getPermissions());
                    interactorInfo.setUsage(handler.getUsage());

                    interactorCustomizer.registerInteractorInfo(interactorInfo.getName(), interactorInfo);
                }
            }

            handler.setInteractors(interactors);
            registerInteractor(handler);
        }
    }

    default <T extends Plugin> void registerInteractors(Interactors<T> interactors, T plugin) {
        registerInteractors(interactors, null, plugin);
    }

    void unregisterInteractors(Plugin plugin);

    void unregisterInteractors(Interactors interactors);

    /**
     * 智能参数解析器
     */
    List<InteractorParameterParserHandler> getParameterParsers();

    <T> void registerParameterParser(InteractorParameterParserHandler<T> handler);

    default <T> void registerParameterParser(Class<T> clazz, InteractorParameterParser<T> parser, boolean share, Plugin plugin) {
        registerParameterParser(new InteractorParameterParserHandler<>(clazz, parser, plugin, share));
    }

    default List<InteractorParameterParserHandler> getParameterParsers(Plugin plugin) {
        return CollectionUtil.filter(getParameterParsers(), new ArrayList<>(), parser -> (parser.getPlugin() == plugin));
    }

    void unregisterParameterParsers(Plugin plugin);

    /**
     * 用内核 parser 或某插件的 parser 解析
     */
    default <T> Container<T> parseParameter(InteractorParameterContext<T> context) {
        final Plugin plugin = context.getPlugin();
        final Class<T> parameterClass = context.getParameterClass();

        for (InteractorParameterParserHandler handler : getParameterParsers()) {
            if (Objects.nonNull(handler.getPlugin()) && (!handler.isShared() && handler.getPlugin() != plugin)) {
                continue;
            }
            if (!parameterClass.isAssignableFrom(handler.getParameterClass())) {
                continue;
            }

            final Container<T> result = (Container<T>) handler.getParser().parse(context);
            if (Objects.isNull(result)) {
                return null;
            } else if (result.isSet()) {
                return result;
            }
        }
        return null;
    }

    /**
     * 异常捕捉器
     */
    List<SimpleInteractExceptionHandler> getThrowableCaughters();

    <T extends Throwable> void registerThrowableCaughter(SimpleInteractExceptionHandler<T> handler);

    default <T extends Throwable> void registerThrowableCaughter(Class<T> clazz, InteractExceptionHandler<T> handler, boolean share, Plugin plugin) {
        registerThrowableCaughter(new SimpleInteractExceptionHandler<>(clazz, handler, plugin, share));
    }

    void unregisterThrowableCaughters(Plugin plugin);

    @SuppressWarnings("all")
    default void onThrowable(InteractorContext context, Throwable throwable) {
        final Plugin plugin = context.getPlugin();
        final XiaoMingUser user = context.getUser();

        for (SimpleInteractExceptionHandler handler : getThrowableCaughters()) {
            if (Objects.nonNull(handler.getPlugin()) && !handler.isShared() && plugin != handler.getPlugin()) {
                continue;
            }

            if (!handler.getHandledClass().isInstance(throwable)) {
                continue;
            }

            try {
                handler.handle(context, throwable);
                throwable = null;
                break;
            } catch (Throwable nextThrowable) {
                throwable = nextThrowable;
            }
        }

        if (Objects.nonNull(throwable)) {
            final InteractorErrorEvent event = new InteractorErrorEvent(context, throwable);
            getXiaoMingBot().getEventManager().callEventAsync(event);
        }
    }

    default void unregisterPlugin(Plugin plugin) {
        unregisterInteractors(plugin);
        unregisterParameterParsers(plugin);
        unregisterThrowableCaughters(plugin);
    }
}
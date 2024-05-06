package cn.chuanwise.xiaoming.interactor;

import cn.chuanwise.exception.UnsupportedVersionException;
import cn.chuanwise.optional.ValueWithMessage;
import cn.chuanwise.toolkit.container.Container;
import cn.chuanwise.util.Arguments;
import cn.chuanwise.util.NumberUtil;
import cn.chuanwise.util.Times;
import cn.chuanwise.xiaoming.account.Account;
import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.contact.message.MessageImpl;
import cn.chuanwise.xiaoming.group.GroupInformation;
import cn.chuanwise.xiaoming.interactor.exception.SimpleInteractExceptionHandler;
import cn.chuanwise.xiaoming.interactor.handler.Interactor;
import cn.chuanwise.xiaoming.interactor.parser.InteractorParameterParserHandler;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.plugin.PluginHandler;
import cn.chuanwise.xiaoming.user.XiaoMingUser;
import cn.chuanwise.xiaoming.util.Ats;
import cn.chuanwise.xiaoming.util.Registers;
import lombok.Getter;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

/**
 * 交互器管理器
 *
 * @author Chuanwise
 */
@Getter
public class InteractorManagerImpl extends ModuleObjectImpl implements InteractorManager {
    protected final List<Interactor> interactors = new CopyOnWriteArrayList<>();
    protected final List<InteractorParameterParserHandler> parameterParsers = new CopyOnWriteArrayList<>();
    protected final List<SimpleInteractExceptionHandler> throwableCaughters = new CopyOnWriteArrayList<>();

    public InteractorManagerImpl(XiaoMingBot xiaoMingBot) {
        super(xiaoMingBot);
        initialize();
    }

    private void initialize() {
        registerParameterParser(XiaoMingBot.class, context -> Container.of(getXiaoMingBot()), true, null);
        registerParameterParser(long.class, context -> {
            final XiaoMingUser user = context.getUser();
            final String inputValue = context.getInputValue();
            switch (context.getParameterName()) {
                case "qq":
                case "code":
                case "account":
                    final Optional<Long> optionalCode = Ats.parseAt(context.getInputValue());
                    if (optionalCode.isPresent()) {
                        return Container.of(optionalCode.get());
                    } else {
                        user.sendError("「" + inputValue + "」并不是一个合理的 QQ 哦");
                        return null;
                    }
                case "time":
                case "period":
                case "时长":
                case "周期":
                    final Optional<Long> time = Times.parseTimeLength(inputValue);
                    if (time.isPresent()) {
                        return Container.of(time.get());
                    } else {
                        user.sendError("「" + inputValue + "」并不是一个合理的时长哦");
                        return null;
                    }
                case "group":
                    final Optional<Long> groupCode = NumberUtil.parseLong(inputValue);
                    if (groupCode.isPresent()) {
                        return Container.of(groupCode.get());
                    } else {
                        user.sendError("「" + inputValue + "」并不是一个合理的群号哦");
                        return null;
                    }
                default:
                    final Optional<Long> parseLong = NumberUtil.parseLong(inputValue);
                    if (parseLong.isPresent()) {
                        return Container.of(parseLong.get());
                    } else {
                        return null;
                    }
            }
        }, true, null);
        registerParameterParser(Account.class, context -> {
            final XiaoMingUser user = context.getUser();
            final String inputValue = context.getInputValue();
            final Optional<Long> optionalCode = Ats.parseAt(inputValue);
            if (optionalCode.isPresent()) {
                final Account account = getXiaoMingBot().getAccountManager().createAccount(optionalCode.get());
                if (Objects.nonNull(account)) {
                    return Container.of(account);
                } else {
                    return null;
                }
            } else {
                user.sendError("「" + inputValue + "」并不是一个合理的 QQ 哦");
                return null;
            }
        }, true, null);
        registerParameterParser(int.class, context -> {
            final String parameterName = context.getParameterName();
            final String inputValue = context.getInputValue();
            final XiaoMingUser user = context.getUser();

            final Optional<Integer> optionalInteger = NumberUtil.parseInteger(inputValue);
            if (optionalInteger.isPresent()) {
                final Integer integer = optionalInteger.get();
                switch (parameterName) {
                    case "index":
                    case "序号":
                        if (integer <= 0) {
                            user.sendError("「" + inputValue + "」并不是一个合理的序号哦");
                            return null;
                        } else {
                            return Container.of(integer - 1);
                        }
                    default:
                        return Container.of(integer);
                }
            } else {
                user.sendError("「" + inputValue + "」并不是一个合理的整数哦");
                return null;
            }
        }, true, null);
        registerParameterParser(Plugin.class, context -> {
            final String inputValue = context.getInputValue();
            if (Objects.equals(inputValue, "内核") || Objects.equals(inputValue, "core")) {
                return Container.ofNull();
            }

            final XiaoMingUser user = context.getUser();
            final Optional<Plugin> optionalPlugin = getXiaoMingBot().getPluginManager().getPlugin(inputValue);
            if (!optionalPlugin.isPresent()) {
                user.sendError("插件「" + inputValue + "」尚未加载");
                return null;
            } else {
                return Container.ofOptional(optionalPlugin);
            }
        }, true, null);
        registerParameterParser(double.class, context -> {
            final String inputValue = context.getInputValue();
            final XiaoMingUser user = context.getUser();
            final Optional<Double> optionalDouble = NumberUtil.parseDouble(inputValue);
            if (optionalDouble.isPresent()) {
                return Container.of(optionalDouble.get());
            } else {
                user.sendError("「" + inputValue + "」并不是一个合理的小数哦");
                return null;
            }
        }, true, null);
        registerParameterParser(GroupInformation.class, context -> {
            final String inputValue = context.getInputValue();
            final XiaoMingUser user = context.getUser();

            final Optional<Long> optionalGroupCode = NumberUtil.parseLong(inputValue);
            if (optionalGroupCode.isPresent()) {
                final GroupInformation groupInformation = getXiaoMingBot().getGroupInformationManager().forCode(optionalGroupCode.get());
                if (Objects.isNull(groupInformation)) {
                    user.sendError("没有找到群聊「" + inputValue + "」的信息记录");
                    return null;
                } else {
                    return Container.of(groupInformation);
                }
            } else {
                user.sendError("「" + inputValue + "」并不是一个合理的群号哦");
                return null;
            }
        }, true, null);
        registerParameterParser(PluginHandler.class, context -> {
            final String inputValue = context.getInputValue();
            final XiaoMingUser user = context.getUser();
            final Optional<PluginHandler> optionalHandler = getXiaoMingBot().getPluginManager().getPluginHandler(inputValue);

            if (!optionalHandler.isPresent()) {
                user.sendError("插件「" + inputValue + "」尚未加载");
                return null;
            } else {
                return Container.ofOptional(optionalHandler);
            }
        }, true, null);
        registerParameterParser(String[].class, context -> {
            switch (context.getParameterName()) {
                case "args":
                case "arguments":
                    return Container.of(Arguments.split(context.getMessage().serialize()).toArray(new String[0]));
                default:
                    return null;
            }
        }, true, null);
    }

    /**
     * 智能参数解析器
     */
    @Override
    public List<InteractorParameterParserHandler> getParameterParsers() {
        return Collections.unmodifiableList(parameterParsers);
    }

    @Override
    public <T> void registerParameterParser(InteractorParameterParserHandler<T> handler) {
        Registers.checkRegister(getXiaoMingBot(), handler.getPlugin(), "parameter parser");
        parameterParsers.add(handler);
    }

    @Override
    public void unregisterParameterParsers(Plugin plugin) {
        Registers.checkUnregister(getXiaoMingBot(), plugin, "parameter parser");
        parameterParsers.removeIf(parser -> (parser.getPlugin() == plugin));
    }

    /**
     * 异常捕捉器
     */
    @Override
    public List<SimpleInteractExceptionHandler> getThrowableCaughters() {
        return Collections.unmodifiableList(throwableCaughters);
    }

    @Override
    public <T extends Throwable> void registerThrowableCaughter(SimpleInteractExceptionHandler<T> handler) {
        Registers.checkRegister(getXiaoMingBot(), handler.getPlugin(), "throwable caughter");
        throwableCaughters.add(handler);
    }

    @Override
    public void unregisterThrowableCaughters(Plugin plugin) {
        Registers.checkUnregister(getXiaoMingBot(), plugin, "throwable caughter");
        throwableCaughters.removeIf(caughter -> (caughter.getPlugin() == plugin));
    }

    @Override
    public boolean interactIf(XiaoMingUser user, MessageChain messages, Predicate<Interactor> filter) {
        return interactIf(user, new MessageImpl(xiaoMingBot, messages), filter);
    }

    /**
     * 和用户交互
     */
    @Override
    public boolean interactIf(XiaoMingUser user, Message message, Predicate<Interactor> filter) {
        boolean interacted = false;

        for (Interactor interactor : interactors) {
            if (Objects.nonNull(filter) && !filter.test(interactor)) {
                continue;
            }

            boolean thisTimeInteracted = false;
            final ValueWithMessage<InteractResult> interactResult = interactor.interact(user, message);
            switch (interactResult.getValue()) {
                case ILLEGAL_TYPE:
                    user.sendError("「" + Plugin.getChineseName(interactor.getPlugin()) + "」的交互器「" + interactor.getName() + "」出现类型解析错误");
                    getLogger().error("当前用户：" + user.getAliasAndCode() + "\n" +
                            "交互器：" + interactor.getName() + "\n" +
                            "该交互器方法带有 " + interactResult.getMessage() + " 类型的参数。该形式参数不带 @FilterParameter(...) 注解，也不是小明能够识别的类型");
                    thisTimeInteracted = true;
                    break;
                case ILLEGAL_PARAMETER:
                    user.sendError("「" + Plugin.getChineseName(interactor.getPlugin()) + "」的交互器「" + interactor.getName() + "」出现类型解析错误");
                    getLogger().error("当前用户：" + user.getAliasAndCode() + "\n" +
                            "交互器：" + interactor.getName() + "\n" +
                            "错误参数名：" + interactResult.getMessage() + "\n" +
                            "解析该方法时，该参数带有 @FilterParameter(...) 注解，但智能参数解析器（或变量运算器）将其解析成不符合函数参数的类型");
                    thisTimeInteracted = true;
                    break;
                case INTERACTED:
                case TIMEOUT_CANCELLED:
                    thisTimeInteracted = true;
                    break;
                case INTERRUPTED:
                case EXIT:
                case EVENT_CANCELLED:
                case LACK_ACCOUNT_TAGS:
                case LACK_GROUP_TAGS:
                case ACCOUNT_BLOCK_PLUGIN:
                case QUITE_MODE_DENIED:
                case NOT_ENABLE_YET:
                case PARSE_FAILED:
                case ILLEGAL_FORMAT:
                case INTERACTED_BUT_FALSE:
                case ILLEGAL_SCOPE:
                    break;
                case LACK_PERMISSIONS:
                    user.sendError("小明不能帮你做这件事，因为你还缺少权限「" + interactResult.getMessage() + "」");
                    thisTimeInteracted = true;
                    break;
                case ERROR:
                    thisTimeInteracted = true;
                    break;
                default:
                    throw new UnsupportedVersionException();
            }

            interacted = interacted || thisTimeInteracted;

            // 检查是否需要阻断响应
            if (thisTimeInteracted && interactor.isNonNext()) {
                return interacted;
            }
        }
        return interacted;
    }

    /**
     * 交互器
     */
    @Override
    public List<Interactor> getInteractors() {
        return Collections.unmodifiableList(interactors);
    }

    @Override
    public void registerInteractor(Interactor interactor) {
        Registers.checkRegister(getXiaoMingBot(), interactor.getPlugin(), "interactor");
        interactors.add(interactor);
    }

    @Override
    public void unregisterInteractors(Plugin plugin) {
        Registers.checkUnregister(getXiaoMingBot(), plugin, "interactor");
        interactors.removeIf(interactor -> (interactor.getPlugin() == plugin));
    }

    @Override
    public void unregisterInteractors(Interactors interactors) {
        this.interactors.removeIf(interactor -> {
            if (interactor.getInteractors() != interactors) {
                return false;
            }
            Registers.checkUnregister(getXiaoMingBot(), interactor.getPlugin(), "interactor");
            return true;
        });
    }
}
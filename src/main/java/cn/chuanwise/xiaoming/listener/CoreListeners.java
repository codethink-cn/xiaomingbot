package cn.chuanwise.xiaoming.listener;

import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.util.Throwables;
import cn.chuanwise.util.Times;
import cn.chuanwise.xiaoming.account.Account;
import cn.chuanwise.xiaoming.annotation.EventListener;
import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.event.InteractorErrorEvent;
import cn.chuanwise.xiaoming.event.SendMessageEvent;
import cn.chuanwise.xiaoming.event.SimpleListeners;
import cn.chuanwise.xiaoming.interactor.context.InteractorContext;
import cn.chuanwise.xiaoming.interactor.handler.Interactor;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.user.XiaoMingUser;
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.PlainText;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CoreListeners extends SimpleListeners {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

    @EventListener
    public void onFriendAddRequest(NewFriendRequestEvent event) {
        if (getXiaoMingBot().getConfiguration().isAutoAcceptFriendAddRequest()) {
            event.accept();
        }
    }

    @EventListener
    public void onGroupInvite(BotInvitedJoinGroupRequestEvent event) {
        if (getXiaoMingBot().getConfiguration().isAutoAcceptGroupInvite()) {
            event.accept();
        }
    }

    @EventListener
    public void onSendMessage(SendMessageEvent event) {
        try {
            xiaoMingBot.getContactManager().readyToSend(event).get(xiaoMingBot.getConfiguration().getSendMessageTimeout(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            xiaoMingBot.getLogger().error("发送消息过程被打断");
        } catch (TimeoutException e) {
            xiaoMingBot.getLogger().warn("发送消息超时");
        } catch (ExecutionException e) {
            event.setCause(e.getCause());
        }
    }

    /**
     * 对将要发送的消息进行切割, 使其不超过 QQ 限制的长度 <br>
     * 监听 SendMessageEvent, 每切割出一条消息就会发出新的 SendMessageEvent 事件 <br>
     * 切割时打断会停止对消息的切割, 并尝试直接发送原消息
     *
     * @param event SendMessageEvent
     */
    @EventListener(priority = ListenerPriority.HIGHEST)
    public void splitMessage(SendMessageEvent event) {
        if (event.isMessageChainChanged() || !getXiaoMingBot().getConfiguration().isAutoSplitMessage()) {
            return;
        }

        String[] strings = event.getMessageChain().serializeToMiraiCode().split("\\\\n");

        // 在配置文件和 QQ 允许的最大消息长度内取较小值
        int maxImage = Math.min(getXiaoMingBot().getConfiguration().getMiraiCodePerPage(), 30);
        int maxText = Math.min(getXiaoMingBot().getConfiguration().getTextPerPage(), 5000);

        StringBuilder sb = new StringBuilder();
        try {
            if (strings.length < 2) {
                char[] chars = strings[0].toCharArray();
                // 对于不分段的消息，遍历每一个字符
                for (char ch : chars) {
                    long miraiCodeInPage = MiraiCode.deserializeMiraiCode(sb.toString()).stream()
                            .filter(f -> !(f instanceof PlainText))
                            .count();

                    if (sb.length() < maxText
                            && miraiCodeInPage < maxImage) {
                        sb.append(ch);
                    } else {
                        // 等待 50ms，确保顺序不乱
                        Thread.sleep(50);
                        final SendMessageEvent sendMessageEvent = new SendMessageEvent(event.getTarget(),
                                MiraiCode.deserializeMiraiCode(sb.toString()), true);
                        xiaoMingBot.getEventManager().callEventAsync(sendMessageEvent);

                        sb = new StringBuilder().append(ch);
                    }
                }
            } else {
                long miraiCodesInPage = 0;
                // 对于分段的消息，遍历切割后的每一个字符串
                for (String string : strings) {
                    long miraiCodesInFutureMessage = MiraiCode.deserializeMiraiCode(string).stream()
                            .filter(f -> !(f instanceof PlainText))
                            .count();

                    if (sb.length() < maxText
                            && string.length() + sb.length() < maxText
                            && miraiCodesInPage < maxImage
                            && miraiCodesInFutureMessage + miraiCodesInPage < maxImage) {
                        sb.append(string);
                        if (!string.equals(strings[strings.length - 1]))
                            sb.append("\n");

                        miraiCodesInPage = miraiCodesInPage + miraiCodesInFutureMessage;
                    } else {
                        // 等待 50ms，确保顺序不乱
                        Thread.sleep(50);
                        final SendMessageEvent sendMessageEvent = new SendMessageEvent(event.getTarget(),
                                MiraiCode.deserializeMiraiCode(sb.toString()), true);
                        xiaoMingBot.getEventManager().callEventAsync(sendMessageEvent);

                        sb = new StringBuilder(string);
                        miraiCodesInPage = 0;
                    }
                }
            }
            // 等待 50ms，确保顺序不乱
            Thread.sleep(50);
            final SendMessageEvent sendMessageEvent = new SendMessageEvent(event.getTarget(),
                    MiraiCode.deserializeMiraiCode(sb.toString()), true);
            xiaoMingBot.getEventManager().callEventAsync(sendMessageEvent);
            // 取消原事件
            event.cancel();
        } catch (InterruptedException e) {
            getLogger().error("处理消息时被打断");
        }
    }

    @EventListener
    public void onInteractorError(InteractorErrorEvent event) {
        final InteractorContext context = event.getContext();
        final Throwable throwable = event.getThrowable();
        final XiaoMingUser user = context.getUser();

        final String errorObjectName = Objects.isNull(context.getPlugin()) ? "小明内核" : ("插件「" + context.getPlugin().getName() + "」");

        // save error log
        final File errorLog = new File(xiaoMingBot.getReportDirectory(), "error-" + dateFormat.format(System.currentTimeMillis()) + ".txt");
        try {
            errorLog.createNewFile();
            try (OutputStream outputStream = new FileOutputStream(errorLog)) {
                final PrintStream stream = new PrintStream(outputStream);
                final Account account = context.getUser().getAccount();
                final Message message = context.getMessage();
                final Interactor interactor = context.getInteractor();

                stream.print("【错误报告】\n" +
                                "交互器交互时出现异常\n" +
                                "\n" +
                                "【异常概述】\n" +
                                "异常时间：" + Times.formatNow() + "\n" +
                                "触发人：" + context.getUser().getCompleteName() + "\n" +
                                "交互器：" + interactor.getName() + "\n" +
                                "交互类：" + interactor.getMethod().getDeclaringClass().getName() + "\n" +
                                "注册方：" + Plugin.getChineseName(context.getPlugin()) + "\n" +
                                "触发消息：" + message.serialize() + "\n" +
                                "原始消息：" + message.serializeOriginalMessage() + "\n" +
                                "\n" +
                                "【异常信息】\n" +
                                Throwables.toStackTraces(throwable) +
                                "\n" +
                                "【详细信息】\n" +
                                "用户标签：" + CollectionUtil.toString(context.getUser().getTags()) + "\n" +
                                "为封禁用户：" + account.isBanned() + "\n" +
                                "为管理员用户：" + account.isAdministrator() + "\n" +
                                "过滤器参数：" + context.getArguments() + "\n" +
                                "交互器方法：" + interactor.getMethod() + "\n" +
                                "交互器方法参数：" + context.getFinalArguments() + "\n" +
                                "\n" +
                                "【小明信息】\n" +
                                "内核版本：" + XiaoMingBot.VERSION + "\n" +
                                "启动时间：" + Times.format(xiaoMingBot.getStatistician().getBeginTime()) + "\n" +
                                "运行时长：" + Times.toTimeLength(System.currentTimeMillis() - xiaoMingBot.getStatistician().getBeginTime()) + "\n" +
                                "插件信息：" + Optional.ofNullable(CollectionUtil.toIndexString(xiaoMingBot.getPluginManager().getPlugins().values(), p -> {
                                    try {
                                        return p.getCompleteName() + "（" + p.getStatus().toChinese() + "）";
                                    } catch (Throwable e) {
                                        throw new RuntimeException(e);
                                    }
                                }))
                                .map(x -> "\n" + x)
                                .orElse("（无）") + "\n" +
                                "\n" +
                                "【环境信息】\n" +
                                "JDK 版本：" + System.getProperty("java.version") + "\n" +
                                "JVM 名称：" + System.getProperty("java.vm.name") + "\n" +
                                "OS 名称：" + System.getProperty("os.name")
                );
            }

            user.sendError(errorObjectName + "出现错误：" + throwable.getClass().getSimpleName() + "，错误信息已保存在「" + errorLog.getName() + "」");
        } catch (IOException exception) {
            exception.printStackTrace();
            user.sendError(errorObjectName + "出现错误：" + throwable + "\n" +
                    "（错误信息无法保存无法保存：" + exception + "）");
        }
        getLogger().error("和用户 " + user.getCompleteName() + " 交互时出现异常", throwable);
    }
}

package cn.chuanwise.xiaoming.recept;

import cn.chuanwise.toolkit.sized.SizedResidentConcurrentHashMap;
import cn.chuanwise.util.Maps;
import cn.chuanwise.xiaoming.annotation.EventListener;
import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.contact.message.MessageImpl;
import cn.chuanwise.xiaoming.event.Listeners;
import cn.chuanwise.xiaoming.event.MessageEvent;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
import cn.chuanwise.xiaoming.user.*;
import lombok.Getter;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.GroupTempMessageEvent;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.OnlineMessageSource;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * @author Chuanwise
 */
@Getter
public class ReceptionistManagerImpl
        extends ModuleObjectImpl
        implements ReceptionistManager, Listeners {
    /**
     * 用户接待员记录器
     */
    final Map<Long, Receptionist> receptionists;

    public ReceptionistManagerImpl(XiaoMingBot xiaoMingBot) {
        super(xiaoMingBot);
        this.receptionists = new SizedResidentConcurrentHashMap<>(xiaoMingBot.getConfiguration().getMaxReceptionistQuantity());
    }

    @Override
    public Receptionist getReceptionist(long code) {
        return Maps.getOrPutSupply(receptionists, code, () -> new ReceptionistImpl(getXiaoMingBot(), code));
    }

    @Override
    @EventListener
    public void onGroupMessageEvent(GroupMessageEvent event) {
        final Group group = event.getGroup();
        final Member member = event.getSender();

        final long accountCode = member.getId();
        final Receptionist receptionist = getReceptionist(accountCode);

        final long groupCode = group.getId();
        final GroupXiaoMingUser user = receptionist.getGroupXiaoMingUser(groupCode).orElseThrow(NoSuchElementException::new);
        final OnlineMessageSource.Incoming.FromGroup source = event.getSource();
        final Message message = new MessageImpl(xiaoMingBot,
                event.getMessage(),
                source.getIds(),
                source.getInternalIds(),
                ((long) event.getTime()) * 1000);

        xiaoMingBot.getEventManager().callEventAsync(new MessageEvent(user, message));
        xiaoMingBot.getStatistician().increaseCallNumber();
    }

    @Override
    @EventListener
    public void onPrivateMessageEvent(FriendMessageEvent event) {
        final Friend friend = event.getFriend();

        final long accountCode = friend.getId();
        final Receptionist receptionist = getReceptionist(accountCode);
        final PrivateXiaoMingUser user = receptionist.getPrivateXiaoMingUser().orElseThrow(NoSuchElementException::new);

        final OnlineMessageSource.Incoming.FromFriend source = event.getSource();
        final Message message = new MessageImpl(xiaoMingBot,
                event.getMessage(),
                source.getIds(),
                source.getInternalIds(),
                ((long) event.getTime()) * 1000);

        xiaoMingBot.getEventManager().callEventAsync(new MessageEvent(user, message));
        xiaoMingBot.getStatistician().increaseCallNumber();
    }

    @Override
    @EventListener
    public void onMemberMessageEvent(GroupTempMessageEvent event) {
        final Group group = event.getGroup();
        final NormalMember member = event.getSender();

        final long accountCode = member.getId();
        final Receptionist receptionist = getReceptionist(accountCode);

        final long groupCode = group.getId();
        final MemberXiaoMingUser user = receptionist.getMemberXiaoMingUser(groupCode).orElseThrow(NoSuchElementException::new);
        final OnlineMessageSource.Incoming.FromTemp source = event.getSource();
        final Message message = new MessageImpl(xiaoMingBot,
                event.getMessage(),
                source.getIds(),
                source.getInternalIds(),
                ((long) event.getTime()) * 1000);

        xiaoMingBot.getEventManager().callEventAsync(new MessageEvent(user, message));
        xiaoMingBot.getStatistician().increaseCallNumber();
    }

    @EventListener
    public void onMessageEvent(MessageEvent messageEvent) {
        final Message message = messageEvent.getMessage();
        final XiaoMingUser user = messageEvent.getUser();

        if (xiaoMingBot.getConfiguration().isTrimMessage()) {
            final String beforeTrim = message.serialize();
            final String afterTrim = beforeTrim.trim();

            if (!Objects.equals(beforeTrim, afterTrim)) {
                message.setMessageChain(MiraiCode.deserializeMiraiCode(afterTrim));
            }
        }

        // 唤醒正在等待这一条消息的线程
        xiaoMingBot.getContactManager().onNextMessageEvent(messageEvent);

        if (user instanceof ConsoleXiaoMingUser) {
            return;
        }

        final boolean privateInteractorsDisabled = user instanceof PrivateXiaoMingUser && !xiaoMingBot.getConfiguration().isEnablePrivateInteractors();
        final boolean memberInteractorsDisabled = user instanceof MemberXiaoMingUser && !xiaoMingBot.getConfiguration().isEnableMemberInteractors();
        final boolean groupInteractorsDisabled = user instanceof GroupXiaoMingUser && !xiaoMingBot.getConfiguration().isEnableGroupInteractors();
        if (privateInteractorsDisabled || memberInteractorsDisabled || groupInteractorsDisabled) {
            return;
        }

        if (Objects.nonNull(user.getInteractorContext())) {
            xiaoMingBot.getStatistician().increaseEffectiveCallNumber();
            getLogger().info(user.getCompleteName() + "已有交互上下文，不再启动新的接待任务");
            return;
        }

        xiaoMingBot.getScheduler().run(new ReceptionTaskImpl<>(user, message));
    }
}

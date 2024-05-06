package cn.chuanwise.xiaoming.recept;

import cn.chuanwise.xiaoming.annotation.EventListener;
import cn.chuanwise.xiaoming.event.Listeners;
import cn.chuanwise.xiaoming.object.ModuleObject;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.GroupTempMessageEvent;

import java.util.Map;
import java.util.Optional;

public interface ReceptionistManager extends ModuleObject, Listeners {
    /**
     * 获得某用户的接待员
     *
     * @param code 用户 QQ
     * @return 其接待员。如果无此接待员，则创建一个
     */
    Receptionist getReceptionist(long code);

    /**
     * 取消某个用户的接待员
     *
     * @param code 该用户
     */
    default Optional<Receptionist> removeReceptionist(long code) {
        return Optional.ofNullable(getReceptionists().remove(code));
    }

    /**
     * 标准的小明群聊交互事件响应器
     *
     * @param event 来自 mirai 的群消息事件
     */
    @EventListener
    void onGroupMessageEvent(GroupMessageEvent event);

    /**
     * 标准的小明私聊事件响应器
     *
     * @param event 来自 mirai 的私聊事件
     */
    @EventListener
    void onPrivateMessageEvent(FriendMessageEvent event);

    /**
     * 标准的小明临时会话事件响应器
     *
     * @param event 来自 mirai 的临时会话消息事件
     */
    @EventListener
    void onMemberMessageEvent(GroupTempMessageEvent event);

    /**
     * 获得接待员记录器
     *
     * @return 接待员的 Map
     */
    Map<Long, Receptionist> getReceptionists();
}
package cn.chuanwise.xiaoming.event;

import cn.chuanwise.xiaoming.object.XiaoMingObject;
import net.mamoe.mirai.event.Event;

public interface XiaoMingEvent extends Event, XiaoMingObject {
    default void onCall() {
    }
}
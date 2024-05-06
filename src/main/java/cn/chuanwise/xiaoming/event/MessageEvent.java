package cn.chuanwise.xiaoming.event;

import cn.chuanwise.xiaoming.contact.message.Message;
import cn.chuanwise.xiaoming.user.XiaoMingUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MessageEvent extends SimpleXiaoMingCancellableEvent {
    final XiaoMingUser user;
    final Message message;

    public MessageEvent(XiaoMingUser user, Message message) {
        setXiaoMingBot(user.getXiaoMingBot());
        this.user = user;
        this.message = message;
    }
}

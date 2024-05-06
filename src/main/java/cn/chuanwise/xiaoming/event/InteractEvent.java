package cn.chuanwise.xiaoming.event;

import cn.chuanwise.xiaoming.interactor.context.InteractorContext;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class InteractEvent
        extends SimpleXiaoMingCancellableEvent {
    final InteractorContext context;
}

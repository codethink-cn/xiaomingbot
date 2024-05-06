package cn.chuanwise.xiaoming.exception;

import cn.chuanwise.xiaoming.interactor.context.InteractorContext;
import cn.chuanwise.xiaoming.user.XiaoMingUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InteractInterrtuptedException extends XiaoMingRuntimeException {
    InteractorContext context;
    XiaoMingUser user;
}

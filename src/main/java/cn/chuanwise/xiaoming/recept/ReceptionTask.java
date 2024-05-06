package cn.chuanwise.xiaoming.recept;

import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.xiaoming.user.XiaoMingUser;

import java.util.concurrent.Callable;

public interface ReceptionTask<U extends XiaoMingUser<?>> extends ModuleObject, Callable<Boolean> {
    U getUser();

    Thread getThread();

    boolean isBusy();
}

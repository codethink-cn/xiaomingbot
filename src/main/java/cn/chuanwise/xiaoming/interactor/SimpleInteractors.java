package cn.chuanwise.xiaoming.interactor;

import cn.chuanwise.xiaoming.object.PluginObjectImpl;
import cn.chuanwise.xiaoming.plugin.Plugin;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 交互器标准实现
 *
 * @author Chuanwise
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SimpleInteractors<T extends Plugin>
        extends PluginObjectImpl<T>
        implements Interactors<T> {
}
package cn.chuanwise.xiaoming.listener;

import cn.chuanwise.util.Maps;
import cn.chuanwise.util.Preconditions;
import cn.chuanwise.util.Reflects;
import cn.chuanwise.xiaoming.annotation.EventListener;
import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import cn.chuanwise.xiaoming.event.Listeners;
import cn.chuanwise.xiaoming.object.ModuleObjectImpl;
import cn.chuanwise.xiaoming.object.PluginObject;
import cn.chuanwise.xiaoming.plugin.Plugin;
import cn.chuanwise.xiaoming.util.Registers;
import lombok.Getter;
import net.mamoe.mirai.event.Event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 消息处理函数管理器
 */
@Getter
public class EventManagerImpl extends ModuleObjectImpl implements EventManager {
    final Map<ListenerPriority, List<ListenerHandler>> listeners = new ConcurrentHashMap<>();

    public EventManagerImpl(XiaoMingBot xiaoMingBot) {
        super(xiaoMingBot);
    }

    @Override
    public Map<ListenerPriority, List<ListenerHandler>> getListeners() {
        return Collections.unmodifiableMap(listeners);
    }

    @Override
    public void unregisterListeners(Plugin plugin) {
        Preconditions.nonNull(plugin, "plugin");
        listeners.values().forEach(list -> list.removeIf(handler -> Objects.equals(handler.getPlugin(), plugin)));
    }

    @Override
    @SuppressWarnings("all")
    public <T extends Plugin> void registerListeners(Listeners<T> listeners, T plugin) {
        if (listeners instanceof PluginObject) {
            final PluginObject<T> pluginObject = (PluginObject<T>) listeners;
            pluginObject.setXiaoMingBot(getXiaoMingBot());
            pluginObject.setPlugin(plugin);
        }
        listeners.onRegister();
        for (Method method : Reflects.getExistedMethods(listeners.getClass())) {
            final EventListener[] handlers = method.getAnnotationsByType(EventListener.class);
            final Parameter[] parameters = method.getParameters();
            if (handlers.length == 0 || parameters.length != 1) {
                continue;
            }

            final EventListener handler = handlers[0];
            final Parameter parameter = parameters[0];

            // 如果监听函数的参数不是 Event 则免谈
            final Class<?> eventClass = parameter.getType();
            if (!Event.class.isAssignableFrom(eventClass)) {
                getLogger().error("监听函数 " + method.getName() + " 虽带有监听器注解，但参数并非事件，注册失败");
                continue;
            }

            final List<ListenerHandler> listenerHandlers = Maps.getOrPutSupply(this.listeners, handler.priority(), CopyOnWriteArrayList::new);
            listenerHandlers.add(new ListenerHandler(handler.priority(), eventClass, event -> {
                try {
                    method.setAccessible(true);
                    method.invoke(listeners, event);
                } catch (IllegalAccessException ignored) {
                } catch (InvocationTargetException exception) {
                    getLogger().error("监听函数 " + method.getName() + " 响应事件 " + event + " 时出现异常", exception.getCause());
                }
            }, handler.listenCancelledEvent(), plugin));
        }
    }

    @Override
    public void registerListener(ListenerHandler<?> handler) {
        final Plugin plugin = handler.getPlugin();
        Registers.checkRegister(xiaoMingBot, plugin, "listener");
        final List<ListenerHandler> samePriorityListeners = Maps.getOrPutSupply(listeners, handler.getPriority(), CopyOnWriteArrayList::new);
        samePriorityListeners.add(handler);
    }
}
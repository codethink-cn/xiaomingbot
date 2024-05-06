package cn.chuanwise.xiaoming.plugin;

import cn.chuanwise.api.SimpleSetableStatusHolder;
import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import cn.chuanwise.xiaoming.object.PluginObject;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;

@Getter
public class JavaPlugin
        extends SimpleSetableStatusHolder<Plugin.Status>
        implements Plugin {
    @Setter
    protected XiaoMingBot xiaoMingBot;

    protected PluginHandler handler;
    @Setter
    @NonNull
    Logger logger;
    @Setter
    @NonNull
    File dataFolder;

    public JavaPlugin() {
        super(Status.LOADED);
    }

    @Override
    public void setHandler(PluginHandler handler) {
        this.handler = handler;
        handler.setPlugin(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends cn.chuanwise.toolkit.preservable.Preservable> T setupConfiguration(Class<T> clazz, File file) throws IOException {
        final T result = getXiaoMingBot().getFileLoader().load(clazz, file);
        if (result instanceof PluginObject) {
            ((PluginObject) result).setPlugin(this);
            ((PluginObject<?>) result).setXiaoMingBot(xiaoMingBot);
        }
        return result;
    }

    public <T extends cn.chuanwise.toolkit.preservable.Preservable> T setupConfigurations(Class<T> clazz, File file) throws IOException {
        final T result = getXiaoMingBot().getFileLoader().load(clazz, file);
        if (result instanceof PluginObject) {
            ((PluginObject) result).setPlugin(this);
            ((PluginObject<?>) result).setXiaoMingBot(xiaoMingBot);
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (Objects.isNull(o) || !(o instanceof JavaPlugin)) {
            return false;
        }
        final JavaPlugin javaPlugin = (JavaPlugin) o;
        return Objects.equals(getName(), javaPlugin.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public Set<String> getOriginalTags() {
        return handler.getOriginalTags();
    }

    @Override
    public void flush() {
        handler.flush();
    }

    @Override
    public Set<String> getTags() {
        return handler.getTags();
    }

    @Override
    public boolean addTag(String tag) {
        return handler.addTag(tag);
    }

    @Override
    public boolean hasTag(String tag) {
        return handler.hasTag(tag);
    }

    @Override
    public boolean removeTag(String tag) {
        return handler.removeTag(tag);
    }
}
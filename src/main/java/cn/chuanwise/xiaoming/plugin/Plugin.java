package cn.chuanwise.xiaoming.plugin;

import cn.chuanwise.api.ChineseConvertable;
import cn.chuanwise.api.OriginalTagMarkable;
import cn.chuanwise.api.SetableStatusHolder;
import cn.chuanwise.exception.UnsupportedVersionException;
import cn.chuanwise.util.Resources;
import cn.chuanwise.xiaoming.classloader.XiaoMingClassLoader;
import cn.chuanwise.xiaoming.object.PluginObject;
import cn.chuanwise.xiaoming.object.XiaoMingObject;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 小明插件主类
 * 插件主类是小明加载插件的唯一入口。通过 {@link XiaoMingClassLoader} 加载插件类后，会检查该类是否是
 * 本接口的实现。如果是，才会继续通过多态性调用本接口的 {@link Plugin#onLoad()} 和 {@link Plugin#onEnable()} 以
 * 启动插件。
 * {@link Plugin#onLoad()} 的执行顺序是随机的，但 {@link Plugin#onEnable()} 的执行顺序必然是一个前置插件关系的拓
 * 扑序列。
 *
 * @author Chuanwise
 * @version 3.1
 * @date 2021年5月3日
 * @see PluginManager
 */
public interface Plugin
        extends XiaoMingObject, SetableStatusHolder<Plugin.Status>, OriginalTagMarkable {
    /**
     * 配置文件名
     */
    String CONFIGURATION_FILE_NAME = "configurations.json";

    static String getChineseName(Plugin plugin) {
        return Optional.ofNullable(plugin).map(Plugin::getName).orElse("小明内核");
    }

    static String getEnglishName(Plugin plugin) {
        return Optional.ofNullable(plugin).map(Plugin::getName).orElse("core");
    }

    /**
     * 获取插件名。如果插件属性中没有插件名，以 {@code jar} 文件名作为插件名
     */
    default String getName() {
        try {
            return getHandler().getName();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取插件别名
     */
    default String getAlias() {
        final Object alias = getHandler().get("alias");
        if (alias instanceof String) {
            return ((String) alias);
        } else if (alias instanceof List && !((List<?>) alias).isEmpty()) {
            final Object firstObject = ((List<?>) alias).get(0);
            if (firstObject instanceof String) {
                return ((String) firstObject);
            }
        }
        return getName();
    }

    /**
     * 获取插件版本号
     */
    default String getVersion() {
        try {
            return getHandler().getVersion();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获得插件名（版本）
     */
    default String getCompleteName() throws Throwable {
        if (Objects.equals(getVersion(), PluginHandler.DEFAULT_VERSION)) {
            return getName();
        } else {
            return getName() + "-" + getVersion();
        }
    }

    /**
     * 插件启动时执行
     */
    default void onEnable() {
    }

    /**
     * 插件关闭时执行
     */
    default void onDisable() {
    }

    /**
     * 软依赖卸载前调用
     */
    default void onDisableSoftDepend(Plugin plugin) {
        getXiaoMingBot().getPluginManager().disablePlugin(this);
    }

    /**
     * 插件加载时执行
     */
    default void onLoad() {
    }

    /**
     * 插件卸载时执行
     */
    default void onUnload() {
    }

    PluginHandler getHandler();

    void setHandler(PluginHandler handler);

    Logger getLogger();

    void setLogger(Logger logger);

    File getDataFolder();

    void setDataFolder(File folder);

    default File getConfigurationFile() {
        return new File(getDataFolder(), CONFIGURATION_FILE_NAME);
    }

    default boolean copyResource(String path, File to, boolean replace) throws IOException {
        return Resources.copyResource(getClass().getClassLoader(), path, to, replace);
    }

    default boolean copyDefaultConfiguration(boolean replace) throws IOException {
        return copyResource(CONFIGURATION_FILE_NAME, getConfigurationFile(), replace);
    }

    default <T extends cn.chuanwise.toolkit.preservable.Preservable> T setupConfiguration(Class<T> clazz) throws IOException {
        return setupConfiguration(clazz, getConfigurationFile());
    }

    default <T extends cn.chuanwise.toolkit.preservable.Preservable> T setupConfigurations(Class<T> clazz) throws IOException {
        return setupConfigurations(clazz, getConfigurationFile());
    }

    <T extends cn.chuanwise.toolkit.preservable.Preservable> T setupConfiguration(Class<T> clazz, File file) throws IOException;

    <T extends cn.chuanwise.toolkit.preservable.Preservable> T setupConfigurations(Class<T> clazz, File file) throws IOException;

    default <T extends cn.chuanwise.toolkit.preservable.Preservable> T setupConfiguration(Class<T> clazz, String fileName) throws IOException {
        return setupConfiguration(clazz, new File(getDataFolder(), fileName));
    }

    default <T extends cn.chuanwise.toolkit.preservable.Preservable> T setupConfigurations(Class<T> clazz, String fileName) throws IOException {
        return setupConfigurations(clazz, new File(getDataFolder(), fileName));
    }

    default <T extends cn.chuanwise.toolkit.preservable.Preservable> T setupConfiguration(Class<T> clazz, Supplier<T> supplier) {
        return setupConfiguration(clazz, getConfigurationFile(), supplier);
    }

    default <T extends cn.chuanwise.toolkit.preservable.Preservable> T setupConfigurations(Class<T> clazz, Supplier<T> supplier) {
        return setupConfigurations(clazz, getConfigurationFile(), supplier);
    }

    /**
     * 从文件中读取配置信息，并生成配置对象。读取失败时使用默认配置信息
     *
     * @param clazz    配置类类对象
     * @param file     文件位置
     * @param supplier 生成默认配置信息的方法
     * @param <T>      配置类类型
     * @return 从文件中导入的值，或由默认配置生成器生成的值
     */
    @SuppressWarnings("unchecked")
    default <T extends cn.chuanwise.toolkit.preservable.Preservable> T setupConfiguration(Class<T> clazz, File file, Supplier<T> supplier) {
        final T data = getXiaoMingBot().getFileLoader().loadOrSupply(clazz, file, supplier);
        if (data instanceof PluginObject) {
            ((PluginObject) data).setPlugin(this);
            ((PluginObject<?>) data).setXiaoMingBot(getXiaoMingBot());
        }
        return data;
    }

    default <T extends cn.chuanwise.toolkit.preservable.Preservable> T setupConfigurations(Class<T> clazz, File file, Supplier<T> supplier) {
        final T data = getXiaoMingBot().getFileLoader().loadOrSupply(clazz, file, supplier);
        if (data instanceof PluginObject) {
            ((PluginObject) data).setPlugin(this);
            ((PluginObject<?>) data).setXiaoMingBot(getXiaoMingBot());
        }
        return data;
    }

    default <T extends cn.chuanwise.toolkit.preservable.Preservable> T setupConfiguration(Class<T> clazz, String fileName, Supplier<T> supplier) {
        return setupConfiguration(clazz, new File(getDataFolder(), fileName), supplier);
    }

    default <T extends cn.chuanwise.toolkit.preservable.Preservable> T setupConfigurations(Class<T> clazz, String fileName, Supplier<T> supplier) {
        return setupConfigurations(clazz, new File(getDataFolder(), fileName), supplier);
    }

    /**
     * 小明插件的各种状态，默认 UNLOADED
     */
    enum Status implements ChineseConvertable {
        CONSTRUCTED,
        LOADING,
        LOADED,
        ENABLING,
        ENABLED,
        DISABLING,
        DISABLED,
        UNLOADING,
        UNLOADED,

        ERROR;

        @Override
        public String toChinese() {
            switch (this) {
                case DISABLING:
                    return "正在关闭";
                case CONSTRUCTED:
                    return "尚未加载";
                case ENABLING:
                    return "正在启动";
                case DISABLED:
                    return "已关闭";
                case LOADING:
                    return "正在载入";
                case ENABLED:
                    return "已启动";
                case LOADED:
                    return "已载入";
                case ERROR:
                    return "异常";
                default:
                    throw new UnsupportedVersionException();
            }
        }
    }
}
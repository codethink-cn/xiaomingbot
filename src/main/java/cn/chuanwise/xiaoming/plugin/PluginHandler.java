package cn.chuanwise.xiaoming.plugin;

import cn.chuanwise.api.OriginalTagMarkable;
import cn.chuanwise.exception.IllegalOperationException;
import cn.chuanwise.toolkit.map.PathSetter;
import cn.chuanwise.toolkit.map.TypePathGetter;
import cn.chuanwise.util.Arrays;
import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.util.Tags;
import cn.chuanwise.xiaoming.permission.Permission;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public interface PluginHandler
        extends TypePathGetter, PathSetter, OriginalTagMarkable {
    String DEFAULT_VERSION = "unknown";

    String MAIN_CLASS_NAME_PATH = "main";
    String SINGLE_AUTHOR_PATH = "author";
    String MULTIPLE_AUTHORS_PATH = "authors";
    String DEPENDS_PATH = "depends";
    String SOFT_DEPENDS_PATH = "soft-depends";
    String NAME_PATH = "name";
    String VERSION_PATH = "version";
    String USER_PERMISSIONS_PATH = "user-permissions";
    String TAGS_PATH = "tags";

    Map<String, Object> getValues();

    default Permission[] getUserPermissions() throws Throwable {
        return getAsArrayContainer(USER_PERMISSIONS_PATH, Permission.class).orElse(Arrays.emptyArray(Permission.class));
    }

    default String getName() {
        try {
            return getStringContainer(NAME_PATH).orElseGet(() -> {
                final String jarFileName = getFile().getName();
                return jarFileName.substring(0, jarFileName.lastIndexOf('.'));
            });
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    default String getVersion() {
        try {
            return getStringContainer(VERSION_PATH).orElse(DEFAULT_VERSION);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    default String getMainClassName() {
        return getString(MAIN_CLASS_NAME_PATH);
    }

    default String getSingleAuthor() {
        return getString(SINGLE_AUTHOR_PATH);
    }

    default String[] getMultipleAuthors() {
        try {
            return getStringArrayContainer(MULTIPLE_AUTHORS_PATH).orElse(Arrays.emptyArray(String.class));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    default String[] getDepends() {
        try {
            return getStringArrayContainer(DEPENDS_PATH).orElse(Arrays.emptyArray(String.class));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    default String[] getSoftDepends() {
        try {
            return getStringArrayContainer(SOFT_DEPENDS_PATH).orElse(Arrays.emptyArray(String.class));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    default boolean isSoftDepend(String pluginName) {
        return java.util.Arrays.asList(getSoftDepends()).contains(pluginName);
    }

    default boolean isSoftDepend(Plugin plugin) {
        return isSoftDepend(plugin.getName());
    }

    default boolean isSoftDepend(PluginHandler information) {
        return isSoftDepend(information.getName());
    }

    default boolean isDepend(String pluginName) {
        return java.util.Arrays.asList(getDepends()).contains(pluginName);
    }

    default boolean isDepend(Plugin plugin) {
        return isDepend(plugin.getName());
    }

    default boolean isDepend(PluginHandler information) {
        return isDepend(information.getName());
    }

    default boolean isAllDependsEnabled() {
        final PluginManager pluginManager = getPlugin().getXiaoMingBot().getPluginManager();
        for (String depend : getDepends()) {
            if (!pluginManager.isEnabled(depend)) {
                return false;
            }
        }
        return true;
    }

    default boolean isAllSoftDependsEnabled() {
        final PluginManager pluginManager = getPlugin().getXiaoMingBot().getPluginManager();
        for (String depend : getSoftDepends()) {
            if (pluginManager.isExists(depend)) {
                final Optional<PluginHandler> optionalHandler = pluginManager.getPluginHandler(depend);
                if (!optionalHandler.isPresent()) {
                    return false;
                }

                final PluginHandler handler = optionalHandler.get();
                if (handler.getPlugin().getStatus() != Plugin.Status.ENABLED
                        && handler.getPlugin().getStatus() != Plugin.Status.ERROR) {
                    return false;
                }
            }
        }
        return true;
    }

    default boolean isError() {
        return getPlugin().getStatus() == Plugin.Status.ERROR;
    }

    default boolean isEnabled() {
        final Plugin plugin = getPlugin();
        if (Objects.isNull(plugin)) {
            return false;
        }

        return plugin.getStatus() == Plugin.Status.ENABLED;
    }

    default boolean isLoaded() {
        final Plugin plugin = getPlugin();
        if (Objects.isNull(plugin)) {
            return false;
        }

        return plugin.getStatus() != Plugin.Status.CONSTRUCTED;
    }

    @Override
    default Set<String> getOriginalTags() {
        try {
            return CollectionUtil.asSet(Tags.ALL, getName());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    default void flush() {
    }

    @Override
    default Set<String> getTags() {
        final Set<String> originalTags = getOriginalTags();
        try {
            return getAsStringListContainer(TAGS_PATH)
                    .map(x -> x.stream().collect(Collectors.toSet()))
                    .map(x -> {
                        x.addAll(originalTags);
                        return x;
                    })
                    .map(Collections::unmodifiableSet)
                    .orElse(originalTags);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    default boolean addTag(String tag) {
        throw new IllegalOperationException("can not modify plugin.json at run time!");
    }

    @Override
    default boolean hasTag(String tag) {
        return getTags().contains(tag);
    }

    @Override
    default boolean removeTag(String tag) {
        throw new IllegalOperationException("can not modify plugin.json at run time!");
    }

    File getFile();

    void setFile(File file);

    Plugin getPlugin();

    void setPlugin(Plugin plugin);
}

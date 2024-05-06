package cn.chuanwise.xiaoming.group;

import cn.chuanwise.api.TagMarkable;
import cn.chuanwise.toolkit.preservable.Preservable;
import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.xiaoming.object.XiaoMingObject;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public interface GroupInformationManager extends XiaoMingObject, Preservable {
    @Deprecated
    default GroupInformation forCode(long group) {
        return getGroupInformation(group).orElse(null);
    }

    default Optional<GroupInformation> getGroupInformation(long group) {
        try {
            return CollectionUtil.findFirst(getGroups(), groupInformation -> groupInformation.getCode() == group).toOptional();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    default List<GroupInformation> searchGroupsByTag(String tag) {
        return getGroups().stream()
                .filter(information -> information.hasTag(tag))
                .collect(Collectors.toList());
    }

    default Set<String> getTags(long group) {
        return getGroupInformation(group)
                .map(TagMarkable::getTags)
                .orElseGet(() -> GroupInformation.originalTagsOf(group));
    }

    GroupInformation addGroupInformation(long groupCode);

    boolean addGroupInformation(GroupInformation information);

    Set<GroupInformation> getGroups();

    default String getAliasAndCode(long group) {
        return getGroupInformation(group)
                .map(GroupInformation::getAliasAndCode)
                .orElseGet(() -> String.valueOf(group));
    }

    default Optional<String> getAlias(long group) {
        return getGroupInformation(group)
                .map(GroupInformation::getAlias);
    }
}
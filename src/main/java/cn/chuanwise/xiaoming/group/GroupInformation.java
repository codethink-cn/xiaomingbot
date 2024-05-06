package cn.chuanwise.xiaoming.group;

import cn.chuanwise.api.OriginalTagMarkable;
import cn.chuanwise.util.CollectionUtil;
import cn.chuanwise.util.StringUtil;
import cn.chuanwise.util.Tags;
import cn.chuanwise.xiaoming.contact.contact.GroupContact;
import cn.chuanwise.xiaoming.object.XiaoMingObject;

import java.util.Optional;
import java.util.Set;

public interface GroupInformation extends XiaoMingObject, OriginalTagMarkable {
    static Set<String> originalTagsOf(long group) {
        return CollectionUtil.asSet(String.valueOf(group), Tags.ALL);
    }

    long getCode();

    default String getCodeString() {
        return String.valueOf(getCode());
    }

    String getAlias();

    void setAlias(String alias);

    default String getAliasAndCode() {
        final String alias = getAlias();
        if (StringUtil.isEmpty(alias)) {
            return getCodeString();
        } else {
            return alias + "（" + getCodeString() + "）";
        }
    }

    default Optional<GroupContact> getContact() {
        return getXiaoMingBot().getContactManager().getGroupContact(getCode());
    }

    @Override
    default Set<String> getOriginalTags() {
        return originalTagsOf(getCode());
    }
}
package cn.chuanwise.xiaoming.recept;

import cn.chuanwise.xiaoming.object.ModuleObject;
import cn.chuanwise.xiaoming.property.PropertyHandler;
import cn.chuanwise.xiaoming.user.GroupXiaoMingUser;
import cn.chuanwise.xiaoming.user.MemberXiaoMingUser;
import cn.chuanwise.xiaoming.user.PrivateXiaoMingUser;

import java.util.Map;
import java.util.Optional;

/**
 * 小明接待员
 */
public interface Receptionist extends ModuleObject, PropertyHandler {
    long getCode();

    default String getCodeString() {
        return String.valueOf(getCode());
    }

    Map<Long, GroupXiaoMingUser> getGroupXiaoMingUsers();

    Map<Long, MemberXiaoMingUser> getMemberXiaoMingUsers();

    Optional<PrivateXiaoMingUser> getPrivateXiaoMingUser();

    Optional<GroupXiaoMingUser> getGroupXiaoMingUser(long groupCode);

    Optional<MemberXiaoMingUser> getMemberXiaoMingUser(long code);
}
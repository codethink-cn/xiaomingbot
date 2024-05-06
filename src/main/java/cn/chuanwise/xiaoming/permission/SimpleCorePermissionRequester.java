package cn.chuanwise.xiaoming.permission;

import cn.chuanwise.xiaoming.account.Account;
import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import cn.chuanwise.xiaoming.group.GroupInformation;
import cn.chuanwise.xiaoming.preservable.SimplePreservable;
import cn.chuanwise.xiaoming.user.GroupXiaoMingUser;
import cn.chuanwise.xiaoming.user.XiaoMingUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SimpleCorePermissionRequester
        extends SimplePreservable
        implements PermissionRequester {
    public SimpleCorePermissionRequester(XiaoMingBot xiaoMingBot) {
        setXiaoMingBot(xiaoMingBot);
    }

    @Override
    public boolean hasPermission(XiaoMingUser user, Permission permission) {
        final Account account = user.getAccount();
        if (user instanceof GroupXiaoMingUser) {
            final GroupXiaoMingUser groupXiaoMingUser = (GroupXiaoMingUser) user;
            return hasPermission(account, groupXiaoMingUser.getGroupInformation(), permission);
        } else {
            return hasPermission(account, permission);
        }
    }

    @Override
    public boolean hasPermission(Account account, Permission permission) {
        return account.isAdministrator();
    }

    @Override
    public boolean hasPermission(Account account, GroupInformation groupInformation, Permission permission) {
        return account.isAdministrator();
    }
}
